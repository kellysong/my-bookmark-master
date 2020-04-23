package com.sjl.bookmark.ui.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ctetin.expandabletextviewlibrary.ExpandableTextView;
import com.sjl.bookmark.R;
import com.sjl.bookmark.entity.zhuishu.HotCommentDto;
import com.sjl.bookmark.net.HttpConstant;
import com.sjl.core.util.datetime.TimeUtils;
import com.sjl.core.widget.EasyRatingBar;
import com.sjl.core.net.GlideCircleTransform;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookHotCommentAdapter.java
 * @time 2018/12/7 11:10
 * @copyright(C) 2018 song
 */
public class BookHotCommentAdapter extends CommonAdapter<HotCommentDto.HotComment> {
    public BookHotCommentAdapter(Context context, int layoutId, List<HotCommentDto.HotComment> datas) {
        super(context, layoutId, datas);
    }

    @Override
    protected void convert(ViewHolder holder, HotCommentDto.HotComment hotCommentDto, int position) {

        //头像
        ImageView mIvPortrait = holder.getView(R.id.hot_comment_iv_cover);

        Glide.with(getContext())
                .load(HttpConstant.ZHUISHU_IMG_BASE_URL + hotCommentDto.getAuthor().getAvatar())
                .placeholder(R.mipmap.ic_default_portrait)
                .error(R.mipmap.ic_load_error)
                .transform(new GlideCircleTransform(getContext()))
                .into(mIvPortrait);
        //作者
        holder.setText(R.id.hot_comment_tv_author, hotCommentDto.getAuthor().getNickname());

        //等级
        holder.setText(R.id.hot_comment_tv_lv, getContext().getResources().getString(R.string.nb_user_lv,
                hotCommentDto.getAuthor().getLv()));

        //标题
        holder.setText(R.id.hot_comment_title, hotCommentDto.getTitle());

        //评分,由于控件是自定义，holder不支持
        EasyRatingBar easyRatingBar = holder.getView(R.id.hot_comment_erb_rate);
        easyRatingBar.setRating(hotCommentDto.getRating());

        //内容
        ExpandableTextView expandableTextView = holder.getView(R.id.hot_comment_tv_content);
        expandableTextView.setContent(hotCommentDto.getContent());
        //点赞数
        holder.setText(R.id.hot_comment_tv_helpful, String.valueOf(hotCommentDto.getLikeCount()));

        //时间
        holder.setText(R.id.hot_comment_tv_time, TimeUtils.dateConvert(hotCommentDto.getUpdated(), TimeUtils.DATE_FORMAT_7));

    }
}
