package com.sjl.bookmark.ui.adapter;

import android.support.annotation.Nullable;
import android.text.Html;
import android.view.View;
import android.widget.CheckBox;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sjl.bookmark.R;
import com.sjl.bookmark.entity.table.Collection;
import com.sjl.core.util.datetime.TimeUtils;

import java.util.List;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename MyCollectionAdapter.java
 * @time 2018/3/26 15:10
 * @copyright(C) 2018 song
 */
public class MyCollectionAdapter extends BaseQuickAdapter<Collection, BaseViewHolder> {
    boolean mEditMode = false;

    public MyCollectionAdapter(int layoutResId, @Nullable List<Collection> data) {
        super(layoutResId, data);

    }

    @Override
    protected void convert(BaseViewHolder holder, Collection item) {
        CheckBox checkBox = holder.getView(R.id.cb_item);
        if (mEditMode) {
            if (item.isSelectItem()) {
                item.setSelectItem(true);
                checkBox.setChecked(true);
            } else {
                item.setSelectItem(false);
                checkBox.setChecked(false);
            }
            checkBox.setVisibility(View.VISIBLE);
        } else {
            item.setSelectItem(false);
            checkBox.setChecked(false);
            checkBox.setVisibility(View.INVISIBLE);
        }
        holder.setText(R.id.tv_title, Html.fromHtml(item.getTitle()));//转义特殊字符,如&--&amp
        holder.setText(R.id.tv_collect_date, TimeUtils.getRangeByDate(item.getDate()));
        holder.setText(R.id.tv_content, item.getHref());
        if (item.getType() == 0) {
            holder.setText(R.id.tv_collect_type, "网页");
            holder.setVisible(R.id.tv_content, false);
        } else if (item.getType() == 1) {
            holder.setText(R.id.tv_collect_type, "笔记");
            holder.setVisible(R.id.tv_content, true);
        } else {
            holder.setText(R.id.tv_collect_type, "其它");
            holder.setVisible(R.id.tv_content, false);

        }
        if (item.getTop() == 1) {
            holder.itemView.setBackgroundResource(R.drawable.list_top_oval_item_selector);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.list_oval_item_selector);
        }

    }

    public void setEditMode(boolean editMode) {
        mEditMode = editMode;
        notifyDataSetChanged();
    }
}
