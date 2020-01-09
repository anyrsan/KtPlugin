package com.any.org.gradleplugin

import com.android.build.api.transform.*
import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.pipeline.TransformManager
import com.any.org.gradleplugin.asm.AnyClassVisitor
import com.any.org.gradleplugin.common.Constant
import com.any.gradle.data.ClazzData
import com.any.gradle.data.CustomData
import com.any.org.gradleplugin.common.ConvertMap
import com.any.org.gradleplugin.common.Log
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.File
import java.io.FileOutputStream
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

/**
 *
 * @author any
 * @time 2019/07/29 14.45
 * @details
 */


class MyCustomPlugin : Transform(), Plugin<Project> {

    //插件名称
    private val mPluginName = "testKp"

    private lateinit var customData: CustomData

    override fun getName(): String = mPluginName

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> = TransformManager.CONTENT_CLASS

    override fun isIncremental(): Boolean = false

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> = TransformManager.SCOPE_FULL_PROJECT

    override fun apply(project: Project) {
        val android = project.extensions.getByType(AppExtension::class.java)
        android.registerTransform(this)
        //引入自定义参数
        val extension = project.extensions.create("testAk", CustomData::class.java)
        project.afterEvaluate {
            //默认是输出log
            Log.setDebug(extension.debug)
            Log.w("配置数据完成加载  $extension")
            customData = extension
            //初始化数据
            ConvertMap.getInstance().initMap(
                customData.annotationName,
                customData.packageName,
                customData.includeClass,
                customData.excludeClass,
                customData.eventList
            )
        }

    }


    override fun transform(transformInvocation: TransformInvocation?) {
        transformInvocation?.apply {
            val startTime = System.currentTimeMillis()
            Log.w("扫描方法进入")
            val op = outputProvider
            op?.deleteAll()
            val ip = inputs
            ip.forEach {
                it.directoryInputs.forEach { dirInput ->
                    handlerDirectory(dirInput, op)
                }

                it.jarInputs.forEach { jarInput ->
                    handlerJarInput(jarInput, op)
                }
            }
            val endTime = System.currentTimeMillis() - startTime
            Log.w("扫描完成，总共耗时 ${endTime / 1000} s")
        }
    }

    //处理目录扫描
    private fun handlerDirectory(dirInput: DirectoryInput, outP: TransformOutputProvider) {
        val dirFile = dirInput.file
        if (dirFile.isDirectory) {
            val dest = outP.getContentLocation(dirInput.name, dirInput.contentTypes, dirInput.scopes, Format.DIRECTORY)
            Log.w("输出目录  $dest")
            dirFile.walk().filter {
                it.isFile
            }.filter {
                it.extension == "class"
            }.filter {
                //过滤掉系统的配置的文件
                it.nameWithoutExtension != "R" && !it.nameWithoutExtension.startsWith("R$") && it.nameWithoutExtension != "BuildConfig"
            }.forEach {
                //获取类正确的名称
                val data = convertData(it, dirFile)
                //处理当前类进行字节码操作
                val isOk = Constant.containsClass(data)
                if (isOk) {
                    Log.w("输出条件 $isOk 的文件  $data ")
                    val classReader = ClassReader(it.readBytes())
                    val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    val classVisitor = AnyClassVisitor(data, classWriter)
                    classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES)
                    val byteCode = classWriter.toByteArray()
                    val newFile = File(it.parentFile.absoluteFile, it.name)
                    val fos = FileOutputStream(newFile)
                    fos.write(byteCode)
                    fos.close()
                }
            }
        }
        //处理完输入文件之后，要把输出给下一个任务
        val dest = outP.getContentLocation(
            dirInput.name,
            dirInput.contentTypes, dirInput.scopes,
            Format.DIRECTORY
        )
        FileUtils.copyDirectory(dirInput.file, dest)

    }

    //对引入的jar进行扫描 可以是第三方jar,或者是aar文件，可以动态修改里面的文件
    private fun handlerJarInput(jarInput: JarInput, outP: TransformOutputProvider) {
        if (jarInput.file.extension == "jar") {
            val jarName = jarInput.file.nameWithoutExtension
            val md5Name = DigestUtils.md5Hex(jarInput.file.absolutePath)
            val jarFile = JarFile(jarInput.file)
            val enumeration = jarFile.entries()
            val tempFile = File(jarInput.file.parent, "classes_temp.jar")
            if (tempFile.exists()) {
                tempFile.delete()
            }
            val jarOutputStream = JarOutputStream(FileOutputStream(tempFile))
            while (enumeration.hasMoreElements()) {
                val jarEntry = enumeration.nextElement()
                val entryName = jarEntry.name
                val zipEntry = ZipEntry(entryName)
                val inputStream = jarFile.getInputStream(jarEntry)
                //获取类文件  // R$xxx.class没有过滤
                if (filterEntryName(entryName)) {
                    val data = convertData(entryName)
                    val isOk = Constant.containsClass(data)
                    jarOutputStream.putNextEntry(zipEntry)
                    if (isOk) {
                        Log.w("取到jar中类文件。。。   $data  $entryName")
                        val classReader = ClassReader(IOUtils.toByteArray(inputStream))
                        val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                        val classVisitor = AnyClassVisitor(data, classWriter)
                        classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES)
                        val byteCode = classWriter.toByteArray()
                        jarOutputStream.write(byteCode)
                    } else {
                        jarOutputStream.write(IOUtils.toByteArray(inputStream))
                    }
                }
                jarOutputStream.closeEntry()
            }
            jarOutputStream.close()
            jarFile.close()
            val dest = outP.getContentLocation(jarName + md5Name, jarInput.contentTypes, jarInput.scopes, Format.JAR)
            FileUtils.copyFile(tempFile, dest)
            tempFile.delete()
        }
    }


    //  分析包文件 ，解析当前类的类名及包名
    private fun convertData(file: File, dirFile: File): ClazzData {
        val result = file.absolutePath.replace(dirFile.absolutePath, "").replace("\\", ".").substring(1)
        val cPackage = result.replace(".${file.name}", "")
        val cName = file.nameWithoutExtension
        return ClazzData("$cPackage.$cName", cName, cPackage, null)
    }


    //com/any/bn/MyClass.class
    private fun convertData(entryName: String): ClazzData {
        val newName = entryName.replace("/", ".").replace(".class", "")
        val cName = newName.substringAfterLast(".")
        val cPackage = newName.substringBeforeLast(".")
        return ClazzData(newName, cName, cPackage, null)
    }


    private fun filterEntryName(entryName: String): Boolean {
        return if (entryName.endsWith(".class")) {
            val cName = entryName.substringAfterLast("/")
            cName != "R.class" && cName != "BuildConfig.class" && !cName.startsWith("R$")
        } else {
            false
        }
    }


}