package com.sjl.bookmark.ui.presenter;

import com.sjl.bookmark.api.WanAndroidApiService;
import com.sjl.bookmark.entity.Article;
import com.sjl.bookmark.entity.DataResponse;
import com.sjl.bookmark.entity.HotKey;
import com.sjl.bookmark.ui.contract.ArticleSearchContract;
import com.sjl.core.net.RetrofitHelper;
import com.sjl.core.net.RxSchedulers;
import com.sjl.core.util.log.LogUtils;

import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ArticleSearchPresenter.java
 * @time 2018/4/1 12:03
 * @copyright(C) 2018 song
 */
public class ArticleSearchPresenter extends ArticleSearchContract.Presenter {

    private int mCurrentPage;



    /**
     * 获取热门搜索关键字
     */
    @Override
    public void getHotKeyData() {
        WanAndroidApiService apiService = RetrofitHelper.getInstance().getApiService(WanAndroidApiService.class);
        apiService.getHotKeys()
                .compose(RxSchedulers.<DataResponse<List<HotKey>>>applySchedulers()).as(this.<DataResponse<List<HotKey>>>bindLifecycle())
                .subscribe(new Consumer<DataResponse<List<HotKey>>>() {
                    @Override
                    public void accept(DataResponse<List<HotKey>> dataResponse) throws Exception {
                        mView.getHotKeySuccess(dataResponse.getData());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.showFailMsg("热门搜索：" + throwable.getMessage());
                    }
                });
    }

    /**
     * 根据关键字搜索
     * @param key
     */
    @Override
    public void searchData(String key) {
        mCurrentPage = 0;
        WanAndroidApiService apiService = RetrofitHelper.getInstance().getApiService(WanAndroidApiService.class);

        apiService.getSearchArticles(mCurrentPage, key)
                .compose(RxSchedulers.<DataResponse<Article>>applySchedulers()).as(this.<DataResponse<Article>>bindLifecycle())
                .subscribe(new Consumer<DataResponse<Article>>() {
                    @Override
                    public void accept(DataResponse<Article> dataResponse) throws Exception {
                        mView.searchDataSuccess(dataResponse.getData().getDatas());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e(throwable);
                        mView.showFailMsg("搜索：" + throwable.getMessage());
                    }
                });
    }

    /**
     * 上拉加载更多数据
     * @param keyWord
     */
    @Override
    public void getMoreData(String keyWord) {
        mCurrentPage += 1;
        WanAndroidApiService apiService = RetrofitHelper.getInstance().getApiService(WanAndroidApiService.class);

        apiService.getSearchArticles(mCurrentPage, keyWord)
                .compose(RxSchedulers.<DataResponse<Article>>applySchedulers()).as(this.<DataResponse<Article>>bindLifecycle())
                .subscribe(new Consumer<DataResponse<Article>>() {
                    @Override
                    public void accept(DataResponse<Article> dataResponse) throws Exception {
                        mView.loadMoreDataSuccess(dataResponse.getData().getDatas());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.showFailMsg("分页加载：" + throwable.getMessage());
                    }
                });

    }
}
