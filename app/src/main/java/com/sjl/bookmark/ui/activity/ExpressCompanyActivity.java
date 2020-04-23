package com.sjl.bookmark.ui.activity;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.sjl.bookmark.R;
import com.sjl.bookmark.entity.ExpressCompany;
import com.sjl.bookmark.kotlin.language.I18nUtils;
import com.sjl.bookmark.ui.adapter.ExpressCompanyAdapter;
import com.sjl.bookmark.ui.contract.ExpressCompanyContract;
import com.sjl.bookmark.ui.presenter.ExpressCompanyPresenter;
import com.sjl.core.mvp.BaseActivity;
import com.sjl.core.util.log.LogUtils;
import com.sjl.core.widget.IndexBar;

import java.util.List;

import butterknife.BindView;

/**
 * 快递公司
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ExpressCompanyActivity.java
 * @time 2018/4/28 18:02
 * @copyright(C) 2018 song
 */
public class ExpressCompanyActivity extends BaseActivity<ExpressCompanyPresenter> implements IndexBar.OnIndexChangedListener, ExpressCompanyContract.View {
    @BindView(R.id.common_toolbar)
    Toolbar mToolBar;
    @BindView(R.id.rv_company)
    RecyclerView rvCompany;
    @BindView(R.id.ib_indicator)
    IndexBar ibIndicator;
    @BindView(R.id.tv_indicator)
    TextView tvIndicator;
    private List<ExpressCompany> companyList;
    private ExpressCompanyAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.express_company_activity;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {
        bindingToolbar(mToolBar, I18nUtils.getString(R.string.title_choose_express_company));
        ibIndicator.setOnIndexChangedListener(this);

    }

    @Override
    protected void initData() {
        companyList = mPresenter.initCompany();
        adapter = new ExpressCompanyAdapter(this, companyList);
        rvCompany.setLayoutManager(new LinearLayoutManager(this));
        rvCompany.setAdapter(adapter);
        rvCompany.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }


    @Override
    public void onIndexChanged(String index, boolean isDown) {
        LogUtils.i("current index is " + index);
        int position = -1;
        for (ExpressCompany company : companyList) {
            if (TextUtils.equals(company.getName(), index)) {
                position = companyList.indexOf(company);
                break;
            }
        }
        if (position != -1) {
//            rvCompany.scrollToPosition(position);
            /**准确定位到指定位置，并且将指定位置的item置顶，
             若直接调用scrollToPosition(...)方法，则不会置顶。**/
            LinearLayoutManager layoutManager = (LinearLayoutManager) rvCompany.getLayoutManager();
            layoutManager.scrollToPositionWithOffset(position, 0);

            layoutManager.setStackFromEnd(true);//设置为true时，RecycelrView会自动滑倒尾部，直到最后一条数据完整展示
        }


        tvIndicator.setText(index);
        tvIndicator.setVisibility(isDown ? View.VISIBLE : View.GONE);
    }
}
