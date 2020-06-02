package com.sjl.bookmark.ui.fragment;

import android.content.Intent;
import android.os.Parcelable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.sjl.bookmark.R;
import com.sjl.bookmark.entity.Category;
import com.sjl.bookmark.net.HttpConstant;
import com.sjl.bookmark.ui.activity.ArticleTypeActivity;
import com.sjl.bookmark.ui.adapter.CategoryAdapter;
import com.sjl.bookmark.ui.contract.CategoryContract;
import com.sjl.bookmark.ui.presenter.CategoryPresenter;
import com.sjl.core.entity.EventBusDto;
import com.sjl.core.mvp.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename CategoryFragment.java
 * @time 2018/3/21 11:26
 * @copyright(C) 2018 song
 */
public class CategoryFragment extends BaseFragment<CategoryPresenter> implements CategoryContract.View, CategoryAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.rvKnowledgeSystems)
    RecyclerView mRvKnowledgeSystems;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private CategoryAdapter mCategoryAdapter;



    @Override
    protected void onFirstUserVisible() {
        /**请求数据*/
        mPresenter.loadCategoryData();
    }

    @Override
    protected void onUserVisible() {

    }

    @Override
    protected void onUserInvisible() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.category_fragment;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.blueStatus);
        mCategoryAdapter = new CategoryAdapter(R.layout.category_knowledge_recycle_item,null);
        /**设置RecyclerView*/
        mRvKnowledgeSystems.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvKnowledgeSystems.setAdapter(mCategoryAdapter);

        /**设置事件监听*/
        mCategoryAdapter.setOnItemClickListener(this);
        mSwipeRefreshLayout.setOnRefreshListener(this);


    }

    @Override
    public void onEventComing(EventBusDto eventCenter) {

    }


    @Override
    public void setCategory(List<Category> categories) {
        mCategoryAdapter.setNewData(categories);
        mSwipeRefreshLayout.setRefreshing(false);
    }


    @Override
    public void showLoading() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void showFail(String message) {
        mSwipeRefreshLayout.setRefreshing(false);
    }


    @Override
    public void onRefresh() {
        mPresenter.refresh();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Intent intent = new Intent(mActivity, ArticleTypeActivity.class);
        intent.putExtra(HttpConstant.CONTENT_TITLE_KEY, mCategoryAdapter.getItem(position).getName());
        List<Category.ChildrenBean> children = mCategoryAdapter.getItem(position).getChildren();
        intent.putParcelableArrayListExtra(HttpConstant.CONTENT_CHILDREN_DATA_KEY, (ArrayList<? extends Parcelable>) children);
        intent.putExtra(HttpConstant.CONTENT_OPEN_FLAG, "1");

        mActivity.startActivity(intent);
    }
}
