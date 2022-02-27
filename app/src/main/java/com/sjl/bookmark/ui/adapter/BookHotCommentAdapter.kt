package com.sjl.bookmark.ui.adapter

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.ctetin.expandabletextviewlibrary.ExpandableTextView
import com.sjl.bookmark.R
import com.sjl.bookmark.entity.zhuishu.HotCommentDto.HotComment
import com.sjl.bookmark.net.HttpConstant
import com.sjl.core.net.GlideCircleTransform
import com.sjl.core.util.datetime.TimeUtils
import com.sjl.core.widget.EasyRatingBar
import com.zhy.adapter.recyclerview.CommonAdapter
import com.zhy.adapter.recyclerview.base.ViewHolder

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookHotCommentAdapter.java
 * @time 2018/12/7 11:10
 * @copyright(C) 2018 song
 */
class BookHotCommentAdapter(context: Context?, layoutId: Int, datas: List<HotComment>?) : CommonAdapter<HotComment>(context, layoutId, datas) {
    override fun convert(holder: ViewHolder, hotCommentDto: HotComment, position: Int) {

        //头像
        val mIvPortrait = holder.getView<ImageView>(R.id.hot_comment_iv_cover)
        Glide.with(context)
                .load(HttpConstant.ZHUISHU_IMG_BASE_URL + hotCommentDto.author.avatar)
                .placeholder(R.mipmap.ic_default_portrait)
                .error(R.mipmap.ic_load_error)
                .transform(GlideCircleTransform())
                .into(mIvPortrait)
        //作者
        holder.setText(R.id.hot_comment_tv_author, hotCommentDto.author.nickname)

        //等级
        holder.setText(R.id.hot_comment_tv_lv, context.resources.getString(R.string.nb_user_lv,
                hotCommentDto.author.lv))

        //标题
        holder.setText(R.id.hot_comment_title, hotCommentDto.title)

        //评分,由于控件是自定义，holder不支持
        val easyRatingBar = holder.getView<EasyRatingBar>(R.id.hot_comment_erb_rate)
        easyRatingBar.setRating(hotCommentDto.rating)

        //内容
        val expandableTextView = holder.getView<ExpandableTextView>(R.id.hot_comment_tv_content)
        expandableTextView.setContent(hotCommentDto.content)
        //点赞数
        holder.setText(R.id.hot_comment_tv_helpful, hotCommentDto.likeCount.toString())

        //时间
        holder.setText(R.id.hot_comment_tv_time, TimeUtils.dateConvert(hotCommentDto.updated, TimeUtils.DATE_FORMAT_7))
    }
}