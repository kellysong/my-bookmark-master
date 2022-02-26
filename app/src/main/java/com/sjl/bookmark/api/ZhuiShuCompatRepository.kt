package com.sjl.bookmark.api

import com.sjl.bookmark.entity.zhuishu.BookChapterDto
import com.sjl.bookmark.entity.zhuishu.BookChapterDto.MixTocBean
import com.sjl.bookmark.entity.zhuishu.ChapterInfoDto.ChapterInfo
import com.sjl.bookmark.entity.zhuishu.table.BookChapter
import com.sjl.core.net.RetrofitHelper
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * 追书神器兼容类
 *
 * 由于版权问题，之前的追书神器接口不能使用。在兼容后，只能看书籍的部分章节内容
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ZhuiShuCompatRepository.java
 * @time 2019/7/30 14:41
 * @copyright(C) 2019 song
 */
object ZhuiShuCompatRepository {
    /**
     * 根据书籍id获取书籍章节
     *
     * @param bookId
     * @return
     */
    fun getBookChapter(bookId: String?): Single<List<BookChapter>> {
        val apiService = RetrofitHelper.getInstance().getApiService(
            ZhuiShuShenQiApi::class.java
        )
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
            .flatMap { bookSummaryDtos ->
                val bookSummaryDto = bookSummaryDtos[0]
                apiService.getBookChapterPackageCompat(bookSummaryDto._id)
            }
            .map<List<BookChapter>> { bookChapterDto2 ->
                val bookChapterDto = BookChapterDto()
                /**
                 * _id : 572072a2e3ee1dcc0accdb9a
                 * book : 57206c3539a913ad65d35c7b
                 * chaptersCount1 : 288
                 * chaptersUpdated : 2017-05-09T10:02:34.705Z
                 */
                /**
                 * _id : 572072a2e3ee1dcc0accdb9a
                 * book : 57206c3539a913ad65d35c7b
                 * chaptersCount1 : 288
                 * chaptersUpdated : 2017-05-09T10:02:34.705Z
                 */
                val mixTocBean = MixTocBean()
                bookChapterDto.mixToc = mixTocBean
                val chapters: MutableList<BookChapter> = ArrayList()
                val chapters2 = bookChapterDto2.chapters
                var bookChapter: BookChapter
                for (chaptersBean in chapters2) {
                    bookChapter = BookChapter()
                    bookChapter.bookId = bookChapterDto2.book
                    bookChapter.id = chaptersBean.id
                    //                            String encode = URLEncoder.encode(chaptersBean.getLink(), "utf-8");
//                            String url="https://chapter2.zhuishushenqi.com/chapter/"+encode;//章节细节
                    bookChapter.link = chaptersBean.link
                    bookChapter.title = chaptersBean.title
                    bookChapter.unreadble = chaptersBean.isUnreadble
                    chapters.add(bookChapter)
                }
                mixTocBean._id = bookChapterDto2._id
                mixTocBean.chaptersUpdated = bookChapterDto2.updated
                mixTocBean.book = bookChapterDto2.book //书籍id
                mixTocBean.chapters = chapters
                chapters
            }.subscribeOn(Schedulers.io())
    }

    /**
     * 根据链接获取章节内容
     *
     * @param link
     * @return
     */
    fun getBookChapterInfo(link: String?): Single<ChapterInfo> {
        val apiService = RetrofitHelper.getInstance().getApiService(
            ZhuiShuShenQiApi::class.java
        )
        /**
         * 下面不能用了
         */
        /*Single<ChapterInfoDto.ChapterInfo> chapterInfoSingle = apiService.getChapterInfoPackage(link).map(new Function<ChapterInfoDto, ChapterInfoDto.ChapterInfo>() {
            @Override
            public ChapterInfoDto.ChapterInfo apply(ChapterInfoDto chapterInfoDto) throws Exception {
                return chapterInfoDto.getChapter();
            }
        });
*/return apiService.getChapterInfoPackageCompat(link).map { chapterInfoDto2 ->
            val chapterInfo = ChapterInfo()
            if (chapterInfoDto2.isOk) {
                val chapter = chapterInfoDto2.chapter
                chapterInfo.title = chapter.title
                if (!chapter.isIsVip) {
                    chapterInfo.body = chapter.cpContent //小说内容
                } else {
                    chapterInfo.body = "很遗憾，该章节是VIP才能看!" //小说内容
                }
            } else {
                chapterInfo.title = "温馨提示"
                chapterInfo.body = "加载失败..."
            }
            chapterInfo
        }
    }

}