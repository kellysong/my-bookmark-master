package com.sjl.bookmark.ui.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ctetin.expandabletextviewlibrary.ExpandableTextView;
import com.sjl.bookmark.R;
import com.sjl.bookmark.entity.zhihu.NewsCommentDto;
import com.sjl.core.net.GlideCircleTransform;
import com.sjl.core.util.datetime.TimeUtils;

import java.util.List;

import androidx.annotation.Nullable;

/**
 * 日报评论适配器
 *
 * @author Kelly
 * @version 1.0.0
 * @filename NewsCommentAdapter.java
 * @time 2018/12/24 15:04
 * @copyright(C) 2018 song
 */
public class NewsCommentAdapter extends BaseQuickAdapter<NewsCommentDto.Comment, BaseViewHolder> {


    public NewsCommentAdapter(int layoutResId, @Nullable List data) {
        super(layoutResId, data);

    }

    @Override
    protected void convert(BaseViewHolder helper, NewsCommentDto.Comment item) {
        helper.setText(R.id.like, String.valueOf(item.getLikes()));
        Glide.with(mContext)
                .load(item.getAvatar())
                .placeholder(R.mipmap.ic_default_portrait)
                .error(R.mipmap.ic_load_error)
                .transform(new GlideCircleTransform())
                .into((ImageView) helper.getView(R.id.user_avatar));
        //用户
        TextView userName = helper.getView(R.id.user_name);
        userName.getPaint().setFakeBoldText(true);//加粗
        userName.setText(item.getAuthor());
        //评论内容
        ExpandableTextView expandableTextView = helper.getView(R.id.comment_content);
        expandableTextView.setContent(item.getContent());
        //评论时间
        helper.setText(R.id.comment_time, TimeUtils.formatDateToStr((long) item.getTime() * 1000, TimeUtils.DATE_FORMAT_2));

    }
}
