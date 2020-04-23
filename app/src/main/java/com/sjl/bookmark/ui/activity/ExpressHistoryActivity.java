package com.sjl.bookmark.ui.activity;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
 * 快递历史记录
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ExpressHistoryActivity.java
 * @time 2018/5/2 18:09
 * @copyright(C) 2018 song
 */
public class ExpressHistoryActivity extends BaseActivity<ExpressPresenter> implements ExpressContract.View {
    @BindView(R.id.common_toolbar)
    Toolbar mToolBar;

    @BindView(R.id.rv_history_list)
    RecyclerView rvHistoryList;
    @BindView(R.id.tv_empty)
    TextView tvEmpty;

    private List<HistoryExpress> historyExpresses = new ArrayList<>();
    private HistoryExpressAdapter historyExpressAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.express_history_activity;
    }

    @Override
    protected void initView() {

    }


    @Override
    protected void initListener() {
        bindingToolbar(mToolBar, I18nUtils.getString(R.string.title_history_record));
    }

    @Override
    protected void initData() {
        historyExpressAdapter = new HistoryExpressAdapter(this, R.layout.history_express_recycle_item, historyExpresses);
        rvHistoryList.setLayoutManager(new LinearLayoutManager(this));
        rvHistoryList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvHistoryList.setAdapter(historyExpressAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mPresenter.getHistoryExpresses();
    }

    @Override
    public void setHistoryExpress(List<HistoryExpress> historyExpresses) {
        if (AppUtils.isEmpty(historyExpresses)) {
            rvHistoryList.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            rvHistoryList.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            this.historyExpresses.clear();
            this.historyExpresses.addAll(historyExpresses);
            historyExpressAdapter.notifyDataSetChanged();

        }
    }
}
