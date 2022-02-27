package com.sjl.bookmark.ui.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.ctetin.expandabletextviewlibrary.ExpandableTextView
import com.sjl.bookmark.R
import com.sjl.bookmark.entity.zhihu.NewsCommentDto
import com.sjl.core.net.GlideCircleTransform
import com.sjl.core.util.datetime.TimeUtils

/**
 * 日报评论适配器
 *
 * @author Kelly
 * @version 1.0.0
 * @filename NewsCommentAdapter.java
 * @time 2018/12/24 15:04
 * @copyright(C) 2018 song
 */
class NewsCommentAdapter(layoutResId: Int, data: List<NewsCommentDto.Comment>?) : BaseQuickAdapter<NewsCommentDto.Comment, BaseViewHolder>(layoutResId, data) {
    override fun convert(helper: BaseViewHolder, item: NewsCommentDto.Comment) {
        helper.setText(R.id.like, item.likes.toString())
        Glide.with(mContext)
                .load(item.avatar)
                .placeholder(R.mipmap.ic_default_portrait)
                .error(R.mipmap.ic_load_error)
                .transform(GlideCircleTransform())
                .into((helper.getView<View>(R.id.user_avatar) as ImageView))
        //用户
        val userName = helper.getView<TextView>(R.id.user_name)
        userName.paint.isFakeBoldText = true //加粗
        userName.text = item.author
        //评论内容
        val expandableTextView = helper.getView<ExpandableTextView>(R.id.comment_content)
        expandableTextView.setContent(item.content)
        //评论时间
        helper.setText(R.id.comment_time, TimeUtils.formatDateToStr(item.time.toLong() * 1000, TimeUtils.DATE_FORMAT_2))
    }
}