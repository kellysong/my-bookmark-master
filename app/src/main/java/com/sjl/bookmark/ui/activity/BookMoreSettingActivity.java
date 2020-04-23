package com.sjl.bookmark.ui.activity;

import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.sjl.bookmark.R;
import com.sjl.bookmark.kotlin.language.I18nUtils;
import com.sjl.bookmark.widget.reader.ReadSettingManager;
import com.sjl.core.mvp.BaseActivity;

import butterknife.BindView;

/**
 * 阅读器更多设置
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookMoreSettingActivity.java
 * @time 2018/12/5 11:24
 * @copyright(C) 2018 song
 */
public class BookMoreSettingActivity extends BaseActivity {

    @BindView(R.id.common_toolbar)
    Toolbar mToolBar;

    @BindView(R.id.more_setting_rl_volume)
    RelativeLayout mRlVolume;
    @BindView(R.id.more_setting_sc_volume)
    SwitchCompat mScVolume;
    @BindView(R.id.more_setting_rl_full_screen)
    RelativeLayout mRlFullScreen;
    @BindView(R.id.more_setting_sc_full_screen)
    SwitchCompat mScFullScreen;
    @BindView(R.id.more_setting_rl_convert_type)
    RelativeLayout mRlConvertType;
    @BindView(R.id.more_setting_sc_convert_type)
    Spinner mScConvertType;
    private ReadSettingManager mSettingManager;
    private boolean isVolumeTurnPage;
    private boolean isFullScreen;

    @Override
    protected int getLayoutId() {
        return R.layout.book_more_setting_activity;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {
        bindingToolbar(mToolBar, I18nUtils.getString(R.string.title_read_setting));
        mRlVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isVolumeTurnPage) {
                    isVolumeTurnPage = false;
                } else {
                    isVolumeTurnPage = true;
                }
                mScVolume.setChecked(isVolumeTurnPage);
                mSettingManager.setVolumeTurnPage(isVolumeTurnPage);
            }
        });

        mRlFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFullScreen) {
                    isFullScreen = false;
                } else {
                    isFullScreen = true;
                }
                mScFullScreen.setChecked(isFullScreen);
                mSettingManager.setFullScreen(isFullScreen);
            }
        });
    }

    @Override
    protected void initData() {
        mSettingManager = ReadSettingManager.getInstance();
        isVolumeTurnPage = mSettingManager.isVolumeTurnPage();
        isFullScreen = mSettingManager.isFullScreen();
        int convertType = mSettingManager.getConvertType();
        mScVolume.setChecked(isVolumeTurnPage);
        mScFullScreen.setChecked(isFullScreen);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.conversion_type_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mScConvertType.setAdapter(adapter);
        mScConvertType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view;
                tv.setGravity(Gravity.RIGHT);//设置居中
                mSettingManager.setConvertType(position);//设置语言转换类型
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mScConvertType.setSelection(convertType);
    }
}
