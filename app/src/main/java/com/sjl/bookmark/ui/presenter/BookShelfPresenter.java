package com.sjl.bookmark.ui.presenter;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import com.sjl.bookmark.R;
import com.sjl.bookmark.api.ZhuiShuCompatRepository;
import com.sjl.bookmark.api.ZhuiShuShenQiApi;
import com.sjl.bookmark.app.MyApplication;
import com.sjl.bookmark.dao.impl.BookRecordDaoImpl;
import com.sjl.bookmark.dao.impl.CollectBookDaoImpl;
import com.sjl.bookmark.dao.impl.DaoFactory;
import com.sjl.bookmark.entity.zhuishu.CollectBookDto;
import com.sjl.bookmark.entity.zhuishu.table.BookChapter;
import com.sjl.bookmark.entity.zhuishu.table.CollectBook;
import com.sjl.bookmark.ui.contract.BookShelfContract;
import com.sjl.core.net.RetrofitHelper;
import com.sjl.core.net.RxSchedulers;
import com.sjl.core.net.RxVoid;
import com.sjl.core.util.datetime.TimeUtils;
import com.sjl.core.util.log.LogUtils;
import com.sjl.core.util.security.MD5Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookShelfPresenter.java
 * @time 2018/11/30 14:41
 * @copyright(C) 2018 song
 */
public class BookShelfPresenter extends BookShelfContract.Presenter {
    private CollectBookDaoImpl collBookBeanService;
    private BookRecordDaoImpl bookRecordService;

    public BookShelfPresenter() {
        collBookBeanService = new CollectBookDaoImpl(MyApplication.getContext());
        bookRecordService = new BookRecordDaoImpl(MyApplication.getContext());
    }

