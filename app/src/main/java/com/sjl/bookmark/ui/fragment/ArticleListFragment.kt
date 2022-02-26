package com.sjl.bookmark.ui.fragment;

import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.sjl.bookmark.R;
import com.sjl.bookmark.entity.Article;
import com.sjl.bookmark.net.HttpConstant;
import com.sjl.bookmark.ui.activity.BrowserActivity;
import com.sjl.bookmark.ui.adapter.ArticleAdapter;
import com.sjl.bookmark.ui.contract.ArticleListContract;
import com.sjl.bookmark.ui.presenter.ArticleListPresenter;
import com.sjl.core.entity.EventBusDto;
import com.sjl.core.mvp.BaseFragment;
import com.sjl.core.util.log.LogUtils;

import java.util.List;

import butterknife.BindView;

/**
 * 文章列表
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ArticleListFragment.java
 * @time 2018/3/23 14:00
 * @copyright(C) 2018 song
 */
public class ArticleListFragment extends BaseFragment<ArticleListPresenter> implements ArticleListContract.View, ArticleAdapter.OnItemClickListener, ArticleAdapter.OnItemChildClickListener,
        SwipeRefreshLayout.OnRefreshListener, ArticleAdapter.RequestLoadMoreListener {
    @BindView(R.id.rvArticleList)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    private int cid;

    private ArticleAdapter mArticleAdapter;




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
        return R.layout.article_list_fragment;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {
        Bundle arguments = getArguments();
        cid = arguments.getInt(HttpConstant.CONTENT_CID_KEY);
        LogUtils.i("当前文章种类id"+cid);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.blueStatus);

        /**设置RecyclerView*/
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mArticleAdapter = new ArticleAdapter(R.layout.articlelist_recycle_item,null);
        /**隐藏文章类型*/
        mArticleAdapter.setChapterNameVisible(false);
        mRecyclerView.setAdapter(mArticleAdapter);

        /**设置事件监听*/
        mArticleAdapter.setOnItemClickListener(this);
        mArticleAdapter.setOnItemChildClickListener(this);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mArticleAdapter.setOnLoadMoreListener(this,mRecyclerView);

        /**请求数据*/
        mPresenter.loadCategoryArticles(cid);
    }

    @Override
    public void onEventComing(EventBusDto eventCenter) {

    }



    @Override
    public void setCategoryArticles(Article article, int loadType) {
        setLoadDataResult(mArticleAdapter, mSwipeRefreshLayout, article.getDatas(), loadType);
    }

    protected void setLoadDataResult(BaseQuickAdapter articleAdapter, SwipeRefreshLayout refreshLayout, List list, int loadType) {
        switch (loadType) {
            case HttpConstant.LoadType.TYPE_REFRESH_SUCCESS:
                if (list != null && list.size() > 0){
                    articleAdapter.setNewData(list);
                }
                refreshLayout.setRefreshing(false);
                break;
            case HttpConstant.LoadType.TYPE_REFRESH_ERROR:
                refreshLayout.setRefreshing(false);
                break;
            case HttpConstant.LoadType.TYPE_LOAD_MORE_SUCCESS://加载更多
                if (list != null && list.size() > 0){
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
            //成功获取更多数据
            LogUtils.i("加载完成");
            articleAdapter.loadMoreComplete(); //成功获取更多数据
        }
    }

    /**
     * 下拉刷新
     */
    @Override
    public void onRefresh() {
        mPresenter.refresh();
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Article.DatasBean item = mArticleAdapter.getItem(position);
        mArticleAdapter.addBrowseTrack(String.valueOf(item.getId()),position);
        BrowserActivity.startWithParams(mActivity, mArticleAdapter.getItem(position).getTitle(),
                mArticleAdapter.getItem(position).getLink());
    }

    /**
     * 上拉加载
     */
    @Override
    public void onLoadMoreRequested() {
        mPresenter.loadMore();
    }
}
