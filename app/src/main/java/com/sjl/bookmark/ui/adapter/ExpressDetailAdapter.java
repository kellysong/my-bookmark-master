package com.sjl.bookmark.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sjl.bookmark.R;
import com.sjl.bookmark.entity.ExpressDetail;
import com.sjl.core.util.ViewUtils;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

/**
 * 快递明细适配器
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ExpressDetailAdapter.java
 * @time 2018/5/2 14:13
 * @copyright(C) 2018 song
 */
public class ExpressDetailAdapter extends CommonAdapter<ExpressDetail.DataBean> {
    private Context context;

    public ExpressDetailAdapter(Context context, int layoutId, List<ExpressDetail.DataBean> datas) {
        super(context, layoutId, datas);
        this.context = context;
    }

    @Override
    protected void convert(ViewHolder holder, ExpressDetail.DataBean dataBean, int position) {
        TextView time = holder.getView(R.id.tv_time);
        TextView detail = holder.getView(R.id.tv_detail);
        ImageView ivLogistics = holder.getView(R.id.iv_logistics);
        time.setText(dataBean.getTime());
        detail.setText(dataBean.getContext());
        boolean first = (position == 0);
        View line = holder.getView(R.id.line);
        line.setPadding(0, ViewUtils.dp2px(context, first ? 12 : 0), 0, 0);
        ivLogistics.setSelected(first);
        time.setSelected(first);
        detail.setSelected(first);
    }

}