    @Override
    public void refreshCollectBooks() {

        ZhuiShuShenQiApi apiService = RetrofitHelper.getInstance().getApiService(ZhuiShuShenQiApi.class);
        Single<CollectBookDto> recommendBookPackage = apiService.getRecommendBookPackage();
        recommendBookPackage.map(new Function<CollectBookDto, List<CollectBook>>() {
            @Override
            public List<CollectBook> apply(CollectBookDto collectBookDto) throws Exception {
                return collectBookDto.getBooks();
            }
        }).doOnSuccess(new Consumer<List<CollectBook>>() {

            @Override
            public void accept(List<CollectBook> collBookBeans) throws Exception {
                //更新书籍章节目录
                updateCategory(collBookBeans);
                //保存推荐的图书到数据库
                collBookBeanService.saveCollectBookBeans(collBookBeans);
            }
        }).compose(RxSchedulers.<List<CollectBook>>applySingle()).as(this.<List<CollectBook>>bindLifecycle())

                .subscribe(new Consumer<List<CollectBook>>() {
                    @Override
                    public void accept(List<CollectBook> CollBookBean) throws Exception {
                        mView.showRecommendBook(CollBookBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("获取推荐书籍异常：" + throwable.getMessage(), throwable);
                        mView.showErrorMsg(mContext.getString(R.string.request_failed));
                    }
                });
    }

    @Override
    public void getRecommendBook() {
        List<CollectBook> all = collBookBeanService.findAll();
        if (all != null && all.size() > 0) {
//            LogUtils.i("从本地数据库获取书籍:" + all.size());
//            for (CollectBook bean : all) {
//                LogUtils.i(bean.get_id() + "," + bean.getTitle() + "," + bean.getBookSortId());
//            }
            mView.showRecommendBook(all);//没有联网从本地数据库取
        } else {
            LogUtils.i("联网获取书籍");
            refreshCollectBooks();
        }

    }

    @Override
    public void deleteBook(final CollectBook collectBook) {
        if (collectBook.isLocal()) {//本地小说删除逻辑
            View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.dialog_delete, null);
            final CheckBox cb = (CheckBox) view.findViewById(R.id.delete_cb_select);
            new AlertDialog.Builder(mContext)
                    .setTitle(mContext.getString(R.string.delete_file_hint))
                    .setView(view)
                    .setPositiveButton(mContext.getResources().getString(R.string.nb_common_sure), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            boolean isSelected = cb.isChecked();
                            if (isSelected) {
                                ProgressDialog progressDialog = new ProgressDialog(mContext);
                                progressDialog.setMessage(mContext.getString(R.string.file_deleting));
                                progressDialog.show();
                                //删除
                                File file = new File(collectBook.getCover());
                                if (file.exists()) file.delete();
                                DaoFactory.getBookChapterDao().deleteBookChapter(collectBook.get_id());
                                collBookBeanService.deleteCollectBook(collectBook);
                                bookRecordService.deleteBookRecord(collectBook.get_id());
                                mView.refreshBook();
                                progressDialog.dismiss();

                            } else {
                                DaoFactory.getBookChapterDao().deleteBookChapter(collectBook.get_id());
                                collBookBeanService.deleteCollectBook(collectBook);
                                bookRecordService.deleteBookRecord(collectBook.get_id());
                                //从Adapter中删除
                                mView.refreshBook();
                            }
                        }
                    })
                    .setNegativeButton(mContext.getResources().getString(R.string.nb_common_cancel), null)
                    .show();
        } else {//在线图书删除逻辑
            final ProgressDialog progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage(mContext.getString(R.string.file_deleting));
            progressDialog.show();
            collBookBeanService.deleteCollBookInRx(collectBook)
                    .compose(RxSchedulers.<RxVoid>applySingle())
                    .subscribe(new Consumer<RxVoid>() {
                        @Override
                        public void accept(RxVoid rxVoid) throws Exception {
                            mView.refreshBook();
                            progressDialog.dismiss();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            LogUtils.e("deleteCollBookInRx", throwable);
                        }
                    });


        }
    }

    @Override
    public void deleteAllBook() {
        List<CollectBook> all = collBookBeanService.findAll();
        if (all != null && all.size() > 0) {
            LogUtils.i("本次删除书籍数量:" + all.size());
            collBookBeanService.deleteAllCollectBookInRx(all)
                    .compose(RxSchedulers.<RxVoid>applySingle())
                    .subscribe(new Consumer<RxVoid>() {
                        @Override
                        public void accept(RxVoid rxVoid) throws Exception {
                            mView.showRecommendBook(new ArrayList<CollectBook>());
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            LogUtils.e("deleteAllCollectBookInRx", throwable);
                        }
                    });

        } else {
            mView.showRecommendBook(new ArrayList<CollectBook>());
        }
    }


    /**
     * 更新每个CollectBook的目录
     *
     * @param collBookBeans
     */
    private void updateCategory(List<CollectBook> collBookBeans) {
        List<Single<List<BookChapter>>> observables = new ArrayList<>(collBookBeans.size());
        ZhuiShuShenQiApi apiService = RetrofitHelper.getInstance().getApiService(ZhuiShuShenQiApi.class);
        for (CollectBook bean : collBookBeans) {
            observables.add(getBookChapters(apiService, bean.get_id()));
        }
        final Iterator<CollectBook> it = collBookBeans.iterator();
        //执行在上一个方法中的子线程中
        Single.concat(observables).subscribe(new Consumer<List<BookChapter>>() {
            @Override
            public void accept(List<BookChapter> bookChapters) throws Exception {//concat会根据观察者数量多次回调accept
                for (BookChapter bean : bookChapters) {
                    bean.setId(MD5Utils.strToMd5By16(bean.getLink()));//path 的 md5 值作为本地书籍的 id
                }
                CollectBook bean = it.next();
                bean.setLastRead(TimeUtils.formatDateToStr(System.currentTimeMillis(), TimeUtils.DATE_FORMAT_7));
                bean.setBookChapters(bookChapters);
            }
        });
    }

    /**
     * 获取书籍的章节
     *
     * @param apiService
     * @param bookId
     * @return
     */
    public Single<List<BookChapter>> getBookChapters(ZhuiShuShenQiApi apiService, String bookId) {
        Single<List<BookChapter>> bookChapter = ZhuiShuCompatRepository.getInstance().getBookChapter(bookId);
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
                });*/
        return bookChapter;
    }
}
