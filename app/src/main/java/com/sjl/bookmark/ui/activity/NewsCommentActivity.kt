package com.sjl.bookmark.ui.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import cn.feng.skin.manager.loader.SkinManager
import com.sjl.bookmark.R
import com.sjl.bookmark.kotlin.language.I18nUtils
import com.sjl.bookmark.ui.adapter.TopTabPagerAdapter
import com.sjl.bookmark.ui.fragment.NewsCommentFragment
import com.sjl.bookmark.widget.ColorFlipPagerTitleView
import com.sjl.core.mvp.BaseActivity
import com.sjl.core.mvp.BaseFragment
import com.sjl.core.mvp.NoPresenter
import kotlinx.android.synthetic.main.news_comment_activity.*
import kotlinx.android.synthetic.main.toolbar_default.*
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgeAnchor
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgePagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgeRule
import java.util.*

/**
 * 日报评论
 *
 * @author Kelly
 * @version 1.0.0
 * @filename NewsCommentActivity.java
 * @time 2018/12/19 18:07
 * @copyright(C) 2018 song
 */
class NewsCommentActivity : BaseActivity<NoPresenter>() {

    private val TITLES: Array<String> = arrayOf(
        I18nUtils.getString(R.string.news_short_comment),
        I18nUtils.getString(R.string.news_long_comment)
    )
    private var mTopTabPagerAdapter: TopTabPagerAdapter? = null
    private var mDataList: List<String>? = null
    private var id: String? = null
    private var shortCommentsCountStr: String? = null
    private var longCommentsCountStr: String? = null
    override fun getLayoutId(): Int {
        return R.layout.news_comment_activity
    }

    override fun initView() {}
    override fun initListener() {}
    override fun initData() {
        val intent: Intent = intent
        id = intent.getStringExtra("id")
        val longCommentsCount: Int = intent.getIntExtra("long_comments", 0)
        val shortCommentsCount: Int = intent.getIntExtra("short_comments", 0)
        longCommentsCountStr = if (longCommentsCount > 99) "99+" else longCommentsCount.toString()
        shortCommentsCountStr =
            if (shortCommentsCount > 99) "99+" else shortCommentsCount.toString()
        val commentsCount: Int = intent.getIntExtra("comments", 0)
        bindingToolbar(
            common_toolbar,
            commentsCount.toString() + getString(R.string.news_sub_comments)
        )
        val fragmentList: MutableList<BaseFragment<*>> = ArrayList()
        mDataList = Arrays.asList(*TITLES)
        for (i in TITLES.indices) {
            val newsCommentFragment: NewsCommentFragment = NewsCommentFragment()
            val argus: Bundle = Bundle()
            argus.putInt("position", i)
            argus.putString("newsId", id)
            newsCommentFragment.arguments = argus
            fragmentList.add(newsCommentFragment)
        }
        mTopTabPagerAdapter = TopTabPagerAdapter(supportFragmentManager, fragmentList)
        view_pager.adapter = mTopTabPagerAdapter
        initMagicIndicator()
    }

    /**
     * 初始化指示器
     */
    private fun initMagicIndicator() {
//        magicIndicator.setBackgroundColor(Color.WHITE);
        val commonNavigator: CommonNavigator = CommonNavigator(this)
        commonNavigator.isAdjustMode = true
        commonNavigator.scrollPivotX = 0.65f
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return if (mDataList == null) 0 else mDataList!!.size
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val badgePagerTitleView: BadgePagerTitleView = BadgePagerTitleView(context)
                val simplePagerTitleView: SimplePagerTitleView = ColorFlipPagerTitleView(context)
                simplePagerTitleView.text = mDataList!!.get(index)
                simplePagerTitleView.normalColor = Color.parseColor("#9e9e9e")
                simplePagerTitleView.selectedColor = resources.getColor(R.color.black2)
                simplePagerTitleView.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View) {
                        view_pager!!.currentItem = index
                        //                        badgePagerTitleView.setBadgeView(null); // cancel badge when click tab
                    }
                })
                badgePagerTitleView.innerPagerTitleView = simplePagerTitleView
                // setup badge
                if (index == 0) {
                    val badgeTextView: TextView = LayoutInflater.from(context)
                        .inflate(R.layout.simple_count_badge_layout, null) as TextView
                    badgeTextView.text = shortCommentsCountStr
                    badgePagerTitleView.badgeView = badgeTextView
                } else if (index == 1) {
                    val badgeTextView: TextView = LayoutInflater.from(context)
                        .inflate(R.layout.simple_count_badge_layout, null) as TextView
                    badgeTextView.text = longCommentsCountStr
                    badgePagerTitleView.badgeView = badgeTextView
                }

                // set badge position
                badgePagerTitleView.xBadgeRule = BadgeRule(
                    BadgeAnchor.CONTENT_RIGHT,
                    UIUtil.dip2px(context, 2.0)
                )
                badgePagerTitleView.yBadgeRule = BadgeRule(
                    BadgeAnchor.CONTENT_TOP,
                    -UIUtil.dip2px(context, 5.0)
                )

                // don't cancel badge when tab selected
                badgePagerTitleView.isAutoCancelBadge = false
                return badgePagerTitleView
            }

            override fun getIndicator(context: Context): IPagerIndicator {
                val indicator: LinePagerIndicator = LinePagerIndicator(context)
                indicator.mode = LinePagerIndicator.MODE_EXACTLY
                indicator.lineHeight = UIUtil.dip2px(context, 2.0).toFloat()
                indicator.lineWidth = UIUtil.dip2px(context, 35.0).toFloat()
                indicator.roundRadius = UIUtil.dip2px(context, 2.0).toFloat()
                indicator.startInterpolator = AccelerateInterpolator()
                indicator.endInterpolator = DecelerateInterpolator(2.0f)
                indicator.setColors(colorPrimary)
                return indicator
            }
        }
        magic_indicator!!.navigator = commonNavigator
        ViewPagerHelper.bind(magic_indicator, view_pager)
    }

    protected val colorPrimary: Int
        protected get() {
            val color: Int = SkinManager.getInstance().colorPrimary
            return if (color != -1) color else resources.getColor(R.color.colorPrimary)
        }
}