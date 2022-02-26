package com.sjl.bookmark.ui.contract;

import android.content.Intent;
import android.util.ArrayMap;

import com.sjl.bookmark.entity.table.Account;
import com.sjl.core.mvp.BaseContract;
import com.sjl.core.mvp.BasePresenter;

import java.util.List;

/**
 * 改良mvp模式
 *
 * @author Kelly
 * @version 1.0.0
 * @filename AccountEditContract.java
 * @time 2018/11/26 10:16
 * @copyright(C) 2018 song
 */
public interface AccountEditContract {
    interface View extends BaseContract.IBaseView {
        void initSpinner(ArrayMap<String, List<String>> data);

        void initCreateModel(int position);

        void initViewModel(Account account);
    }

    abstract class Presenter extends BasePresenter<View> {
        public abstract void init(Intent intent);

        /**
         * 新增或者修改账号信息
         *
         * @param account
         * @return 0修改, 1新增
         */
        public abstract long saveAccount(Account account);

        /**
         * 删除账号信息
         */
        public abstract void deleteAccount();
    }
}
