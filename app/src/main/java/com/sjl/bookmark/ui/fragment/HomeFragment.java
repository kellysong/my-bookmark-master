package com.sjl.bookmark.ui.fragment;

import android.arch.lifecycle.Lifecycle;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lwj.widget.viewpagerindicator.ViewPagerIndicator;
import com.sjl.bookmark.R;
import com.sjl.bookmark.app.AppConstant;
import com.sjl.bookmark.entity.Article;
import com.sjl.bookmark.entity.Category;
import com.sjl.bookmark.entity.TopBanner;
import com.sjl.bookmark.net.HttpConstant;
import com.sjl.bookmark.ui.activity.ArticleTypeActivity;
import com.sjl.bookmark.ui.activity.BrowserActivity;
import com.sjl.bookmark.ui.adapter.ArticleAdapter;
import com.sjl.bookmark.ui.adapter.ImagePagerAdapter;
import com.sjl.bookmark.ui.contract.HomeContract;
import com.sjl.bookmark.ui.presenter.HomePresenter;
import com.sjl.core.entity.EventBusDto;
import com.sjl.core.mvp.BaseFragment;
import com.sjl.core.net.RxBus;
import com.sjl.core.util.log.LogUtils;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.youth.banner.Banner;
import com.zhy.adapter.viewpager.BasePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * 首页
 *
 * @author Kelly
 * @version 1.0.0
 * @filename HomeFragment.java
 * @time 2018/3/21 11:23
 * @copyright(C) 2018 song
 */
public class HomeFragment extends BaseFragment<HomePresenter> implements HomeContract.View, ArticleAdapter.OnItemClickListener, ArticleAdapter.OnItemChildClickListener,
        SwipeRefreshLayout.OnRefreshListener, ArticleAdapter.RequestLoadMoreListener {

    @BindView(R.id.rvHomeArticles)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    ViewPager mViewpager;
    ViewPagerIndicator mViewPagerIndicator;

    private ImagePagerAdapter mBannerAdapter;


    private ArticleAdapter mArticleAdapter;
    private Banner mBannerAds;

    private View mHomeBannerHeadView;


    @Override
    protected void onFirstUserVisible() {

    }

    @Override
    protected void onUserVisible() {

    }

    @Override
    protected void onUserInvisible() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.home_fragment;

    }

    @Override
    protected void initView() {

    }


    @Override
    protected void initListener() {
        RxBus.getInstance()
                .toObservable(AppConstant.RxBusFlag.FLAG_1, EventBusDto.class)
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.<EventBusDto>autoDisposable(AndroidLifecycleScopeProvider.from(this, Lifecycle.Event.ON_DESTROY)))//Lifecycle.Event.ON_DESTROY不加，跳转页面时,fragment回调onStop导致订阅会失效无法接收事件
                .subscribe(new Consumer<EventBusDto>() {
                    @Override
                    public void accept(EventBusDto s) throws Exception {
                        LogUtils.i("触发清除浏览记录，开始更新HomeFragment列表：" + s);
                        if (s.getEventCode() == 0) {
                            mArticleAdapter.refreshBrowseTrack();
                            mArticleAdapter.notifyDataSetChanged();//方法可能不生效
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e(throwable);
                    }
                });
    }

    @Override
    protected void initData() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.blueStatus);

        /**设置RecyclerView*/
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mArticleAdapter = new ArticleAdapter(R.layout.home_article_recycle_item, null);
        mRecyclerView.setAdapter(mArticleAdapter);

        /**设置BannerHead BaseView*/
        mHomeBannerHeadView = LayoutInflater.from(getContext()).inflate(R.layout.home_head_banner, null);
        mViewpager = (ViewPager) mHomeBannerHeadView.findViewById(R.id.viewpager);
        mViewPagerIndicator = (ViewPagerIndicator) mHomeBannerHeadView.findViewById(R.id.indicator_line);

