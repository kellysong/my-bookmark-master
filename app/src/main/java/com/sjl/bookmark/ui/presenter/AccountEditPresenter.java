package com.sjl.bookmark.ui.presenter;


import android.content.Intent;
import android.text.TextUtils;
import android.util.ArrayMap;

import com.sjl.bookmark.R;
import com.sjl.bookmark.app.AppConstant;
import com.sjl.bookmark.dao.impl.AccountService;
import com.sjl.bookmark.entity.table.Account;
import com.sjl.bookmark.kotlin.language.I18nUtils;
import com.sjl.bookmark.ui.contract.AccountEditContract;
import com.sjl.core.util.security.DESUtils;

import java.util.Arrays;
import java.util.List;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename AccountEditPresenter.java
 * @time 2018/3/8 14:45
 * @copyright(C) 2018 song
 */
public class AccountEditPresenter extends AccountEditContract.Presenter {
    private List<String> accountTypes = Arrays.asList(I18nUtils.getString(R.string.account_enum_security), I18nUtils.getString(R.string.account_enum_entertainment),
            I18nUtils.getString(R.string.account_enum_social), I18nUtils.getString(R.string.account_enum_development),
            I18nUtils.getString(R.string.account_enum_other));
    private List<String> accountStates = Arrays.asList(I18nUtils.getString(R.string.account_in_use), I18nUtils.getString(R.string.account_idle), I18nUtils.getString(R.string.account_invalid));

    private int createMode;
    private Account mGodInfo;

    @Override
    public void init(Intent intent) {
        ArrayMap<String, List<String>> data = new ArrayMap<String, List<String>>();
        data.put("accountType", accountTypes);
        data.put("accountState", accountStates);
        mView.initSpinner(data);

        createMode = intent.getIntExtra("CREATE_MODE", 1);
        switch (createMode) {
            case 0:// 查看、修改、删除
                // 密码类型
                long accountId = intent.getLongExtra("accountId", 0);
                mGodInfo = AccountService.getInstance(mContext).loadAccount(accountId);
                mView.initViewModel(mGodInfo);
                break;
            case 1:// 添加
                int position = intent.getIntExtra("position", 0);
                mView.initCreateModel(position);
                break;
        }
    }


    /**
     * 新增或者修改账号信息
     *
     * @param account
     * @return 0修改, 1新增
     */
    @Override
    public long saveAccount(Account account) {
        int temp = 0;
        switch (createMode) {
            case 0://修改
                account.setId(mGodInfo.getId());
                account.setAccountTitle(DESUtils.encodeDESToBase64(AppConstant.DES_ENCRYPTKEY, account.getAccountTitle()));
                account.setUsername(DESUtils.encodeDESToBase64(AppConstant.DES_ENCRYPTKEY, account.getUsername()));
                account.setPassword(DESUtils.encodeDESToBase64(AppConstant.DES_ENCRYPTKEY, account.getPassword()));
                if (!TextUtils.isEmpty(account.getEmail())) {
                    account.setEmail(DESUtils.encodeDESToBase64(AppConstant.DES_ENCRYPTKEY, account.getEmail()));
                }
                if (!TextUtils.isEmpty(account.getPhone())) {
                    account.setPhone(DESUtils.encodeDESToBase64(AppConstant.DES_ENCRYPTKEY, account.getPhone()));
                }
                temp = 0;
                break;
            case 1:// 添加
                temp = 1;
                account.setAccountTitle(DESUtils.encodeDESToBase64(AppConstant.DES_ENCRYPTKEY, account.getAccountTitle()));
                account.setUsername(DESUtils.encodeDESToBase64(AppConstant.DES_ENCRYPTKEY, account.getUsername()));
                account.setPassword(DESUtils.encodeDESToBase64(AppConstant.DES_ENCRYPTKEY, account.getPassword()));
                if (!TextUtils.isEmpty(account.getEmail())) {
                    account.setEmail(DESUtils.encodeDESToBase64(AppConstant.DES_ENCRYPTKEY, account.getEmail()));
                }
                if (!TextUtils.isEmpty(account.getPhone())) {
                    account.setPhone(DESUtils.encodeDESToBase64(AppConstant.DES_ENCRYPTKEY, account.getPhone()));
                }

                break;
        }
        AccountService.getInstance(mContext).saveAccount(account);
        return temp;
    }

    /**
     * 删除账号信息
     */
    @Override
    public void deleteAccount() {
        AccountService.getInstance(mContext).deleteAccount(mGodInfo);
    }


}
