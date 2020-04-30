package com.sjl.bookmark.ui.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.sjl.core.mvp.BaseFragment;

import java.util.List;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename MainViewPagerAdapter.java
 * @time 2018/6/25 14:09
 * @copyright(C) 2018 song
 */
public class MainViewPagerAdapter extends FragmentPagerAdapter {
    private List<BaseFragment> mFragments;

    public MainViewPagerAdapter(FragmentManager fm, List<BaseFragment> list) {
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
