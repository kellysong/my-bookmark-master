package com.sjl.bookmark.api

import com.sjl.bookmark.entity.*
import com.sjl.bookmark.entity.Article.DatasBean
import io.reactivex.Observable
import retrofit2.http.*

interface WanAndroidApiService {
    /**
     * 置顶文章
     * https://www.wanandroid.com/article/top/json
     */
    @get:GET("article/top/json")
    @get:Headers("Domain-Name:wanandroid")
    val topArticles: Observable<DataResponse<List<DatasBean>>>

    /**
     * 首页数据
     * http://www.wanandroid.com/article/list/0/json
     *
     * @param page page
     */
    @Headers("Domain-Name:wanandroid")
    @GET("article/list/{page}/json")
    fun getHomeArticles(@Path("page") page: Int): Observable<DataResponse<Article>>

    /**
     * 首页Banner
     * http://www.wanandroid.com/banner/json
     */
    @get:GET("banner/json")
    @get:Headers("Domain-Name:wanandroid")
    val homeBanners: Observable<DataResponse<List<TopBanner>>>

    /**
     * 知识体系
     * http://www.wanandroid.com/tree/json
     *
     */
    @get:Headers("Domain-Name:wanandroid")
    @get:GET("tree/json")
    val knowledgeCategory: Observable<DataResponse<List<Category>>>

    /**
     * 知识体系下的文章
     * http://www.wanandroid.com/article/list/0/json?cid=168
     * cid 分类的id，上述二级目录的id
     *
     * @param page page
     * @param cid  cid
     */
    @Headers("Domain-Name:wanandroid")
    @GET("article/list/{page}/json")
    fun getKnowledgeCategoryArticles(
        @Path("page") page: Int,
        @Query("cid") cid: Int
    ): Observable<DataResponse<Article>>

    /**
     * 热门搜索
     * http://www.wanandroid.com/hotkey/json
     */
    @get:GET("hotkey/json")
    @get:Headers("Domain-Name:wanandroid")
    val hotKeys: Observable<DataResponse<List<HotKey>>>

    /**
     * 搜索
     * http://www.wanandroid.com/article/query/0/json
     *
     * @param page page
     * @param k    POST search key
     */
    @Headers("Domain-Name:wanandroid")
    @POST("article/query/{page}/json")
    @FormUrlEncoded
    fun getSearchArticles(
        @Path("page") page: Int,
        @Field("k") k: String?
    ): Observable<DataResponse<Article>>

    /**
     * 登录，用于获取签到获取积分
     * @param username
     * @param password
     * @return
     */
    @Headers("Domain-Name:wanandroid")
    @FormUrlEncoded
    @POST("user/login")
    fun login(
        @Field("username") username: String?,
        @Field("password") password: String?
    ): Observable<DataResponse<UserLogin>>
}