package com.sjl.bookmark.api

import com.sjl.bookmark.entity.zhihu.NewsCommentDto
import com.sjl.bookmark.entity.zhihu.NewsDetailDto
import com.sjl.bookmark.entity.zhihu.NewsDto
import com.sjl.bookmark.entity.zhihu.NewsExtraDto
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

/**
 * 知乎日报api
 */
interface ZhiHuApiService {
    /**
     * 最新消息
     *
     * @return
     */
    @get:GET("api/4/news/latest")
    @get:Headers("Domain-Name:zhihu")
    val news: Observable<NewsDto>

    /**
     * 日报详情
     *
     * @param id
     * @return
     */
    @Headers("Domain-Name:zhihu")
    @GET("api/4/news/{id}")
    fun getNewsDetail(@Path("id") id: String?): Observable<NewsDetailDto>

    /**
     * 历史日报
     *
     * @param date
     * @return
     */
    @Headers("Domain-Name:zhihu")
    @GET("api/4/news/before/{date}")
    fun getBeforeNews(@Path("date") date: String?): Observable<NewsDto>

    /**
     * 新闻额外信息
     * 输入新闻的ID，获取对应新闻的额外信息，如评论数量，所获的『赞』的数量。
     *
     * @param id
     * @return
     */
    @Headers("Domain-Name:zhihu")
    @GET("api/4/story-extra/{id}")
    fun getStoryExtra(@Path("id") id: String?): Observable<NewsExtraDto>

    /**
     * 新闻长评论
     *
     * @param id
     * @return
     */
    @Headers("Domain-Name:zhihu")
    @GET("api/4/story/{id}/long-comments")
    fun getLongComment(@Path("id") id: String?): Observable<NewsCommentDto>

    /**
     * 新闻短评论
     *
     * @param id
     * @return
     */
    @Headers("Domain-Name:zhihu")
    @GET("api/4/story/{id}/short-comments")
    fun getShortComment(@Path("id") id: String?): Observable<NewsCommentDto>
}