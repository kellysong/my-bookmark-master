package com.sjl.bookmark.ui.adapter

import android.content.Context
import android.text.TextUtils
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.sjl.bookmark.R
import com.sjl.bookmark.app.AppConstant
import com.sjl.bookmark.dao.impl.HistoryExpressService
import com.sjl.bookmark.entity.ExpressSearchInfo
import com.sjl.bookmark.entity.table.HistoryExpress
import com.sjl.bookmark.net.HttpConstant
import com.sjl.bookmark.ui.activity.ExpressDetailActivity.Companion.start
import com.sjl.bookmark.ui.activity.ExpressHistoryActivity
import com.zhy.adapter.recyclerview.CommonAdapter
import com.zhy.adapter.recyclerview.base.ViewHolder
import java.lang.String

/**
 * 历史快递适配器
 *
 * @author Kelly
 * @version 1.0.0
 * @filename HistoryExpressAdapter.java
 * @time 2018/4/26 15:43
 * @copyright(C) 2018 song
 */
class HistoryExpressAdapter(context: Context, layoutId: Int, datas: List<HistoryExpress>?) : CommonAdapter<HistoryExpress>(context, layoutId, datas) {
    override fun convert(holder: ViewHolder, historyExpress: HistoryExpress, position: Int) {

        //https://www.kuaidi100.com/images/all/56/shentong.png
        val imageView = holder.getView<ImageView>(R.id.iv_logo)
        Glide.with(context)
                .load(HttpConstant.KUAIDI100_BASE_URL + "images/all/" + historyExpress.companyIcon)
                .dontAnimate()
                .placeholder(R.mipmap.ic_default_logo)
                .into(imageView)
        var isCheck = historyExpress.checkStatus
        val checkTextColor: Int
        if (TextUtils.equals(isCheck, String.valueOf(AppConstant.SignStatus.NOT_SINGED))) {
            isCheck = context.getString(R.string.uncheck)
            checkTextColor = context.resources.getColor(R.color.orange_700)
        } else {
            isCheck = context.getString(R.string.ischeck)
            checkTextColor = context.resources.getColor(R.color.grey2)
        }
        holder.setText(R.id.tv_is_check, isCheck)
        holder.setTextColor(R.id.tv_is_check, checkTextColor)
        val remark = historyExpress.remark
        if (TextUtils.isEmpty(remark)) {
            holder.setText(R.id.tv_name, historyExpress.companyName)
            holder.setText(R.id.tv_post_id, historyExpress.postId)
        } else {
            holder.setText(R.id.tv_name, remark)
            holder.setText(R.id.tv_post_id, historyExpress.companyName + " " + historyExpress.postId)
        }
        //签收时间或者更新时间
        val signTime = historyExpress.signTime
        if (!TextUtils.isEmpty(signTime)) {
            holder.setGone(R.id.tv_sign_time, false)
            holder.setText(R.id.tv_sign_time, signTime)
        } else {
            holder.setGone(R.id.tv_sign_time, true)
        }
        holder.itemView.setOnClickListener { //点击查询快递明细
            val searchInfo = ExpressSearchInfo()
            searchInfo.post_id = historyExpress.postId
            searchInfo.code = historyExpress.companyParam
            searchInfo.name = historyExpress.companyName
            searchInfo.logo = historyExpress.companyIcon
            start(context, searchInfo)
        }
        if (context is ExpressHistoryActivity) {
            holder.itemView.setOnLongClickListener {
                AlertDialog.Builder(context)
                        .setTitle(context.getString(R.string.tips))
                        .setMessage(context.getString(R.string.sure_delete_history))
                        .setPositiveButton(R.string.sure) { dialog, which ->
                            val historyExpressService = HistoryExpressService(context)
                            historyExpressService.deleteHistoryExpress(historyExpress)
                            remove(position)
                            dialog.dismiss()
                        }
                        .setNegativeButton(R.string.cancel, null)
                        .show()
                true
            }
        }
    }
}