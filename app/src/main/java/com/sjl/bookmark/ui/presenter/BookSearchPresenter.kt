package com.sjl.bookmark.ui.presenter;

import com.sjl.bookmark.api.ZhuiShuShenQiApi;
import com.sjl.bookmark.entity.zhuishu.HotWordDto;
import com.sjl.bookmark.entity.zhuishu.KeyWordDto;
import com.sjl.bookmark.entity.zhuishu.SearchBookDto;
import com.sjl.bookmark.ui.contract.BookSearchContract;
import com.sjl.core.net.RetrofitHelper;
import com.sjl.core.net.RxSchedulers;
import com.sjl.core.util.log.LogUtils;

import java.util.List;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookSearchPresenter.java
 * @time 2018/11/30 16:58
 * @copyright(C) 2018 song
 */
public class BookSearchPresenter extends BookSearchContract.Presenter {

    @Override
    public void searchHotWord() {
        ZhuiShuShenQiApi apiService = RetrofitHelper.getInstance().getApiService(ZhuiShuShenQiApi.class);

        apiService.getHotWordPackage().map(new Function<HotWordDto, List<String>>() {
            @Override
            public List<String> apply(HotWordDto hotWordDto) throws Exception {
                return hotWordDto.getHotWords();
            }
        }).compose(RxSchedulers.<List<String>>applySingle()).as(this.<List<String>>bindLifecycle())
                .subscribe(new Consumer<List<String>>() {
                    @Override
                    public void accept(List<String> strings) throws Exception {
                        mView.finishHotWords(strings);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("获取搜索热词失败", throwable);
                    }
                });

    }

    @Override
    public void searchKeyWord(String query) {
        ZhuiShuShenQiApi apiService = RetrofitHelper.getInstance().getApiService(ZhuiShuShenQiApi.class);

        apiService.getKeyWordPacakge(query).map(new Function<KeyWordDto, List<String>>() {

            @Override
            public List<String> apply(KeyWordDto keyWordDto) throws Exception {
                return keyWordDto.getKeywords();
            }
        }).compose(RxSchedulers.<List<String>>applySingle()).as(this.<List<String>>bindLifecycle())
                .subscribe(new Consumer<List<String>>() {
                    @Override
                    public void accept(List<String> strings) throws Exception {
                        LogUtils.i("关键字自动补全，匹配数量" + strings.size());
                        mView.finishKeyWords(strings);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("关键字自动补全，查询失败", throwable);
                    }
                });
    }

    @Override
    public void searchBook(String query) {
        ZhuiShuShenQiApi apiService = RetrofitHelper.getInstance().getApiService(ZhuiShuShenQiApi.class);

        apiService.getSearchBookPackage(query).map(new Function<SearchBookDto, List<SearchBookDto.BooksBean>>() {

            @Override
            public List<SearchBookDto.BooksBean> apply(SearchBookDto searchBookDto) throws Exception {
                return searchBookDto.getBooks();
            }
        }).compose(RxSchedulers.<List<SearchBookDto.BooksBean>>applySingle()).as(this.<List<SearchBookDto.BooksBean>>bindLifecycle())
                .subscribe(new Consumer<List<SearchBookDto.BooksBean>>() {
                    @Override
                    public void accept(List<SearchBookDto.BooksBean> bean) throws Exception {
                        LogUtils.i("书籍查询，匹配数量" + bean.size());
                        mView.finishBooks(bean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("书籍查询失败", throwable);
                        mView.errorBooks();
                    }
                });

    }
}
