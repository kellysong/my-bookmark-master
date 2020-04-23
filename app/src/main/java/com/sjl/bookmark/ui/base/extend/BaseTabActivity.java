package com.sjl.bookmark.ui.base.extend;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.sjl.bookmark.R;
import com.sjl.core.mvp.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BaseTabActivity.java
 * @time 2018/12/10 14:33
 * @copyright(C) 2018 song
 */
public abstract class BaseTabActivity extends BaseActivity {
    /**************View***************/
    @BindView(R.id.tab_tl_indicator)
    protected TabLayout mTabLayout;
    @BindView(R.id.tab_vp)
    protected ViewPager mViewPager;
    /************Params*******************/
    private List<Fragment> mFragmentList;
    private List<String> mTitleList;

    /**************abstract***********/
    protected abstract List<Fragment> createTabFragments();

    protected abstract List<String> createTabTitles();

    @Override
    protected void initTab() {
        setUpTabLayout();
    }


    private void setUpTabLayout() {
        mFragmentList = createTabFragments();
        mTitleList = createTabTitles();
        checkParamsIsRight();
        TabFragmentPageAdapter adapter = new TabFragmentPageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(3);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    /**
     * 检查输入的参数是否正确。即Fragment和title是成对的。
     */
    private void checkParamsIsRight() {
        if (mFragmentList == null || mTitleList == null) {
            throw new IllegalArgumentException("fragmentList or titleList doesn't have null.");
        }

        if (mFragmentList.size() != mTitleList.size())
            throw new IllegalArgumentException("fragment and title size must equal.");
    }


    protected List<Fragment> buildFragmentList(Fragment... fragmentParams) {
        if (fragmentParams == null || fragmentParams.length == 0) {
            throw new IllegalArgumentException("fragmentParams is null.");
        }

        List<Fragment> fragments = new ArrayList<>();
        for (Fragment f:fragmentParams){
            fragments.add(f);
        }
        return fragments;
    }


    /******************inner class*****************/
    class TabFragmentPageAdapter extends FragmentPagerAdapter {

        public TabFragmentPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitleList.get(position);
        }
    }
}
