package com.sjl.bookmark.ui.activity

import android.content.DialogInterface
import android.os.Build
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.ArrayMap
import android.view.*
import android.view.View.OnFocusChangeListener
import android.view.View.OnTouchListener
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.CompoundButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.sjl.bookmark.R
import com.sjl.bookmark.app.AppConstant
import com.sjl.bookmark.entity.table.Account
import com.sjl.bookmark.kotlin.language.I18nUtils
import com.sjl.bookmark.ui.activity.AccountIndexActivity
import com.sjl.bookmark.ui.contract.AccountEditContract
import com.sjl.bookmark.ui.fragment.AccountListFragment
import com.sjl.bookmark.ui.presenter.AccountEditPresenter
import com.sjl.core.mvp.BaseActivity
import com.sjl.core.util.ValidatorUtils
import com.sjl.core.util.ViewUtils
import com.sjl.core.util.datetime.TimeUtils
import com.sjl.core.util.log.LogUtils
import com.sjl.core.util.security.DESUtils
import kotlinx.android.synthetic.main.account_edit_activity.*
import kotlinx.android.synthetic.main.toolbar_scroll.*
import java.util.*

/**
 * 账号保存、修改和删除Activity
 *
 * @author Kelly
 * @version 1.0.0
 * @filename AccountEditActivity.java
 * @time 2018/3/7 10:42
 * @copyright(C) 2018 song
 */
