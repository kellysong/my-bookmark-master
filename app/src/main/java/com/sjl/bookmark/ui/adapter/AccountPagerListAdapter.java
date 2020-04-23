package com.sjl.bookmark.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.sjl.core.util.log.LogUtils;

import java.util.List;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename AccountPagerListAdapter.java
 * @time 2018/3/7 14:04
 * @copyright(C) 2018 song
 */
public class AccountPagerListAdapter extends FragmentPagerAdapter {
    private List<Fragment> list;
    private String[] titles;

    public AccountPagerListAdapter(FragmentManager fm, List<Fragment> list, String[] titles) {
        super(fm);
        this.list = list;
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        LogUtils.i("AccountPagerListAdapter:"+position);
        Fragment fragment = list.get(position);
        Bundle argus = new Bundle();
        argus.putInt("position", position);
        fragment.setArguments(argus);
        return fragment;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    //重写这个方法，将设置每个Tab的标题
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
