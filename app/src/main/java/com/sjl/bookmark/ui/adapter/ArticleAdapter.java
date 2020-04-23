package com.sjl.bookmark.ui.adapter;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sjl.bookmark.R;
import com.sjl.bookmark.dao.impl.DaoFactory;
import com.sjl.bookmark.entity.Article;

import java.util.List;
import java.util.Map;

/**
 * 玩安卓首页和分类列表适配器
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ArticleAdapter.java
 * @time 2018/3/21 16:24
 * @copyright(C) 2018 song
 */
public class ArticleAdapter extends BaseQuickAdapter<Article.DatasBean, BaseViewHolder> {
    private boolean mChapterNameVisible = true;
    private Map<String, Boolean> browseTrackMap;

    public ArticleAdapter(int layoutResId, @Nullable List<Article.DatasBean> data) {
        super(layoutResId, data);
        refreshBrowseTrack();//一次性查询出来，单个查询站性能
    }

    @Override
    protected void convert(BaseViewHolder holder, Article.DatasBean item) {
        if (!TextUtils.isEmpty(item.getAuthor())){
            holder.setText(R.id.tvAuthor, item.getAuthor());
        }else {
            holder.setText(R.id.tvAuthor, item.getShareUser());
        }
        holder.setText(R.id.tvNiceDate, item.getNiceDate());
        holder.setText(R.id.tvTitle, Html.fromHtml(item.getTitle()));
        if (mChapterNameVisible) {//首页，搜索显示
            holder.setText(R.id.tvChapterName, Html.fromHtml(formatChapterName(item.getSuperChapterName(), item.getChapterName())));
            holder.setGone(R.id.tv_top, item.isTop());//是否置顶
            holder.setGone(R.id.tv_new, item.isFresh());//是否新
            if (item.getTags() != null && item.getTags().size() > 0) {
                holder.setText(R.id.tv_tag, item.getTags().get(0).getName());//是否新
                holder.setGone(R.id.tv_tag, true);//是否新
            } else {
                holder.setGone(R.id.tv_tag, false);//是否新
            }
        }

        //设置子View的点击事件
        holder.addOnClickListener(R.id.tvChapterName);
        //文章阅读后变色
        if (browseTrackMap.get(String.valueOf(item.getId())) != null) {
            holder.setTextColor(R.id.tvTitle, ContextCompat.getColor(mContext, R.color.gray_600));
        } else {
            holder.setTextColor(R.id.tvTitle, ContextCompat.getColor(mContext, R.color.black2));
        }

    }

    public void setChapterNameVisible(boolean chapterNameVisible) {
        this.mChapterNameVisible = chapterNameVisible;
    }

    /**
     * 添加浏览足迹,并刷新条目
     *
     * @param id
     * @param position
     */
    public void addBrowseTrack(String id, int position) {
        boolean ret = DaoFactory.getBrowseTrackDao().saveBrowseTrackByType(0, id);
        if (!ret) {//已经存在，不用刷新
            return;
        }
        this.browseTrackMap.put(id, true);
        refreshNotifyItemChanged(position);//使用封装好的方法刷新指定位置Item，否则报错
    }

    /**
     * 刷新浏览记录
     */
    public void refreshBrowseTrack() {
        browseTrackMap = DaoFactory.getBrowseTrackDao().browseTrackToMap(0);//一次性查询出来，单个查询站性能
    }


    private  String formatChapterName(String... names) {
        StringBuilder format = new StringBuilder();
        for (String name : names) {
            if (!TextUtils.isEmpty(name)) {
                if (format.length() > 0) {
                    format.append("·");
                }
                format.append(name);
            }
        }
        return format.toString();
    }
}
