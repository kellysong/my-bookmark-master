package com.sjl.bookmark.ui.activity

import android.content.*
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.sjl.bookmark.R
import com.sjl.bookmark.dao.impl.DaoFactory
import com.sjl.bookmark.entity.zhuishu.table.CollectBook
import com.sjl.bookmark.kotlin.language.I18nUtils
import com.sjl.bookmark.ui.activity.FileSystemActivity
import com.sjl.bookmark.ui.base.extend.BaseFileFragment
import com.sjl.bookmark.ui.base.extend.BaseFileFragment.OnFileCheckedListener
import com.sjl.bookmark.ui.base.extend.BaseTabActivity
import com.sjl.bookmark.ui.fragment.FileCategoryFragment
import com.sjl.bookmark.ui.fragment.LocalBookFragment
import com.sjl.core.net.RxBus
import com.sjl.core.net.RxLifecycleUtils
import com.sjl.core.util.datetime.TimeUtils
import com.sjl.core.util.security.MD5Utils
import kotlinx.android.synthetic.main.file_system_activity.*
import kotlinx.android.synthetic.main.toolbar_default.*
import java.io.File
import java.util.*

/**
 * 书籍本地导入
 *
 * @author Kelly
 * @version 1.0.0
 * @filename FileSystemActivity.java
 * @time 2018/11/30 17:02
 * @copyright(C) 2018 song
 */
class FileSystemActivity : BaseTabActivity() {

    private var mLocalFragment: LocalBookFragment? = null
    private var mCategoryFragment: FileCategoryFragment? = null
    private var mCurFragment: BaseFileFragment<*>? = null
    override fun getLayoutId(): Int {
        return R.layout.file_system_activity
    }

