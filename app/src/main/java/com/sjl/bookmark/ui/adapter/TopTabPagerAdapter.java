package com.sjl.bookmark.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.sjl.core.mvp.BaseFragment;

import java.util.List;

/**
 * 顶部tab页面适配
 */
public class TopTabPagerAdapter extends FragmentPagerAdapter {
    private List<BaseFragment> mFragments;

    public TopTabPagerAdapter(FragmentManager fm, List<BaseFragment> list) {
        super(fm);
        this.mFragments = list;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

}
