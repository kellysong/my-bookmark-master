package com.sjl.bookmark.ui.activity

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.view.View
import butterknife.OnClick
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.interfaces.OnInputConfirmListener
import com.sjl.bookmark.R
import com.sjl.bookmark.app.AppConstant.SETTING_PWD
import com.sjl.bookmark.dao.impl.BrowseTrackDaoImpl
import com.sjl.bookmark.kotlin.language.I18nUtils
import com.sjl.bookmark.ui.contract.AboutContract
import com.sjl.bookmark.ui.presenter.AboutPresenter
import com.sjl.core.mvp.BaseActivity
import kotlinx.android.synthetic.main.about_activity.*
import kotlinx.android.synthetic.main.browse_history_activity.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

/**
 * 关于页面
 */
class AboutActivity : BaseActivity<AboutPresenter>(), AboutContract.View {

    override fun getLayoutId(): Int {
        return R.layout.about_activity
    }

    public override fun initView() {
        tv_mail.paint.flags = Paint.UNDERLINE_TEXT_FLAG
        tv_csdn.paint.flags = Paint.UNDERLINE_TEXT_FLAG
        tv_github.paint.flags = Paint.UNDERLINE_TEXT_FLAG
    }

    public override fun initListener() {
        bindingToolbar(common_toolbar, I18nUtils.getString(R.string.title_about))
        tv_appVersion.setOnLongClickListener {
            XPopup.Builder(mContext)
                .isDestroyOnDismiss(true)
                .asInputConfirm(getString(R.string.please_enter_password),null, OnInputConfirmListener(){
                    if (SETTING_PWD == it){
                        openActivity(SettingHideActivity::class.java)
                    }
                }).show()

            false
        }
    }

    public override fun initData() {
        EventBus.getDefault().register(this)
        mPresenter.getCurrentVersion()
        val date = Calendar.getInstance()
        val year = date[Calendar.YEAR].toString()
        tv_copyright.text = resources.getString(R.string.str_copyright, year)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun showCurrentVersion(version: String) {
        tv_appVersion.text = "V$version"
    }

    @OnClick(R.id.tv_mail, R.id.tv_csdn, R.id.tv_github)
    fun onClick(view: View) {
        when (view.id) {
            R.id.tv_mail -> sendMail()
            R.id.tv_csdn -> openUrl(tv_csdn.text.toString().trim())
            R.id.tv_github -> openUrl(tv_github.text.toString().trim())
            else -> {}
        }
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW) //"android.intent.action.VIEW"
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    private fun sendMail() {
        val email = Intent(Intent.ACTION_SEND)
        email.type = "message/rfc822"
        email.putExtra(Intent.EXTRA_EMAIL, arrayOf("kelly168163@163.com"))
        email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback))
        email.putExtra(Intent.EXTRA_TEXT, getString(R.string.suggestions))
        startActivity(Intent.createChooser(email, getString(R.string.select_email_client)))
    }
}