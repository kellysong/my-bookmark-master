package com.sjl.bookmark.net;

import com.sjl.core.net.BaseUrlAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * 多个baserUrl适配器
 *
 * @author Kelly
 * @version 1.0.0
 * @filename MyBaseUrlAdapter.java
 * @time 2019/1/11 9:15
 * @copyright(C) 2019 song
 */
public class MyBaseUrlAdapter implements BaseUrlAdapter {
    @Override
    public String getDefaultBaseUrl() {
        return HttpConstant.DEFAULT_BASE_URL;
    }

    @Override
    public Map<String, String> getAppendBaseUrl() {
        Map<String, String>  baserUrl = new HashMap<>();
        baserUrl.put("wanandroid",HttpConstant.DEFAULT_BASE_URL);
        baserUrl.put("kuaidi100",HttpConstant.KUAIDI100_BASE_URL);
        baserUrl.put("my-bookmark",HttpConstant.MY_BOOKMARK_BASE_URL);//个人应用
        baserUrl.put("zhuishushenqi",HttpConstant.ZHUISHU_BASE_URL);
        baserUrl.put("zhihu",HttpConstant.ZHIHU_BASE_URL);
        return baserUrl;
    }

}
