package com.sjl.bookmark.api;

import com.sjl.bookmark.entity.zhuishu.BookChapterDto;
import com.sjl.bookmark.entity.zhuishu.BookChapterDto2;
import com.sjl.bookmark.entity.zhuishu.BookSummaryDto;
import com.sjl.bookmark.entity.zhuishu.ChapterInfoDto;
import com.sjl.bookmark.entity.zhuishu.ChapterInfoDto2;
import com.sjl.bookmark.entity.zhuishu.table.BookChapter;
import com.sjl.core.net.RetrofitHelper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 追书神器兼容类
 * <p>由于版权问题，之前的追书神器接口不能使用。在兼容后，只能看书籍的部分章节内容</p>
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ZhuiShuCompatRepository.java
 * @time 2019/7/30 14:41
 * @copyright(C) 2019 song
 */
public class ZhuiShuCompatRepository {
    private static ZhuiShuCompatRepository zhuiShuCompatRepository = new ZhuiShuCompatRepository();

    private ZhuiShuCompatRepository() {

    }

    public static ZhuiShuCompatRepository getInstance() {
        return zhuiShuCompatRepository;
    }

    /**
     * 根据书籍id获取书籍章节
     *
     * @param bookId
     * @return
     */
    public Single<List<BookChapter>> getBookChapter(final String bookId) {

        final ZhuiShuShenQiApi apiService = RetrofitHelper.getInstance().getApiService(ZhuiShuShenQiApi.class);
        //下面这个不能用了
 /*       return apiService.getBookChapterPackage(bookId, "chapter")
                .subscribeOn(Schedulers.io()).map(new Function<BookChapterDto, List<BookChapter>>() {
                    @Override
                    public List<BookChapter> apply(BookChapterDto bookChapterDto) throws Exception {
                        if (bookChapterDto.getMixToc() == null) {
                            return new ArrayList<BookChapter>(1);
                        } else {
                            return bookChapterDto.getMixToc().getChapters();
                        }
                    }
                });*/
        /**
         * 操作流程：
         * 1.先根据书籍id获取章节id
         * 2.然后根据章节id获取书籍章节列表
         * 3.再适配章节列表数据为原来的数据（防止改动太大）
         */
        return apiService.getRealBookChapterIdCompat(bookId)
                .flatMap(new Function<List<BookSummaryDto>, Single<BookChapterDto2>>() {
                    @Override
                    public Single<BookChapterDto2> apply(List<BookSummaryDto> bookSummaryDtos) throws Exception {
                        BookSummaryDto bookSummaryDto = bookSummaryDtos.get(0);
                        Single<BookChapterDto2> bookChapterPackageCompat = apiService.getBookChapterPackageCompat(bookSummaryDto.get_id());
                        return bookChapterPackageCompat;

                    }
                }).map(new Function<BookChapterDto2, List<BookChapter>>() {

                    @Override
                    public List<BookChapter> apply(BookChapterDto2 bookChapterDto2) throws Exception {
                        BookChapterDto bookChapterDto = new BookChapterDto();
                        /**
                         * _id : 572072a2e3ee1dcc0accdb9a
                         * book : 57206c3539a913ad65d35c7b
                         * chaptersCount1 : 288
                         * chaptersUpdated : 2017-05-09T10:02:34.705Z
                         */
                        BookChapterDto.MixTocBean mixTocBean = new BookChapterDto.MixTocBean();
                        bookChapterDto.setMixToc(mixTocBean);
                        List<BookChapter> chapters = new ArrayList<BookChapter>();
                        List<BookChapterDto2.ChaptersBean> chapters2 = bookChapterDto2.getChapters();
                        BookChapter bookChapter;
                        for (BookChapterDto2.ChaptersBean chaptersBean : chapters2) {
                            bookChapter = new BookChapter();
                            bookChapter.setBookId(bookChapterDto2.getBook());
                            bookChapter.setId(chaptersBean.getId());
//                            String encode = URLEncoder.encode(chaptersBean.getLink(), "utf-8");
//                            String url="https://chapter2.zhuishushenqi.com/chapter/"+encode;//章节细节
                            bookChapter.setLink(chaptersBean.getLink());
                            bookChapter.setTitle(chaptersBean.getTitle());
                            bookChapter.setUnreadble(chaptersBean.isUnreadble());
                            chapters.add(bookChapter);
                        }
                        mixTocBean.set_id(bookChapterDto2.get_id());
                        mixTocBean.setChaptersUpdated(bookChapterDto2.getUpdated());
                        mixTocBean.setBook(bookChapterDto2.getBook());//书籍id
                        mixTocBean.setChapters(chapters);
                        return chapters;
                    }
                }).subscribeOn(Schedulers.io());

    }

    /**
     * 根据链接获取章节内容
     *
     * @param link
     * @return
     */
    public Single<ChapterInfoDto.ChapterInfo> getBookChapterInfo(String link) {
        ZhuiShuShenQiApi apiService = RetrofitHelper.getInstance().getApiService(ZhuiShuShenQiApi.class);
        /**
         * 下面不能用了
         */
        /*Single<ChapterInfoDto.ChapterInfo> chapterInfoSingle = apiService.getChapterInfoPackage(link).map(new Function<ChapterInfoDto, ChapterInfoDto.ChapterInfo>() {
            @Override
            public ChapterInfoDto.ChapterInfo apply(ChapterInfoDto chapterInfoDto) throws Exception {
                return chapterInfoDto.getChapter();
            }
        });
*/
        return apiService.getChapterInfoPackageCompat(link).map(new Function<ChapterInfoDto2, ChapterInfoDto.ChapterInfo>() {
            @Override
            public ChapterInfoDto.ChapterInfo apply(ChapterInfoDto2 chapterInfoDto2) throws Exception {
                ChapterInfoDto.ChapterInfo chapterInfo = new ChapterInfoDto.ChapterInfo();
                if (chapterInfoDto2.isOk()) {
                    ChapterInfoDto2.ChapterBean chapter = chapterInfoDto2.getChapter();
                    chapterInfo.setTitle(chapter.getTitle());
                    if (!chapter.isIsVip()) {
                        chapterInfo.setBody(chapter.getCpContent());//小说内容
                    } else {
                        chapterInfo.setBody("很遗憾，该章节是VIP才能看!");//小说内容
                    }
                } else {
                    chapterInfo.setTitle("温馨提示");
                    chapterInfo.setBody("加载失败...");
                }
                return chapterInfo;
            }
        });
    }
}
