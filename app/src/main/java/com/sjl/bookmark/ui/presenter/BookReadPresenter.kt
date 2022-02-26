package com.sjl.bookmark.ui.presenter

import com.sjl.bookmark.api.ZhuiShuCompatRepository
import com.sjl.bookmark.api.ZhuiShuShenQiApi
import com.sjl.bookmark.entity.zhuishu.ChapterInfoDto.ChapterInfo
import com.sjl.bookmark.entity.zhuishu.table.BookChapter
import com.sjl.bookmark.ui.contract.BookReadContract
import com.sjl.bookmark.widget.reader.BookManager
import com.sjl.bookmark.widget.reader.bean.TxtChapter
import com.sjl.core.net.RetrofitHelper
import com.sjl.core.net.RxSchedulers
import com.sjl.core.util.log.LogUtils
import com.sjl.core.util.security.MD5Utils
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import java.util.*

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookReadPresenter.java
 * @time 2018/12/4 15:54
 * @copyright(C) 2018 song
 */
class BookReadPresenter : BookReadContract.Presenter() {
    private var mChapterSub: Subscription? = null
    override fun loadCategory(bookId: String) {
        ZhuiShuCompatRepository.getBookChapter(bookId)
            .doOnSuccess(Consumer<List<BookChapter>> { bookChapterBeen -> //进行设定BookChapter所属的书的id。
                for (bookChapter in bookChapterBeen) {
                    bookChapter.id = MD5Utils.strToMd5By16(bookChapter.link)
                    bookChapter.bookId = bookId
                }
            }).compose(RxSchedulers.applySingle<List<BookChapter>>())
            .`as`(bindLifecycle<List<BookChapter>>())
            .subscribe(Consumer<List<BookChapter>> { bookChapters ->
                mView.showCategory(
                    bookChapters
                )
            }, Consumer<Throwable?> { throwable -> LogUtils.e(throwable) }
            )
    }

    override fun loadChapter(bookId: String, bookChapterList: List<TxtChapter>) {
        val size = bookChapterList.size

        //取消上次的任务，防止多次加载
        if (mChapterSub != null) {
            mChapterSub!!.cancel()
        }
        val chapterInfos: MutableList<Single<ChapterInfo>> = ArrayList(bookChapterList.size)
        val titles = ArrayDeque<String>(bookChapterList.size)
        val apiService = RetrofitHelper.getInstance().getApiService(
            ZhuiShuShenQiApi::class.java
        )

        // 将要下载章节，转换成网络请求。
        for (i in 0 until size) {
            val bookChapter = bookChapterList[i]
            LogUtils.i("下载章节:$bookChapter")
            // 网络中获取数据
            //http://book.xbiquge.com/getBooks.aspx?method=content&bookId=2347074&chapterFile=U_2347074_201805221833153185_3011_3.txt
            val chapterInfoSingle: Single<ChapterInfo> =
                ZhuiShuCompatRepository.getBookChapterInfo(bookChapter.link)
            /*   apiService.getChapterInfoPackage(bookChapter.link).map(new Function<ChapterInfoDto, ChapterInfoDto.ChapterInfo>() {
                @Override
                public ChapterInfoDto.ChapterInfo apply(ChapterInfoDto chapterInfoDto) throws Exception {
                    return chapterInfoDto.getChapter();
                }
            });*/chapterInfos.add(chapterInfoSingle)
            titles.add(bookChapter.title)
        }
        //concat Observable的顺序一个个执行
        Single.concat(chapterInfos) //组合起来缓存章节
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                object : Subscriber<ChapterInfo> {
                    var title = titles.poll() //从队首获取元素
                    override fun onSubscribe(s: Subscription) {
                        s.request(Int.MAX_VALUE.toLong())
                        mChapterSub = s
                    }

                    override fun onNext(chapterInfoBean: ChapterInfo) {
                        //缓存章节
                        BookManager.getInstance()
                            .saveChapterInfo(bookId, title, chapterInfoBean.body)
                        mView.finishChapter()
                        LogUtils.i("缓存章节：" + title + ",titles size:" + titles.size)
                        title = titles.poll() //取出首元素
                    }

                    override fun onError(t: Throwable) {
                        //只有第一个加载失败才会调用errorChapter
                        if (bookChapterList[0].title == title) {
                            mView.errorChapter()
                        }
                        LogUtils.e(t)
                    }

                    override fun onComplete() {}
                }
            )
    }
}