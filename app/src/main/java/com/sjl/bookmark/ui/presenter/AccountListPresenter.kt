package com.sjl.bookmark.ui.presenter

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding2.view.RxView
import com.lid.lib.LabelTextView
import com.sjl.bookmark.R
import com.sjl.bookmark.app.AppConstant
import com.sjl.bookmark.app.MyApplication
import com.sjl.bookmark.dao.impl.AccountService
import com.sjl.bookmark.entity.table.Account
import com.sjl.bookmark.kotlin.language.I18nUtils
import com.sjl.bookmark.ui.activity.AccountEditActivity
import com.sjl.bookmark.ui.contract.AccountListContract
import com.sjl.core.entity.EventBusDto
import com.sjl.core.util.PreferencesHelper
import com.sjl.core.util.datetime.TimeUtils
import com.sjl.core.util.log.LogUtils
import com.sjl.core.util.security.DESUtils
import com.zhy.adapter.recyclerview.CommonAdapter
import com.zhy.adapter.recyclerview.base.ViewHolder
import io.reactivex.functions.Consumer
import java.util.concurrent.TimeUnit

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename AccountListPresenter.java
 * @time 2018/3/8 14:45
 * @copyright(C) 2018 song
 */
class AccountListPresenter : AccountListContract.Presenter() {
    private lateinit var mAdapter: AccountListAdapter
    private var accounts: List<Account>? = null
    private var position //0在用，1闲置，2作废，和fragment对应
            : Int = 0
    private val isOpenShow: Boolean
    override fun onFirstUserVisible() { //只触发一次
        LogUtils.i("onFirstUserVisible")
        accounts = queryAccount()
        if (null != accounts && accounts!!.size > 0) {
            mView.hideEmptyView()
        } else {
            mView.showEmptyView()
        }
        mAdapter = AccountListAdapter(mContext, R.layout.accountlist_recycle_item, accounts)
        mView.initRecycler(LinearLayoutManager(mContext), mAdapter)
    }

    override fun onUserVisible() { //以后可见加载，需要实时更新在这里控制
        LogUtils.i("onUserVisible")
        accounts = queryAccount()
        if (null != accounts && accounts!!.size > 0) {
            mAdapter.refreshData(accounts)
            mView.hideEmptyView()
        } else {
            mView.showEmptyView()
        }
    }

    override fun setPosition(position: Int) {
        this.position = position //当前fragment索引
    }

    /**
     * 下拉刷新
     */
    override fun pullRefreshDown() {
        onUserVisible()
    }

    private inner class AccountListAdapter constructor(
        context: Context?,
        layoutId: Int,
        datas: List<Account>?
    ) : CommonAdapter<Account>(context, layoutId, datas) {
        override fun convert(holder: ViewHolder, account: Account, position: Int) {
            holder.setText(
                R.id.tv_title,
                DESUtils.decryptBase64DES(AppConstant.DES_ENCRYPTKEY, account.accountTitle)
            )
            val accountNo: String = I18nUtils.getString(R.string.account_number)
            val pwd: String = I18nUtils.getString(R.string.password)
            holder.setText(R.id.tv_account_no, accountNo + ":")
                .setText(R.id.tv_account_pwd, pwd + ":")
            holder.setText(
                R.id.tv_username,
                DESUtils.decryptBase64DES(AppConstant.DES_ENCRYPTKEY, account.username)
            )
            if (isOpenShow) { //密码可见
                holder.setText(
                    R.id.tv_password,
                    DESUtils.decryptBase64DES(AppConstant.DES_ENCRYPTKEY, account.password)
                )
            } else {
                holder.setText(R.id.tv_password, "*********")
            }
            holder.setText(R.id.tv_date, TimeUtils.getRangeByDate(account.date))
            //"安全", "娱乐", "社会", "开发", "其它"
            var labelMsg: String? = ""
            when (account.accountType) {
                0 -> labelMsg = I18nUtils.getString(R.string.account_enum_security)
                1 -> labelMsg = I18nUtils.getString(R.string.account_enum_entertainment)
                2 -> labelMsg = I18nUtils.getString(R.string.account_enum_social)
                3 -> labelMsg = I18nUtils.getString(R.string.account_enum_development)
                4 -> labelMsg = I18nUtils.getString(R.string.account_enum_other)
                else -> {}
            }
            val labelTextView: LabelTextView = holder.getView<View>(R.id.tv_state) as LabelTextView
            labelTextView.labelText = labelMsg
            preventRepeatedClick(
                holder.getView(R.id.mrl_account_content),
                object : View.OnClickListener {
                    override fun onClick(v: View) {
                        mView.readGo(
                            AccountEditActivity::class.java,
                            AppConstant.SETTING.VIEW_MODE,
                            account
                        )
                    }
                })
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
        fun refreshData(accounts: List<Account>?) {
            datas.clear()
            datas.addAll((accounts)!!)
            notifyDataSetChanged()
        }
    }

    /**
     * 防止重复点击
     *
     * @param target   目标view
     * @param listener 监听器
     */
    private fun preventRepeatedClick(target: View, listener: View.OnClickListener) {
        RxView.clicks(target).throttleFirst(1, TimeUnit.SECONDS).`as`(bindLifecycle())
            .subscribe(object : Consumer<Any?> {
                @Throws(Exception::class)
                override fun accept(o: Any?) {
                    listener.onClick(target)
                } //相当于onNext
            })
    }

    /***
     * 由于初始化了三个fragment，每个fragment里都注册了消息接受，故会触发三次事件
     * @param eventBusDto
     */
    override fun onEventComing(eventBusDto: EventBusDto<*>) {
        if (eventBusDto.eventCode == AppConstant.ACCOUNT_REFRESH_EVENT_CODE && eventBusDto.position == position) {
            LogUtils.i("正在刷新数据")
            LogUtils.i("eventBusDto的position=" + eventBusDto.position + ",position=" + position)
            val data: Boolean = eventBusDto.data as Boolean
            if (data) {
                accounts = queryAccount()
                if (null != accounts && accounts!!.size > 0) {
                    mAdapter.refreshData(accounts)
                    mView.hideEmptyView()
                } else {
                    mView.showEmptyView()
                }
            }
        }
    }

    private fun queryAccount(): List<Account> {
        return AccountService.getInstance(mContext).queryAccount(
            " where ACCOUNT_STATE =  ? order By date desc",
            *arrayOf(position.toString())
        )
    }

    init {
        val preferencesHelper: PreferencesHelper =
            PreferencesHelper.getInstance(MyApplication.getContext())
        isOpenShow =
            preferencesHelper.get(AppConstant.SETTING.OPEN_PASS_WORD_SHOW, false) as Boolean
    }
}