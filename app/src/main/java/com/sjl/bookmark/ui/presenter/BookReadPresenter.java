package com.sjl.bookmark.ui.presenter;

import com.sjl.bookmark.api.ZhuiShuCompatRepository;
import com.sjl.bookmark.api.ZhuiShuShenQiApi;
import com.sjl.bookmark.entity.zhuishu.ChapterInfoDto;
import com.sjl.bookmark.entity.zhuishu.table.BookChapter;
import com.sjl.bookmark.ui.contract.BookReadContract;
import com.sjl.bookmark.widget.reader.BookManager;
import com.sjl.bookmark.widget.reader.bean.TxtChapter;
import com.sjl.core.net.RetrofitHelper;
import com.sjl.core.net.RxSchedulers;
import com.sjl.core.util.log.LogUtils;
import com.sjl.core.util.security.MD5Utils;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookReadPresenter.java
 * @time 2018/12/4 15:54
 * @copyright(C) 2018 song
 */
public class BookReadPresenter extends BookReadContract.Presenter {
    private Subscription mChapterSub;

    @Override
    public void loadCategory(final String bookId) {
        ZhuiShuCompatRepository.getInstance().getBookChapter(bookId).doOnSuccess(new Consumer<List<BookChapter>>() {
            @Override
            public void accept(List<BookChapter> bookChapterBeen) throws Exception {
                //进行设定BookChapter所属的书的id。
                for (BookChapter bookChapter : bookChapterBeen) {
                    bookChapter.setId(MD5Utils.strToMd5By16(bookChapter.getLink()));
                    bookChapter.setBookId(bookId);
                }
            }
        }).compose(RxSchedulers.<List<BookChapter>>applySingle())
                .as(this.<List<BookChapter>>bindLifecycle())
                .subscribe(new Consumer<List<BookChapter>>() {
                               @Override
                               public void accept(List<BookChapter> bookChapters) throws Exception {
                                   mView.showCategory(bookChapters);
                               }
                           }
                        , new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                LogUtils.e(throwable);
                            }
                        }
                );
    }

    @Override
    public void loadChapter(final String bookId, final List<TxtChapter> bookChapterList) {
        int size = bookChapterList.size();

        //取消上次的任务，防止多次加载
        if (mChapterSub != null) {
            mChapterSub.cancel();
        }

        List<Single<ChapterInfoDto.ChapterInfo>> chapterInfos = new ArrayList<>(bookChapterList.size());
        final ArrayDeque<String> titles = new ArrayDeque<>(bookChapterList.size());
        ZhuiShuShenQiApi apiService = RetrofitHelper.getInstance().getApiService(ZhuiShuShenQiApi.class);

        // 将要下载章节，转换成网络请求。
        for (int i = 0; i < size; ++i) {
            TxtChapter bookChapter = bookChapterList.get(i);
            LogUtils.i("下载章节:"+bookChapter.toString());
            // 网络中获取数据
            //http://book.xbiquge.com/getBooks.aspx?method=content&bookId=2347074&chapterFile=U_2347074_201805221833153185_3011_3.txt
            Single<ChapterInfoDto.ChapterInfo> chapterInfoSingle = ZhuiShuCompatRepository.getInstance().getBookChapterInfo(bookChapter.link);
                 /*   apiService.getChapterInfoPackage(bookChapter.link).map(new Function<ChapterInfoDto, ChapterInfoDto.ChapterInfo>() {
                @Override
                public ChapterInfoDto.ChapterInfo apply(ChapterInfoDto chapterInfoDto) throws Exception {
                    return chapterInfoDto.getChapter();
                }
            });*/

            chapterInfos.add(chapterInfoSingle);

            titles.add(bookChapter.title);
        }
        //concat Observable的顺序一个个执行
        Single.concat(chapterInfos)//组合起来缓存章节
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Subscriber<ChapterInfoDto.ChapterInfo>() {
                            String title = titles.poll();//从队首获取元素

                            @Override
                            public void onSubscribe(Subscription s) {
                                s.request(Integer.MAX_VALUE);
                                mChapterSub = s;
                            }

                            @Override
                            public void onNext(ChapterInfoDto.ChapterInfo chapterInfoBean) {
                                //缓存章节
                                BookManager.getInstance().saveChapterInfo(bookId, title, chapterInfoBean.getBody());

                                mView.finishChapter();
                                LogUtils.i("缓存章节："+title+",titles size:"+titles.size());

                                title = titles.poll();//取出首元素

                            }

                            @Override
                            public void onError(Throwable t) {
                                //只有第一个加载失败才会调用errorChapter
                                if (bookChapterList.get(0).title.equals(title)) {
                                    mView.errorChapter();
                                }
                                LogUtils.e(t);
                            }

                            @Override
                            public void onComplete() {
                            }
                        }
                );
    }


}
