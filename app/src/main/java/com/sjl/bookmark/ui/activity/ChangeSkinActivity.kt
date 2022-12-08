package com.sjl.bookmark.ui.activity

import android.os.*
import android.view.*
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import cn.feng.skin.manager.listener.ILoaderListener
import cn.feng.skin.manager.loader.SkinManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.sjl.bookmark.R
import com.sjl.bookmark.app.AppConstant
import com.sjl.bookmark.entity.ThemeSkin
import com.sjl.bookmark.kotlin.darkmode.DarkModeUtils
import com.sjl.bookmark.kotlin.darkmode.DarkModeUtils.isNightMode
import com.sjl.bookmark.kotlin.language.I18nUtils
import com.sjl.bookmark.ui.adapter.ThemeSkinAdapter
import com.sjl.core.mvp.BaseActivity
import com.sjl.core.mvp.NoPresenter
import com.sjl.core.util.PreferencesHelper
import com.sjl.core.util.file.FileUtils
import com.sjl.core.util.log.LogUtils
import kotlinx.android.synthetic.main.change_skin_activity.*
import kotlinx.android.synthetic.main.toolbar_default.*
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.*

/**
 * 更换主题
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ChangeSkinActivity.java
 * @time 2018/12/27 16:20
 * @copyright(C) 2018 song
 */
class ChangeSkinActivity : BaseActivity<NoPresenter>() {

    private var skinDir: String? = null
    private lateinit var mThemeSkinAdapter: ThemeSkinAdapter
    private lateinit var themeSkinList: MutableList<ThemeSkin>
    override fun getLayoutId(): Int {
        return R.layout.change_skin_activity
    }

    override fun initView() {}
    override fun initListener() {
        bindingToolbar(common_toolbar, I18nUtils.getString(R.string.title_change_subject))
    }

    override fun initData() {
        skinDir = getSkinDir().absolutePath
        themeSkinList = ArrayList()
        themeSkinList.add(ThemeSkin(0, getString(R.string.skin_default), "", "#03A9F4"))
        themeSkinList.add(
            ThemeSkin(
                1,
                getString(R.string.skin_noble_brown),
                "skin_brown.skin",
                "#4e342e"
            )
        )
        themeSkinList.add(
            ThemeSkin(
                2,
                getString(R.string.skin_cool_black),
                "skin_black.skin",
                "#212121"
            )
        )
        themeSkinList.add(
            ThemeSkin(
                3,
                getString(R.string.skin_passion_red),
                "skin_red.skin",
                "#F44336"
            )
        )
        themeSkinList.add(
            ThemeSkin(
                4,
                getString(R.string.skin_comfortable_green),
                "skin_green.skin",
                "#4CAF50"
            )
        )
        themeSkinList.add(
            ThemeSkin(
                5,
                getString(R.string.skin_vibrant_orange),
                "skin_orange.skin",
                "#FF9800"
            )
        )
        themeSkinList.add(
            ThemeSkin(
                6,
                getString(R.string.skin_elegant_grey),
                "skin_grey.skin",
                "#9E9E9E"
            )
        )
        mThemeSkinAdapter = ThemeSkinAdapter(R.layout.theme_skin_recycle_item, themeSkinList)
        val layoutManager: GridLayoutManager = GridLayoutManager(this, 3)
        theme_recyclerview.layoutManager = layoutManager
        theme_recyclerview.adapter = mThemeSkinAdapter
        mThemeSkinAdapter.onItemClickListener = object : BaseQuickAdapter.OnItemClickListener {
            override fun onItemClick(
                adapter: BaseQuickAdapter<*, *>,
                view: View,
                position: Int
            ) {
                val nightMode: Boolean = isNightMode(mContext)
                if (nightMode) {
                   DarkModeUtils.setDarkMode(DarkModeUtils.MODE_DEFAULT)
                }

                val skin: Int = PreferencesHelper.getInstance(mContext)
                    .getInteger(AppConstant.SETTING.CURRENT_SELECT_SKIN, 0)
                if (skin == position) {
                    return
                }
                PreferencesHelper.getInstance(mContext)
                    .put(AppConstant.SETTING.CURRENT_SELECT_SKIN, position)
                val item: ThemeSkin? = adapter.getItem(position) as ThemeSkin?
                if (position == 0) {
                    SkinManager.getInstance().restoreDefaultTheme()
                } else {
                    loadSkin(item!!.skinFileName)
                }
                adapter.notifyDataSetChanged()
            }
        }
        swipeRefreshLayout.setColorSchemeResources(R.color.blueStatus) //设置下拉圆圈的颜色
        swipeRefreshLayout.setOnRefreshListener(object : OnRefreshListener {
            override fun onRefresh() { //下拉刷新
                Handler().postDelayed(object : Runnable {
                    override fun run() {
                        if (isDestroy(this@ChangeSkinActivity)) {
                            return
                        }
                        updateSkin()
                        swipeRefreshLayout.isRefreshing = false
                        val skin: Int = PreferencesHelper.getInstance(mContext)
                            .getInteger(AppConstant.SETTING.CURRENT_SELECT_SKIN, 0)
                        val themeSkin: ThemeSkin = mThemeSkinAdapter.data.get(skin)
                        loadSkin(themeSkin.skinFileName)
                        showShortToast(getString(R.string.skin_update_success))
                    }
                }, 1000)
            }
        })
    }

    /**
     * 手动更新皮肤
     */
    private fun updateSkin() {
        val data: List<ThemeSkin>? = mThemeSkinAdapter.data
        if (data != null && !data.isEmpty()) {
            for (themeSkin: ThemeSkin in data) {
                val skinFullName: String = skinDir + File.separator + themeSkin.skinFileName
                val file: File = File(skinFullName)
                if (file.exists()) {
                    val delete: Boolean = file.delete()
                    LogUtils.i("delete:" + delete)
                }
            }
        }
    }

    /**
     * 加载皮肤
     *
     * @param skinName 皮肤插件名
     */
    @Synchronized
    private fun loadSkin(skinName: String) {
        val skinFullName: String = skinDir + File.separator + skinName
        val file: File = File(skinFullName)
        if (!file.exists()) {
            try {
                val parentDir: File = file.parentFile
                if (!parentDir.exists()) {
                    parentDir.mkdirs()
                }
                file.createNewFile()
                val inputStream: InputStream = mContext.assets.open("skin/" + skinName)
                FileUtils.fileCopy(inputStream, file)
            } catch (e: IOException) {
                LogUtils.e("皮肤拷贝异常", e)
                return
            }
        }
        SkinManager.getInstance().load(file.absolutePath,
            object : ILoaderListener {
                override fun onStart() {}
                override fun onSuccess() {
                    Toast.makeText(mContext, R.string.skin_theme_change_success, Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onFailed() {
                    Toast.makeText(mContext, R.string.skin_theme_change_fail, Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

    /**
     * 得到皮肤目录
     *
     * @return
     */
    fun getSkinDir(): File {
        val skinDir: File = File(FileUtils.getFilePath(), "skin")
        if (!skinDir.exists()) {
            skinDir.mkdirs()
        }
        return skinDir
    }
}