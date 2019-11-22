package com.any.ktplugin;

import com.any.event.anno.KAnn;

/**
 * @author any
 * @time 2019/08/02 13.57
 * @details
 */
public class JDoMain {

    @KAnn(flag = 1, data = "http://www.baidu.com")
    public String getApi() {
        String url = "kkkbbbb";
        return url;
    }
}
