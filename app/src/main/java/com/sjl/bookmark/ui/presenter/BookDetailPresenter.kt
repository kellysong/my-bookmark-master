package com.sjl.bookmark.ui.presenter;

import android.text.TextUtils;

import com.sjl.bookmark.api.ZhuiShuCompatRepository;
import com.sjl.bookmark.api.ZhuiShuShenQiApi;
import com.sjl.bookmark.app.AppConstant;
import com.sjl.bookmark.dao.impl.DaoFactory;
import com.sjl.bookmark.dao.util.ZhuiShuParse;
import com.sjl.bookmark.entity.zhuishu.BookDetailDto;
import com.sjl.bookmark.entity.zhuishu.HotCommentDto;
import com.sjl.bookmark.entity.zhuishu.table.BookChapter;
import com.sjl.bookmark.entity.zhuishu.table.CollectBook;
import com.sjl.bookmark.entity.zhuishu.table.RecommendBook;
import com.sjl.bookmark.ui.contract.BookDetailContract;
import com.sjl.core.net.RetrofitHelper;
import com.sjl.core.net.RxSchedulers;
import com.sjl.core.util.log.LogUtils;
import com.sjl.core.util.datetime.TimeUtils;
import com.sjl.core.util.security.MD5Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookDetailPresenter.java
 * @time 2018/12/7 10:10
 * @copyright(C) 2018 song
 */
public class BookDetailPresenter extends BookDetailContract.Presenter {
    private String bookId;

    @Override
    public void refreshBookDetail(String bookId) {
        this.bookId = bookId;
        refreshBook();
        refreshComment();
        refreshRecommend();

    }


