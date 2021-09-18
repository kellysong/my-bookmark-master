package com.sjl.bookmark.ui.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sjl.bookmark.R;
import com.sjl.bookmark.kotlin.language.I18nUtils;
import com.sjl.core.mvp.BaseActivity;

import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 个性签名
 *
 * @author Kelly
 * @version 1.0.0
 * @filename PersonalityActivity.java
 * @time 2018/11/29 10:00
 * @copyright(C) 2018 song
 */
public class PersonalityActivity extends BaseActivity {

    @BindView(R.id.common_toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_personality)
    EditText etPersonality;
    @BindView(R.id.tv_msg)
    TextView tvMsg;
    private static final int MAX_WORD = 120;

    @Override
    protected int getLayoutId() {
        return R.layout.personality_activity;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {
        bindingToolbar(toolbar,I18nUtils.getString(R.string.title_signature));
        etPersonality.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 120) {
                    Toast.makeText(PersonalityActivity.this, R.string.word_full, Toast.LENGTH_SHORT).show();
                    etPersonality.setText(etPersonality.getText().toString().substring(0, MAX_WORD));
                } else {
                    tvMsg.setText(getString(R.string.input_hint,(MAX_WORD - etPersonality.getText().toString().length())));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void initData() {
        String personality = getIntent().getStringExtra("personality");
        if (!TextUtils.isEmpty(personality)) {
            etPersonality.setText(personality);
            tvMsg.setText(getString(R.string.input_hint,(MAX_WORD - etPersonality.getText().toString().length())));
        }
    }

    @OnClick(R.id.btn_commit)
    public void onClick() {
        if (TextUtils.isEmpty(etPersonality.getText().toString())) {
            Toast.makeText(this, R.string.input_empty_hint, Toast.LENGTH_SHORT).show();
        } else {
            setResult(RESULT_OK, new Intent().putExtra("personality", etPersonality.getText().toString()));
            finish();
        }
    }
}
