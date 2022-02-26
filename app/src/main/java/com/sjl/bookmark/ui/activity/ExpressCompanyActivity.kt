package com.sjl.bookmark.ui.activity

import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sjl.bookmark.R
import com.sjl.bookmark.entity.ExpressCompany
import com.sjl.bookmark.kotlin.language.I18nUtils
import com.sjl.bookmark.ui.adapter.ExpressCompanyAdapter
import com.sjl.bookmark.ui.contract.ExpressCompanyContract
import com.sjl.bookmark.ui.presenter.ExpressCompanyPresenter
import com.sjl.core.mvp.BaseActivity
import com.sjl.core.util.log.LogUtils
import com.sjl.core.widget.IndexBar.OnIndexChangedListener
import kotlinx.android.synthetic.main.express_company_activity.*
import kotlinx.android.synthetic.main.toolbar_default.*

/**
 * 快递公司
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ExpressCompanyActivity.java
 * @time 2018/4/28 18:02
 * @copyright(C) 2018 song
 */
class ExpressCompanyActivity : BaseActivity<ExpressCompanyPresenter>(),
    OnIndexChangedListener, ExpressCompanyContract.View {

    private lateinit var companyList: List<ExpressCompany>
    private lateinit var adapter: ExpressCompanyAdapter
    override fun getLayoutId(): Int {
        return R.layout.express_company_activity
    }

    override fun initView() {}
    override fun initListener() {
        bindingToolbar(common_toolbar, I18nUtils.getString(R.string.title_choose_express_company))
        ib_indicator.setOnIndexChangedListener(this)
    }

    override fun initData() {
        companyList = mPresenter.initCompany()
        adapter = ExpressCompanyAdapter(this, companyList)
        rv_company.layoutManager = LinearLayoutManager(this)
        rv_company.adapter = adapter
        rv_company.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }

    override fun onIndexChanged(index: String, isDown: Boolean) {
        LogUtils.i("current index is " + index)
        var position: Int = -1
        for (company: ExpressCompany in companyList) {
            if (TextUtils.equals(company.name, index)) {
                position = companyList.indexOf(company)
                break
            }
        }
        if (position != -1) {
//            rvCompany.scrollToPosition(position);
            /**准确定位到指定位置，并且将指定位置的item置顶，
             * 若直接调用scrollToPosition(...)方法，则不会置顶。 */
            val layoutManager: LinearLayoutManager =
                rv_company.layoutManager as LinearLayoutManager
            layoutManager.scrollToPositionWithOffset(position, 0)
            layoutManager.stackFromEnd = true //设置为true时，RecycelrView会自动滑倒尾部，直到最后一条数据完整展示
        }
        tv_indicator.text = index
        tv_indicator.visibility = if (isDown) View.VISIBLE else View.GONE
    }
}