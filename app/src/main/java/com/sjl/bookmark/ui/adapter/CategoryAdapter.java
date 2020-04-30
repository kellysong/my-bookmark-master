package com.sjl.bookmark.ui.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sjl.bookmark.R;
import com.sjl.bookmark.entity.Category;

import java.util.List;

/**
 * Android知识分类
 *
 * @author Kelly
 * @version 1.0.0
 * @filename CategoryAdapter.java
 * @time 2018/3/22 16:02
 * @copyright(C) 2018 song
 */
public class CategoryAdapter extends BaseQuickAdapter<Category, BaseViewHolder> {

    public CategoryAdapter(int layoutResId, @Nullable List<Category> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Category item) {
        helper.setText(R.id.typeItemFirst, item.getName());
        StringBuffer sb = new StringBuffer();
        for (Category.ChildrenBean childrenBean : item.getChildren()) {
            sb.append(childrenBean.getName() + "     ");
        }
        helper.setText(R.id.typeItemSecond, sb.toString());

    }

}