class AccountEditActivity : BaseActivity<AccountEditPresenter>(),
    AccountEditContract.View, TextWatcher, OnFocusChangeListener, View.OnClickListener {

    private var menuItem: MenuItem? = null
    private var mAccountTypePosition: Int = 0
    private var mAccountStatePosition //账号状态索引
            : Int = 0

    override fun getLayoutId(): Int {
        return R.layout.account_edit_activity
    }

    override fun initView() {}
    override fun initListener() {
        bindingToolbar(common_toolbar, I18nUtils.getString(R.string.text_add))
        //修复ScrollView中EditText导致自动滚动问题
        sv_content.descendantFocusability = ViewGroup.FOCUS_BEFORE_DESCENDANTS
        sv_content.isFocusable = true
        sv_content.isFocusableInTouchMode = true
        sv_content.setOnTouchListener(object : OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                v.requestFocusFromTouch()
                return false
            }
        })
        np_type.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                mAccountTypePosition = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })
        np_state.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                mAccountStatePosition = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })
        cb_eye.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
                if (isChecked) {
                    met_password.transformationMethod = PasswordTransformationMethod
                        .getInstance()
                } else {
                    met_password.transformationMethod = HideReturnsTransformationMethod
                        .getInstance()
                }
                met_password.setSelection(met_password.text.toString().length)
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun initData() {
        mPresenter.init(intent)
    }

    /**
     * 每次在display Menu之前，都会去调用，只要按一次Menu按鍵，就会调用一次。所以可以在这里动态的改变menu。
     * @param menu
     * @return
     */
    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menuItem = menu.getItem(0)
        setItemMenuVisible(false)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_edit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.done -> {
                val titleName: String = met_title.text.toString().trim { it <= ' ' }
                val userName: String = met_userName.text.toString().trim { it <= ' ' }
                val passWord: String = met_password.text.toString().trim { it <= ' ' }
                val userEmail: String = met_userEmail.text.toString().trim { it <= ' ' }
                val userPhone: String = met_userPhone.text.toString().trim { it <= ' ' }
                val remark: String = met_remark.text.toString().trim { it <= ' ' }
                if (!TextUtils.isEmpty(userEmail) && !ValidatorUtils.isEmail(userEmail)) {
                    Toast.makeText(this, R.string.mailbox_format_error, Toast.LENGTH_SHORT).show()
                    return false
                }
                if (!TextUtils.isEmpty(userPhone) && !ValidatorUtils.isMobile(userPhone)) {
                    Toast.makeText(this, R.string.phone_format_error, Toast.LENGTH_SHORT).show()
                    return false
                }
                val result: Long = mPresenter!!.saveAccount(
                    Account(
                        null,
                        mAccountTypePosition,
                        mAccountStatePosition,
                        titleName,
                        userName,
                        passWord,
                        userEmail,
                        userPhone,
                        remark,
                        Date()
                    )
                )
                if (result == 0L) {
                    ViewUtils.hideKeyBoard(this, met_title)
                    closeActivity(AccountListFragment.EDIT_SUCCESS) //触发fragment里面的onActivityResult
                } else if (result == 1L) { //触发activity里面的onActivityResult
                    ViewUtils.hideKeyBoard(this, met_title)
                    closeActivity(AccountIndexActivity.Companion.ADD_SUCCESS)
                } else {
                    val error: String = getString(R.string.unknown_error)
                    Toast.makeText(this, error + ",result=" + result, Toast.LENGTH_SHORT).show()
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun initSpinner(data: ArrayMap<String, List<String>>) {
        np_type!!.attachDataSource(data.get("accountType"))
        np_state!!.attachDataSource(data.get("accountState"))
    }

    override fun initCreateModel(position: Int) {
        mAccountStatePosition = position
        LogUtils.i("mAccountStatePosition=" + mAccountStatePosition)
        np_state.setBackgroundColor(resources.getColor(R.color.cl_ns_forbid_bg))
        np_state.selectedIndex = mAccountStatePosition
        np_state.isEnabled = false
        addTextChangedListener()
    }

    override fun initViewModel(account: Account?) {
        if (account == null) {
            LogUtils.w("account为空,初始化账号信息失败")
            return
        }
        common_toolbar.title = I18nUtils.getString(R.string.text_detail)
        timeTextView.visibility = View.VISIBLE
        timeTextView.text = getString(R.string.last_update_date) + TimeUtils.formatDateToStr(
            account.date,
            TimeUtils.DATE_FORMAT_1
        )
        ViewUtils.hideKeyBoard(this, met_title)
        met_title.setText(
            DESUtils.decryptBase64DES(
                AppConstant.DES_ENCRYPTKEY,
                account.accountTitle
            )
        )
        met_userName.setText(
            DESUtils.decryptBase64DES(
                AppConstant.DES_ENCRYPTKEY,
                account.username
            )
        )
        met_password.setText(
            DESUtils.decryptBase64DES(
                AppConstant.DES_ENCRYPTKEY,
                account.password
            )
        )
        if (!TextUtils.isEmpty(account.email)) {
            met_userEmail.setText(
                DESUtils.decryptBase64DES(
                    AppConstant.DES_ENCRYPTKEY,
                    account.email
                )
            )
        }
        if (!TextUtils.isEmpty(account.phone)) {
            met_userPhone.setText(
                DESUtils.decryptBase64DES(
                    AppConstant.DES_ENCRYPTKEY,
                    account.phone
                )
            )
        }
        np_type.selectedIndex = account.accountType
        mAccountStatePosition = account.accountState
        np_state.selectedIndex = account.accountState
        met_password.transformationMethod = HideReturnsTransformationMethod
            .getInstance()
        cb_eye.isChecked = false
        addFocusChangeListener()
        addTextChangedListener()
        btn_delete.visibility = View.VISIBLE
        btn_delete.setOnClickListener(this)
    }

    private fun addTextChangedListener() {
        met_title.addTextChangedListener(this)
        met_userName.addTextChangedListener(this)
        met_password.addTextChangedListener(this)
        met_userEmail.addTextChangedListener(this)
        met_userPhone.addTextChangedListener(this)
        np_type.addTextChangedListener(this)
        np_state.addTextChangedListener(this)
    }

    private fun addFocusChangeListener() {
        met_title.onFocusChangeListener = this
        met_userName.onFocusChangeListener = this
        met_password.onFocusChangeListener = this
        met_userEmail.onFocusChangeListener = this
        met_userPhone.onFocusChangeListener = this
        np_type.onFocusChangeListener = this
        np_state.onFocusChangeListener = this
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        val titleName: String = met_title.text.toString().trim { it <= ' ' }
        val userName: String = met_userName.text.toString().trim { it <= ' ' }
        val passWord: String = met_password.text.toString().trim { it <= ' ' }
        if (!TextUtils.isEmpty(passWord) && !TextUtils.isEmpty(titleName) && !TextUtils.isEmpty(
                userName
            )
        ) {
            setItemMenuVisible(true)
        } else {
            setItemMenuVisible(false)
        }
    }

    override fun afterTextChanged(s: Editable) {}
    override fun onFocusChange(v: View, hasFocus: Boolean) {
        if (hasFocus) {
            common_toolbar!!.setTitle(R.string.edit)
        }
    }

    private fun setItemMenuVisible(visible: Boolean) {
        if (menuItem != null) {
            menuItem!!.isVisible = visible
        } else {
            LogUtils.i("menuItem为空")
        }
    }

    override fun onClick(v: View) {
        val id: Int = v.id
        if (id == R.id.btn_delete) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.nb_common_tip)
            builder.setMessage(R.string.account_delete_hint)
            builder.setPositiveButton(R.string.sure, object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, which: Int) {
                    mPresenter!!.deleteAccount()
                    setResult(AccountIndexActivity.Companion.ADD_SUCCESS)
                    finish()
                }
            })
            builder.setNegativeButton(R.string.cancel, object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, which: Int) {}
            })
            builder.show()
        }
    }

    private fun closeActivity(resultCode: Int) {
        setResult(resultCode)
        finish()
    }
}