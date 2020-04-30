package com.sjl.bookmark.ui.activity;

import android.content.Intent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.sjl.bookmark.R;
import com.sjl.bookmark.app.AppConstant;
import com.sjl.bookmark.kotlin.language.I18nUtils;
import com.sjl.bookmark.ui.adapter.AccountPagerListAdapter;
import com.sjl.bookmark.ui.fragment.AccountListFragment;
import com.sjl.core.entity.EventBusDto;
import com.sjl.core.mvp.BaseActivity;
import com.sjl.core.util.log.LogUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename AccountIndexActivity.java
 * @time 2018/3/7 10:44
 * @copyright(C) 2018 song
 */
public class AccountIndexActivity extends BaseActivity {
    private static final int INDEX_REQUEST_CODE = 1;
    public static final int ADD_SUCCESS = 1;

    @BindView(R.id.common_toolbar)
    Toolbar mToolBar;
    @BindView(R.id.tablayout)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private List<Fragment> list;
    private AccountPagerListAdapter adapter;
    private int position;
    private String[] titles = {"在用", "闲置", "作废"};


    @Override
    protected int getLayoutId() {
        return R.layout.activity_account_list;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initListener() {
        bindingToolbar(mToolBar, I18nUtils.getString(R.string.tool_account_manager));
        fab.setOnClickListener(new View.OnClickListener() {//添加
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountIndexActivity.this, AccountEditActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("CREATE_MODE", AppConstant.SETTING.CREATE_MODE);
                startActivityForResult(intent, INDEX_REQUEST_CODE);
            }
        });
    }

    @Override
    public void initData() {
        //页面，数据源
        list = new ArrayList<>();
        list.add(new AccountListFragment());
        list.add(new AccountListFragment());
        list.add(new AccountListFragment());
        //ViewPager的适配器
        adapter = new AccountPagerListAdapter(getSupportFragmentManager(), list, titles);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                AccountIndexActivity.this.position = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //绑定
        tabLayout.setupWithViewPager(viewPager);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.i("requestCode=" + requestCode + ",resultCode=" + resultCode);
        if (requestCode == INDEX_REQUEST_CODE) {
            if (resultCode == ADD_SUCCESS) {
                //添加回调
                EventBusDto eventBusDto = new EventBusDto(position, AppConstant.ACCOUNT_REFRESH_EVENT_CODE, true);//只更新当前对应页
                EventBus.getDefault().post(eventBusDto);
            }
        }

    }
}
