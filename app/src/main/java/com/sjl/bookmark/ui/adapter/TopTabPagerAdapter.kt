package com.sjl.bookmark.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.sjl.core.mvp.BaseFragment

/**
 * 顶部tab页面适配
 */
class TopTabPagerAdapter(fm: FragmentManager, private val mFragments: List<BaseFragment<*>>) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return mFragments[position]
    }

    override fun getCount(): Int {
        return mFragments.size
    }
}