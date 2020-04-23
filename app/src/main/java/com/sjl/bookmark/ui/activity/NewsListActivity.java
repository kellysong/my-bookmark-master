package com.sjl.bookmark.ui.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.jakewharton.rxbinding2.view.RxView;
import com.sjl.bookmark.R;
import com.sjl.bookmark.entity.zhihu.NewsList;
import com.sjl.bookmark.kotlin.language.I18nUtils;
import com.sjl.bookmark.ui.adapter.NewsMultiDelegateAdapter;
import com.sjl.bookmark.ui.adapter.RecyclerViewDivider;
import com.sjl.bookmark.ui.contract.NewsListContract;
import com.sjl.bookmark.ui.presenter.NewsListPresenter;
import com.sjl.core.mvp.BaseActivity;
import com.sjl.core.util.log.LogUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * 知乎日报列表
 *
 * @author Kelly
 * @version 1.0.0
 * @filename NewsListActivity.java
 * @time 2018/12/18 17:05
 * @copyright(C) 2018 song
 */
public class NewsListActivity extends BaseActivity<NewsListPresenter> implements NewsListContract.View,
        SwipeRefreshLayout.OnRefreshListener, NewsMultiDelegateAdapter.RequestLoadMoreListener {
    @BindView(R.id.common_toolbar)
    Toolbar mToolBar;
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private NewsMultiDelegateAdapter newsMultiDelegateAdapter;
    private LinearLayoutManager layoutManager;

    @Override
    protected int getLayoutId() {
        return R.layout.news_list_activity;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {
        bindingToolbar(mToolBar, I18nUtils.getString(R.string.tool_zhihu_daily));
        doubleClickDetect(mToolBar);
    }

    @Override
    protected void initData() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.blueStatus);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        newsMultiDelegateAdapter = new NewsMultiDelegateAdapter(this, R.layout.news_list_activity, null);
        mPresenter.loadNews();
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(newsMultiDelegateAdapter);
        newsMultiDelegateAdapter.setOnLoadMoreListener(this, mRecyclerView);
        changeToolbarTitle();

    }

    private int dateViewPosition = -1;
    /**
     * 滚动改变ToolBar Title
     */
    private void changeToolbarTitle() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            /**
             *
             * @param recyclerView
             * @param dx
             * @param dy  dy > 0 时为向上滚动, dy < 0 时为向下滚动
             */
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                int firstVisibleItemViewType = newsMultiDelegateAdapter.getItemViewType(firstVisibleItemPosition);

//                LogUtils.i("firstVisibleItemPosition:" + firstVisibleItemPosition + ",firstVisibleItemViewType:" + firstVisibleItemViewType);

                if (firstVisibleItemViewType == NewsMultiDelegateAdapter.TYPE_HEADER) {
                    mToolBar.setTitle("知乎日报");
                } else if (firstVisibleItemViewType == NewsMultiDelegateAdapter.TYPE_HEADER_SECOND) {
                    mToolBar.setTitle("今日热闻");
                } else if (firstVisibleItemViewType == NewsMultiDelegateAdapter.TYPE_DATE) {
                    String date = newsMultiDelegateAdapter.getItem(firstVisibleItemPosition).getDate();
                    mToolBar.setTitle(date);
                } else {
                    if (firstVisibleItemPosition < dateViewPosition) {
                        mToolBar.setTitle("今日热闻");
                    } else {
                        mToolBar.setTitle(newsMultiDelegateAdapter.getItem(firstVisibleItemPosition).getDate());
                    }
                }


            }
        });
    }

    /**
     * 双击监听
     *
     * @param view
     */
    public void doubleClickDetect(View view) {
        Observable<Object> share = RxView.clicks(view).share();
        share.buffer(share.debounce(200, TimeUnit.MILLISECONDS))
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<List<Object>>bindLifecycle())
                .subscribe(new Consumer<List<Object>>() {
                    @Override
                    public void accept(List<Object> objects) throws Exception {
                        if (objects.size() >= 2) {
                            LogUtils.i("double click detected.");
                            //double click detected
                            //双击toolbar,平滑滚回顶部
                            mRecyclerView.smoothScrollToPosition(0);

                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }


    @Override
    public void onRefresh() {
        mPresenter.refresh();
    }

    @Override
    public void onLoadMoreRequested() {//滑动最后一个Item的时候回调onLoadMoreRequested方法
        mPresenter.loadMore();
    }

    @Override
    public void refreshNewsList(List<NewsList> newsLists) {
        newsMultiDelegateAdapter.firstLoadFlag = mPresenter.isFirstLoadFlag();
        newsMultiDelegateAdapter.setNewData(newsLists);
        mSwipeRefreshLayout.setRefreshing(false);
        dateViewPosition = newsLists.size() + 1;

    }

    @Override
    public void showMoreNewsList(List<NewsList> newsLists) {
        newsMultiDelegateAdapter.addData(newsLists);
        if (newsLists == null || newsLists.isEmpty()) {
            LogUtils.i("没有日报数据了");
            newsMultiDelegateAdapter.loadMoreEnd(false); //数据全部加载完毕,显示没有更多数据
        } else {
            newsMultiDelegateAdapter.loadMoreComplete(); //注意不是加载结束，而是本次数据加载结束并且还有下页数据
        }
    }
}
