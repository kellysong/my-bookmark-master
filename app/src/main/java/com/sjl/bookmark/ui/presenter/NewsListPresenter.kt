package com.sjl.bookmark.ui.presenter;

import com.sjl.bookmark.R;
import com.sjl.bookmark.api.ZhiHuApiService;
import com.sjl.bookmark.app.AppConstant;
import com.sjl.bookmark.entity.zhihu.NewsDto;
import com.sjl.bookmark.entity.zhihu.NewsList;
import com.sjl.bookmark.entity.zhihu.Story;
import com.sjl.bookmark.ui.adapter.NewsMultiDelegateAdapter;
import com.sjl.bookmark.ui.contract.NewsListContract;
import com.sjl.core.net.RetrofitHelper;
import com.sjl.core.net.RxSchedulers;
import com.sjl.core.util.BeanPropertiesUtils;
import com.sjl.core.util.log.LogUtils;
import com.sjl.core.util.PreferencesHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename NewsListPresenter.java
 * @time 2018/12/18 17:09
 * @copyright(C) 2018 song
 */
public class NewsListPresenter extends NewsListContract.Presenter {

    private int pageNum = 1;

    @Override
    public void loadNews() {
        ZhiHuApiService apiService = RetrofitHelper.getInstance().getApiService(ZhiHuApiService.class);
        apiService.getNews().map(new Function<NewsDto, List<NewsList>>() {
            @Override
            public List<NewsList> apply(NewsDto news) throws Exception {
                List<NewsList> newsLists = new ArrayList<>();
                NewsList top = new NewsList();
                top.setItemType(NewsMultiDelegateAdapter.TYPE_HEADER);
                top.setTop_stories(news.getTop_stories());
                newsLists.add(top);
                NewsList today = new NewsList();
                today.setItemType(NewsMultiDelegateAdapter.TYPE_HEADER_SECOND);
                today.setToday(mContext.getString(R.string.group_tile_today_news));
                newsLists.add(today);
                ArrayList<Story> stories = news.getStories();
                NewsList item;
                String[] excludeArray = new String[]{"today", "date", "top_stories"};
                PreferencesHelper preferencesHelper = PreferencesHelper.getInstance(mContext);
                int  firstId = preferencesHelper.getInteger(AppConstant.SETTING.FIRST_STORY_ID, -1);
                try {
                    for (Story story : stories) {
                        item = new NewsList();
                        item.setItemType(NewsMultiDelegateAdapter.TYPE_ITEM);
                        BeanPropertiesUtils.copyPropertiesExclude(story, item, excludeArray);
                        item.setImage(story.getImages().get(0));
                        newsLists.add(item);
                    }

                    for (Story story : stories) {
                        if (story.getId() == firstId){//说明不是第一次获取
                            setFirstLoadFlag(false);
                            break;
                        }else{
                            setFirstLoadFlag(true);
                        }
                    }
                    preferencesHelper.put(AppConstant.SETTING.FIRST_STORY_ID,stories.get(0).getId());
                } catch (Exception e) {
                    LogUtils.e("拷贝属性值异常", e);
                }
                return newsLists;
            }
        }).
                compose(RxSchedulers.<List<NewsList>>applySchedulers()).as(this.<List<NewsList>>bindLifecycle())
                .subscribe(new Consumer<List<NewsList>>() {
                    @Override
                    public void accept(List<NewsList> newsLists) throws Exception {
                        mView.refreshNewsList(newsLists);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("请求知乎最新日报异常", throwable);
                    }
                });
    }


    private boolean firstLoadFlag;

    public boolean isFirstLoadFlag() {
        return firstLoadFlag;
    }

    public void setFirstLoadFlag(boolean firstLoadFlag) {
        this.firstLoadFlag = firstLoadFlag;
    }

    @Override
    public void loadMore() {
        final String beforeDate = getBeforeDate(-pageNum);
        LogUtils.i("加载更多日报：" + beforeDate);
        ZhiHuApiService apiService = RetrofitHelper.getInstance().getApiService(ZhiHuApiService.class);
        apiService.getBeforeNews(beforeDate).map(new Function<NewsDto, List<NewsList>>() {
            @Override
            public List<NewsList> apply(NewsDto news) throws Exception {
                //新闻列表
                List<NewsList> newsLists = new ArrayList<>();
                ArrayList<Story> stories = news.getStories();
                if (stories == null || stories.size() == 0) {
                    return newsLists;
                }
                //标题日期
                NewsList date = new NewsList();
                date.setItemType(NewsMultiDelegateAdapter.TYPE_DATE);
                String titleDate = formatTitleDate(beforeDate);
                date.setDate(titleDate);
                newsLists.add(date);

                NewsList item;
                String[] excludeArray = new String[]{"today", "date", "top_stories"};
                try {
                    for (Story story : stories) {
                        item = new NewsList();
                        item.setItemType(NewsMultiDelegateAdapter.TYPE_ITEM);
                        BeanPropertiesUtils.copyPropertiesExclude(story, item, excludeArray);
                        item.setImage(story.getImages().get(0));
                        item.setDate(titleDate);//方便滚动时直接设置toolBar日期
                        newsLists.add(item);
                    }
                } catch (Exception e) {
                    LogUtils.e("拷贝属性值异常", e);
                }
                return newsLists;
            }
        }).
                compose(RxSchedulers.<List<NewsList>>applySchedulers()).as(this.<List<NewsList>>bindLifecycle())
                .subscribe(new Consumer<List<NewsList>>() {
                    @Override
                    public void accept(List<NewsList> newsLists) throws Exception {
                        mView.showMoreNewsList(newsLists);
                        pageNum++;
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("加载更多日报异常", throwable);
                    }
                });
    }

    /**
     * 获取下一个分页日期
     *
     * @param num
     * @return
     */
    private String getBeforeDate(int num) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd",Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.roll(Calendar.DAY_OF_YEAR, num);
        return format.format(calendar.getTime());
    }

    /**
     * 格式化标题日期
     * @param yyyyMMdd
     * @return
     */
    private  String formatTitleDate(String yyyyMMdd) {
        try {
            SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd",Locale.getDefault());
            //MM月dd日 EEEE
            String string = mContext.getString(R.string.date_format1);
            SimpleDateFormat format2 = new SimpleDateFormat(string,Locale.getDefault());
            return format2.format(format1.parse(yyyyMMdd));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return yyyyMMdd;
    }
    @Override
    public void refresh() {
        pageNum = 1;
        loadNews();
    }
}