//        mBannerAds = (Banner) mHomeBannerHeadView.findViewById(R.id.banner_ads);
        mArticleAdapter.addHeaderView(mHomeBannerHeadView);


        /**设置事件监听*/
        mArticleAdapter.setOnItemClickListener(this);
        mArticleAdapter.setOnItemChildClickListener(this);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mArticleAdapter.setOnLoadMoreListener(this, mRecyclerView);

        /**请求数据*/
        mPresenter.loadHomeData();
    }


    @Override
    public void setHomeBanners(final List<TopBanner> banners) {
        List<String> images = new ArrayList();
        List<String> titles = new ArrayList();
        //左右添加多一张图片
        for (TopBanner banner : banners) {
            images.add(banner.getImagePath());
            titles.add(banner.getTitle());
        }

        initBanner(images, banners);
      /*  mBannerAds.setImages(images)
                .setBannerTitles(titles)
//                .setIndicatorGravity(BannerConfig.CENTER)
                .setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE)
                .setImageLoader(new GlideImageLoader())
                .start();

        mBannerAds.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                TopBanner banner = banners.get(position);
                BrowserActivity.startWithParams(mActivity, banner.getTitle(), banner.getUrl());
            }
        });*/
    }


    private void initBanner(final List<String> images, final List<TopBanner> banners) {
        if (mBannerAdapter == null) {//new一次适配器，否则下拉刷新后出现图片、指示器滑动过快
            mBannerAdapter = new ImagePagerAdapter(getActivity(), images, mViewpager);
        } else {
            mBannerAdapter.setData(images);
        }
        mViewpager.setAdapter(mBannerAdapter);
        mViewPagerIndicator.setViewPager(mViewpager, images.size());
        mBannerAdapter.setOnItemClickListener(new BasePagerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                int newPosition = position % images.size();
                TopBanner banner = banners.get(newPosition);
                BrowserActivity.startWithParams(mActivity, banner.getTitle(), banner.getUrl());
            }
        });
        mBannerAdapter.startLoop(3);
        mViewpager.setCurrentItem(0);
    }


    @Override
    public void setHomeArticles(Article article, int loadType) {
        setLoadDataResult(mArticleAdapter, mSwipeRefreshLayout, article.getDatas(), loadType);
    }

    @Override
    public void collectArticleSuccess(int position, Article.DatasBean bean) {
    }


    @Override
    public void showFaild(String message) {
        mSwipeRefreshLayout.setRefreshing(false);
    }


    @Override
    public void showLoading() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideLoading() {

    }

    protected void setLoadDataResult(BaseQuickAdapter articleAdapter, SwipeRefreshLayout refreshLayout, List list, int loadType) {
        switch (loadType) {
            case HttpConstant.LoadType.TYPE_REFRESH_SUCCESS:
                articleAdapter.setNewData(list);
                refreshLayout.setRefreshing(false);
                break;
            case HttpConstant.LoadType.TYPE_REFRESH_ERROR:
                refreshLayout.setRefreshing(false);
                break;
            case HttpConstant.LoadType.TYPE_LOAD_MORE_SUCCESS://加载更多
                if (list != null) {
                    articleAdapter.addData(list);
                }
                break;
            case HttpConstant.LoadType.TYPE_LOAD_MORE_ERROR:
                articleAdapter.loadMoreFail();
                break;
            default:
                break;
        }
        if (list == null || list.isEmpty() || list.size() < HttpConstant.PAGE_SIZE) {
            LogUtils.i("没有文章数据了");
            articleAdapter.loadMoreEnd(false); //数据全部加载完毕
        } else {
            articleAdapter.loadMoreComplete(); //注意不是加载结束，而是本次数据加载结束并且还有下页数据
        }
    }


    /**
     * Item子控件的点击事件
     *
     * @param adapter
     * @param view
     * @param position
     */
    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if (view.getId() == R.id.tvChapterName) {
            String chapterName = mArticleAdapter.getItem(position).getChapterName();
            Intent intent = new Intent(mActivity, ArticleTypeActivity.class);
            intent.putExtra(HttpConstant.CONTENT_TITLE_KEY, chapterName);
            List<Category.ChildrenBean> children = new ArrayList<>();
            children.add(new Category.ChildrenBean(mArticleAdapter.getItem(position).getChapterId(),
                    chapterName));
            intent.putParcelableArrayListExtra(HttpConstant.CONTENT_CHILDREN_DATA_KEY, (ArrayList<? extends Parcelable>) children);
            intent.putExtra(HttpConstant.CONTENT_OPEN_FLAG, "0");

            mActivity.startActivity(intent);
        }
    }

    /**
     * Item点击事件
     *
     * @param adapter
     * @param view
     * @param position
     */
    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Article.DatasBean item = mArticleAdapter.getItem(position);

        mArticleAdapter.addBrowseTrack(String.valueOf(item.getId()), position);

        BrowserActivity.startWithParams(mActivity, item.getTitle(), item.getLink());
    }

    /**
     * 下拉刷新
     */
    @Override
    public void onRefresh() {
        mPresenter.refresh();
    }

    /**
     * 上拉加载
     */
    @Override
    public void onLoadMoreRequested() {
        mPresenter.loadMore();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mBannerAdapter != null) {
            mBannerAdapter.stopLoop();
        }
    }
}
