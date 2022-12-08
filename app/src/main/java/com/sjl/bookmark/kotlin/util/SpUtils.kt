package com.sjl.bookmark.kotlin.util

import android.text.TextUtils
import com.google.gson.Gson
import com.sjl.bookmark.app.AppConstant
import com.sjl.bookmark.app.MyApplication
import com.sjl.bookmark.net.HttpConstant
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

    /**
     * 保存书签base url
     * @param baseUrl String
     */
    fun saveBookmarkBaseUrl(baseUrl: String) {
        ph.put(AppConstant.SETTING.BOOKMARK_BASE_URL, baseUrl)
    }


    /**
     * 获取书签base url
     * @return String
     */
    fun getBookmarkBaseUrl():String{
        val baseUrl = ph.get(AppConstant.SETTING.BOOKMARK_BASE_URL, HttpConstant.MY_BOOKMARK_BASE_URL) as String
        return baseUrl
    }

    /**
     * 保存哀悼日
     * @param mourningDays String
     */
    fun saveMourningDays(mourningDays: String) {
        ph.put(AppConstant.SETTING.MOURNING_DAYS, mourningDays)
    }

    /**
     * 获取哀悼日
     * @return Array<String>
     */
    fun getMourningDays():Array<String>{
        val mourningDays = ph.get(AppConstant.SETTING.MOURNING_DAYS, "") as String
        val split = mourningDays.split(",")
        return split.toTypedArray()
    }
}

