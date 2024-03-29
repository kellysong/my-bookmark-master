package com.sjl.bookmark.widget.reader;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sjl.bookmark.R;
import com.sjl.bookmark.app.MyApplication;
import com.sjl.bookmark.ui.activity.BookMoreSettingActivity;
import com.sjl.bookmark.ui.activity.BookReadActivity;
import com.sjl.bookmark.util.BrightnessUtils;
import com.sjl.bookmark.widget.reader.bean.PageMode;
import com.sjl.bookmark.widget.reader.bean.PageStyle;
import com.sjl.bookmark.widget.reader.loader.PageLoader;
import com.sjl.core.util.ViewUtils;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 阅读设置dialog
 */

public class ReadSettingDialog extends Dialog {
    private static final String TAG = "ReadSettingDialog";
    private static final int DEFAULT_TEXT_SIZE = 16;

    @BindView(R.id.read_setting_iv_brightness_minus)
    ImageView mIvBrightnessMinus;
    @BindView(R.id.read_setting_sb_brightness)
    SeekBar mSbBrightness;
    @BindView(R.id.read_setting_iv_brightness_plus)
    ImageView mIvBrightnessPlus;
    @BindView(R.id.read_setting_cb_brightness_auto)
    CheckBox mCbBrightnessAuto;
    @BindView(R.id.read_setting_tv_font_minus)
    TextView mTvFontMinus;
    @BindView(R.id.read_setting_tv_font)
    TextView mTvFont;
    @BindView(R.id.read_setting_tv_font_plus)
    TextView mTvFontPlus;
    @BindView(R.id.read_setting_cb_font_default)
    CheckBox mCbFontDefault;
    @BindView(R.id.read_setting_rg_page_mode)
    RadioGroup mRgPageMode;

    @BindView(R.id.read_setting_rb_simulation)
    RadioButton mRbSimulation;
    @BindView(R.id.read_setting_rb_cover)
    RadioButton mRbCover;
    @BindView(R.id.read_setting_rb_slide)
    RadioButton mRbSlide;
    @BindView(R.id.read_setting_rb_scroll)
    RadioButton mRbScroll;
    @BindView(R.id.read_setting_rb_none)
    RadioButton mRbNone;
    @BindView(R.id.read_setting_rv_bg)
    RecyclerView mRvBg;
    @BindView(R.id.read_setting_tv_more)
    TextView mTvMore;
    /************************************/
    private PageStyleAdapter mPageStyleAdapter;
    private ReadSettingManager mSettingManager;
    private PageLoader mPageLoader;
    private Activity mActivity;

    private PageMode mPageMode;
    private PageStyle mPageStyle;

    private int mBrightness;
    private int mTextSize;

    private boolean isBrightnessAuto;
    private boolean isTextDefault;


