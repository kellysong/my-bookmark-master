package com.sjl.bookmark.ui.adapter;

import android.graphics.Color;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sjl.bookmark.R;
import com.sjl.bookmark.app.AppConstant;
import com.sjl.bookmark.entity.ThemeSkin;
import com.sjl.core.util.PreferencesHelper;

import java.util.List;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ThemeSkinAdapter.java
 * @time 2018/12/29 22:13
 * @copyright(C) 2018 song
 */
public class ThemeSkinAdapter extends BaseQuickAdapter<ThemeSkin,BaseViewHolder> {
    private PreferencesHelper instance;
    public ThemeSkinAdapter(int layoutResId, @Nullable List<ThemeSkin> data) {
        super(layoutResId, data);
        instance = PreferencesHelper.getInstance(mContext);

    }

    @Override
    protected void convert(BaseViewHolder helper, ThemeSkin item) {
        int selectSkin = instance.getInteger(AppConstant.SETTING.CURRENT_SELECT_SKIN, 0);
        if (selectSkin == item.getSkinIndex()){
            helper.setVisible(R.id.cb_select_color,true);
            helper.setChecked(R.id.cb_select_color,true);
        }else{
            helper.setVisible(R.id.cb_select_color,false);
            helper.setChecked(R.id.cb_select_color,false);
        }
        helper.setText(R.id.tv_color_title,item.getSkinTitle());
        helper.setBackgroundColor(R.id.iv_color, Color.parseColor(item.getSkinColor()));

    }
}
