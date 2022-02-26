package com.sjl.bookmark.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import cn.feng.skin.manager.loader.SkinManager
import com.sjl.bookmark.R
import com.sjl.bookmark.entity.ModuleMenu
import com.sjl.bookmark.kotlin.language.I18nUtils
import com.sjl.bookmark.ui.activity.*
import com.sjl.core.entity.EventBusDto
import com.sjl.core.mvp.BaseFragment
import com.sjl.core.mvp.NoPresenter
import com.zhy.adapter.recyclerview.CommonAdapter
import com.zhy.adapter.recyclerview.base.ViewHolder
import kotlinx.android.synthetic.main.tool_fragment.*
import java.util.*

/**
 * 功能菜单fragment
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ToolFragment.java
 * @time 2018/3/27 17:10
 * @copyright(C) 2018 song
 */
class ToolFragment : BaseFragment<NoPresenter>() {

    private lateinit var moduleMenus: MutableList<ModuleMenu>
    override fun getLayoutId(): Int {
        return R.layout.tool_fragment
    }

    override fun initData() {}
    override fun onFirstUserVisible() {
        val layoutManager = GridLayoutManager(mActivity, 3)
        rv_tool.layoutManager = layoutManager
        moduleMenus = ArrayList()
        moduleMenus.add(
            ModuleMenu(
                I18nUtils.getString(R.string.tool_account_manager),
                R.mipmap.menu_password,
                AccountIndexActivity::class.java
            )
        )
        moduleMenus.add(
            ModuleMenu(
                I18nUtils.getString(R.string.tool_wifi_query),
                R.mipmap.menu_wifi,
                WifiQueryActivity::class.java
            )
        )
        moduleMenus.add(
            ModuleMenu(
                I18nUtils.getString(R.string.tool_balance_query),
                R.mipmap.menu_card,
                MyNfcActivity::class.java
            )
        )
        //http://www.szmc.net/page/html5.html
        moduleMenus.add(
            ModuleMenu(
                I18nUtils.getString(R.string.tool_shenzhen_subway),
                R.mipmap.menu_subway,
                "http://jtapi.bendibao.com/ditie/inc/sz/xianluda.gif"
            )
        )
        moduleMenus.add(
            ModuleMenu(
                I18nUtils.getString(R.string.tool_my_express),
                R.mipmap.menu_express_query,
                ExpressActivity::class.java
            )
        )
        moduleMenus.add(
            ModuleMenu(
                I18nUtils.getString(R.string.tool_cartoon),
                R.mipmap.menu_caricature,
                "https://m.ac.qq.com/search/index"
            )
        )
        moduleMenus.add(
            ModuleMenu(
                I18nUtils.getString(R.string.tool_novel_read),
                R.mipmap.menu_book,
                BookShelfActivity::class.java
            )
        )
        moduleMenus.add(
            ModuleMenu(
                I18nUtils.getString(R.string.tool_zhihu_daily),
                R.mipmap.menu_news,
                NewsListActivity::class.java
            )
        )
        moduleMenus.add(
            ModuleMenu(
                I18nUtils.getString(R.string.tool_barrage),
                R.mipmap.menu_barrage,
                BarrageShowActivity::class.java
            )
        )
        moduleMenus.add(
            ModuleMenu(
                I18nUtils.getString(R.string.tool_speed_detection),
                R.mipmap.menu_speed,
                SpeedDetectionActivity::class.java
            )
        )
        rv_tool.adapter =
            object : CommonAdapter<ModuleMenu>(mActivity, R.layout.tool_recycle_item, moduleMenus) {
                override fun convert(holder: ViewHolder, moduleMenu: ModuleMenu, position: Int) {
                    holder.setText(R.id.tv_title, moduleMenu.title)
                    holder.setImageResource(R.id.iv_icon, moduleMenu.drawableId)
                    holder.itemView.setOnClickListener {
                        when (position) {
                            2 -> {
                                val color = SkinManager.getInstance().colorPrimary
                                val statusBarColor: Int = if (color != -1) {
                                    color
                                } else {
                                    resources.getColor(R.color.colorPrimary)
                                }
                                val bundle = Bundle()
                                bundle.putInt("nfcColor", statusBarColor) //适配主题更换是，nfc模块标题栏和状态栏颜色
                                bundle.putInt("isActivityOpen", 1)
                                bundle.putString(
                                    "title",
                                    I18nUtils.getString(R.string.tool_balance_query)
                                )
                                openActivity(moduleMenu.clz, bundle)
                            }
                            3, 5 -> BrowserActivity.startWithParams(
                                mActivity, moduleMenu.title,
                                moduleMenu.webUrl
                            )
                            else -> openActivity(moduleMenu.clz)
                        }
                    }
                }
            }
    }

    override fun onUserVisible() {}
    override fun onUserInvisible() {}
    override fun initView() {}
    override fun initListener() {}
    public override fun onEventComing(eventCenter: EventBusDto<*>?) {}
}