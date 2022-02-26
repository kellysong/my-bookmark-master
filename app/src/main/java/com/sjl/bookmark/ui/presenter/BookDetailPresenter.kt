package com.sjl.bookmark.ui.presenter

import android.text.TextUtils
import com.sjl.bookmark.api.ZhuiShuCompatRepository
import com.sjl.bookmark.api.ZhuiShuShenQiApi
import com.sjl.bookmark.app.AppConstant
import com.sjl.bookmark.dao.impl.DaoFactory
import com.sjl.bookmark.dao.util.ZhuiShuParse
import com.sjl.bookmark.entity.zhuishu.BookDetailDto.BookDetail
import com.sjl.bookmark.entity.zhuishu.HotCommentDto
import com.sjl.bookmark.entity.zhuishu.HotCommentDto.HotComment
import com.sjl.bookmark.entity.zhuishu.table.BookChapter
import com.sjl.bookmark.entity.zhuishu.table.CollectBook
import com.sjl.bookmark.entity.zhuishu.table.RecommendBook
import com.sjl.bookmark.ui.contract.BookDetailContract
import com.sjl.core.net.RetrofitHelper
import com.sjl.core.net.RxSchedulers
import com.sjl.core.util.datetime.TimeUtils
import com.sjl.core.util.log.LogUtils
import com.sjl.core.util.security.MD5Utils
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookDetailPresenter.java
 * @time 2018/12/7 10:10
 * @copyright(C) 2018 song
 */
class BookDetailPresenter : BookDetailContract.Presenter() {
    private var bookId: String? = null
    override fun refreshBookDetail(bookId: String) {
        this.bookId = bookId
        refreshBook()
        refreshComment()
        refreshRecommend()
    }

    override fun addToBookShelf(collectBook: CollectBook) {
        ZhuiShuCompatRepository.getBookChapter(bookId)
            .doOnSubscribe(Consumer<Disposable?>
            //默认情况下， doOnSubscribe() 执行在 subscribe() 发生的线程；而如果在 doOnSubscribe() 之后有 subscribeOn() 的话，
            // 它将执行在离它最近的 subscribeOn() 所指定的线程。
            {
                mView.waitToBookShelf() //等待加载
            })
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .`as`(bindLifecycle<List<BookChapter>>())
            .subscribe(Consumer<List<BookChapter>> { bookChapters -> //设置 id
                for (bean in bookChapters) {
                    bean.id = MD5Utils.strToMd5By16(bean.link)
                }

                //设置目录
                collectBook.bookChapters = bookChapters
                //存储收藏
                DaoFactory.getCollectBookDao()
                    .saveCollBookWithAsync(collectBook)
                mView.succeedToBookShelf()
            },
                Consumer<Throwable?> { throwable ->
                    mView.errorToBookShelf()
                    LogUtils.e("addToBookShelf异常", throwable)
                }
            )
    }

    /**
     * 初始化书籍详情
     */
    private fun refreshBook() {
        val apiService = RetrofitHelper.getInstance().getApiService(
            ZhuiShuShenQiApi::class.java
        )
        apiService
            .getBookDetail(bookId)
            .compose(RxSchedulers.applySingle<BookDetail>())
            .`as`(bindLifecycle())
            .subscribe(object : SingleObserver<BookDetail?> {
                override fun onSubscribe(d: Disposable) {}
                override fun onSuccess(value: BookDetail) {
                    mView.finishRefresh(value)
                    mView.complete()
                }

                override fun onError(e: Throwable) {
                    LogUtils.e("初始化书籍详情异常", e)
                    mView.showError()
                }
            })
    }

    /**
     * 初始化评论列表
     */
    private fun refreshComment() {
        val apiService = RetrofitHelper.getInstance().getApiService(
            ZhuiShuShenQiApi::class.java
        )
        apiService.getHotComment(bookId)
            .map(Function<HotCommentDto, List<HotComment>> { hotCommentDto ->
                if (hotCommentDto.reviews == null) {
                    ArrayList()
                } else hotCommentDto.reviews
            }).compose(RxSchedulers.applySingle())
            .`as`(bindLifecycle())
            .subscribe({ hotComments -> mView.finishHotComment(hotComments) }
            ) { throwable -> LogUtils.e("初始化评论列表异常", throwable) }
    }

    /**
     * 初始化推荐书籍列表
     */
    private fun refreshRecommend() {
        val recommendBooks = DaoFactory.getRecommendBookDao().queryRecommendBookByBookId(bookId)
        if (recommendBooks != null && recommendBooks.size > 0) {
            val recommendBook = recommendBooks[0]
            if (!TextUtils.isEmpty(recommendBook.updateTime)) {
                val sd = SimpleDateFormat(TimeUtils.DATE_FORMAT_1)
                try {
                    val lastTime = sd.parse(recommendBook.updateTime)
                    val dateDiff = TimeUtils.dateDiff(lastTime, Date())
                    if (dateDiff >= AppConstant.RECOMMEND_BOOK_VALID_TIME) { //超出三天未使用应用，重新登录
                        LogUtils.i("推荐书籍缓存过期")
                        DaoFactory.getRecommendBookDao().deleteRecommendBookByBookId(bookId)
                        refreshRecommendBook()
                    } else { //使用缓存数据
                        LogUtils.i("使用缓存推荐书籍数据:" + recommendBooks.size)
                        mView.finishRecommendBookList(recommendBooks)
                    }
                } catch (e: ParseException) {
                    mView.showError()
                }
            } else {
                refreshRecommendBook()
            }
        } else {
            refreshRecommendBook()
        }
    }

    /**
     * 爬虫解析网页获取：喜欢这本书的也喜欢
     */
    private fun refreshRecommendBook() {
        LogUtils.i("refreshRecommendBook爬虫获取推荐书籍")
        //http://www.zhuishushenqi.com/book/53e56ee335f79bb626a496c9
        //不能用了
//        final String url = "http://www.zhuishushenqi.com/book/" + bookId;


        //https://m.zhuishushenqi.com/book/59439b346915617d5fa836c7?exposure=59439b346915617d5fa836c7
        val url = "http://m.zhuishushenqi.com/book/$bookId?exposure=$bookId"
        Observable.create<List<RecommendBook>>(ObservableOnSubscribe<List<RecommendBook>> { observableEmitter -> //很容易超时
            val bookLists = ZhuiShuParse.parseRecommendBook2(bookId, url)
            LogUtils.i("爬虫获取推荐书籍数量:" + bookLists.size)
            DaoFactory.getRecommendBookDao().saveRecommendBookByBookId(bookLists)
            observableEmitter.onNext(bookLists)
        }).compose(RxSchedulers.applySchedulers())
            .`as`(bindLifecycle())
            .subscribe({ bookLists -> mView.finishRecommendBookList(bookLists) }) { throwable ->
                LogUtils.e(
                    "初始化推荐书籍列表异常",
                    throwable
                )
            }
    }
}