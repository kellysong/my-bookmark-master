package com.sjl.bookmark.ui.presenter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.jakewharton.rxbinding2.view.RxView;
import com.lid.lib.LabelTextView;
import com.lid.lib.LabelView;
import com.sjl.bookmark.R;
import com.sjl.bookmark.app.AppConstant;
import com.sjl.bookmark.app.MyApplication;
import com.sjl.bookmark.dao.impl.AccountService;
import com.sjl.bookmark.entity.table.Account;
import com.sjl.bookmark.ui.activity.AccountEditActivity;
import com.sjl.bookmark.ui.contract.AccountListContract;
import com.sjl.core.util.security.DESUtils;
import com.sjl.core.util.datetime.TimeUtils;
import com.sjl.core.entity.EventBusDto;
import com.sjl.core.util.log.LogUtils;
import com.sjl.core.util.PreferencesHelper;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename AccountListPresenter.java
 * @time 2018/3/8 14:45
 * @copyright(C) 2018 song
 */
public class AccountListPresenter extends AccountListContract.Presenter {
    private AccountListAdapter mAdapter;
    private List<Account> accounts;
    private int position;//0在用，1闲置，2作废，和fragment对应
    private boolean isOpenShow;

    public AccountListPresenter() {
        PreferencesHelper preferencesHelper = PreferencesHelper.getInstance(MyApplication.getContext());
        isOpenShow = (Boolean) preferencesHelper.get(AppConstant.SETTING.OPEN_PASS_WORD_SHOW, false);
    }

    @Override
    public void onFirstUserVisible() {//只触发一次
        LogUtils.i("onFirstUserVisible");
        accounts = queryAccount();
        if (null != accounts && accounts.size() > 0) {
            mView.hideEmptyView();
        } else {
            mView.showEmptyView();
        }
        mAdapter = new AccountListAdapter(mContext, R.layout.accountlist_recycle_item, accounts);
        mView.initRecycler(new LinearLayoutManager(mContext), mAdapter);
    }

    @Override
    public void onUserVisible() {//以后可见加载，需要实时更新在这里控制
        LogUtils.i("onUserVisible");
        accounts = queryAccount();
        if (null != accounts && accounts.size() > 0) {
            mAdapter.refreshData(accounts);
            mView.hideEmptyView();
        } else {
            mView.showEmptyView();
        }

    }

    @Override
    public void setPosition(int position) {
        this.position = position;//当前fragment索引
    }

    /**
     * 下拉刷新
     */
    @Override
    public void pullRefreshDown() {
        onUserVisible();
    }


    private class AccountListAdapter extends CommonAdapter<Account> {


        public AccountListAdapter(Context context, int layoutId, List<Account> datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(ViewHolder holder, final Account account, int position) {
            holder.setText(R.id.tv_title, DESUtils.decryptBase64DES(AppConstant.DES_ENCRYPTKEY, account.getAccountTitle()));
            holder.setText(R.id.tv_username, DESUtils.decryptBase64DES(AppConstant.DES_ENCRYPTKEY, account.getUsername()));
            if (isOpenShow) {//密码可见
                holder.setText(R.id.tv_password, DESUtils.decryptBase64DES(AppConstant.DES_ENCRYPTKEY, account.getPassword()));
            } else {
                holder.setText(R.id.tv_password, "*********");
            }

            holder.setText(R.id.tv_date, TimeUtils.getRangeByDate(account.getDate()));
            LabelView label = new LabelView(mContext);
            //"安全", "娱乐", "社会", "开发", "其它"
            String labelMsg = "";
            switch (account.getAccountType()) {
                case 0:
                    labelMsg = "安全";
                    break;
                case 1:
                    labelMsg = "娱乐";
                    break;
                case 2:
                    labelMsg = "社会";
                    break;
                case 3:
                    labelMsg = "开发";
                    break;
                case 4:
                    labelMsg = "其它";
                    break;
                default:
                    break;
            }
            LabelTextView labelTextView = (LabelTextView) holder.getView(R.id.tv_state);

            labelTextView.setLabelText(labelMsg);

            preventRepeatedClick(holder.getView(R.id.mrl_account_content), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mView.readGo(AccountEditActivity.class, AppConstant.SETTING.VIEW_MODE, account);
                }
            });
//            holder.setOnClickListener(R.id.mrl_account_content, new BaseView.OnClickListener() {
//                @Override
//                public void onClick(BaseView v) {
//                    mAccountListView.readGo(AccountEditActivity.class, AppConstant.SETTING.VIEW_MODE,account);
//
//                }
//            });
        }


        /**
         * 刷新数据
         *
         * @param accounts
         */
        public void refreshData(List<Account> accounts) {
            getDatas().clear();
            getDatas().addAll(accounts);
            notifyDataSetChanged();
        }

    }

    /**
     * 防止重复点击
     *
     * @param target   目标view
     * @param listener 监听器
     */
    private void preventRepeatedClick(final View target, final View.OnClickListener listener) {
        RxView.clicks(target).throttleFirst(1, TimeUnit.SECONDS).as(bindLifecycle()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                listener.onClick(target);
            }//相当于onNext

        });

    }


    /***
     * 由于初始化了三个fragment，每个fragment里都注册了消息接受，故会触发三次事件
     * @param eventBusDto
     */
    @Override
    public void onEventComing(EventBusDto eventBusDto) {
        if (eventBusDto.getEventCode() == AppConstant.ACCOUNT_REFRESH_EVENT_CODE && eventBusDto.getPosition() == position) {
            LogUtils.i("正在刷新数据");
            LogUtils.i("eventBusDto的position=" + eventBusDto.getPosition() + ",position=" + position);
            boolean data = (boolean) eventBusDto.getData();
            if (data) {
                accounts = queryAccount();
                if (null != accounts && accounts.size() > 0) {
                    mAdapter.refreshData(accounts);
                    mView.hideEmptyView();
                } else {
                    mView.showEmptyView();
                }
            }
        }
    }


    private List<Account> queryAccount() {
        return AccountService.getInstance(mContext).queryAccount(" where ACCOUNT_STATE =  ? order By date desc", new String[]{String.valueOf(position)});
    }


}
