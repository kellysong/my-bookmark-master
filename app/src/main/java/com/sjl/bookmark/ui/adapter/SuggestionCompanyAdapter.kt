package com.sjl.bookmark.ui.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.Html
import android.view.View
import com.sjl.bookmark.R
import com.sjl.bookmark.app.AppConstant
import com.sjl.bookmark.entity.ExpressCompany
import com.sjl.bookmark.entity.ExpressSearchInfo
import com.sjl.bookmark.ui.activity.BrowserActivity.Companion.startWithParams
import com.sjl.bookmark.ui.activity.ExpressCompanyActivity
import com.sjl.bookmark.ui.activity.ExpressDetailActivity.Companion.start
import com.zhy.adapter.recyclerview.CommonAdapter
import com.zhy.adapter.recyclerview.base.ViewHolder

/**
 * 建议公司列表
 *
 * @author Kelly
 * @version 1.0.0
 * @filename SuggestionCompanyAdapter.java
 * @time 2018/4/26 17:27
 * @copyright(C) 2018 song
 */
class SuggestionCompanyAdapter(context: Context, layoutId: Int, datas: List<ExpressCompany?>?) : CommonAdapter<ExpressCompany>(context, layoutId, datas) {
    var postId: String? = null
    override fun convert(holder: ViewHolder, companyEntity: ExpressCompany, position: Int) {
        holder.setText(R.id.tv_suggestion, Html.fromHtml(companyEntity.name))
        holder.itemView.setOnClickListener(View.OnClickListener { //没有找到合适快递公司，进入手动选择
            if (position == datas.size - 2) {
                val activity = context as Activity
                activity.startActivityForResult(Intent(activity, ExpressCompanyActivity::class.java), AppConstant.REQUEST_COMPANY)
                return@OnClickListener
            }
            //使用网页查询
            if (position == datas.size - 1) {
                startWithParams(context, "快递100", "https://m.kuaidi100.com/result.jsp?nu=$postId")
                return@OnClickListener
            }
            val searchInfo = ExpressSearchInfo()
            searchInfo.post_id = postId
            searchInfo.code = companyEntity.code
            searchInfo.name = companyEntity.name
            searchInfo.logo = companyEntity.logo
            start(context, searchInfo)
        })
    }
}