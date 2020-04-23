package com.sjl.bookmark.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.View;

import com.sjl.bookmark.R;
import com.sjl.bookmark.app.AppConstant;
import com.sjl.bookmark.entity.ExpressCompany;
import com.sjl.bookmark.entity.ExpressSearchInfo;
import com.sjl.bookmark.ui.activity.BrowserActivity;
import com.sjl.bookmark.ui.activity.ExpressCompanyActivity;
import com.sjl.bookmark.ui.activity.ExpressDetailActivity;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

/**
 * 建议公司列表
 *
 * @author Kelly
 * @version 1.0.0
 * @filename SuggestionCompanyAdapter.java
 * @time 2018/4/26 17:27
 * @copyright(C) 2018 song
 */
public class SuggestionCompanyAdapter extends CommonAdapter<ExpressCompany> {
    private Context context;
    private String postId;
    public SuggestionCompanyAdapter(Context context, int layoutId, List<ExpressCompany> datas) {
        super(context, layoutId, datas);
        this.context = context;
    }

    @Override
    protected void convert(ViewHolder holder, final ExpressCompany companyEntity, final int position) {
        holder.setText(R.id.tv_suggestion,Html.fromHtml(companyEntity.getName()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //没有找到合适快递公司，进入手动选择
                if (position == getDatas().size() - 2) {
                    Activity activity = (Activity) context;
                    activity.startActivityForResult(new Intent(activity, ExpressCompanyActivity.class), AppConstant.REQUEST_COMPANY);
                    return;
                }
                //使用网页查询
                if (position == getDatas().size() - 1) {
                    BrowserActivity.startWithParams(context,"快递100","https://m.kuaidi100.com/result.jsp?nu="+getPostId());
                    return;
                }

                ExpressSearchInfo searchInfo = new ExpressSearchInfo();
                searchInfo.setPost_id(getPostId());
                searchInfo.setCode(companyEntity.getCode());
                searchInfo.setName(companyEntity.getName());
                searchInfo.setLogo(companyEntity.getLogo());
                ExpressDetailActivity.start(context, searchInfo);
            }
        });
    }



    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
