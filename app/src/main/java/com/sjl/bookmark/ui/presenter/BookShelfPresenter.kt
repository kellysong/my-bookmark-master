package com.sjl.bookmark.ui.presenter

import android.app.ProgressDialog
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import androidx.appcompat.app.AlertDialog
import com.sjl.bookmark.R
import com.sjl.bookmark.api.ZhuiShuCompatRepository
import com.sjl.bookmark.api.ZhuiShuShenQiApi
import com.sjl.bookmark.app.MyApplication
import com.sjl.bookmark.dao.impl.BookRecordDaoImpl
import com.sjl.bookmark.dao.impl.CollectBookDaoImpl
import com.sjl.bookmark.dao.impl.DaoFactory
import com.sjl.bookmark.entity.zhuishu.CollectBookDto
import com.sjl.bookmark.entity.zhuishu.table.BookChapter
import com.sjl.bookmark.entity.zhuishu.table.CollectBook
import com.sjl.bookmark.ui.contract.BookShelfContract
import com.sjl.core.net.RetrofitHelper
import com.sjl.core.net.RxSchedulers
import com.sjl.core.net.RxVoid
import com.sjl.core.util.datetime.TimeUtils
import com.sjl.core.util.log.LogUtils
import com.sjl.core.util.security.MD5Utils
import io.reactivex.Single
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import java.io.File
import java.util.*

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookShelfPresenter.java
 * @time 2018/11/30 14:41
 * @copyright(C) 2018 song
 */
class BookShelfPresenter : BookShelfContract.Presenter() {
    private val collBookBeanService: CollectBookDaoImpl
    private val bookRecordService: BookRecordDaoImpl
    override fun refreshCollectBooks() {
        val apiService = RetrofitHelper.getInstance().getApiService(
            ZhuiShuShenQiApi::class.java
        )
        val recommendBookPackage = apiService.recommendBookPackage
        recommendBookPackage.map(Function<CollectBookDto, List<CollectBook>> { collectBookDto -> collectBookDto.books })
            .doOnSuccess(
                Consumer { collBookBeans -> //更新书籍章节目录
                    updateCategory(collBookBeans)
                    //保存推荐的图书到数据库
                    collBookBeanService.saveCollectBookBeans(collBookBeans)
                }).compose(RxSchedulers.applySingle()).`as`(bindLifecycle())
            .subscribe(object : Consumer<List<CollectBook>> {
                @Throws(Exception::class)
                override fun accept(CollBookBean: List<CollectBook>) {
                    mView.showRecommendBook(CollBookBean)
                }
            }, object : Consumer<Throwable> {
                @Throws(Exception::class)
                override fun accept(throwable: Throwable) {
                    LogUtils.e("获取推荐书籍异常：" + throwable.message, throwable)
                    mView.showErrorMsg(mContext.getString(R.string.request_failed))
                }
            })
    }

    override fun getRecommendBook() {
        val all = collBookBeanService.findAll()
        if (all != null && all.size > 0) {
//            LogUtils.i("从本地数据库获取书籍:" + all.size());
//            for (CollectBook bean : all) {
//                LogUtils.i(bean.get_id() + "," + bean.getTitle() + "," + bean.getBookSortId());
//            }
            mView.showRecommendBook(all) //没有联网从本地数据库取
        } else {
            LogUtils.i("联网获取书籍")
            refreshCollectBooks()
        }
    }

