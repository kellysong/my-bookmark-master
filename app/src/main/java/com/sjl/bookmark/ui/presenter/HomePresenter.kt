package com.sjl.bookmark.ui.presenter

import com.sjl.bookmark.api.WanAndroidApiService
import com.sjl.bookmark.entity.Article
import com.sjl.bookmark.entity.Article.DatasBean
import com.sjl.bookmark.entity.DataResponse
import com.sjl.bookmark.entity.TopBanner
import com.sjl.bookmark.net.HttpConstant
import com.sjl.bookmark.ui.contract.HomeContract
import com.sjl.core.net.RetrofitHelper
import com.sjl.core.net.RxSchedulers
import com.sjl.core.util.log.LogUtils
import com.uber.autodispose.ObservableSubscribeProxy
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename HomePresenter.java
 * @time 2018/3/21 16:09
 * @copyright(C) 2018 song
 */
class HomePresenter : HomeContract.Presenter() {
    private var mPage = 0
    private var mIsRefresh //true表示下拉刷新
            = true

    override fun loadHomeData() {
        mView.showLoading()
        val start = System.currentTimeMillis()
        val apiService = RetrofitHelper.getInstance().getApiService(
            WanAndroidApiService::class.java
        )
        val observableBanner = apiService.homeBanners.subscribeOn(Schedulers.io())
        val observableTop = loadTopArticles(apiService).subscribeOn(Schedulers.io())
        val observableArticle = apiService.getHomeArticles(mPage)
            .subscribeOn(Schedulers.io())
        //合并数据显示
        val topAndHomeList =
            Observable.zip<DataResponse<Article>, DataResponse<Article>, DataResponse<Article>>(
                observableTop,
                observableArticle,
                BiFunction<DataResponse<Article>, DataResponse<Article>, DataResponse<Article>> { articleDataResponse, articleDataResponse2 ->
                    articleDataResponse.data.datas.addAll(articleDataResponse2.data.datas)
                    articleDataResponse
                }).subscribeOn(Schedulers.io())
        Observable.zip<DataResponse<List<TopBanner>>, DataResponse<Article>, Map<String, Any>>(
            observableBanner,
            topAndHomeList,
            BiFunction<DataResponse<List<TopBanner>>, DataResponse<Article>, Map<String, Any>> { listDataResponse, articleDataResponse ->
                val objMap: MutableMap<String, Any> = HashMap()
                objMap[HttpConstant.BANNER_KEY] = listDataResponse.data
                objMap[HttpConstant.ARTICLE_KEY] = articleDataResponse.data
                objMap
            }).observeOn(AndroidSchedulers.mainThread())
            .`as`(bindLifecycle()).subscribe(
                Consumer<Map<String, Any>> { map ->
                    val banners = map[HttpConstant.BANNER_KEY] as List<TopBanner>
                    val article = map[HttpConstant.ARTICLE_KEY] as Article
                    mView.setHomeArticles(article, HttpConstant.LoadType.TYPE_REFRESH_SUCCESS)
                    mView.setHomeBanners(banners)
                    val end = System.currentTimeMillis()
                    LogUtils.i("耗时：" + (end - start) / 1000.0 + "s")
                }, Consumer { throwable ->
                    LogUtils.e("首页数据合成失败：" + throwable.message, throwable)
                    mView.showFaild(throwable.message)
                })
    }

    /**
     * 加载置顶文章
     *
     * @param apiService
     * @return
     */
    private fun loadTopArticles(apiService: WanAndroidApiService): Observable<DataResponse<Article>> {
        val topArticles =
            apiService.topArticles
        return topArticles.flatMap(Function<DataResponse<List<DatasBean>>, ObservableSource<DataResponse<Article>>> { listDataResponse ->
            val dataResponse: DataResponse<Article> = DataResponse()
            val errorCode = listDataResponse.errorCode
            if (errorCode == 0) {
                val article = Article()
                val datas = listDataResponse.data.toMutableList()
                val it = datas.iterator()
                while (it.hasNext()) {
                    val datasBean = it.next()
                    datasBean.isTop = true //置顶
                    if (!"问答".equals(datasBean.superChapterName, ignoreCase = true)) {
                        it.remove() //过滤网络课程，净化学习环境
                    }
                }
                article.datas = datas
                dataResponse.setData(article)
            }
            Observable.just(dataResponse)
        })
    }

    /**
     * 上拉加载更多
     */
    override fun loadMore() {
        mPage++
        mIsRefresh = false
        loadHomeArticles()
    }

    /**
     * 加载更换文章
     */
    private fun loadHomeArticles() {
        val apiService = RetrofitHelper.getInstance().getApiService(
            WanAndroidApiService::class.java
        )
        val observableArticle = apiService.getHomeArticles(mPage)
        val concat: Observable<DataResponse<Article>>
        concat = if (mIsRefresh) {
            val observableTop = loadTopArticles(apiService)
            //合并数据显示
            Observable.zip<DataResponse<Article>, DataResponse<Article>, DataResponse<Article>>(
                observableTop.subscribeOn(Schedulers.io()),
                observableArticle.subscribeOn(Schedulers.io()),
                BiFunction<DataResponse<Article>, DataResponse<Article>, DataResponse<Article>> { articleDataResponse, articleDataResponse2 ->
                    articleDataResponse.data.datas.addAll(articleDataResponse2.data.datas)
                    articleDataResponse
                })
        } else {
            observableArticle
        }
        concat!!.compose(RxSchedulers.applySchedulers())
            .`as`<ObservableSubscribeProxy<DataResponse<Article>>>(bindLifecycle<DataResponse<Article>>())
            .subscribe(Consumer<DataResponse<Article>> { dataResponse ->
                LogUtils.i("刷新成功")
                val loadType =
                    if (mIsRefresh) HttpConstant.LoadType.TYPE_REFRESH_SUCCESS else HttpConstant.LoadType.TYPE_LOAD_MORE_SUCCESS
                mView.setHomeArticles(dataResponse.data, loadType)
            }, Consumer
            //异常
            {
                val loadType =
                    if (mIsRefresh) HttpConstant.LoadType.TYPE_REFRESH_ERROR else HttpConstant.LoadType.TYPE_LOAD_MORE_ERROR
                mView.setHomeArticles(Article(), loadType)
            })
    }

    /**
     * 下拉刷新
     */
    override fun refresh() {
        mPage = 0
        mIsRefresh = true
        loadHomeBanners()
        loadHomeArticles()
    }

    /**
     * 加载首页轮播
     */
    private fun loadHomeBanners() {
        val apiService = RetrofitHelper.getInstance().getApiService(
            WanAndroidApiService::class.java
        )
        val observableBanner = apiService.homeBanners
        observableBanner
            .compose(RxSchedulers.applySchedulers())
            .`as`<ObservableSubscribeProxy<DataResponse<List<TopBanner>>>>(
                bindLifecycle<DataResponse<List<TopBanner>>>()
            )
            .subscribe(Consumer<DataResponse<List<TopBanner>>> { dataResponse ->
                mView.setHomeBanners(
                    dataResponse.data
                )
            }, Consumer { throwable -> mView.showFaild(throwable.message) })
    }
}