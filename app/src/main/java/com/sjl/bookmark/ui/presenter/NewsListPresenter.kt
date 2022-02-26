package com.sjl.bookmark.ui.presenter

import com.sjl.bookmark.R
import com.sjl.bookmark.api.ZhiHuApiService
import com.sjl.bookmark.app.AppConstant
import com.sjl.bookmark.entity.zhihu.NewsDto
import com.sjl.bookmark.entity.zhihu.NewsList
import com.sjl.bookmark.ui.adapter.NewsMultiDelegateAdapter
import com.sjl.bookmark.ui.contract.NewsListContract
import com.sjl.core.net.RetrofitHelper
import com.sjl.core.net.RxSchedulers
import com.sjl.core.util.BeanPropertiesUtils
import com.sjl.core.util.PreferencesHelper
import com.sjl.core.util.log.LogUtils
import io.reactivex.functions.Function
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename NewsListPresenter.java
 * @time 2018/12/18 17:09
 * @copyright(C) 2018 song
 */
class NewsListPresenter : NewsListContract.Presenter() {
    private var pageNum = 1
    override fun loadNews() {
        val apiService = RetrofitHelper.getInstance().getApiService(
            ZhiHuApiService::class.java
        )
        apiService.news.map(object : Function<NewsDto, List<NewsList>> {
            @Throws(Exception::class)
            override fun apply(news: NewsDto): List<NewsList> {
                val newsLists: MutableList<NewsList> = ArrayList()
                val top = NewsList()
                top.itemType = NewsMultiDelegateAdapter.TYPE_HEADER
                top.setTop_stories(news.getTop_stories())
                newsLists.add(top)
                val today = NewsList()
                today.itemType = NewsMultiDelegateAdapter.TYPE_HEADER_SECOND
                today.today = mContext.getString(R.string.group_tile_today_news)
                newsLists.add(today)
                val stories = news.getStories()
                var item: NewsList
                val excludeArray = arrayOf("today", "date", "top_stories")
                val preferencesHelper = PreferencesHelper.getInstance(mContext)
                val firstId = preferencesHelper.getInteger(AppConstant.SETTING.FIRST_STORY_ID, -1)
                try {
                    for (story in stories) {
                        item = NewsList()
                        item.itemType = NewsMultiDelegateAdapter.TYPE_ITEM
                        BeanPropertiesUtils.copyPropertiesExclude(story, item, excludeArray)
                        item.image = story.images[0]
                        newsLists.add(item)
                    }
                    for (story in stories) {
                        if (story.id == firstId) { //说明不是第一次获取
                            isFirstLoadFlag = false
                            break
                        } else {
                            isFirstLoadFlag = true
                        }
                    }
                    preferencesHelper.put(AppConstant.SETTING.FIRST_STORY_ID, stories[0].id)
                } catch (e: Exception) {
                    LogUtils.e("拷贝属性值异常", e)
                }
                return newsLists
            }
        }).compose(RxSchedulers.applySchedulers()).`as`(bindLifecycle())
            .subscribe({ newsLists -> mView.refreshNewsList(newsLists) }) { throwable ->
                LogUtils.e(
                    "请求知乎最新日报异常",
                    throwable
                )
            }
    }

    var isFirstLoadFlag = false
    override fun loadMore() {
        val beforeDate = getBeforeDate(-pageNum)
        LogUtils.i("加载更多日报：$beforeDate")
        val apiService = RetrofitHelper.getInstance().getApiService(
            ZhiHuApiService::class.java
        )
        apiService.getBeforeNews(beforeDate)
            .map(Function<NewsDto, List<NewsList>> { news -> //新闻列表
                val newsLists: MutableList<NewsList> = ArrayList()
                val stories = news.getStories()
                if (stories == null || stories.size == 0) {
                    return@Function newsLists
                }
                //标题日期
                val date = NewsList()
                date.itemType = NewsMultiDelegateAdapter.TYPE_DATE
                val titleDate = formatTitleDate(beforeDate)
                date.date = titleDate
                newsLists.add(date)
                var item: NewsList
                val excludeArray = arrayOf("today", "date", "top_stories")
                try {
                    for (story in stories) {
                        item = NewsList()
                        item.itemType = NewsMultiDelegateAdapter.TYPE_ITEM
                        BeanPropertiesUtils.copyPropertiesExclude(story, item, excludeArray)
                        item.image = story.images[0]
                        item.date = titleDate //方便滚动时直接设置toolBar日期
                        newsLists.add(item)
                    }
                } catch (e: Exception) {
                    LogUtils.e("拷贝属性值异常", e)
                }
                newsLists
            }).compose(RxSchedulers.applySchedulers()).`as`(bindLifecycle())
            .subscribe({ newsLists ->
                mView.showMoreNewsList(newsLists)
                pageNum++
            }) { throwable -> LogUtils.e("加载更多日报异常", throwable) }
    }

    /**
     * 获取下一个分页日期
     *
     * @param num
     * @return
     */
    private fun getBeforeDate(num: Int): String {
        val format = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.roll(Calendar.DAY_OF_YEAR, num)
        return format.format(calendar.time)
    }

    /**
     * 格式化标题日期
     * @param yyyyMMdd
     * @return
     */
    private fun formatTitleDate(yyyyMMdd: String): String {
        try {
            val format1 = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            //MM月dd日 EEEE
            val string = mContext.getString(R.string.date_format1)
            val format2 = SimpleDateFormat(string, Locale.getDefault())
            return format2.format(format1.parse(yyyyMMdd))
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return yyyyMMdd
    }

    override fun refresh() {
        pageNum = 1
        loadNews()
    }
}