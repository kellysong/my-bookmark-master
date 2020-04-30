package com.sjl.bookmark.ui.activity;

import android.content.Intent;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.sjl.bookmark.R;
import com.sjl.bookmark.entity.table.HistoryExpress;
import com.sjl.bookmark.kotlin.language.I18nUtils;
import com.sjl.bookmark.ui.adapter.HistoryExpressAdapter;
import com.sjl.bookmark.ui.contract.ExpressContract;
import com.sjl.bookmark.ui.presenter.ExpressPresenter;
import com.sjl.core.mvp.BaseActivity;
import com.sjl.core.util.AppUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * ExpressActivity和ExpressHistoryActivity共用一个ExpressContract、ExpressPresenter
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ExpressActivity.java
 * @time 2018/4/26 14:26
 * @copyright(C) 2018 song
 */
public class ExpressActivity extends BaseActivity<ExpressPresenter> implements ExpressContract.View {
    @BindView(R.id.common_toolbar)
    Toolbar mToolBar;
    @BindView(R.id.rv_un_check)
    RecyclerView rvUnCheck;
    @BindView(R.id.tv_empty)
    TextView tvEmpty;

    private List<HistoryExpress> unCheckList = new ArrayList<>();
    private HistoryExpressAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.express_activity;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {
        bindingToolbar(mToolBar, I18nUtils.getString(R.string.tool_my_express));
    }

    @Override
    protected void initData() {
        adapter = new HistoryExpressAdapter(this, R.layout.history_express_recycle_item, unCheckList);
        rvUnCheck.setLayoutManager(new LinearLayoutManager(this));
        rvUnCheck.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvUnCheck.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.express_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuSearch) {//查件
            startActivity(new Intent(this, ExpressSearchActivity.class));
        } else if (item.getItemId() == R.id.menuHistory) {//历史记录
            startActivity(new Intent(this, ExpressHistoryActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.unCheckList.clear();
        this.unCheckList.addAll(unCheckList);
        mPresenter.getUnCheckList();
    }

    @Override
    public void setHistoryExpress(List<HistoryExpress> historyExpresses) {
        if (AppUtils.isEmpty(historyExpresses)) {
            rvUnCheck.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            rvUnCheck.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            this.unCheckList.addAll(historyExpresses);
            adapter.notifyDataSetChanged();
        }
    }
}
