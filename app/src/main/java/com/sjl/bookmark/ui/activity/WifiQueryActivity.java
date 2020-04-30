package com.sjl.bookmark.ui.activity;

import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sjl.bookmark.R;
import com.sjl.bookmark.entity.WifiInfo;
import com.sjl.bookmark.kotlin.language.I18nUtils;
import com.sjl.bookmark.ui.contract.WifiQueryContract;
import com.sjl.bookmark.ui.presenter.WifiQueryPresenter;
import com.sjl.core.mvp.BaseActivity;
import com.sjl.core.util.log.LogUtils;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * wifi密码查询
 * add by Kelly on 20170302
 */
public class WifiQueryActivity extends BaseActivity<WifiQueryPresenter> implements WifiQueryContract.View{
    @BindView(R.id.common_toolbar)
    Toolbar mToolBar;
    @BindView(R.id.listview)
    ListView mListView;
    @BindView(R.id.ll_empty)
    LinearLayout ll_empty;
    @BindView(R.id.text_hint)
    TextView mTextHint;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_wifi_query;
    }

    @Override
    protected void initView() {

    }


    @Override
    protected void initListener() {
        bindingToolbar(mToolBar, I18nUtils.getString(R.string.tool_wifi_query));
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
    }

    /**
     * ctivity界面被显示出来的时候执行的，用户可见，包括有一个activity在他上面，但没有将它完全覆盖，用户可以看到部分activity但不能与它交互
     */
    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.initWifiInfo();
    }

    /**
     * 只能有一个参数
     * @param msg
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void noRoot(String msg) {
        LogUtils.i("msg1:"+msg);
        ll_empty.setVisibility(View.VISIBLE);
        mTextHint.setText(R.string.not_root_hint_txt);
        Toast.makeText(getApplicationContext(), R.string.not_root_hint, Toast.LENGTH_LONG).show();
    }

    /*测试=======================start*/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void noRoot2(String msg) {
        LogUtils.i("msg2:"+msg);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void no1(Map msg) {

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void no2(Map msg) {

    }
    /*测试=======================end*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void showWifiInfo(final List<WifiInfo> wifiInfos) {
        if (wifiInfos != null && wifiInfos.size() > 0) {
            mListView.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);
            // 列表倒序
            Collections.reverse(wifiInfos);
            mListView.setAdapter(new CommonAdapter<WifiInfo>(this, R.layout.wifi_query_list_item, wifiInfos) {
                @Override
                protected void convert(ViewHolder viewHolder, WifiInfo item, int position) {
                    viewHolder.setText(R.id.item_name, getString(R.string.iten_name_hint) + item.getName());
                    viewHolder.setText(R.id.item_password, getString(R.string.item_pasword_hint) + item.getPassword());
                    viewHolder.setText(R.id.item_type,"加密类型："+item.getEncryptType());
                }
            });
            mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    mPresenter.copyWifiPassword(wifiInfos.get(position).getPassword());
                    return true;
                }
            });
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mPresenter.connectWifi(wifiInfos.get(position));
                }
            });
        } else {
            mListView.setVisibility(View.GONE);
            ll_empty.setVisibility(View.VISIBLE);
        }
    }
}
