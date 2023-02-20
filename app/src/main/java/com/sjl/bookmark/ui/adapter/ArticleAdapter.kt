package com.sjl.bookmark.ui.adapter

import android.text.Html
import android.text.TextUtils
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sjl.bookmark.R
import com.sjl.bookmark.dao.impl.DaoFactory
import com.sjl.bookmark.entity.Article.DatasBean

/**
 * 玩安卓首页和分类列表适配器
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ArticleAdapter.java
 * @time 2018/3/21 16:24
 * @copyright(C) 2018 song
 */
class ArticleAdapter(layoutResId: Int, data: List<DatasBean>?) : BaseQuickAdapter<DatasBean, BaseViewHolder>(layoutResId, data) {
    private var mChapterNameVisible = true
    private lateinit var browseTrackMap: MutableMap<String, Boolean>
    override fun convert(holder: BaseViewHolder, item: DatasBean) {
        if (!TextUtils.isEmpty(item.author)) {
            holder.setText(R.id.tvAuthor, item.author)
        } else {
            holder.setText(R.id.tvAuthor, item.shareUser)
        }
        holder.setText(R.id.tvNiceDate, item.niceDate)
        holder.setText(R.id.tvTitle, Html.fromHtml(item.title))
        if (mChapterNameVisible) { //首页，搜索显示
            holder.setText(R.id.tvChapterName, Html.fromHtml(formatChapterName(item.superChapterName, item.chapterName)))
            holder.setGone(R.id.tv_top, item.isTop) //是否置顶
            holder.setGone(R.id.tv_new, item.isFresh) //是否新
            if (item.tags != null && item.tags.size > 0) {
                holder.setText(R.id.tv_tag, item.tags[0].name) //是否新
                holder.setGone(R.id.tv_tag, true) //是否新
            } else {
                holder.setGone(R.id.tv_tag, false) //是否新
            }
        }

        //设置子View的点击事件
        holder.addOnClickListener(R.id.tvChapterName)
        //文章阅读后变色
        if (browseTrackMap[item.id.toString()] != null) {
            holder.setTextColor(R.id.tvTitle, ContextCompat.getColor(mContext, R.color.gray_600))
        } else {
            holder.setTextColor(R.id.tvTitle, ContextCompat.getColor(mContext, R.color.black2))
        }
    }

    fun setChapterNameVisible(chapterNameVisible: Boolean) {
        mChapterNameVisible = chapterNameVisible
    }

    /**
     * 添加浏览足迹,并刷新条目
     *
     * @param id
     * @param position
     */
    fun addBrowseTrack(item: DatasBean, position: Int) {
        val ret = DaoFactory.getBrowseTrackDao().saveBrowseTrackByType(0, item.id.toString(),item.link,item.title, Html.fromHtml(formatChapterName(item.superChapterName, item.chapterName)).toString())
        if (!ret) { //已经存在，不用刷新
            return
        }
        browseTrackMap[item.id.toString()] = true
        refreshNotifyItemChanged(position) //使用封装好的方法刷新指定位置Item，否则报错
    }

    /**
     * 刷新浏览记录
     */
    fun refreshBrowseTrack() {
        browseTrackMap = DaoFactory.getBrowseTrackDao().browseTrackToMap(0) //一次性查询出来，单个查询站性能
    }

    private fun formatChapterName(vararg names: String): String {
        val format = StringBuilder()
        for (name in names) {
            if (!TextUtils.isEmpty(name)) {
                if (format.isNotEmpty()) {
                    format.append("·")
                }
                format.append(name)
            }
        }
        return format.toString()
    }

    init {
        refreshBrowseTrack() //一次性查询出来，单个查询站性能
    }
}