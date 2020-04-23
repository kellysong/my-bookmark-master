package com.sjl.bookmark.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sjl.bookmark.R;
import com.sjl.bookmark.app.AppConstant;
import com.sjl.bookmark.entity.ExpressCompany;
import com.sjl.bookmark.entity.ExpressSearchInfo;
import com.sjl.bookmark.net.HttpConstant;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ItemViewDelegate;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

/**
 * 快递公司适配器
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ExpressCompanyAdapter.java
 * @time 2018/4/29 20:35
 * @copyright(C) 2018 song
 */
public class ExpressCompanyAdapter extends MultiItemTypeAdapter<ExpressCompany> {
    private Context context;

    public ExpressCompanyAdapter(Context context, List<ExpressCompany> datas) {
        super(context, datas);
        this.context = context;
        addItemViewDelegate(new CompanyIndexItemDelagate());//委派模式
        addItemViewDelegate(new CompanyMainItemDelagate());
    }

    /**
     * 索引
     */
    private class CompanyIndexItemDelagate implements ItemViewDelegate<ExpressCompany> {

        @Override
        public int getItemViewLayoutId() {
            return R.layout.express_company_index_recycle_item;
        }

        @Override
        public boolean isForViewType(ExpressCompany item, int position) {
            if (TextUtils.isEmpty(item.getCode())) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void convert(ViewHolder holder, ExpressCompany companyEntity, int position) {
            holder.setText(R.id.tv_index, companyEntity.getName());
        }
    }

    /**
     * 公司条目
     */
    private class CompanyMainItemDelagate implements ItemViewDelegate<ExpressCompany> {

        @Override
        public int getItemViewLayoutId() {
            return R.layout.express_company_main_recycle_item;
        }

        @Override
        public boolean isForViewType(ExpressCompany item, int position) {
            if (TextUtils.isEmpty(item.getCode())) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        public void convert(ViewHolder holder, final ExpressCompany companyEntity, int position) {
            ImageView imageView = holder.getView(R.id.iv_logo);
            Glide.with(context)
                    .load(HttpConstant.KUAIDI100_BASE_URL + "images/all/" + companyEntity.getLogo())
                    .dontAnimate()
                    .placeholder(R.mipmap.ic_default_logo)
                    .into(imageView);
            holder.setText(R.id.tv_name, companyEntity.getName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Activity activity = (Activity) context;
                    ExpressSearchInfo searchInfo = new ExpressSearchInfo();
                    searchInfo.setName(companyEntity.getName());
                    searchInfo.setLogo(companyEntity.getLogo());
                    searchInfo.setCode(companyEntity.getCode());
                    Intent intent = new Intent();
                    intent.putExtra(AppConstant.Extras.SEARCH_INFO, searchInfo);
                    activity.setResult(Activity.RESULT_OK, intent);
                    activity.finish();
                }
            });
        }
    }


}
