package com.sjl.bookmark.ui.activity

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.sjl.bookmark.R
import com.sjl.bookmark.app.AppConstant
import com.sjl.bookmark.entity.ExpressDetail
import com.sjl.bookmark.entity.ExpressDetail.DataBean
import com.sjl.bookmark.entity.ExpressSearchInfo
import com.sjl.bookmark.net.HttpConstant
import com.sjl.bookmark.ui.adapter.ExpressDetailAdapter
import com.sjl.bookmark.ui.contract.ExpressDetailContract
import com.sjl.bookmark.ui.presenter.ExpressDetailPresenter
import com.sjl.core.mvp.BaseActivity
import com.sjl.core.util.SnackbarUtils
import kotlinx.android.synthetic.main.express_detail_activity.*
import kotlinx.android.synthetic.main.toolbar_default.*
import java.util.*

/**
 * 快递明细
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ExpressDetailActivity.java
 * @time 2018/5/2 11:03
 * @copyright(C) 2018 song
 */
class ExpressDetailActivity : BaseActivity<ExpressDetailPresenter>(),
    ExpressDetailContract.View, View.OnClickListener {

    private var searchInfo: ExpressSearchInfo? = null
    private val resultItemList: MutableList<DataBean> = ArrayList()
    private lateinit var expressDetailAdapter: ExpressDetailAdapter
    private var remark: String? = null
    override fun getLayoutId(): Int {
        return R.layout.express_detail_activity
    }

    override fun initView() {}
    override fun initListener() {
        bindingToolbar(common_toolbar, getString(R.string.express_logistics_detail))
        btn_remark.setOnClickListener(this)
        btn_save.setOnClickListener(this)
        btn_retry.setOnClickListener(this)
    }

    override fun initData() {
        mPresenter.init(intent)
        expressDetailAdapter =
            ExpressDetailAdapter(this, R.layout.express_detail_recycle_item, resultItemList)
        rv_result_list.layoutManager = LinearLayoutManager(this)
        rv_result_list.adapter = expressDetailAdapter
        mPresenter.queryExpressDetail((searchInfo)!!)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_remark -> remark() //运单备注
            R.id.btn_save -> if (TextUtils.equals(
                    btn_save.text.toString(),
                    getString(R.string.waybill_note)
                )
            ) {
                remark()
            } else { //保存运单信息，当查询不到且本地没有缓存记录时触发该动作
                searchInfo?.is_check = AppConstant.SignStatus.NOT_SINGED.toString()
                mPresenter.updateExpressDetail((searchInfo)!!, null)
                val view: View = window.decorView.findViewById(android.R.id.content)
                SnackbarUtils.makeShort(view, R.string.save_success).show()
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
            R.id.btn_retry -> {
                ll_result.visibility = View.GONE
                ll_no_exist.visibility = View.GONE
                ll_error.visibility = View.GONE
                tv_searching.visibility = View.VISIBLE
                mPresenter.queryExpressDetail((searchInfo)!!)
            }
            else -> {}
        }
    }

    override fun showExpressSource(expressSearchInfo: ExpressSearchInfo) {
        searchInfo = expressSearchInfo
        Glide.with(this)
            .load(HttpConstant.KUAIDI100_BASE_URL + "images/all/" + searchInfo!!.logo)
            .dontAnimate()
            .placeholder(R.mipmap.ic_default_logo)
            .into((iv_logo)!!)
        showExpressRemark()
    }

    /**
     * 显示快递备注信息
     */
    private fun showExpressRemark() {
        searchInfo?.let {
            remark = mPresenter.getExpressRemark(it.post_id)
            if (TextUtils.isEmpty(remark)) {
                tv_name.text = it.name
                tv_post_id.text = it.post_id
            } else {
                tv_name.text = remark
                tv_post_id.text = it.name + " " + it.post_id
            }
        }

    }

    override fun showExpressDetail(expressDetail: ExpressDetail) {
        if ((expressDetail.status == "200")) {
            ll_result.visibility = View.VISIBLE
            ll_no_exist.visibility = View.GONE
            ll_error.visibility = View.GONE
            tv_searching.visibility = View.GONE
            resultItemList.addAll(expressDetail.data)
            expressDetailAdapter.notifyDataSetChanged()
            searchInfo?.is_check = expressDetail.ischeck
            mPresenter.updateExpressDetail((searchInfo)!!, expressDetail)
        } else { //失败
            ll_result.visibility = View.GONE
            ll_no_exist.visibility = View.VISIBLE
            ll_error.visibility = View.GONE
            tv_searching.visibility = View.GONE
            val ret: Boolean = mPresenter.checkExistExpress(searchInfo!!.post_id)
            btn_save.text =
                if (ret) getText(R.string.waybill_note) else getText(R.string.waybill_note_save)
            showLongToast(expressDetail.status + ":" + expressDetail.message)
        }
    }

    override fun showErrorInfo() {
        ll_result.visibility = View.GONE
        ll_no_exist.visibility = View.GONE
        ll_error.visibility = View.VISIBLE
        tv_searching.visibility = View.GONE
    }

    /**
     * 备注信息
     */
    private fun remark() {
        val view: View = layoutInflater.inflate(R.layout.dialog_input, null)
        val etRemark: EditText = view.findViewById(R.id.et_remark)
        etRemark.setText(remark)
        etRemark.setSelection(etRemark.length())
        AlertDialog.Builder(this)
            .setTitle(R.string.remark)
            .setView(view)
            .setPositiveButton(R.string.sure, object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, which: Int) {
                    mPresenter.updateExpressRemark(
                        searchInfo!!.post_id,
                        etRemark.text.toString()
                    )
                    showExpressRemark()
                    val view: View = window.decorView.findViewById(android.R.id.content)
                    SnackbarUtils.makeShort(view, R.string.remark_success).show()
                }
            })
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    companion object {
        @kotlin.jvm.JvmStatic
        fun start(context: Context, searchInfo: ExpressSearchInfo?) {
            val intent: Intent = Intent(context, ExpressDetailActivity::class.java)
            intent.putExtra(AppConstant.Extras.SEARCH_INFO, searchInfo)
            context.startActivity(intent)
        }
    }
}