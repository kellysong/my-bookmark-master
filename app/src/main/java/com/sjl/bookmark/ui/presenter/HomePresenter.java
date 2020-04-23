package com.sjl.bookmark.ui.presenter;

import com.sjl.bookmark.api.WanAndroidApiService;
import com.sjl.bookmark.entity.Article;
import com.sjl.bookmark.entity.DataResponse;
import com.sjl.bookmark.entity.TopBanner;
import com.sjl.bookmark.net.HttpConstant;
import com.sjl.bookmark.ui.contract.HomeContract;
import com.sjl.core.net.RetrofitHelper;
import com.sjl.core.net.RxSchedulers;
import com.sjl.core.util.log.LogUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename HomePresenter.java
 * @time 2018/3/21 16:09
 * @copyright(C) 2018 song
 */
public class HomePresenter extends HomeContract.Presenter {
    private int mPage = 0;
    private boolean mIsRefresh;//true表示下拉刷新

    public HomePresenter() {
        this.mIsRefresh = true;
    }


    @Override
    public void loadHomeData() {
        mView.showLoading();
        final long start = System.currentTimeMillis();
        WanAndroidApiService apiService = RetrofitHelper.getInstance().getApiService(WanAndroidApiService.class);
        Observable<DataResponse<List<TopBanner>>> observableBanner = apiService.getHomeBanners().subscribeOn(Schedulers.io());

        Observable<DataResponse<Article>> observableTop = loadTopArticles(apiService).subscribeOn(Schedulers.io());
        Observable<DataResponse<Article>> observableArticle = apiService.getHomeArticles(mPage).subscribeOn(Schedulers.io());
        //合并数据显示
        Observable<DataResponse<Article>> topAndHomeList = Observable.zip(observableTop, observableArticle, new BiFunction<DataResponse<Article>, DataResponse<Article>, DataResponse<Article>>() {
            @Override
            public DataResponse<Article> apply(DataResponse<Article> articleDataResponse, DataResponse<Article> articleDataResponse2) throws Exception {
                articleDataResponse.getData().getDatas().addAll(articleDataResponse2.getData().getDatas());
                return articleDataResponse;
            }
        }).subscribeOn(Schedulers.io());
        Observable.zip(observableBanner, topAndHomeList, new BiFunction<DataResponse<List<TopBanner>>, DataResponse<Article>, Map<String, Object>>() {

            @Override
            public Map<String, Object> apply(DataResponse<List<TopBanner>> listDataResponse, DataResponse<Article> articleDataResponse) throws Exception {
                Map<String, Object> objMap = new HashMap<>();
                objMap.put(HttpConstant.BANNER_KEY, listDataResponse.getData());
                objMap.put(HttpConstant.ARTICLE_KEY, articleDataResponse.getData());
                return objMap;
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .as(this.<Map<String, Object>>bindLifecycle()).
                subscribe(new Consumer<Map<String, Object>>() {
                    @Override
                    public void accept(Map<String, Object> map) throws Exception {
                        List<TopBanner> banners = (List<TopBanner>) map.get(HttpConstant.BANNER_KEY);
                        Article article = (Article) map.get(HttpConstant.ARTICLE_KEY);
                        mView.setHomeArticles(article, HttpConstant.LoadType.TYPE_REFRESH_SUCCESS);
                        mView.setHomeBanners(banners);
                        long end = System.currentTimeMillis();
                        LogUtils.i("耗时：" + (end - start) / 1000.0 + "s");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("首页数据合成失败：" + throwable.getMessage(), throwable);
                        mView.showFaild(throwable.getMessage());
                    }
                });
    }

    /**
     * 加载置顶文章
     *
     * @param apiService
     * @return
     */
    private Observable<DataResponse<Article>> loadTopArticles(WanAndroidApiService apiService) {
        Observable<DataResponse<List<Article.DatasBean>>> topArticles = apiService.getTopArticles();
        Observable<DataResponse<Article>> observableTop = topArticles.flatMap(new Function<DataResponse<List<Article.DatasBean>>, ObservableSource<DataResponse<Article>>>() {
            @Override
            public ObservableSource<DataResponse<Article>> apply(DataResponse<List<Article.DatasBean>> listDataResponse) throws Exception {
                DataResponse<Article> dataResponse = new DataResponse<Article>();
                int errorCode = listDataResponse.getErrorCode();
                if (errorCode == 0) {
                    Article article = new Article();
                    List<Article.DatasBean> datas = listDataResponse.getData();
                    Iterator<Article.DatasBean> it = datas.iterator();
                    while (it.hasNext()) {
                        Article.DatasBean datasBean = it.next();
                        datasBean.setTop(true);//置顶
                        if (!"问答".equalsIgnoreCase(datasBean.getSuperChapterName())) {
                            it.remove();//过滤网络课程，净化学习环境
                        }

                    }
                    article.setDatas(datas);
                    dataResponse.setData(article);
                }
                return Observable.just(dataResponse);
            }
        });
        return observableTop;
    }

    /**
     * 上拉加载更多
     */
    @Override
    public void loadMore() {
        mPage++;
        mIsRefresh = false;
        loadHomeArticles();
    }

    /**
     * 加载更换文章
     */
    private void loadHomeArticles() {
        WanAndroidApiService apiService = RetrofitHelper.getInstance().getApiService(WanAndroidApiService.class);
        Observable<DataResponse<Article>> observableArticle = apiService.getHomeArticles(mPage);
        Observable<DataResponse<Article>> concat;
        if (mIsRefresh) {
            Observable<DataResponse<Article>> observableTop = loadTopArticles(apiService);
            //合并数据显示
            concat = Observable.zip(observableTop.subscribeOn(Schedulers.io()), observableArticle.subscribeOn(Schedulers.io()), new BiFunction<DataResponse<Article>, DataResponse<Article>, DataResponse<Article>>() {
                @Override
                public DataResponse<Article> apply(DataResponse<Article> articleDataResponse, DataResponse<Article> articleDataResponse2) throws Exception {
                    articleDataResponse.getData().getDatas().addAll(articleDataResponse2.getData().getDatas());
                    return articleDataResponse;
                }
            });
        } else {
            concat = observableArticle;
        }
        concat.compose(RxSchedulers.<DataResponse<Article>>applySchedulers())
                .as(this.<DataResponse<Article>>bindLifecycle())
                .subscribe(new Consumer<DataResponse<Article>>() {
                    @Override
                    public void accept(DataResponse<Article> dataResponse) throws Exception {
                        LogUtils.i("刷新成功");
                        int loadType = mIsRefresh ? HttpConstant.LoadType.TYPE_REFRESH_SUCCESS : HttpConstant.LoadType.TYPE_LOAD_MORE_SUCCESS;
                        mView.setHomeArticles(dataResponse.getData(), loadType);
                    }
                }, new Consumer<Throwable>() {//异常
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        int loadType = mIsRefresh ? HttpConstant.LoadType.TYPE_REFRESH_ERROR : HttpConstant.LoadType.TYPE_LOAD_MORE_ERROR;
                        mView.setHomeArticles(new Article(), loadType);
                    }
                });

    }

    /**
     * 下拉刷新
     */
    @Override
    public void refresh() {
        mPage = 0;
        mIsRefresh = true;
        loadHomeBanners();
        loadHomeArticles();
    }

    /**
     * 加载首页轮播
     */
    private void loadHomeBanners() {
        WanAndroidApiService apiService = RetrofitHelper.getInstance().getApiService(WanAndroidApiService.class);
        Observable<DataResponse<List<TopBanner>>> observableBanner = apiService.getHomeBanners();

        observableBanner
                .compose(RxSchedulers.<DataResponse<List<TopBanner>>>applySchedulers())
                .as(this.<DataResponse<List<TopBanner>>>bindLifecycle())
                .subscribe(new Consumer<DataResponse<List<TopBanner>>>() {
                    @Override
                    public void accept(DataResponse<List<TopBanner>> dataResponse) throws Exception {
                        mView.setHomeBanners(dataResponse.getData());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.showFaild(throwable.getMessage());
                    }
                });

    }
}