    public ReadSettingDialog(@NonNull Activity activity, PageLoader mPageLoader) {
        super(activity, R.style.ReadSettingDialog);
        mActivity = activity;
        this.mPageLoader = mPageLoader;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_read_setting);
        ButterKnife.bind(this);
        setUpWindow();
        initData();
        initWidget();
        initClick();
    }

    //设置Dialog显示的位置
    private void setUpWindow() {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);
    }

    private void initData() {
        mSettingManager = ReadSettingManager.getInstance();

        isBrightnessAuto = mSettingManager.isBrightnessAuto();
        mBrightness = mSettingManager.getBrightness();
        mTextSize = mSettingManager.getTextSize();
        isTextDefault = mSettingManager.isDefaultTextSize();
        mPageMode = mSettingManager.getPageMode();
        mPageStyle = mSettingManager.getPageStyle();
    }

    private void initWidget() {
        mSbBrightness.setProgress(mBrightness);
        mTvFont.setText(mTextSize + "");
        mCbBrightnessAuto.setChecked(isBrightnessAuto);
        mCbFontDefault.setChecked(isTextDefault);
        initPageMode();
        //RecyclerView
        setUpAdapter();
    }

    private void setUpAdapter() {
        //主题颜色设置
        Drawable[] drawables = {
                getDrawable(R.color.nb_read_bg_1)
                , getDrawable(R.color.nb_read_bg_2)
                , getDrawable(R.color.nb_read_bg_3)
                , getDrawable(R.color.nb_read_bg_4)
                , getDrawable(R.color.nb_read_bg_5)};

        mPageStyleAdapter = new PageStyleAdapter(mActivity,R.layout.item_read_bg,Arrays.asList(drawables));
        mRvBg.setLayoutManager(new GridLayoutManager(getContext(), 5));
        mPageStyleAdapter.setPageStyleChecked(mPageStyle);//从设置中取页面样式
        mRvBg.setAdapter(mPageStyleAdapter);

    }

    private void initPageMode() {
        switch (mPageMode) {
            case SIMULATION:
                mRbSimulation.setChecked(true);
                break;
            case COVER:
                mRbCover.setChecked(true);
                break;
            case SLIDE:
                mRbSlide.setChecked(true);
                break;
            case NONE:
                mRbNone.setChecked(true);
                break;
            case SCROLL:
                mRbScroll.setChecked(true);
                break;
        }
    }

    private Drawable getDrawable(int drawRes) {
        return ContextCompat.getDrawable(getContext(), drawRes);
    }

    private void initClick() {
        //亮度调节，调暗
        mIvBrightnessMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCbBrightnessAuto.isChecked()) {
                    mCbBrightnessAuto.setChecked(false);
                }
                int progress = mSbBrightness.getProgress() - 1;
                if (progress < 0) return;
                mSbBrightness.setProgress(progress);
                BrightnessUtils.setBrightness(mActivity, progress);
            }
        });
        //亮度调节，调亮
        mIvBrightnessPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCbBrightnessAuto.isChecked()) {
                    mCbBrightnessAuto.setChecked(false);
                }
                int progress = mSbBrightness.getProgress() + 1;
                if (progress > mSbBrightness.getMax()) return;
                mSbBrightness.setProgress(progress);
                BrightnessUtils.setBrightness(mActivity, progress);
                //设置进度
                ReadSettingManager.getInstance().setBrightness(progress);
            }
        });

        mSbBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if (mCbBrightnessAuto.isChecked()) {
                    mCbBrightnessAuto.setChecked(false);
                }
                //设置当前 Activity 的亮度
                BrightnessUtils.setBrightness(mActivity, progress);
                //存储亮度的进度条
                ReadSettingManager.getInstance().setBrightness(progress);
            }
        });
        //屏幕亮度随系统
        mCbBrightnessAuto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //获取屏幕的亮度
                    BrightnessUtils.setBrightness(mActivity, BrightnessUtils.getScreenBrightness(mActivity));
                } else {
                    //获取进度条的亮度
                    BrightnessUtils.setBrightness(mActivity, mSbBrightness.getProgress());
                }
                ReadSettingManager.getInstance().setAutoBrightness(isChecked);
            }
        });

        //字体大小调节
        //调小字体
        mTvFontMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCbFontDefault.isChecked()) {
                    mCbFontDefault.setChecked(false);
                }
                int fontSize = Integer.valueOf(mTvFont.getText().toString()) - 1;
                if (fontSize < 0) return;
                mTvFont.setText(fontSize + "");
                mPageLoader.setTextSize(fontSize);
            }
        });

        //调大字体
        mTvFontPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCbFontDefault.isChecked()) {
                    mCbFontDefault.setChecked(false);
                }
                int fontSize = Integer.valueOf(mTvFont.getText().toString()) + 1;
                mTvFont.setText(fontSize + "");
                mPageLoader.setTextSize(fontSize);
            }
        });
        //恢复默认字体
        mCbFontDefault.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    int fontSize = ViewUtils.sp2px(MyApplication.getContext(),DEFAULT_TEXT_SIZE);
                    mTvFont.setText(fontSize + "");
                    mPageLoader.setTextSize(fontSize);
                }
            }
        });

        //Page Mode 切换
        mRgPageMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                PageMode pageMode;
                switch (checkedId) {
                    case R.id.read_setting_rb_simulation:
                        pageMode = PageMode.SIMULATION;
                        break;
                    case R.id.read_setting_rb_cover:
                        pageMode = PageMode.COVER;
                        break;
                    case R.id.read_setting_rb_slide:
                        pageMode = PageMode.SLIDE;
                        break;
                    case R.id.read_setting_rb_scroll:
                        pageMode = PageMode.SCROLL;
                        break;
                    case R.id.read_setting_rb_none:
                        pageMode = PageMode.NONE;
                        break;
                    default:
                        pageMode = PageMode.SIMULATION;
                        break;
                }
                mPageLoader.setPageMode(pageMode);
            }
        });

        //页面背景样式切换点击事件
        mPageStyleAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                PageStyle pageStyle = PageStyle.values()[position];
                mPageLoader.setPageStyle(PageStyle.values()[position]);
                mPageStyleAdapter.setPageStyleChecked(pageStyle);
                mPageStyleAdapter.notifyDataSetChanged();
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });

        //更多设置
        mTvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), BookMoreSettingActivity.class);//调整到阅读设置界面
                mActivity.startActivityForResult(intent, BookReadActivity.REQUEST_MORE_SETTING);
                //关闭当前设置
                dismiss();
            }
        });
    }

    public boolean isBrightFollowSystem() {
        if (mCbBrightnessAuto == null) {
            return false;
        }
        return mCbBrightnessAuto.isChecked();
    }
}
