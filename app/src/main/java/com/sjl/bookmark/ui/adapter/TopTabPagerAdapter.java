package com.sjl.bookmark.ui.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

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