    @Override
    public void addToBookShelf(final CollectBook collectBook) {

        ZhuiShuCompatRepository.getInstance().getBookChapter(bookId).doOnSubscribe(new Consumer<Disposable>() {
            //默认情况下， doOnSubscribe() 执行在 subscribe() 发生的线程；而如果在 doOnSubscribe() 之后有 subscribeOn() 的话，
            // 它将执行在离它最近的 subscribeOn() 所指定的线程。
            @Override
            public void accept(Disposable disposable) throws Exception {
                mView.waitToBookShelf(); //等待加载
            }
        })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<List<BookChapter>>bindLifecycle())
                .subscribe(new Consumer<List<BookChapter>>() {
                               @Override
                               public void accept(List<BookChapter> bookChapters) throws Exception {
                                   //设置 id
                                   for (BookChapter bean : bookChapters) {
                                       bean.setId(MD5Utils.strToMd5By16(bean.getLink()));
                                   }

                                   //设置目录
                                   collectBook.setBookChapters(bookChapters);
                                   //存储收藏
                                   DaoFactory.getCollectBookDao()
                                           .saveCollBookWithAsync(collectBook);

                                   mView.succeedToBookShelf();
                               }
                           }
                        ,
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                mView.errorToBookShelf();
                                LogUtils.e("addToBookShelf异常", throwable);
                            }
                        }
                );
    }

    /**
     * 初始化书籍详情
     */
    private void refreshBook() {
        ZhuiShuShenQiApi apiService = RetrofitHelper.getInstance().getApiService(ZhuiShuShenQiApi.class);
        apiService
                .getBookDetail(bookId)
                .compose(RxSchedulers.<BookDetailDto.BookDetail>applySingle())
                .as(this.<BookDetailDto.BookDetail>bindLifecycle())
                .subscribe(new SingleObserver<BookDetailDto.BookDetail>() {


                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(BookDetailDto.BookDetail value) {
                        mView.finishRefresh(value);
                        mView.complete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e("初始化书籍详情异常", e);
                        mView.showError();
                    }
                });
    }

    /**
     * 初始化评论列表
     */
    private void refreshComment() {
        ZhuiShuShenQiApi apiService = RetrofitHelper.getInstance().getApiService(ZhuiShuShenQiApi.class);
        apiService.getHotComment(bookId).map(new Function<HotCommentDto, List<HotCommentDto.HotComment>>() {
            @Override
            public List<HotCommentDto.HotComment> apply(HotCommentDto hotCommentDto) throws Exception {
                if (hotCommentDto.getReviews() == null){
                    return new ArrayList<>();
                }
                return hotCommentDto.getReviews();
            }
        }).compose(RxSchedulers.<List<HotCommentDto.HotComment>>applySingle())
                .as(this.<List<HotCommentDto.HotComment>>bindLifecycle())
                .subscribe(new Consumer<List<HotCommentDto.HotComment>>() {
                               @Override
                               public void accept(List<HotCommentDto.HotComment> hotComments) throws Exception {
                                   mView.finishHotComment(hotComments);
                               }
                           }
                        , new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                LogUtils.e("初始化评论列表异常", throwable);
                            }
                        });
    }

    /**
     * 初始化推荐书籍列表
     */
    private void refreshRecommend() {
        List<RecommendBook> recommendBooks = DaoFactory.getRecommendBookDao().queryRecommendBookByBookId(bookId);
        if (recommendBooks != null && recommendBooks.size() > 0) {
            RecommendBook recommendBook = recommendBooks.get(0);
            if (!TextUtils.isEmpty(recommendBook.getUpdateTime())) {
                SimpleDateFormat sd = new SimpleDateFormat(TimeUtils.DATE_FORMAT_1);
                try {
                    Date lastTime = sd.parse(recommendBook.getUpdateTime());
                    long dateDiff = TimeUtils.dateDiff(lastTime, new Date());
                    if (dateDiff >= AppConstant.RECOMMEND_BOOK_VALID_TIME) {//超出三天未使用应用，重新登录
                        LogUtils.i("推荐书籍缓存过期");
                        DaoFactory.getRecommendBookDao().deleteRecommendBookByBookId(bookId);
                        refreshRecommendBook();
                    } else {//使用缓存数据
                        LogUtils.i("使用缓存推荐书籍数据:" + recommendBooks.size());
                        mView.finishRecommendBookList(recommendBooks);
                    }
                } catch (ParseException e) {
                    mView.showError();
                }
            } else {
                refreshRecommendBook();
            }
        } else {
            refreshRecommendBook();
        }

    }


    /**
     * 爬虫解析网页获取：喜欢这本书的也喜欢
     */
    private void refreshRecommendBook() {
        LogUtils.i("refreshRecommendBook爬虫获取推荐书籍");
        //http://www.zhuishushenqi.com/book/53e56ee335f79bb626a496c9
        //不能用了
//        final String url = "http://www.zhuishushenqi.com/book/" + bookId;


        //https://m.zhuishushenqi.com/book/59439b346915617d5fa836c7?exposure=59439b346915617d5fa836c7
        final String url = "http://m.zhuishushenqi.com/book/" + bookId + "?exposure=" + bookId;

        Observable.create(new ObservableOnSubscribe<List<RecommendBook>>() {
            @Override
            public void subscribe(ObservableEmitter<List<RecommendBook>> observableEmitter) throws Exception {
                //很容易超时
                List<RecommendBook> bookLists = ZhuiShuParse.parseRecommendBook2(bookId, url);
                LogUtils.i("爬虫获取推荐书籍数量:" + bookLists.size());
                DaoFactory.getRecommendBookDao().saveRecommendBookByBookId(bookLists);
                observableEmitter.onNext(bookLists);
            }
        }).compose(RxSchedulers.<List<RecommendBook>>applySchedulers())
                .as(this.<List<RecommendBook>>bindLifecycle())
                .subscribe(new Consumer<List<RecommendBook>>() {
                    @Override
                    public void accept(List<RecommendBook> bookLists) throws Exception {
                        mView.finishRecommendBookList(bookLists);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("初始化推荐书籍列表异常", throwable);
                    }
                });
    }
}
