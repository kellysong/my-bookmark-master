package com.sjl.bookmark.ui.activity;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import com.sjl.bookmark.R;
import com.sjl.bookmark.kotlin.language.LanguageManager;
import com.sjl.bookmark.widget.MarqueeView;
import com.sjl.core.mvp.BaseActivity;

import butterknife.BindView;

/**
 * 弹幕LED显示
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BarrageShowActivity
 * @time 2021/5/9 13:20
 * @copyright(C) 2021 song
 */
public class BarrageShowActivity extends BaseActivity {
    @BindView(R.id.et_content)
    EditText et_content;
    @BindView(R.id.tv_barrage)
    MarqueeView tv_barrage;

    @Override
    protected int getLayoutId() {
        return R.layout.barrage_show_activity;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void attachBaseContext(Context newBase) {//横屏要加这个才行，未知原因
        super.attachBaseContext(LanguageManager.INSTANCE.getLocalContext(newBase));
    }

    @Override
    protected void initListener() {
        et_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String trim = et_content.getText().toString().trim();
                if (TextUtils.isEmpty(trim)){
                    return;
                }
                tv_barrage.setText(trim);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void initData() {

    }

}
