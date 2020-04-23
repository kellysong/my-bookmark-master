package com.sjl.bookmark.ui.presenter;

import com.sjl.bookmark.api.WanAndroidApiService;
import com.sjl.bookmark.entity.Article;
import com.sjl.bookmark.entity.DataResponse;
import com.sjl.bookmark.net.HttpConstant;
import com.sjl.bookmark.ui.contract.ArticleListContract;
import com.sjl.core.net.RetrofitHelper;
import com.sjl.core.net.RxSchedulers;

import io.reactivex.functions.Consumer;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ArticleListPresenter.java
 * @time 2018/3/23 16:28
 * @copyright(C) 2018 song
 */
public class ArticleListPresenter extends ArticleListContract.Presenter {
    private boolean mIsRefresh;
    private int mPage, mCid;

    public ArticleListPresenter() {
        this.mIsRefresh = true;
    }


    public void loadCategoryArticles(int cid) {
        this.mCid = cid;
        WanAndroidApiService apiService = RetrofitHelper.getInstance().getApiService(WanAndroidApiService.class);
        apiService.getKnowledgeCategoryArticles(mPage, mCid)
                .compose(RxSchedulers.<DataResponse<Article>>applySchedulers()).as(this.<DataResponse<Article>>bindLifecycle())
                .subscribe(new Consumer<DataResponse<Article>>() {
                    @Override
                    public void accept(DataResponse<Article> dataResponse) throws Exception {
                        int loadType = mIsRefresh ? HttpConstant.LoadType.TYPE_REFRESH_SUCCESS : HttpConstant.LoadType.TYPE_LOAD_MORE_SUCCESS;
                        mView.setCategoryArticles(dataResponse.getData(), loadType);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        int loadType = mIsRefresh ? HttpConstant.LoadType.TYPE_REFRESH_ERROR : HttpConstant.LoadType.TYPE_LOAD_MORE_ERROR;
                        mView.setCategoryArticles(new Article(), loadType);
                    }
                });
    }

    @Override
    public void loadMore() {
        mPage++;
        mIsRefresh = false;
        loadCategoryArticles(mCid);
    }

    @Override
    public void refresh() {
        mPage = 0;
        mIsRefresh = true;
        loadCategoryArticles(mCid);
    }
}
