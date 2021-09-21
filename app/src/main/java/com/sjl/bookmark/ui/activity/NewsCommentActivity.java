package com.sjl.bookmark.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.sjl.bookmark.R;
import com.sjl.bookmark.kotlin.language.I18nUtils;
import com.sjl.bookmark.ui.adapter.TopTabPagerAdapter;
import com.sjl.bookmark.ui.fragment.NewsCommentFragment;
import com.sjl.bookmark.widget.ColorFlipPagerTitleView;
import com.sjl.core.mvp.BaseActivity;
import com.sjl.core.mvp.BaseFragment;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgeAnchor;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgePagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgeRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import cn.feng.skin.manager.loader.SkinManager;

/**
 * 日报评论
 *
 * @author Kelly
 * @version 1.0.0
 * @filename NewsCommentActivity.java
 * @time 2018/12/19 18:07
 * @copyright(C) 2018 song
 */
public class NewsCommentActivity extends BaseActivity {
    @BindView(R.id.common_toolbar)
    Toolbar mToolBar;
    @BindView(R.id.magic_indicator)
    MagicIndicator magicIndicator;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    private String[] TITLES = new String[]{I18nUtils.getString(R.string.news_short_comment), I18nUtils.getString(R.string.news_long_comment)};

    private TopTabPagerAdapter mTopTabPagerAdapter;
    private List<String> mDataList;

    private String id;
    private String shortCommentsCountStr;
    private String longCommentsCountStr;


    @Override
    protected int getLayoutId() {
        return R.layout.news_comment_activity;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {
    }

    @Override
    protected void initData() {

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        int longCommentsCount = intent.getIntExtra("long_comments", 0);
        int shortCommentsCount = intent.getIntExtra("short_comments", 0);
        longCommentsCountStr = longCommentsCount > 99 ? "99+" : String.valueOf(longCommentsCount);
        shortCommentsCountStr = shortCommentsCount > 99 ? "99+" : String.valueOf(shortCommentsCount);
        int commentsCount = intent.getIntExtra("comments", 0);
        bindingToolbar(mToolBar, commentsCount + getString(R.string.news_sub_comments));

        List<BaseFragment> fragmentList = new ArrayList<>();
        mDataList = Arrays.asList(TITLES);
        for (int i = 0; i < TITLES.length; i++) {
            NewsCommentFragment newsCommentFragment = new NewsCommentFragment();
            Bundle argus = new Bundle();
            argus.putInt("position", i);
            argus.putString("newsId", id);
            newsCommentFragment.setArguments(argus);
            fragmentList.add(newsCommentFragment);
        }

        mTopTabPagerAdapter = new TopTabPagerAdapter(getSupportFragmentManager(), fragmentList);
        mViewPager.setAdapter(mTopTabPagerAdapter);
        initMagicIndicator();

    }

    /**
     * 初始化指示器
     */
    private void initMagicIndicator() {
//        magicIndicator.setBackgroundColor(Color.WHITE);

        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setScrollPivotX(0.65f);

        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mDataList == null ? 0 : mDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                final BadgePagerTitleView badgePagerTitleView = new BadgePagerTitleView(context);

                SimplePagerTitleView simplePagerTitleView = new ColorFlipPagerTitleView(context);

                simplePagerTitleView.setText(mDataList.get(index));
                simplePagerTitleView.setNormalColor(Color.parseColor("#9e9e9e"));
                simplePagerTitleView.setSelectedColor(NewsCommentActivity.this.getResources().getColor(R.color.black2));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(index);
//                        badgePagerTitleView.setBadgeView(null); // cancel badge when click tab
                    }
                });
                badgePagerTitleView.setInnerPagerTitleView(simplePagerTitleView);
                // setup badge
                if (index == 0) {
                    TextView badgeTextView = (TextView) LayoutInflater.from(context).inflate(R.layout.simple_count_badge_layout, null);
                    badgeTextView.setText(shortCommentsCountStr);
                    badgePagerTitleView.setBadgeView(badgeTextView);
                } else if (index == 1) {

                    TextView badgeTextView = (TextView) LayoutInflater.from(context).inflate(R.layout.simple_count_badge_layout, null);
                    badgeTextView.setText(longCommentsCountStr);
                    badgePagerTitleView.setBadgeView(badgeTextView);
                }

                // set badge position
                badgePagerTitleView.setXBadgeRule(new BadgeRule(BadgeAnchor.CONTENT_RIGHT, UIUtil.dip2px(context, 2)));
                badgePagerTitleView.setYBadgeRule(new BadgeRule(BadgeAnchor.CONTENT_TOP, -UIUtil.dip2px(context, 5)));

                // don't cancel badge when tab selected
                badgePagerTitleView.setAutoCancelBadge(false);
                return badgePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                indicator.setLineHeight(UIUtil.dip2px(context, 2));
                indicator.setLineWidth(UIUtil.dip2px(context, 35));
                indicator.setRoundRadius(UIUtil.dip2px(context, 2));
                indicator.setStartInterpolator(new AccelerateInterpolator());
                indicator.setEndInterpolator(new DecelerateInterpolator(2.0f));
                indicator.setColors(getColorPrimary());
                return indicator;
            }

        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, mViewPager);
    }

    protected int getColorPrimary() {
        int color = SkinManager.getInstance().getColorPrimary();
        return color != -1 ? color : getResources().getColor(R.color.colorPrimary);
    }


}
