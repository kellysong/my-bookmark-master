package com.sjl.bookmark.ui.presenter

import com.sjl.bookmark.api.WanAndroidApiService
import com.sjl.bookmark.entity.Category
import com.sjl.bookmark.entity.DataResponse
import com.sjl.bookmark.ui.contract.CategoryContract
import com.sjl.core.net.RetrofitHelper
import com.sjl.core.net.RxSchedulers
import com.uber.autodispose.ObservableSubscribeProxy
import io.reactivex.functions.Consumer

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename CategoryPresenter.java
 * @time 2018/3/22 16:12
 * @copyright(C) 2018 song
 */
class CategoryPresenter : CategoryContract.Presenter() {
    override fun loadCategoryData() {
        mView.showLoading()
        val apiService = RetrofitHelper.getInstance().getApiService(
            WanAndroidApiService::class.java
        )
        apiService.knowledgeCategory
            .compose(RxSchedulers.applySchedulers())
            .`as`<ObservableSubscribeProxy<DataResponse<List<Category>>>>(
                bindLifecycle<DataResponse<List<Category>>>()
            )
            .subscribe(Consumer<DataResponse<List<Category>>> { dataResponse ->
                mView.setCategory(
                    dataResponse.data
                )
            }, Consumer { throwable -> mView.showFail(throwable.message) })
    }

    /**
     * 下拉刷新
     */
    override fun refresh() {
        loadCategoryData()
    }
}