package com.sjl.bookmark.ui.activity;

import android.content.Intent;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.renny.zxing.Activity.CaptureActivity;
import com.sjl.bookmark.R;
import com.sjl.bookmark.app.AppConstant;
import com.sjl.bookmark.entity.ExpressCompany;
import com.sjl.bookmark.entity.ExpressName;
import com.sjl.bookmark.entity.ExpressSearchInfo;
import com.sjl.bookmark.kotlin.language.I18nUtils;
import com.sjl.bookmark.ui.adapter.SuggestionCompanyAdapter;
import com.sjl.bookmark.ui.contract.ExpressSearchContract;
import com.sjl.bookmark.ui.presenter.ExpressSearchPresenter;
import com.sjl.core.mvp.BaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * 快递搜索
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ExpressSearchActivity.java
 * @time 2018/4/26 16:44
 * @copyright(C) 2018 song
 */
public class ExpressSearchActivity extends BaseActivity<ExpressSearchPresenter> implements TextWatcher, OnClickListener, ExpressSearchContract.View {
    @BindView(R.id.common_toolbar)
    Toolbar mToolBar;

    @BindView(R.id.et_post_id)
    EditText etPostId;
    @BindView(R.id.iv_scan)
    ImageView ivScan;
    @BindView(R.id.iv_clear)
    ImageView ivClear;
    @BindView(R.id.rv_suggestion)
    RecyclerView rvSuggestion;
    private List<ExpressCompany> suggestionList = new ArrayList<>();
    private Map<String, ExpressCompany> companyMap;
    private SuggestionCompanyAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.express_search_activity;
    }

    @Override
    protected void initView() {

    }


    @Override
    protected void initListener() {
        bindingToolbar(mToolBar, I18nUtils.getString(R.string.title_express_query));
    }

    @Override
    protected void initData() {
        companyMap = mPresenter.initCompany();
        etPostId.addTextChangedListener(this);
        ivScan.setOnClickListener(this);
        ivClear.setOnClickListener(this);

        adapter = new SuggestionCompanyAdapter(this, R.layout.company_suggestion_item, suggestionList);
        rvSuggestion.setLayoutManager(new LinearLayoutManager(this));
        rvSuggestion.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvSuggestion.setAdapter(adapter);
//        mPresenter.getSuggestionList(etPostId);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() > 0) {
            ivScan.setVisibility(View.INVISIBLE);
            ivClear.setVisibility(View.VISIBLE);
        } else {
            ivScan.setVisibility(View.VISIBLE);
            ivClear.setVisibility(View.INVISIBLE);
        }
        if (s.length() >= 8) {
            adapter.setPostId(s.toString());
            mPresenter.getSuggestionList(s.toString());
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_scan:
                startCaptureActivity();
                break;
            case R.id.iv_clear:
                etPostId.setText("");
                suggestionList.clear();
                adapter.notifyDataSetChanged();
                break;
        }
    }

    /**
     * 启动扫描
     */
    private void startCaptureActivity() {
        startActivityForResult(new Intent(this, CaptureActivity.class), AppConstant.REQUEST_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }

        switch (requestCode) {
            case AppConstant.REQUEST_CAPTURE:
                // 运单号扫描处理
                String barCode = data.getStringExtra("barCode");
                if (TextUtils.isEmpty(barCode)) {
                    Toast.makeText(this, "扫描失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                etPostId.setText(barCode.trim());
                etPostId.setSelection(etPostId.length());
                break;
            case AppConstant.REQUEST_COMPANY:
                //根据选择的快递公司和当前输入的快递单号查询快递信息
                ExpressSearchInfo mSearchInfo = (ExpressSearchInfo) data.getSerializableExtra(AppConstant.Extras.SEARCH_INFO);
                mSearchInfo.setPost_id(etPostId.getText().toString());
                Intent intent = new Intent(this, ExpressDetailActivity.class);
                intent.putExtra(AppConstant.Extras.SEARCH_INFO, mSearchInfo);
                startActivity(intent);
                break;
            default:
                break;
        }
    }


    @Override
    public void showSuggestionCompany(ExpressName expressName) {
        suggestionList.clear();
        if (expressName != null && expressName.getAuto() != null && !expressName.getAuto().isEmpty()) {
            for (ExpressName.AutoBean bean : expressName.getAuto()) {
                if (companyMap.containsKey(bean.getComCode())) {
                    suggestionList.add(companyMap.get(bean.getComCode()));
                }
            }
        }

        String label = "<font color='%1$s'>没有查到？</font> <font color='%2$s'>请选择快递公司</font>";
        String grey = String.format("#%06X", 0xFFFFFF & getResources().getColor(R.color.grey));
        String blue = String.format("#%06X", 0xFFFFFF & getResources().getColor(R.color.blue));
        ExpressCompany companyEntity = new ExpressCompany();
        companyEntity.setName(String.format(label, grey, blue));
        suggestionList.add(companyEntity);
        suggestionList.add(getOfficialHref());
        adapter.notifyDataSetChanged();
    }

    private ExpressCompany getOfficialHref() {
        String label = "<font color='%1$s'>没有查到？</font> <font color='%2$s'>使用网页查询</font>";
        String grey = String.format("#%06X", 0xFFFFFF & getResources().getColor(R.color.grey));
        String blue = String.format("#%06X", 0xFFFFFF & getResources().getColor(R.color.blue));
        ExpressCompany companyEntity = new ExpressCompany();
        companyEntity.setName(String.format(label, grey, blue));
        return companyEntity;
    }
}
