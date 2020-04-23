package com.sjl.bookmark.ui.contract;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.sjl.bookmark.entity.table.Account;
import com.sjl.core.entity.EventBusDto;
import com.sjl.core.mvp.BaseContract;
import com.sjl.core.mvp.BasePresenter;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename AccountListContract.java
 * @time 2018/11/26 14:53
 * @copyright(C) 2018 song
 */
public interface AccountListContract {
    interface View extends BaseContract.IBaseView {
        void initRecycler(LinearLayoutManager linearLayoutManager, RecyclerView.Adapter adapter);

        void readGo(Class clazz, int operateFlag, Account account);

        void hideEmptyView();

        void showEmptyView();
    }


    abstract class Presenter extends BasePresenter<View> {

        public abstract void setPosition(int position);

        /**
         * 下拉刷新
         */
        public abstract void pullRefreshDown();

        public abstract void onEventComing(EventBusDto eventBusDto);
    }
}
