package com.sjl.bookmark.ui.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.sjl.bookmark.entity.Category.ChildrenBean
import com.sjl.bookmark.net.HttpConstant
import com.sjl.bookmark.ui.fragment.ArticleListFragment
import java.util.*

/**
 * 二级分类页面
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ArticleTypePagerListAdapter.java
 * @time 2018/3/23 13:58
 * @copyright(C) 2018 song
 */
class ArticleTypePagerListAdapter(fm: FragmentManager, private val mChildrenData: List<ChildrenBean>?) : FragmentPagerAdapter(fm) {
    private val mArticleTypeFragments: MutableList<ArticleListFragment>
    override fun getItem(position: Int): Fragment {
        return mArticleTypeFragments[position]
    }

    override fun getCount(): Int {
        return mArticleTypeFragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mChildrenData!![position].name
    }

    init {
        mArticleTypeFragments = ArrayList()
        mChildrenData?.let {
            if (it.isNotEmpty()){
                for (childrenBean in it) {
                    val articleListFragment = ArticleListFragment()
                    val argus = Bundle()
                    argus.putInt(HttpConstant.CONTENT_CID_KEY, childrenBean.id)
                    articleListFragment.arguments = argus
                    mArticleTypeFragments.add(articleListFragment)
                }
            }
        }

    }
}