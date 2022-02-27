package com.sjl.bookmark.ui.adapter

import androidx.fragment.app.FragmentPagerAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.sjl.core.util.log.LogUtils

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename AccountPagerListAdapter.java
 * @time 2018/3/7 14:04
 * @copyright(C) 2018 song
 */
class AccountPagerListAdapter(fm: FragmentManager, private val list: List<Fragment>, private val titles: Array<String>) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        LogUtils.i("AccountPagerListAdapter:$position")
        val fragment = list[position]
        val argus = Bundle()
        argus.putInt("position", position)
        fragment.arguments = argus
        return fragment
    }

    override fun getCount(): Int {
        return list.size
    }

    //重写这个方法，将设置每个Tab的标题
    override fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
    }
}