    override fun initView() {}
    override fun initListener() {
        bindingToolbar(common_toolbar, I18nUtils.getString(R.string.title_book_import))
        //全选和不全选
        file_system_cb_selected_all.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                //设置全选状态
                val isChecked: Boolean = file_system_cb_selected_all.isChecked
                mCurFragment!!.isCheckedAll = isChecked
                //改变菜单状态
                changeMenuStatus()
            }
        })
        tv_cb_text.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                //设置全选状态
                val isChecked: Boolean = file_system_cb_selected_all!!.isChecked
                if (isChecked) {
                    file_system_cb_selected_all!!.isChecked = false
                    mCurFragment!!.setCheckedAll(false)
                } else {
                    file_system_cb_selected_all!!.isChecked = true
                    mCurFragment!!.setCheckedAll(true)
                }
                //改变菜单状态
                changeMenuStatus()
            }
        })
        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    mCurFragment = mLocalFragment
                } else {
                    mCurFragment = mCategoryFragment
                }
                //改变菜单状态
                changeMenuStatus()
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        //添加本地书籍到书架
        file_system_btn_add_book.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                //获取选中的文件
                val files: List<File> = mCurFragment!!.checkedFiles
                //转换成CollBook,并存储
                val collBooks: List<CollectBook> = convertCollectBook(files)
                DaoFactory.getCollectBookDao().saveCollectBooks(collBooks)
                //设置HashMap为false
                mCurFragment!!.isCheckedAll = false
                //改变菜单状态
                changeMenuStatus()
                //改变是否可以全选
                changeCheckedAllStatus()
                //提示加入书架成功
                showShortToast(
                    resources.getString(
                        R.string.nb_file_add_succeed,
                        collBooks.size
                    )
                )
                //刷新书架
                RxBus.getInstance().post(true)
            }
        })
        file_system_btn_delete.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                //弹出，确定删除文件吗。
                AlertDialog.Builder(this@FileSystemActivity)
                    .setTitle(R.string.delete_file)
                    .setMessage(R.string.delete_file_hint)
                    .setPositiveButton(
                        resources.getString(R.string.nb_common_sure),
                        object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface, which: Int) {
                                //删除选中的文件
                                mCurFragment!!.deleteCheckedFiles()
                                //提示删除文件成功
                                showShortToast(getString(R.string.delete_file_success))
                            }
                        })
                    .setNegativeButton(resources.getString(R.string.nb_common_cancel), null)
                    .show()
            }
        })
        mLocalFragment!!.setOnFileCheckedListener(mListener) //监听文件选择
        mCategoryFragment!!.setOnFileCheckedListener(mListener)
    }

    override fun initData() {
        mCurFragment = mLocalFragment
    }

    override fun createTabFragments(): List<Fragment> {
        mLocalFragment = LocalBookFragment()
        mCategoryFragment = FileCategoryFragment()
        return buildFragmentList(mLocalFragment, mCategoryFragment)
    }

    override fun createTabTitles(): List<String> {
        return Arrays.asList(getString(R.string.smart_import), getString(R.string.cellphone_dir))
    }

    private val mListener: OnFileCheckedListener = object : OnFileCheckedListener {
        override fun onItemCheckedChange(isChecked: Boolean) {
            changeMenuStatus()
        }

        override fun onCategoryChanged() {
            //状态归零
            mCurFragment!!.isCheckedAll = false
            //改变菜单
            changeMenuStatus()
            //改变是否能够全选
            changeCheckedAllStatus()
        }
    }

    /**
     * 将文件转换成CollectBook
     *
     * @param files:需要加载的文件列表
     * @return
     */
    private fun convertCollectBook(files: List<File>): List<CollectBook> {
        val collBooks: MutableList<CollectBook> = ArrayList(files.size)
        for (file: File in files) {
            //判断文件是否存在
            if (!file.exists()) continue
            val collBook: CollectBook = CollectBook()
            collBook._id = MD5Utils.strToMd5By16(file.absolutePath)
            collBook.title = file.name.replace(".txt", "")
            collBook.author = ""
            collBook.shortIntro = getString(R.string.nb_book_detail_none)
            collBook.cover = file.absolutePath
            collBook.setLocal(true)
            collBook.lastChapter = getString(R.string.nb_book_detail_start_read)
            collBook.updated = TimeUtils.formatDateToStr(
                file.lastModified(),
                TimeUtils.DATE_FORMAT_7
            )
            collBook.lastRead = TimeUtils.formatDateToStr(
                System.currentTimeMillis(),
                TimeUtils.DATE_FORMAT_7
            )
            collBooks.add(collBook)
        }
        return collBooks
    }

    /**
     * 改变底部选择栏的状态
     */
    private fun changeMenuStatus() {

        //点击、删除状态的设置
        if (mCurFragment!!.checkedCount == 0) {
            file_system_btn_add_book!!.text = getString(R.string.nb_file_add_shelf)
            //设置某些按钮的是否可点击
            setMenuClickable(false)
            if (file_system_cb_selected_all!!.isChecked) {
                mCurFragment!!.setChecked(false)
                file_system_cb_selected_all!!.isChecked = mCurFragment!!.isCheckedAll
            }
        } else {
            file_system_btn_add_book!!.text = getString(
                R.string.nb_file_add_shelves,
                mCurFragment!!.checkedCount
            )
            setMenuClickable(true)

            //全选状态的设置

            //如果选中的全部的数据，则判断为全选
            if (mCurFragment!!.checkedCount == mCurFragment!!.checkableCount) {
                //设置为全选
                mCurFragment!!.setChecked(true)
                file_system_cb_selected_all!!.isChecked = mCurFragment!!.isCheckedAll
            } else if (mCurFragment!!.isCheckedAll) {
                mCurFragment!!.setChecked(false)
                file_system_cb_selected_all!!.isChecked = mCurFragment!!.isCheckedAll
            }
        }

        //重置全选的文字
        if (mCurFragment!!.isCheckedAll) {
            tv_cb_text!!.text = getString(R.string.cancel)
        } else {
            tv_cb_text!!.text = getString(R.string.select_all)
        }
    }

    private fun setMenuClickable(isClickable: Boolean) {

        //设置是否可删除
        file_system_btn_delete.isEnabled = isClickable
        file_system_btn_delete.isClickable = isClickable

        //设置是否可添加书籍
        file_system_btn_add_book.isEnabled = isClickable
        file_system_btn_add_book.isClickable = isClickable
    }

    /**
     * 改变全选按钮的状态
     */
    private fun changeCheckedAllStatus() {
        //获取可选择的文件数量
        val count: Int = mCurFragment!!.checkableCount

        //设置是否能够全选
        if (count > 0) {
            file_system_cb_selected_all.isClickable = true
            file_system_cb_selected_all.isEnabled = true
        } else {
            file_system_cb_selected_all.isClickable = false
            file_system_cb_selected_all.isEnabled = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        RxLifecycleUtils.clear()
    }
}