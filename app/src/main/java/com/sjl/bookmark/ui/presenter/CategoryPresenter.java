package com.sjl.bookmark.ui.presenter;

import com.sjl.bookmark.api.WanAndroidApiService;
import com.sjl.bookmark.entity.Category;
import com.sjl.bookmark.entity.DataResponse;
import com.sjl.bookmark.ui.contract.CategoryContract;
import com.sjl.core.net.RetrofitHelper;
import com.sjl.core.net.RxSchedulers;

import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename CategoryPresenter.java
 * @time 2018/3/22 16:12
 * @copyright(C) 2018 song
 */
public class CategoryPresenter extends CategoryContract.Presenter {


    @Override
    public void loadCategoryData() {
        mView.showLoading();
        WanAndroidApiService apiService = RetrofitHelper.getInstance().getApiService(WanAndroidApiService.class);

        apiService.getKnowledgeCategory()
                .compose(RxSchedulers.<DataResponse<List<Category>>>applySchedulers())
                .as(this.<DataResponse<List<Category>>>bindLifecycle())
                .subscribe(new Consumer<DataResponse<List<Category>>>() {
                    @Override
                    public void accept(DataResponse<List<Category>> dataResponse) throws Exception {
                        mView.setCategory(dataResponse.getData());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.showFail(throwable.getMessage());
                    }
                });

    }

    /**
     * 下拉刷新
     */
    @Override
    public void refresh() {
        loadCategoryData();
    }
}
