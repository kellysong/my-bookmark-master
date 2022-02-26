package com.sjl.bookmark.dao.util

import android.content.Context
import com.sjl.bookmark.dao.impl.CollectDaoImpl
import com.sjl.bookmark.entity.table.Collection
import java.util.*

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename CollectUtils.java
 * @time 2018/3/26 15:33
 * @copyright(C) 2018 song
 */
object CollectUtils {
    /**
     * 收藏网页
     * @param context 上下文
     * @param title 标题
     * @param url 链接
     * @return 0 收藏成功，1改收藏已经存在，-1收藏失败
     */
    fun collectWebPage(context: Context?, title: String?, url: String?): Int {
        val collectService = CollectDaoImpl(context)
        var result = collectService.isExistCollection(url)
        if (result) {
            return 1
        }
        result = collectService.saveCollection(Collection(null, title, 0, url, Date(), 0, 0))
        return if (result) {
            0
        } else {
            -1
        }
    }
}