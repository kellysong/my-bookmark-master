package com.sjl.bookmark.net

import com.sjl.bookmark.kotlin.util.SpUtils

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename HttpConstant.java
 * @time 2018/3/21 9:17
 * @copyright(C) 2018 song
 */
object HttpConstant {
    /**
     * 我的书签服务base url
     */
    const val MY_BOOKMARK_BASE_URL = "http://192.168.0.176:8080/"

    fun getBookmarkBaseUrl():String{
        return SpUtils.getBookmarkBaseUrl()
    }

    /**
     * WanAndroid
     */
    const val DEFAULT_BASE_URL = "https://www.wanandroid.com/"

    /**
     * 快递100
     */
    const val KUAIDI100_BASE_URL = "https://www.kuaidi100.com/"

    /**
     * 追书神器
     */
    const val ZHUISHU_BASE_URL = "http://api.zhuishushenqi.com"

    /**
     * 追书神器图片
     */
    const val ZHUISHU_IMG_BASE_URL = "http://statics.zhuishushenqi.com"

    /**
     * 知乎日报
     * https://news-at.zhihu.com/api/4/news/latest
     */
    const val ZHIHU_BASE_URL = "http://news-at.zhihu.com/"

    /**
     * 区分不同服务
     */
    const val DOMAIN_NAME = "Domain-Name"
    // start玩安卓================
    /**
     * 每页数量
     */
    const val PAGE_SIZE = 20
    const val PAGE_SIZE_15 = 15
    const val BANNER_KEY = "banner"
    const val ARTICLE_KEY = "article"

    /**
     * cid 分类的id，上述二级目录的id
     */
    const val CONTENT_CID_KEY = "cid"

    /**
     * 一级标题title
     */
    const val CONTENT_TITLE_KEY = "title"

    /**
     * 二级标题集合
     */
    const val CONTENT_CHILDREN_DATA_KEY = "childrenData"

    /**
     * 0打开单个类别,隐藏专题页面的搜索按钮，1打开专题
     */
    const val CONTENT_OPEN_FLAG = "openFlag"

    // end玩安卓================
    interface LoadType {
        companion object {
            /**
             * 刷新成功
             */
            const val TYPE_REFRESH_SUCCESS = 1

            /**
             * 刷新错误
             */
            const val TYPE_REFRESH_ERROR = 2

            /**
             * 加载更多成功
             */
            const val TYPE_LOAD_MORE_SUCCESS = 3

            /**
             * 加载更多失败
             */
            const val TYPE_LOAD_MORE_ERROR = 4
        }
    }
}