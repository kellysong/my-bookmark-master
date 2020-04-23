package com.sjl.bookmark.api;

import com.sjl.bookmark.entity.zhuishu.BookChapterDto;
import com.sjl.bookmark.entity.zhuishu.BookChapterDto2;
import com.sjl.bookmark.entity.zhuishu.BookDetailDto;
import com.sjl.bookmark.entity.zhuishu.BookListDto;
import com.sjl.bookmark.entity.zhuishu.BookSummaryDto;
import com.sjl.bookmark.entity.zhuishu.ChapterInfoDto;
import com.sjl.bookmark.entity.zhuishu.ChapterInfoDto2;
import com.sjl.bookmark.entity.zhuishu.CollectBookDto;
import com.sjl.bookmark.entity.zhuishu.HotCommentDto;
import com.sjl.bookmark.entity.zhuishu.HotWordDto;
import com.sjl.bookmark.entity.zhuishu.KeyWordDto;
import com.sjl.bookmark.entity.zhuishu.SearchBookDto;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * 追书神器api
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ZhuiShuShenQiApi.java
 * @time 2018/12/1 14:51
 * @copyright(C) 2018 song
 */
public interface ZhuiShuShenQiApi {


    /**
     * 推荐书籍
     *
     * @return
     */
    @Headers({"Domain-Name:zhuishushenqi"})
    @GET("/book/recommend")
    Single<CollectBookDto> getRecommendBookPackage();


    /**
     * 获取书籍的所有章节列表
     *
     * @param bookId
     * @param view   默认参数为:chapters
     * @return
     */
    @Headers({"Domain-Name:zhuishushenqi"})
    @GET("/mix-atoc/{bookId}")
    @Deprecated
    Single<BookChapterDto> getBookChapterPackage(@Path("bookId") String bookId, @Query("view") String view);
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
    Single<List<BookSummaryDto>> getRealBookChapterIdCompat(@Query("book") String bookId);


    /**
     * 获取书籍的所有章节列表
     *
     * @param bookId
     * @return
     */
    @GET("https://bookapi03.zhuishushenqi.com/btoc/{bookId}?view=chapters&channel=mweb&platform=h5&token=")
    Single<BookChapterDto2> getBookChapterPackageCompat(@Path("bookId") String bookId);

    /**
     * 获取章节的内容
     * 特别说明：@GET使用完整url,无须添加 @Headers,此时baseUrl无效
     * <p>版权问题，无法获取了</p>
     *
     * @param url
     * @return
     */
    @Deprecated
    @GET("http://chapter2.zhuishushenqi.com/chapter/{url}")
    Single<ChapterInfoDto> getChapterInfoPackage(@Path("url") String url);

    /**
     * 获取章节的内容
     * String str="http://vip.zhuishushenqi.com/chapter/5d39bf278a245f6cbbc2ab39?cv=1564065575062";
       String encode = URLEncoder.encode(str, "utf-8");
       String url="https://chapter2.zhuishushenqi.com/chapter/"+encode;//章节细节
     * @param url 来自章节目录的url
     * @return
     */
    @GET("https://chapter2.zhuishushenqi.com/chapter/{url}")
    Single<ChapterInfoDto2> getChapterInfoPackageCompat(@Path("url") String url);


    /*************************书籍详情**********************************/

    /**
     * 书籍热门评论
     *
     * @param book
     * @return
     */
    @Headers({"Domain-Name:zhuishushenqi"})
    @GET("/post/review/best-by-book")
    Single<HotCommentDto> getHotComment(@Query("book") String book);

    /**
     * 书籍更多评论
     * http://www.zhuishushenqi.com/book/review/53e56ee335f79bb626a496c9?page=1
     * 2019年7月开始版权问题已经不能用
     */
    @Deprecated
    @GET("http://www.zhuishushenqi.com/book/review/{bookId}")
    Single<HotCommentDto> getMoreComment(@Path("bookId") String bookId, @Query("page") int page);

    /**
     * 书籍更多评论
     * https://api.zhuishushenqi.com/post/review/best-by-book?book=5d2ea9c9933792125cd1964e&limit=75
     *
     * @param bookId
     * @param limit
     * @return
     */
    @GET("https://api.zhuishushenqi.com/post/review/best-by-book")
    Single<HotCommentDto> getMoreComment2(@Query("book") String bookId, @Query("limit") int limit);


    /**
     * 书籍推荐书单
     * 特别说明：接口返回的书籍实际不存在的
     *
     * @param bookId
     * @param limit
     * @return
     */
    @Headers({"Domain-Name:zhuishushenqi"})
    @GET("/book-list/{bookId}/recommend")
    @Deprecated
    Single<BookListDto> getRecommendBookList(@Path("bookId") String bookId, @Query("limit") String limit);


    /**
     * 获取书单详情(暂时没用)
     *
     * @return
     */
    @Headers({"Domain-Name:zhuishushenqi"})
    @GET("/book-list/{bookListId}")
    Single<BookDetailDto> getBookListDetail(@Path("bookListId") String bookListId);

    /**
     * 获取书籍详情
     *
     * @param bookId
     * @return
     */
    @Headers({"Domain-Name:zhuishushenqi"})
    @GET("/book/{bookId}")
    Single<BookDetailDto.BookDetail> getBookDetail(@Path("bookId") String bookId);


    /************************************搜索书籍******************************************************/
    /**
     * 搜索热词
     *
     * @return
     */
    @Headers({"Domain-Name:zhuishushenqi"})
    @GET("/book/hot-word")
    Single<HotWordDto> getHotWordPackage();

    /**
     * 关键字自动补全
     *
     * @param query
     * @return
     */
    @Headers({"Domain-Name:zhuishushenqi"})
    @GET("/book/auto-complete")
    Single<KeyWordDto> getKeyWordPacakge(@Query("query") String query);

    /**
     * 书籍查询
     *
     * @param query:作者名或者书名
     * @return
     */
    @Headers({"Domain-Name:zhuishushenqi"})
    @GET("/book/fuzzy-search")
    Single<SearchBookDto> getSearchBookPackage(@Query("query") String query);
}
