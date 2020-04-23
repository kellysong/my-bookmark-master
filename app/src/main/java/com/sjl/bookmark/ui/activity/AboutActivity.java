package com.sjl.bookmark.ui.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.sjl.bookmark.R;
import com.sjl.bookmark.kotlin.language.I18nUtils;
import com.sjl.bookmark.ui.contract.AboutContract;
import com.sjl.bookmark.ui.presenter.AboutPresenter;
import com.sjl.core.mvp.BaseActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 关于页面
 */
public class AboutActivity extends BaseActivity<AboutPresenter> implements AboutContract.View {

    @BindView(R.id.common_toolbar)
    Toolbar mToolBar;
    @BindView(R.id.tv_appVersion)
    TextView mAppVersion;
    @BindView(R.id.tv_mail)
    TextView mMail;
    @BindView(R.id.tv_csdn)
    TextView mCsdn;
    @BindView(R.id.tv_github)
    TextView mGithub;
    @BindView(R.id.tv_copyright)
    TextView mCopyright;

    @Override
    protected int getLayoutId() {
        return R.layout.about_activity;
    }

    @Override
    public void initView() {
        mMail.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG);
        mCsdn.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG);
        mGithub.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG);
    }

    @Override
    public void initListener() {
        bindingToolbar(mToolBar, I18nUtils.getString(R.string.title_about));
    }

    @Override
    public void initData() {
        EventBus.getDefault().register(this);
        mPresenter.getCurrentVersion();
        Calendar date = Calendar.getInstance();
        String year = String.valueOf(date.get(Calendar.YEAR));
        mCopyright.setText(getResources().getString(R.string.str_copyright, year));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void showCurrentVersion(String version) {
        mAppVersion.setText("V"+version);
    }

    @OnClick({R.id.tv_mail,R.id.tv_csdn,R.id.tv_github})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_mail:
                sendMail();
                break;
            case R.id.tv_csdn:
                openUrl(mCsdn.getText().toString());
                break;
            case R.id.tv_github:
                openUrl(mGithub.getText().toString());
                break;
            default:
                break;
        }
    }

    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);//"android.intent.action.VIEW"
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    private void sendMail() {
        Intent email = new Intent(Intent.ACTION_SEND);
        email.setType("message/rfc822");
        email.putExtra(Intent.EXTRA_EMAIL, new String[] {"kelly168163@163.com"});
        email.putExtra(Intent.EXTRA_SUBJECT, "对Google书签的反馈");
        email.putExtra(Intent.EXTRA_TEXT, "请写出你对Google书签的建议...");
        startActivity(Intent.createChooser(email, "选择邮箱客户端"));
    }
}
