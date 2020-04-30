package com.sjl.bookmark.ui.adapter;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.sjl.bookmark.entity.Category;
import com.sjl.bookmark.net.HttpConstant;
import com.sjl.bookmark.ui.fragment.ArticleListFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 二级分类页面
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ArticleTypePagerListAdapter.java
 * @time 2018/3/23 13:58
 * @copyright(C) 2018 song
 */
public class ArticleTypePagerListAdapter extends FragmentPagerAdapter {
    private List<Category.ChildrenBean> mChildrenData;
    private List<ArticleListFragment> mArticleTypeFragments;


    public ArticleTypePagerListAdapter(FragmentManager fm, List<Category.ChildrenBean> childrenData) {
        super(fm);
        this.mChildrenData = childrenData;
        mArticleTypeFragments = new ArrayList<>();
        if (mChildrenData == null || mChildrenData.isEmpty()) return;
        for (Category.ChildrenBean childrenBean : mChildrenData) {
            ArticleListFragment articleListFragment = new ArticleListFragment();
            Bundle argus = new Bundle();
            argus.putInt(HttpConstant.CONTENT_CID_KEY, childrenBean.getId());
            articleListFragment.setArguments(argus);
            mArticleTypeFragments.add(articleListFragment);
        }
    }

    @Override
    public Fragment getItem(int position) {
        return mArticleTypeFragments.get(position);
    }

    @Override
    public int getCount() {
        return mArticleTypeFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mChildrenData.get(position).getName();
    }
}
