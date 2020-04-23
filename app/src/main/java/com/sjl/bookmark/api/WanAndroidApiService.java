package com.sjl.bookmark.api;


import com.sjl.bookmark.entity.Article;
import com.sjl.bookmark.entity.TopBanner;
import com.sjl.bookmark.entity.Category;
import com.sjl.bookmark.entity.DataResponse;
import com.sjl.bookmark.entity.HotKey;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WanAndroidApiService {

    /**
     * 置顶文章
     * https://www.wanandroid.com/article/top/json
     */
    @Headers({"Domain-Name:wanandroid"})
    @GET("article/top/json")
    Observable<DataResponse<List<Article.DatasBean>>> getTopArticles();

    /**
     * 首页数据
     * http://www.wanandroid.com/article/list/0/json
     *
     * @param page page
     */
    @Headers({"Domain-Name:wanandroid"})
    @GET("article/list/{page}/json")
    Observable<DataResponse<Article>> getHomeArticles(@Path("page") int page);



    /**
     * 首页Banner
     * http://www.wanandroid.com/banner/json
     */
    @Headers({"Domain-Name:wanandroid"})
    @GET("banner/json")
    Observable<DataResponse<List<TopBanner>>> getHomeBanners();

    /**
     * 知识体系
     * http://www.wanandroid.com/tree/json
     *
     */
    @GET("tree/json")
    @Headers({"Domain-Name:wanandroid"})
    Observable<DataResponse<List<Category>>> getKnowledgeCategory();

    /**
     * 知识体系下的文章
     * http://www.wanandroid.com/article/list/0/json?cid=168
     * cid 分类的id，上述二级目录的id
     *
     * @param page page
     * @param cid  cid
     */
    @Headers({"Domain-Name:wanandroid"})
    @GET("article/list/{page}/json")
    Observable<DataResponse<Article>> getKnowledgeCategoryArticles(@Path("page") int page, @Query("cid") int cid);

    /**
     * 热门搜索
     * http://www.wanandroid.com/hotkey/json
     */
    @Headers({"Domain-Name:wanandroid"})
    @GET("hotkey/json")
    Observable<DataResponse<List<HotKey>>> getHotKeys();

    /**
     * 搜索
     * http://www.wanandroid.com/article/query/0/json
     *
     * @param page page
     * @param k    POST search key
     */
    @Headers({"Domain-Name:wanandroid"})
    @POST("article/query/{page}/json")
    @FormUrlEncoded
    Observable<DataResponse<Article>> getSearchArticles(@Path("page") int page, @Field("k") String k);

}
