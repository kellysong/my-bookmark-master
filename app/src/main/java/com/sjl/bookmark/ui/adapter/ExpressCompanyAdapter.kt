package com.sjl.bookmark.ui.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.sjl.bookmark.R
import com.sjl.bookmark.app.AppConstant
import com.sjl.bookmark.entity.ExpressCompany
import com.sjl.bookmark.entity.ExpressSearchInfo
import com.sjl.bookmark.net.HttpConstant
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter
import com.zhy.adapter.recyclerview.base.ItemViewDelegate
import com.zhy.adapter.recyclerview.base.ViewHolder

/**
 * 快递公司适配器
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ExpressCompanyAdapter.java
 * @time 2018/4/29 20:35
 * @copyright(C) 2018 song
 */
class ExpressCompanyAdapter(context: Context, datas: List<ExpressCompany>?) : MultiItemTypeAdapter<ExpressCompany>(context, datas) {
    /**
     * 索引
     */
    private inner class CompanyIndexItemDelagate : ItemViewDelegate<ExpressCompany> {
        override fun getItemViewLayoutId(): Int {
            return R.layout.express_company_index_recycle_item
        }

        override fun isForViewType(item: ExpressCompany, position: Int): Boolean {
            return TextUtils.isEmpty(item.code)
        }

        override fun convert(holder: ViewHolder, companyEntity: ExpressCompany, position: Int) {
            holder.setText(R.id.tv_index, companyEntity.name)
        }
    }

    /**
     * 公司条目
     */
    private inner class CompanyMainItemDelagate : ItemViewDelegate<ExpressCompany> {
        override fun getItemViewLayoutId(): Int {
            return R.layout.express_company_main_recycle_item
        }

        override fun isForViewType(item: ExpressCompany, position: Int): Boolean {
            return !TextUtils.isEmpty(item.code)
        }

        override fun convert(holder: ViewHolder, companyEntity: ExpressCompany, position: Int) {
            val imageView = holder.getView<ImageView>(R.id.iv_logo)
            Glide.with(context)
                    .load(HttpConstant.KUAIDI100_BASE_URL + "images/all/" + companyEntity.logo)
                    .dontAnimate()
                    .placeholder(R.mipmap.ic_default_logo)
                    .into(imageView)
            holder.setText(R.id.tv_name, companyEntity.name)
            holder.itemView.setOnClickListener {
                val activity = context as Activity
                val searchInfo = ExpressSearchInfo()
                searchInfo.name = companyEntity.name
                searchInfo.logo = companyEntity.logo
                searchInfo.code = companyEntity.code
                val intent = Intent()
                intent.putExtra(AppConstant.Extras.SEARCH_INFO, searchInfo)
                activity.setResult(Activity.RESULT_OK, intent)
                activity.finish()
            }
        }
    }

    init {
        addItemViewDelegate(CompanyIndexItemDelagate()) //委派模式
        addItemViewDelegate(CompanyMainItemDelagate())
    }
}