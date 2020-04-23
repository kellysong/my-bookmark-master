package com.sjl.bookmark.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.sjl.bookmark.R;
import com.sjl.bookmark.app.AppConstant;
import com.sjl.bookmark.entity.table.Account;
import com.sjl.bookmark.ui.adapter.RecyclerViewDivider;
import com.sjl.bookmark.ui.contract.AccountListContract;
import com.sjl.bookmark.ui.presenter.AccountListPresenter;
import com.sjl.core.entity.EventBusDto;
import com.sjl.core.mvp.BaseFragment;
import com.sjl.core.util.log.LogUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename AccountListFragment.java
 * @time 2018/3/7 14:24
 * @copyright(C) 2018 song
 */
public class AccountListFragment extends BaseFragment<AccountListPresenter> implements AccountListContract.View {
    private static final int INDEX_FRAGMENT_REQUEST_CODE = 2;
    public static final int EDIT_SUCCESS = 2;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.exception)
    LinearLayout mException;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private int position;
    private Handler mHandler;

    @Override
    protected void onFirstUserVisible() {
        mPresenter.onFirstUserVisible();
    }

    @Override
    protected void onUserVisible() {
        mPresenter.onUserVisible();
    }

    @Override
    protected void onUserInvisible() {

    }


    @Override
    protected int getLayoutId() {
        return R.layout.account_fragment;
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
        position = arguments.getInt("position");//当前fragment索引
        LogUtils.i("position=" + position);
        mPresenter.setPosition(position);
        mHandler = new Handler();

    }

    /**
     * eventbus事件到来
     *
     * @param eventCenter
     */
    @Override
    public void onEventComing(EventBusDto eventCenter) {
        mPresenter.onEventComing(eventCenter);
    }

    @Override
    public void initRecycler(LinearLayoutManager linearLayoutManager, RecyclerView.Adapter adapter) {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.blueStatus);//设置下拉圆圈的颜色
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {//下拉刷新
                LogUtils.i("正在刷新账号列表数据");
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPresenter.pullRefreshDown();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);

            }
        });
        mRecyclerView.addItemDecoration(new RecyclerViewDivider(mActivity, LinearLayoutManager.VERTICAL));
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void readGo(Class clazz, int operateFlag, Account account) {
        Intent intent = new Intent(mActivity, clazz);
        intent.putExtra("CREATE_MODE", operateFlag);
        intent.putExtra("accountId", account.getId());//根据id查询账号明细
        startActivityForResult(intent, INDEX_FRAGMENT_REQUEST_CODE);
    }

    @Override
    public void hideEmptyView() {
        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        mException.setVisibility(View.GONE);


    }

    @Override
    public void showEmptyView() {
        mSwipeRefreshLayout.setVisibility(View.GONE);
        mException.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.i("requestCode=" + requestCode + ",resultCode=" + resultCode);
        if (requestCode == INDEX_FRAGMENT_REQUEST_CODE) {
            if (resultCode == EDIT_SUCCESS) {
                EventBusDto eventBusDto = new EventBusDto(position, AppConstant.ACCOUNT_REFRESH_EVENT_CODE, true);
                EventBus.getDefault().post(eventBusDto);
            }
        }
    }
}
