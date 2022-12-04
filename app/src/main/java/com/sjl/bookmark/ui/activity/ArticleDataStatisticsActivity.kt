package com.sjl.bookmark.ui.activity

import com.sjl.bookmark.R
import com.sjl.bookmark.dao.impl.BrowseTrackDaoImpl
import com.sjl.bookmark.kotlin.language.I18nUtils
import com.sjl.bookmark.kotlin.util.StatisticsUtils
import com.sjl.core.mvp.BaseActivity
import com.sjl.core.mvp.NoPresenter
import kotlinx.android.synthetic.main.article_data_statistics_activity.*
import kotlinx.android.synthetic.main.toolbar_default.*

/**
 * 文章数据统计
 * @author Kelly
 * @version 1.0.0
 * @filename ArticleDataStatisticsActivity
 * @time 2022/12/3 10:19
 * @copyright(C) 2022 song
 */
class ArticleDataStatisticsActivity : BaseActivity<NoPresenter>() {
    override fun getLayoutId(): Int {
        return R.layout.article_data_statistics_activity
    }

    override fun initView() {
        
    }

    override fun initListener() {
        bindingToolbar(common_toolbar, I18nUtils.getString(R.string.menu_data_statistics))
    }

    override fun initData() {
        var browseTrackDaoImpl = BrowseTrackDaoImpl(this)
        val findWeekData = browseTrackDaoImpl.findWeekData()
        val weekDate = StatisticsUtils.getWeekDateX()
        val dateY = StatisticsUtils.getCountY()
        dataStatisticsChartView.setYAxis(dateY)
        dataStatisticsChartView.setXAxis(weekDate)
        dataStatisticsChartView.setData(findWeekData)
    }
}