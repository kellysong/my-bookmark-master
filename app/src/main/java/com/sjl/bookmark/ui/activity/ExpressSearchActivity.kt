package com.sjl.bookmark.ui.activity

import android.content.Intent
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.renny.zxing.Activity.CaptureActivity
import com.sjl.bookmark.BuildConfig
import com.sjl.bookmark.R
import com.sjl.bookmark.app.AppConstant
import com.sjl.bookmark.entity.ExpressCompany
import com.sjl.bookmark.entity.ExpressName
import com.sjl.bookmark.entity.ExpressName.AutoBean
import com.sjl.bookmark.entity.ExpressSearchInfo
import com.sjl.bookmark.kotlin.language.I18nUtils
import com.sjl.bookmark.ui.activity.ExpressDetailActivity
import com.sjl.bookmark.ui.adapter.SuggestionCompanyAdapter
import com.sjl.bookmark.ui.contract.ExpressSearchContract
import com.sjl.bookmark.ui.presenter.ExpressSearchPresenter
import com.sjl.core.mvp.BaseActivity
import kotlinx.android.synthetic.main.express_search_activity.*
import kotlinx.android.synthetic.main.toolbar_default.*
import java.util.*

/**
 * 快递搜索
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ExpressSearchActivity.java
 * @time 2018/4/26 16:44
 * @copyright(C) 2018 song
 */
class ExpressSearchActivity : BaseActivity<ExpressSearchPresenter>(), TextWatcher,
    View.OnClickListener, ExpressSearchContract.View {

    private val suggestionList: MutableList<ExpressCompany?> = ArrayList()
    private lateinit var companyMap: Map<String, ExpressCompany>
    private lateinit var adapter: SuggestionCompanyAdapter
    override fun getLayoutId(): Int {
        return R.layout.express_search_activity
    }

    override fun initView() {}
    override fun initListener() {
        bindingToolbar(common_toolbar, I18nUtils.getString(R.string.title_express_query))
    }

    override fun initData() {
        companyMap = mPresenter.initCompany()
        et_post_id.addTextChangedListener(this)
        iv_scan.setOnClickListener(this)
        iv_clear.setOnClickListener(this)
        adapter = SuggestionCompanyAdapter(this, R.layout.company_suggestion_item, suggestionList)
        rv_suggestion.layoutManager = LinearLayoutManager(this)
        rv_suggestion.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        rv_suggestion.adapter = adapter
        //        mPresenter.getSuggestionList(etPostId);
        if (BuildConfig.DEBUG) {
            et_post_id.setText("YT580095677642")
        }
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    override fun afterTextChanged(s: Editable) {
        if (s.length > 0) {
            iv_scan.visibility = View.INVISIBLE
            iv_clear.visibility = View.VISIBLE
        } else {
            iv_scan.visibility = View.VISIBLE
            iv_clear.visibility = View.INVISIBLE
        }
        if (s.length >= 8) {
            adapter.postId = s.toString()
            mPresenter.getSuggestionList(s.toString())
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_scan -> startCaptureActivity()
            R.id.iv_clear -> {
                et_post_id!!.setText("")
                suggestionList.clear()
                adapter.notifyDataSetChanged()
            }
        }
    }

    /**
     * 启动扫描
     */
    private fun startCaptureActivity() {
        startActivityForResult(
            Intent(this, CaptureActivity::class.java),
            AppConstant.REQUEST_CAPTURE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK || data == null) {
            return
        }
        when (requestCode) {
            AppConstant.REQUEST_CAPTURE -> {
                // 运单号扫描处理
                val barCode: String? = data.getStringExtra("barCode")
                if (TextUtils.isEmpty(barCode)) {
                    Toast.makeText(this, R.string.express_scan_fail, Toast.LENGTH_SHORT).show()
                    return
                }
                et_post_id!!.setText(barCode?.trim { it <= ' ' })
                et_post_id!!.setSelection(et_post_id!!.length())
            }
            AppConstant.REQUEST_COMPANY -> {
                //根据选择的快递公司和当前输入的快递单号查询快递信息
                val mSearchInfo: ExpressSearchInfo =
                    data.getSerializableExtra(AppConstant.Extras.SEARCH_INFO) as ExpressSearchInfo
                mSearchInfo.post_id = et_post_id!!.text.toString()
                val intent: Intent = Intent(this, ExpressDetailActivity::class.java)
                intent.putExtra(AppConstant.Extras.SEARCH_INFO, mSearchInfo)
                startActivity(intent)
            }
            else -> {}
        }
    }

    override fun showSuggestionCompany(expressName: ExpressName) {
        suggestionList.clear()
        if ((expressName != null) && (expressName.auto != null) && !expressName.auto
                .isEmpty()
        ) {
            for (bean: AutoBean in expressName.auto) {
                if (companyMap.containsKey(bean.comCode)) {
                    suggestionList.add(companyMap[bean.comCode])
                }
            }
        }
        val noQuery: String = getString(R.string.no_query)
        val select: String = getString(R.string.select_hint)
        val label: String =
            "<font color='%1\$s'>$noQuery</font> <font color='%2\$s'>$select</font>"
        val grey: String =
            String.format("#%06X", 0xFFFFFF and resources.getColor(R.color.grey))
        val blue: String =
            String.format("#%06X", 0xFFFFFF and resources.getColor(R.color.blue))
        val companyEntity: ExpressCompany = ExpressCompany()
        companyEntity.name = String.format(label, grey, blue)
        suggestionList.add(companyEntity)
        suggestionList.add(officialHref)
        adapter.notifyDataSetChanged()
    }

    private val officialHref: ExpressCompany
        private get() {
            val noQuery: String = getString(R.string.no_query)
            val select: String = getString(R.string.select_hint2)
            val label: String =
                "<font color='%1\$s'>$noQuery</font> <font color='%2\$s'>$select</font>"
            val grey: String =
                String.format("#%06X", 0xFFFFFF and resources.getColor(R.color.grey))
            val blue: String =
                String.format("#%06X", 0xFFFFFF and resources.getColor(R.color.blue))
            val companyEntity: ExpressCompany = ExpressCompany()
            companyEntity.name = String.format(label, grey, blue)
            return companyEntity
        }
}