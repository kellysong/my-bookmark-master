package com.sjl.bookmark.net

import com.sjl.core.net.BaseUrlAdapter
import java.util.*

/**
 * 多个baserUrl适配器
 *
 * @author Kelly
 * @version 1.0.0
 * @filename MyBaseUrlAdapter.java
 * @time 2019/1/11 9:15
 * @copyright(C) 2019 song
 */
class MyBaseUrlAdapter : BaseUrlAdapter {
    override fun getDefaultBaseUrl(): String {
        return HttpConstant.DEFAULT_BASE_URL
    }

    override fun getAppendBaseUrl(): Map<String, String> {
        val baserUrl: MutableMap<String, String> = HashMap()
        baserUrl["wanandroid"] = HttpConstant.DEFAULT_BASE_URL
        baserUrl["kuaidi100"] = HttpConstant.KUAIDI100_BASE_URL
        baserUrl["my-bookmark"] = HttpConstant.MY_BOOKMARK_BASE_URL //个人应用
        baserUrl["zhuishushenqi"] = HttpConstant.ZHUISHU_BASE_URL
        baserUrl["zhihu"] = HttpConstant.ZHIHU_BASE_URL
        return baserUrl
    }
}