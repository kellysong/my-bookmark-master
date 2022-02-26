package com.sjl.bookmark.ui.activity

import android.content.Intent
import android.view.*
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.sjl.bookmark.R
import com.sjl.bookmark.app.AppConstant
import com.sjl.bookmark.kotlin.language.I18nUtils
import com.sjl.bookmark.ui.adapter.AccountPagerListAdapter
import com.sjl.bookmark.ui.fragment.AccountListFragment
import com.sjl.core.entity.EventBusDto
import com.sjl.core.mvp.BaseActivity
import com.sjl.core.mvp.NoPresenter
import com.sjl.core.util.log.LogUtils
import kotlinx.android.synthetic.main.activity_account_list.*
import kotlinx.android.synthetic.main.toolbar_default.*
import org.greenrobot.eventbus.EventBus
import java.util.*

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename AccountIndexActivity.java
 * @time 2018/3/7 10:44
 * @copyright(C) 2018 song
 */
class AccountIndexActivity : BaseActivity<NoPresenter>() {


    private lateinit var list: MutableList<Fragment>
    private var adapter: AccountPagerListAdapter? = null
    private var position: Int = 0
    private val titles: Array<String> = arrayOf(
        I18nUtils.getString(R.string.account_in_use),
        I18nUtils.getString(R.string.account_idle),
        I18nUtils.getString(R.string.account_invalid)
    )

    override fun getLayoutId(): Int {
        return R.layout.activity_account_list
    }

    public override fun initView() {}
    public override fun initListener() {
        bindingToolbar(common_toolbar, I18nUtils.getString(R.string.tool_account_manager))
        fab.setOnClickListener(object : View.OnClickListener {
            //添加
            override fun onClick(v: View) {
                val intent = Intent(this@AccountIndexActivity, AccountEditActivity::class.java)
                intent.putExtra("position", position)
                intent.putExtra("CREATE_MODE", AppConstant.SETTING.CREATE_MODE)
                startActivityForResult(intent, INDEX_REQUEST_CODE)
            }
        })
    }

    public override fun initData() {
        //页面，数据源
        list = ArrayList()
        for (i in 0..2) {
            list.add(AccountListFragment())
        }
        //ViewPager的适配器
        adapter = AccountPagerListAdapter(supportFragmentManager, list, titles)
        viewpager.offscreenPageLimit = 3
        viewpager.adapter = adapter
        viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                this@AccountIndexActivity.position = position
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        //绑定
        tablayout.setupWithViewPager(viewpager)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        LogUtils.i("requestCode=$requestCode,resultCode=$resultCode")
        if (requestCode == INDEX_REQUEST_CODE) {
            if (resultCode == ADD_SUCCESS) {
                //添加回调
                val eventBusDto: EventBusDto<*> = EventBusDto<Any?>(
                    position,
                    AppConstant.ACCOUNT_REFRESH_EVENT_CODE,
                    true
                ) //只更新当前对应页
                EventBus.getDefault().post(eventBusDto)
            }
        }
    }

    companion object {
        private val INDEX_REQUEST_CODE: Int = 1
        val ADD_SUCCESS: Int = 1
    }
}