    override fun deleteBook(collectBook: CollectBook) {
        if (collectBook.isLocal()) { //本地小说删除逻辑
            val view = LayoutInflater.from(mContext)
                .inflate(R.layout.dialog_delete, null)
            val cb = view.findViewById<View>(R.id.delete_cb_select) as CheckBox
            AlertDialog.Builder(mContext)
                .setTitle(mContext.getString(R.string.delete_file_hint))
                .setView(view)
                .setPositiveButton(
                    mContext.resources.getString(R.string.nb_common_sure),
                    object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface, which: Int) {
                            val isSelected = cb.isChecked
                            if (isSelected) {
                                val progressDialog = ProgressDialog(mContext)
                                progressDialog.setMessage(mContext.getString(R.string.file_deleting))
                                progressDialog.show()
                                //删除
                                val file = File(collectBook.cover)
                                if (file.exists()) file.delete()
                                DaoFactory.getBookChapterDao().deleteBookChapter(collectBook._id)
                                collBookBeanService.deleteCollectBook(collectBook)
                                bookRecordService.deleteBookRecord(collectBook._id)
                                mView.refreshBook()
                                progressDialog.dismiss()
                            } else {
                                DaoFactory.getBookChapterDao().deleteBookChapter(collectBook._id)
                                collBookBeanService.deleteCollectBook(collectBook)
                                bookRecordService.deleteBookRecord(collectBook._id)
                                //从Adapter中删除
                                mView.refreshBook()
                            }
                        }
                    })
                .setNegativeButton(mContext.resources.getString(R.string.nb_common_cancel), null)
                .show()
        } else { //在线图书删除逻辑
            val progressDialog = ProgressDialog(mContext)
            progressDialog.setMessage(mContext.getString(R.string.file_deleting))
            progressDialog.show()
            collBookBeanService.deleteCollBookInRx(collectBook)
                .compose(RxSchedulers.applySingle())
                .subscribe(object : Consumer<RxVoid?> {
                    @Throws(Exception::class)
                    override fun accept(rxVoid: RxVoid?) {
                        mView.refreshBook()
                        progressDialog.dismiss()
                    }
                }, object : Consumer<Throwable?> {
                    @Throws(Exception::class)
                    override fun accept(throwable: Throwable?) {
                        LogUtils.e("deleteCollBookInRx", throwable)
                    }
                })
        }
    }

    override fun deleteAllBook() {
        val all = collBookBeanService.findAll()
        if (all != null && all.size > 0) {
            LogUtils.i("本次删除书籍数量:" + all.size)
            collBookBeanService.deleteAllCollectBookInRx(all)
                .compose(RxSchedulers.applySingle())
                .subscribe(object : Consumer<RxVoid?> {
                    @Throws(Exception::class)
                    override fun accept(rxVoid: RxVoid?) {
                        mView.showRecommendBook(ArrayList())
                    }
                }, object : Consumer<Throwable?> {
                    @Throws(Exception::class)
                    override fun accept(throwable: Throwable?) {
                        LogUtils.e("deleteAllCollectBookInRx", throwable)
                    }
                })
        } else {
            mView.showRecommendBook(ArrayList())
        }
    }

    /**
     * 更新每个CollectBook的目录
     *
     * @param collBookBeans
     */
    private fun updateCategory(collBookBeans: List<CollectBook>) {
        val observables: MutableList<Single<List<BookChapter>>> = ArrayList(collBookBeans.size)
        val apiService = RetrofitHelper.getInstance().getApiService(
            ZhuiShuShenQiApi::class.java
        )
        for (bean: CollectBook in collBookBeans) {
            observables.add(getBookChapters(apiService, bean._id))
        }
        val it = collBookBeans.iterator()
        //执行在上一个方法中的子线程中
        Single.concat(observables).subscribe(object : Consumer<List<BookChapter>> {
            @Throws(Exception::class)
            override fun accept(bookChapters: List<BookChapter>) { //concat会根据观察者数量多次回调accept
                for (bean: BookChapter in bookChapters) {
                    bean.id = MD5Utils.strToMd5By16(bean.link) //path 的 md5 值作为本地书籍的 id
                }
                val bean = it.next()
                bean.lastRead =
                    TimeUtils.formatDateToStr(System.currentTimeMillis(), TimeUtils.DATE_FORMAT_7)
                bean.bookChapters = bookChapters
            }
        })
    }

    /**
     * 获取书籍的章节
     *
     * @param apiService
     * @param bookId
     * @return
     */
    fun getBookChapters(
        apiService: ZhuiShuShenQiApi?,
        bookId: String?
    ): Single<List<BookChapter>> {
        /*return apiService.getBookChapterPackage(bookId, "chapter")
                .map(new Function<BookChapterDto, List<BookChapter>>() {
                    @Override
                    public List<BookChapter> apply(BookChapterDto bookChapterDto) throws Exception {
                        if (bookChapterDto.getMixToc() == null) {
                            return new ArrayList<BookChapter>(1);
                        } else {
                            return bookChapterDto.getMixToc().getChapters();
                        }
                    }
                });*/return ZhuiShuCompatRepository.getBookChapter(bookId)
    }

    init {
        collBookBeanService = CollectBookDaoImpl(MyApplication.getContext())
        bookRecordService = BookRecordDaoImpl(MyApplication.getContext())
    }
}