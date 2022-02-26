package com.sjl.bookmark.ui.presenter

import android.content.Intent
import android.os.Build
import android.text.TextUtils
import android.util.ArrayMap
import androidx.annotation.RequiresApi
import com.sjl.bookmark.R
import com.sjl.bookmark.app.AppConstant
import com.sjl.bookmark.dao.impl.AccountService
import com.sjl.bookmark.entity.table.Account
import com.sjl.bookmark.kotlin.language.I18nUtils
import com.sjl.bookmark.ui.contract.AccountEditContract
import com.sjl.core.util.security.DESUtils

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename AccountEditPresenter.java
 * @time 2018/3/8 14:45
 * @copyright(C) 2018 song
 */
class AccountEditPresenter : AccountEditContract.Presenter() {
    private val accountTypes: List<String> = listOf(
        I18nUtils.getString(R.string.account_enum_security),
        I18nUtils.getString(R.string.account_enum_entertainment),
        I18nUtils.getString(R.string.account_enum_social),
        I18nUtils.getString(R.string.account_enum_development),
        I18nUtils.getString(R.string.account_enum_other)
    )
    private val accountStates: List<String> = listOf(
        I18nUtils.getString(R.string.account_in_use),
        I18nUtils.getString(R.string.account_idle),
        I18nUtils.getString(R.string.account_invalid)
    )
    private var createMode: Int = 0
    private var mGodInfo: Account? = null

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun init(intent: Intent) {
        val data: ArrayMap<String, List<String>> = ArrayMap()
        data.put("accountType", accountTypes)
        data.put("accountState", accountStates)
        mView.initSpinner(data)
        createMode = intent.getIntExtra("CREATE_MODE", 1)
        when (createMode) {
            0 -> {
                // 密码类型
                val accountId: Long = intent.getLongExtra("accountId", 0)
                mGodInfo = AccountService.getInstance(mContext).loadAccount(accountId)
                mView.initViewModel(mGodInfo)
            }
            1 -> {
                val position: Int = intent.getIntExtra("position", 0)
                mView.initCreateModel(position)
            }
        }
    }

    /**
     * 新增或者修改账号信息
     *
     * @param account
     * @return 0修改, 1新增
     */
    override fun saveAccount(account: Account): Long {
        var temp: Int = 0
        when (createMode) {
            0 -> {
                account.id = mGodInfo!!.id
                account.accountTitle = DESUtils.encodeDESToBase64(
                    AppConstant.DES_ENCRYPTKEY,
                    account.accountTitle
                )
                account.username = DESUtils.encodeDESToBase64(
                    AppConstant.DES_ENCRYPTKEY,
                    account.username
                )
                account.password = DESUtils.encodeDESToBase64(
                    AppConstant.DES_ENCRYPTKEY,
                    account.password
                )
                if (!TextUtils.isEmpty(account.email)) {
                    account.email = DESUtils.encodeDESToBase64(
                        AppConstant.DES_ENCRYPTKEY,
                        account.email
                    )
                }
                if (!TextUtils.isEmpty(account.phone)) {
                    account.phone = DESUtils.encodeDESToBase64(
                        AppConstant.DES_ENCRYPTKEY,
                        account.phone
                    )
                }
                temp = 0
            }
            1 -> {
                temp = 1
                account.accountTitle = DESUtils.encodeDESToBase64(
                    AppConstant.DES_ENCRYPTKEY,
                    account.accountTitle
                )
                account.username = DESUtils.encodeDESToBase64(
                    AppConstant.DES_ENCRYPTKEY,
                    account.username
                )
                account.password = DESUtils.encodeDESToBase64(
                    AppConstant.DES_ENCRYPTKEY,
                    account.password
                )
                if (!TextUtils.isEmpty(account.email)) {
                    account.email = DESUtils.encodeDESToBase64(
                        AppConstant.DES_ENCRYPTKEY,
                        account.email
                    )
                }
                if (!TextUtils.isEmpty(account.phone)) {
                    account.phone = DESUtils.encodeDESToBase64(
                        AppConstant.DES_ENCRYPTKEY,
                        account.phone
                    )
                }
            }
        }
        AccountService.getInstance(mContext).saveAccount(account)
        return temp.toLong()
    }

    /**
     * 删除账号信息
     */
    override fun deleteAccount() {
        AccountService.getInstance(mContext).deleteAccount(mGodInfo)
    }
}