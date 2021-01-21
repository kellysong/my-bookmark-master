package com.sjl.bookmark.kotlin.util

import com.sjl.bookmark.app.MyApplication
import com.sjl.core.util.PreferencesHelper

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename SpUtils
 * @time 2021/1/21 11:07
 * @copyright(C) 2021 song
 */
object SpUtils{
    private const val KEY_COOKIE = "key_cookie"
    private var ph = PreferencesHelper.getInstance(MyApplication.getContext())

    fun saveCookie(set: Set<String>?) {
        ph.put(KEY_COOKIE, set)
    }

    fun getCookies() = ph.get(KEY_COOKIE,HashSet<String>()) as? Set<String>

    fun clearCookies() {
        saveCookie(HashSet<String>())
    }


}

