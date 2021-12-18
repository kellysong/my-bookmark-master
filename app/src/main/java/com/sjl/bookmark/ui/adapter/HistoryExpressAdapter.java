package com.sjl.bookmark.ui.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sjl.bookmark.R;
import com.sjl.bookmark.app.AppConstant;
import com.sjl.bookmark.dao.impl.HistoryExpressService;
import com.sjl.bookmark.entity.ExpressSearchInfo;
import com.sjl.bookmark.entity.table.HistoryExpress;
import com.sjl.bookmark.net.HttpConstant;
import com.sjl.bookmark.ui.activity.ExpressDetailActivity;
import com.sjl.bookmark.ui.activity.ExpressHistoryActivity;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

import androidx.appcompat.app.AlertDialog;

/**
 * 历史快递适配器
 *
 * @author Kelly
 * @version 1.0.0
 * @filename HistoryExpressAdapter.java
 * @time 2018/4/26 15:43
 * @copyright(C) 2018 song
 */
public class HistoryExpressAdapter extends CommonAdapter<HistoryExpress> {
    private Context context;

    public HistoryExpressAdapter(Context context, int layoutId, List<HistoryExpress> datas) {
        super(context, layoutId, datas);
        this.context = context;
    }

    @Override
    protected void convert(ViewHolder holder, final HistoryExpress historyExpress, final int position) {

        //https://www.kuaidi100.com/images/all/56/shentong.png
        ImageView imageView = holder.getView(R.id.iv_logo);
        Glide.with(context)
                .load(HttpConstant.KUAIDI100_BASE_URL + "images/all/" + historyExpress.getCompanyIcon())
                .dontAnimate()
                .placeholder(R.mipmap.ic_default_logo)
                .into(imageView);
        String isCheck = historyExpress.getCheckStatus();
        int checkTextColor;
        if (TextUtils.equals(isCheck, String.valueOf(AppConstant.SignStatus.NOT_SINGED))) {
            isCheck = context.getString(R.string.uncheck);
            checkTextColor = context.getResources().getColor(R.color.orange_700);
        } else {
            isCheck = context.getString(R.string.ischeck);
            checkTextColor = context.getResources().getColor(R.color.grey2);
        }
        holder.setText(R.id.tv_is_check, isCheck);
        holder.setTextColor(R.id.tv_is_check, checkTextColor);
        String remark = historyExpress.getRemark();
        if (TextUtils.isEmpty(remark)) {
            holder.setText(R.id.tv_name, historyExpress.getCompanyName());
            holder.setText(R.id.tv_post_id, historyExpress.getPostId());
        } else {
            holder.setText(R.id.tv_name, remark);
            holder.setText(R.id.tv_post_id, historyExpress.getCompanyName().concat(" ").concat(historyExpress.getPostId()));
        }
        //签收时间或者更新时间
        String signTime = historyExpress.getSignTime();
        if (!TextUtils.isEmpty(signTime)){
            holder.setGone(R.id.tv_sign_time,false);
            holder.setText(R.id.tv_sign_time, signTime);
        }else {
            holder.setGone(R.id.tv_sign_time,true);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//点击查询快递明细
                ExpressSearchInfo searchInfo = new ExpressSearchInfo();
                searchInfo.setPost_id(historyExpress.getPostId());
                searchInfo.setCode(historyExpress.getCompanyParam());
                searchInfo.setName(historyExpress.getCompanyName());
                searchInfo.setLogo(historyExpress.getCompanyIcon());
                ExpressDetailActivity.start(context, searchInfo);
            }
        });
        if (context instanceof ExpressHistoryActivity) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final HistoryExpress temp = historyExpress;
                    new AlertDialog.Builder(context)
                            .setTitle(context.getString(R.string.tips))
                            .setMessage(context.getString(R.string.sure_delete_history))
                            .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    HistoryExpressService historyExpressService = new HistoryExpressService(context);
                                    historyExpressService.deleteHistoryExpress(temp);
                                    remove(position);
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton(R.string.cancel, null)
                            .show();
                    return true;
                }
            });
        }
    }
}
