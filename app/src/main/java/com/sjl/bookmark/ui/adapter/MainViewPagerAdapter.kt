package com.sjl.bookmark.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.sjl.core.mvp.BaseFragment

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename MainViewPagerAdapter.java
 * @time 2018/6/25 14:09
 * @copyright(C) 2018 song
 */
class MainViewPagerAdapter(fm: FragmentManager, private val mFragments: List<BaseFragment<*>>) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return mFragments[position]
    }

    override fun getCount(): Int {
        return mFragments.size
    }
}