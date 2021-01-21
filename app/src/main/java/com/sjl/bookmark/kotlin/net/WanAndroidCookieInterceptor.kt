package com.sjl.bookmark.kotlin.net

import com.sjl.bookmark.kotlin.util.SpUtils
import okhttp3.Interceptor
import okhttp3.Response

/**
 * TODO
 * @author Kelly
 * @version 1.0.0
 * @filename WanAndroidCookieInterceptor
 * @time 2021/1/21 11:04
 * @copyright(C) 2021 song
 */
class WanAndroidCookieInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val finalResponse: Response

        val request = chain.request()
        val url = request.url().toString()
        if (url.contains("wanandroid.com/user/login")){
            val cookies = SpUtils.getCookies()
            if (cookies.isNullOrEmpty()) {
                val originResponse = chain.proceed(request)

                finalResponse = originResponse

            } else {
                val builder = request.newBuilder()
                cookies.forEach {
                    builder.addHeader("Cookie", it)
                }
                finalResponse = chain.proceed(builder.build())
            }
        }else{
            finalResponse = chain.proceed(request.newBuilder().removeHeader("Cookie").build())
        }
        return finalResponse
    }

}