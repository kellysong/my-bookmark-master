package com.sjl.bookmark.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sjl.bookmark.R;
import com.sjl.bookmark.app.AppConstant;
import com.sjl.bookmark.entity.ExpressDetail;
import com.sjl.bookmark.entity.ExpressSearchInfo;
import com.sjl.bookmark.net.HttpConstant;
import com.sjl.bookmark.ui.adapter.ExpressDetailAdapter;
import com.sjl.bookmark.ui.contract.ExpressDetailContract;
import com.sjl.bookmark.ui.presenter.ExpressDetailPresenter;
import com.sjl.core.mvp.BaseActivity;
import com.sjl.core.util.SnackbarUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

/**
 * 快递明细
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ExpressDetailActivity.java
 * @time 2018/5/2 11:03
 * @copyright(C) 2018 song
 */
public class ExpressDetailActivity extends BaseActivity<ExpressDetailPresenter> implements ExpressDetailContract.View, View.OnClickListener {
    @BindView(R.id.common_toolbar)
    Toolbar mToolBar;

    @BindView(R.id.iv_logo)
    ImageView ivLogo;
    @BindView(R.id.tv_post_id)
    TextView tvPostId;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.ll_result)
    LinearLayout llResult;
    @BindView(R.id.rv_result_list)
    RecyclerView rvResultList;
    @BindView(R.id.btn_remark)
    Button btnRemark;
    @BindView(R.id.ll_no_exist)
    LinearLayout llNoExist;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.ll_error)
    LinearLayout llError;
    @BindView(R.id.btn_retry)
    Button btnRetry;
    @BindView(R.id.tv_searching)
    TextView tvSearching;
    private ExpressSearchInfo searchInfo;
    private List<ExpressDetail.DataBean> resultItemList = new ArrayList<ExpressDetail.DataBean>();
    private ExpressDetailAdapter expressDetailAdapter;
    private String remark;

    @Override
    protected int getLayoutId() {
        return R.layout.express_detail_activity;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {
        bindingToolbar(mToolBar, getString(R.string.express_logistics_detail));
        btnRemark.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnRetry.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mPresenter.init(getIntent());
        expressDetailAdapter = new ExpressDetailAdapter(this, R.layout.express_detail_recycle_item, resultItemList);
        rvResultList.setLayoutManager(new LinearLayoutManager(this));
        rvResultList.setAdapter(expressDetailAdapter);
        mPresenter.queryExpressDetail(searchInfo);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_remark:
                remark();//运单备注
                break;
            case R.id.btn_save:
                if (TextUtils.equals(btnSave.getText().toString(), getString(R.string.waybill_note))) {
                    remark();
                } else {//保存运单信息，当查询不到且本地没有缓存记录时触发该动作
                    searchInfo.setIs_check(String.valueOf(AppConstant.SignStatus.NOT_SINGED));
                    mPresenter.updateExpressDetail(searchInfo, null);
                    View view = ExpressDetailActivity.this.getWindow().getDecorView().findViewById(android.R.id.content);
                    SnackbarUtils.makeShort(view, R.string.save_success).show();
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (!ExpressDetailActivity.this.isFinishing()) {
//                                startActivity(new Intent(ExpressDetailActivity.this, ExpressActivity.class));
//                                finish();
//                            }
//                        }
//                    }, 400);
                }
                break;
            case R.id.btn_retry://重试
                llResult.setVisibility(View.GONE);
                llNoExist.setVisibility(View.GONE);
                llError.setVisibility(View.GONE);
                tvSearching.setVisibility(View.VISIBLE);
                mPresenter.queryExpressDetail(searchInfo);
                break;
            default:
                break;
        }
    }

    public static void start(Context context, ExpressSearchInfo searchInfo) {
        Intent intent = new Intent(context, ExpressDetailActivity.class);
        intent.putExtra(AppConstant.Extras.SEARCH_INFO, searchInfo);
        context.startActivity(intent);
    }

    @Override
    public void showExpressSource(ExpressSearchInfo expressSearchInfo) {
        this.searchInfo = expressSearchInfo;
        Glide.with(this)
                .load(HttpConstant.KUAIDI100_BASE_URL + "images/all/" + searchInfo.getLogo())
                .dontAnimate()
                .placeholder(R.mipmap.ic_default_logo)
                .into(ivLogo);
        showExpressRemark();

    }

    /**
     * 显示快递备注信息
     */
    private void showExpressRemark() {
        remark = mPresenter.getExpressRemark(searchInfo.getPost_id());
        if (TextUtils.isEmpty(remark)) {
            tvName.setText(searchInfo.getName());
            tvPostId.setText(searchInfo.getPost_id());
        } else {
            tvName.setText(remark);
            tvPostId.setText(searchInfo.getName().concat(" ").concat(searchInfo.getPost_id()));
        }
    }

    @Override
    public void showExpressDetail(ExpressDetail expressDetail) {
        if (expressDetail.getStatus().equals("200")) {
            llResult.setVisibility(View.VISIBLE);
            llNoExist.setVisibility(View.GONE);
            llError.setVisibility(View.GONE);
            tvSearching.setVisibility(View.GONE);
            resultItemList.addAll(expressDetail.getData());
            expressDetailAdapter.notifyDataSetChanged();
            searchInfo.setIs_check(expressDetail.getIscheck());
            mPresenter.updateExpressDetail(searchInfo, expressDetail);
        } else {//失败
            llResult.setVisibility(View.GONE);
            llNoExist.setVisibility(View.VISIBLE);
            llError.setVisibility(View.GONE);
            tvSearching.setVisibility(View.GONE);
            boolean ret = mPresenter.checkExistExpress(searchInfo.getPost_id());
            btnSave.setText(ret ? getText(R.string.waybill_note) : getText(R.string.waybill_note_save));
            showLongToast(expressDetail.getStatus() + ":" + expressDetail.getMessage());
        }
    }

    @Override
    public void showErrorInfo() {
        llResult.setVisibility(View.GONE);
        llNoExist.setVisibility(View.GONE);
        llError.setVisibility(View.VISIBLE);
        tvSearching.setVisibility(View.GONE);
    }


    /**
     * 备注信息
     */
    private void remark() {
        View view = getLayoutInflater().inflate(R.layout.dialog_input, null);
        final EditText etRemark = view.findViewById(R.id.et_remark);
        etRemark.setText(remark);
        etRemark.setSelection(etRemark.length());
        new AlertDialog.Builder(this)
                .setTitle(R.string.remark)
                .setView(view)
                .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPresenter.updateExpressRemark(searchInfo.getPost_id(), etRemark.getText().toString());
                        showExpressRemark();
                        View view = ExpressDetailActivity.this.getWindow().getDecorView().findViewById(android.R.id.content);
                        SnackbarUtils.makeShort(view, R.string.remark_success).show();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}
