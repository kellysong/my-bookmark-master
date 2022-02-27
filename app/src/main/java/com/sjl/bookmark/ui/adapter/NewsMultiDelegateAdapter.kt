package com.sjl.bookmark.ui.adapter

import android.content.res.Resources.Theme
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.util.MultiTypeDelegate
import com.sjl.bookmark.R
import com.sjl.bookmark.dao.impl.DaoFactory
import com.sjl.bookmark.entity.zhihu.NewsList
import com.sjl.bookmark.kotlin.language.LanguageManager.context
import com.sjl.bookmark.net.GlideImageLoader
import com.sjl.bookmark.ui.activity.NewsDetailActivity.Companion.startActivity
import com.sjl.core.widget.imageview.CustomRoundAngleImageView
import com.youth.banner.Banner
import com.youth.banner.BannerConfig
import java.util.*

/**
 * 日志日报首页多条目适配器
 *
 * @author Kelly
 * @version 1.0.0
 * @filename NewsMultiDelegateAdapter.java
 * @time 2018/12/18 18:12
 * @copyright(C) 2018 song
 */
class NewsMultiDelegateAdapter(layoutResId: Int, data: List<NewsList>?) : BaseQuickAdapter<NewsList, BaseViewHolder>(layoutResId, data) {
    private var browseTrackMap: Map<String, Boolean>
    override fun convert(helper: BaseViewHolder, item: NewsList) {
        //Step.3
        when (helper.itemViewType) {
            TYPE_HEADER -> {
                val mBannerAds = helper.getView<Banner>(R.id.banner_ads)
                val images: MutableList<String> = ArrayList()
                val titles: MutableList<String> = ArrayList()
                for (banner in item.getTop_stories()) {
                    images.add(banner.image)
                    titles.add(banner.title)
                }
                mBannerAds.setImages(images)
                        .setBannerTitles(titles)
                        .setBannerStyle(BannerConfig.NUM_INDICATOR_TITLE)
                        .setImageLoader(GlideImageLoader())
                        .start()
                mBannerAds.setOnBannerListener { position ->
                    val topStory = item.getTop_stories()[position]
                    startActivity(mContext, topStory.id, topStory.title, topStory.image)
                }
            }
            TYPE_HEADER_SECOND -> helper.setVisible(R.id.red_dot_indicator, firstLoadFlag)
            TYPE_DATE -> helper.setText(R.id.date_indicator, item.date)
            TYPE_ITEM -> {

                //文章阅读后变色
                if (browseTrackMap[item.id.toString()] != null) {
                    helper.setTextColor(R.id.title_text, ContextCompat.getColor(mContext, R.color.gray_600))
                } else {
                    val theme: Theme = mContext.theme
                    val customTextColor = TypedValue() //自定义字体颜色
                    theme.resolveAttribute(R.attr.customTextColor, customTextColor, true)
                    helper.setTextColor(R.id.title_text, mContext.resources.getColor(customTextColor.resourceId))
                }
                helper.setText(R.id.title_text, item.title)
                Glide.with(mContext)
                        .load(item.image)
                        .placeholder(R.mipmap.img_loading_placehoder)
                        .error(R.mipmap.ic_load_error)
                        .into(helper.getView<View>(R.id.title_image) as CustomRoundAngleImageView)
                helper.getView<View>(R.id.cardview).setOnClickListener { v ->
                    startActivity(mContext, item.id, item.title, item.image)
                    v.postDelayed({
                        helper.setTextColor(R.id.title_text, ContextCompat.getColor(mContext, R.color.gray_600))
                        val ret = DaoFactory.getBrowseTrackDao().saveBrowseTrackByType(1, item.id.toString())
                        if (ret) { //新增刷新
                            browseTrackMap = DaoFactory.getBrowseTrackDao().browseTrackToMap(1)
                        }
                    }, 200)
                }
            }
            else -> {
            }
        }
    }

    var firstLoadFlag = false

    companion object {
        /**
         * 正常条目
         */
        const val TYPE_ITEM = 0

        /**
         * 轮播图
         */
        const val TYPE_HEADER = 1

        /**
         * 今日热闻标题
         */
        const val TYPE_HEADER_SECOND = 2

        /**
         * 日期标题
         */
        const val TYPE_DATE = 3
    }

    init {
        this.mContext = context
        browseTrackMap = DaoFactory.getBrowseTrackDao().browseTrackToMap(1)

        //Step.1
        multiTypeDelegate = object : MultiTypeDelegate<NewsList>() {
            override fun getItemType(entity: NewsList): Int {
                //根据你的实体类来判断布局类型
                return entity.itemType
            }
        }
        //Step.2
        multiTypeDelegate
                .registerItemType(TYPE_ITEM, R.layout.news_list_recycle_item)
                .registerItemType(TYPE_HEADER, R.layout.news_header_recycle_item) //轮播图
                .registerItemType(TYPE_HEADER_SECOND, R.layout.news_header_second_recycle_item)
                .registerItemType(TYPE_DATE, R.layout.news_title_date_recycle_item)
    }
}