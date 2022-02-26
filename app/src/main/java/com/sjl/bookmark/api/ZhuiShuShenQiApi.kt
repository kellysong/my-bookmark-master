package com.sjl.bookmark.api

import com.sjl.bookmark.entity.zhuishu.*
import com.sjl.bookmark.entity.zhuishu.BookDetailDto.BookDetail
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 追书神器api
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ZhuiShuShenQiApi.java
 * @time 2018/12/1 14:51
 * @copyright(C) 2018 song
 */
interface ZhuiShuShenQiApi {
    /**
     * 推荐书籍
     *
     * @return
     */
    @get:GET("/book/recommend")
    @get:Headers("Domain-Name:zhuishushenqi")
    val recommendBookPackage: Single<CollectBookDto>

    /**
     * 获取书籍的所有章节列表
     *
     * @param bookId
     * @param view   默认参数为:chapters
     * @return
     */
    @Headers("Domain-Name:zhuishushenqi")
    @GET("/mix-atoc/{bookId}")
    @Deprecated("")
    fun getBookChapterPackage(
        @Path("bookId") bookId: String?,
        @Query("view") view: String?
    ): Single<BookChapterDto>
    //https://bookapi03.zhuishushenqi.com/btoc/5c6fd8cff7da7c543dc9998d?view=chapters&channel=mweb&platform=h5&token=
    /**
     * 获取书籍章节目录Id
     * https://bookapi03.zhuishushenqi.com/btoc/5c6fd8cff7da7c543dc9998d?view=chapters&channel=mweb&platform=h5&token=
     *
     * //5c6fd8cff7da7c543dc9998b
     * https://bookapi03.zhuishushenqi.com/btoc/5c6fd8cff7da7c543dc9998b?view=chapters&channel=mweb&platform=h5&token=
     * @param bookId
     * @return
     */
    @GET("https://bookapi03.zhuishushenqi.com/btoc/?view=summary&platform=h5&token=")
    fun getRealBookChapterIdCompat(@Query("book") bookId: String?): Single<List<BookSummaryDto>>

    /**
     * 获取书籍的所有章节列表
     *
     * @param bookId
     * @return
     */
    @GET("https://bookapi03.zhuishushenqi.com/btoc/{bookId}?view=chapters&channel=mweb&platform=h5&token=")
    fun getBookChapterPackageCompat(@Path("bookId") bookId: String?): Single<BookChapterDto2>

    /**
     * 获取章节的内容
     * 特别说明：@GET使用完整url,无须添加 @Headers,此时baseUrl无效
     *
     * 版权问题，无法获取了
     *
     * @param url
     * @return
     */
    @Deprecated("")
    @GET("http://chapter2.zhuishushenqi.com/chapter/{url}")
    fun getChapterInfoPackage(@Path("url") url: String?): Single<ChapterInfoDto>

    /**
     * 获取章节的内容
     * String str="http://vip.zhuishushenqi.com/chapter/5d39bf278a245f6cbbc2ab39?cv=1564065575062";
     * String encode = URLEncoder.encode(str, "utf-8");
     * String url="https://chapter2.zhuishushenqi.com/chapter/"+encode;//章节细节
     * @param url 来自章节目录的url
     * @return
     */
    @GET("https://chapter2.zhuishushenqi.com/chapter/{url}")
    fun getChapterInfoPackageCompat(@Path("url") url: String?): Single<ChapterInfoDto2>
    /*************************书籍详情 */
    /**
     * 书籍热门评论
     *
     * @param book
     * @return
     */
    @Headers("Domain-Name:zhuishushenqi")
    @GET("/post/review/best-by-book")
    fun getHotComment(@Query("book") book: String?): Single<HotCommentDto>

    /**
     * 书籍更多评论
     * http://www.zhuishushenqi.com/book/review/53e56ee335f79bb626a496c9?page=1
     * 2019年7月开始版权问题已经不能用
     */
    @Deprecated("")
    @GET("http://www.zhuishushenqi.com/book/review/{bookId}")
    fun getMoreComment(
        @Path("bookId") bookId: String?,
        @Query("page") page: Int
    ): Single<HotCommentDto>

    /**
     * 书籍更多评论
     * https://api.zhuishushenqi.com/post/review/best-by-book?book=5d2ea9c9933792125cd1964e&limit=75
     *
     * @param bookId
     * @param limit
     * @return
     */
    @GET("https://api.zhuishushenqi.com/post/review/best-by-book")
    fun getMoreComment2(
        @Query("book") bookId: String?,
        @Query("limit") limit: Int
    ): Single<HotCommentDto>

    /**
     * 书籍推荐书单
     * 特别说明：接口返回的书籍实际不存在的
     *
     * @param bookId
     * @param limit
     * @return
     */
    @Headers("Domain-Name:zhuishushenqi")
    @GET("/book-list/{bookId}/recommend")
    @Deprecated("")
    fun getRecommendBookList(
        @Path("bookId") bookId: String?,
        @Query("limit") limit: String?
    ): Single<BookListDto>

    /**
     * 获取书单详情(暂时没用)
     *
     * @return
     */
    @Headers("Domain-Name:zhuishushenqi")
    @GET("/book-list/{bookListId}")
    fun getBookListDetail(@Path("bookListId") bookListId: String?): Single<BookDetailDto>

    /**
     * 获取书籍详情
     *
     * @param bookId
     * @return
     */
    @Headers("Domain-Name:zhuishushenqi")
    @GET("/book/{bookId}")
    fun getBookDetail(@Path("bookId") bookId: String?): Single<BookDetail>
    /************************************搜索书籍 */
    /**
     * 搜索热词
     *
     * @return
     */
    @get:GET("/book/hot-word")
    @get:Headers("Domain-Name:zhuishushenqi")
    val hotWordPackage: Single<HotWordDto>

    /**
     * 关键字自动补全
     *
     * @param query
     * @return
     */
    @Headers("Domain-Name:zhuishushenqi")
    @GET("/book/auto-complete")
    fun getKeyWordPacakge(@Query("query") query: String?): Single<KeyWordDto>

    /**
     * 书籍查询
     *
     * @param query:作者名或者书名
     * @return
     */
    @Headers("Domain-Name:zhuishushenqi")
    @GET("/book/fuzzy-search")
    fun getSearchBookPackage(@Query("query") query: String?): Single<SearchBookDto>
}