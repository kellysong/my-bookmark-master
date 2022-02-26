package com.sjl.bookmark.ui.fragment

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import com.sjl.bookmark.R
import com.sjl.bookmark.dao.impl.DaoFactory
import com.sjl.bookmark.ui.adapter.*
import com.sjl.bookmark.ui.base.extend.BaseFileFragment
import com.sjl.bookmark.widget.reader.media.MediaStoreHelper
import com.sjl.bookmark.widget.reader.media.MediaStoreHelper.MediaResultCallback
import com.sjl.core.mvp.NoPresenter
import com.sjl.core.util.security.MD5Utils
import com.sjl.core.widget.RefreshLayout
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter
import kotlinx.android.synthetic.main.fragment_local_book.*

/**
 * 本地书籍
 *
 * @author Kelly
 * @version 1.0.0
 * @filename LocalBookFragment.java
 * @time 2018/12/10 14:38
 * @copyright(C) 2018 song
 */
class LocalBookFragment : BaseFileFragment<NoPresenter>() {


    override fun getLayoutId(): Int {
        return R.layout.fragment_local_book
    }

    override fun initView() {}
    override fun initListener() {}
    override fun initData() {
        setUpAdapter()
    }

    private fun setUpAdapter() {
        mAdapter = FileSystemAdapter(context, R.layout.file_book_recycle_item, null)
        local_book_rv_content.layoutManager = LinearLayoutManager(context)
        local_book_rv_content.addItemDecoration(RecyclerViewDivider(context, LinearLayoutManager.VERTICAL))
        local_book_rv_content.adapter = mAdapter
        mAdapter.setOnItemClickListener(object : MultiItemTypeAdapter.OnItemClickListener {
            override fun onItemClick(view: View, holder: RecyclerView.ViewHolder, position: Int) {
                //如果是已加载的文件，则点击事件无效。
                val id = MD5Utils.strToMd5By16(mAdapter.getItem(position).absolutePath)
                if (DaoFactory.getCollectBookDao().getCollectBook(id) != null) {
                    return
                }

                //点击选中
                mAdapter.setCheckedItem(position)

                //反馈
                if (mListener != null) {
                    mListener.onItemCheckedChange(mAdapter.getItemIsChecked(position))
                }
            }

            override fun onItemLongClick(
                view: View,
                holder: RecyclerView.ViewHolder,
                position: Int
            ): Boolean {
                return false
            }
        })
    }

    override fun onFirstUserVisible() {
        val start = System.currentTimeMillis()
        MediaStoreHelper.getAllBookFile(activity, MediaResultCallback { files ->
            if (isDetached) {
                return@MediaResultCallback
            }
            println("txt文件加载耗时:" + (System.currentTimeMillis() - start) / 1000.0 + "s")
            if (files.isEmpty()) {
                refresh_layout!!.showEmpty()
            } else {
                mAdapter.refreshItems(files) //加载列表
                refresh_layout!!.showFinish()
                //反馈
                if (mListener != null) {
                    mListener.onCategoryChanged()
                }
            }
        })
    }

    override fun onUserVisible() {}
    override fun onUserInvisible() {}
}