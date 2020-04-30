package com.sjl.bookmark.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.util.MultiTypeDelegate;
import com.sjl.bookmark.R;
import com.sjl.bookmark.dao.impl.DaoFactory;
import com.sjl.bookmark.entity.zhihu.NewsList;
import com.sjl.bookmark.entity.zhihu.TopStory;
import com.sjl.bookmark.net.GlideImageLoader;
import com.sjl.bookmark.ui.activity.NewsDetailActivity;
import com.sjl.core.widget.imageview.CustomRoundAngleImageView;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 日志日报首页多条目适配器
 *
 * @author Kelly
 * @version 1.0.0
 * @filename NewsMultiDelegateAdapter.java
 * @time 2018/12/18 18:12
 * @copyright(C) 2018 song
 */
public class NewsMultiDelegateAdapter extends BaseQuickAdapter<NewsList, BaseViewHolder> {
    /**
     * 正常条目
     */
    public static final int TYPE_ITEM = 0;
    /**
     * 轮播图
     */
    public static final int TYPE_HEADER = 1;
    /**
     * 今日热闻标题
     */
    public static final int TYPE_HEADER_SECOND = 2;
    /**
     * 日期标题
     */
    public static final int TYPE_DATE = 3;
    private Context mContext;
    private  Map<String, Boolean> browseTrackMap;
    public NewsMultiDelegateAdapter(Context context, int layoutResId, @Nullable List<NewsList> data) {
        super(layoutResId, data);
        this.mContext = context;
        browseTrackMap = DaoFactory.getBrowseTrackDao().browseTrackToMap(1);

        //Step.1
        setMultiTypeDelegate(new MultiTypeDelegate<NewsList>() {
            @Override
            protected int getItemType(NewsList entity) {
                //根据你的实体类来判断布局类型
                return entity.getItemType();
            }
        });
        //Step.2
        getMultiTypeDelegate()
                .registerItemType(TYPE_ITEM, R.layout.news_list_recycle_item)
                .registerItemType(TYPE_HEADER, R.layout.news_header_recycle_item) //轮播图
                .registerItemType(TYPE_HEADER_SECOND, R.layout.news_header_second_recycle_item)
                .registerItemType(TYPE_DATE, R.layout.news_title_date_recycle_item);
    }

    @Override
    protected void convert(final BaseViewHolder helper, final NewsList item) {
        //Step.3
        switch (helper.getItemViewType()) {
            case TYPE_HEADER:
                com.youth.banner.Banner mBannerAds = helper.getView(R.id.banner_ads);
                List<String> images = new ArrayList();
                List<String> titles = new ArrayList();
                for (TopStory banner : item.getTop_stories()) {
                    images.add(banner.getImage());
                    titles.add(banner.getTitle());
                }
                mBannerAds.setImages(images)
                        .setBannerTitles(titles)
                        .setBannerStyle(BannerConfig.NUM_INDICATOR_TITLE)
                        .setImageLoader(new GlideImageLoader())
                        .start();

                mBannerAds.setOnBannerListener(new OnBannerListener() {
                    @Override
                    public void OnBannerClick(int position) {
                        TopStory topStory = item.getTop_stories().get(position);
                        NewsDetailActivity.startActivity(mContext,topStory.getId(),topStory.getTitle(),topStory.getImage());
                    }
                });
                break;
            case TYPE_HEADER_SECOND:

                helper.setVisible(R.id.red_dot_indicator,firstLoadFlag);

                break;
            case TYPE_DATE:
                helper.setText(R.id.date_indicator,item.getDate());
                break;
            case TYPE_ITEM:

                //文章阅读后变色
                if (browseTrackMap.get(String.valueOf(item.getId())) != null) {
                    helper.setTextColor(R.id.title_text, ContextCompat.getColor(mContext, R.color.gray_600));
                } else {
                    Resources.Theme theme = mContext.getTheme();
                    TypedValue customTextColor = new TypedValue();//自定义字体颜色
                    theme.resolveAttribute(R.attr.customTextColor, customTextColor, true);
                    helper.setTextColor(R.id.title_text, mContext.getResources().getColor(customTextColor.resourceId));
                }
                helper.setText(R.id.title_text, item.getTitle());
                Glide.with(mContext)
                        .load(item.getImage())
                        .placeholder(R.mipmap.img_loading_placehoder)
                        .error(R.mipmap.ic_load_error)
                        .into((CustomRoundAngleImageView) helper.getView(R.id.title_image));
                helper.getView(R.id.cardview).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NewsDetailActivity.startActivity(mContext, item.getId(), item.getTitle(), item.getImage());

                        v.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                helper.setTextColor(R.id.title_text, ContextCompat.getColor(mContext, R.color.gray_600));
                                boolean ret = DaoFactory.getBrowseTrackDao().saveBrowseTrackByType(1, String.valueOf(item.getId()));
                                if(ret){//新增刷新
                                    browseTrackMap = DaoFactory.getBrowseTrackDao().browseTrackToMap(1);
                                }
                            }
                        },200);

                    }
                });
                break;

            default:
                break;
        }
    }



    public boolean firstLoadFlag;


}
