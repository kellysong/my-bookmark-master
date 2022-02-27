package com.sjl.bookmark.ui.fragment

import android.os.Environment
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sjl.bookmark.R
import com.sjl.bookmark.dao.impl.DaoFactory
import com.sjl.bookmark.entity.FileStack
import com.sjl.bookmark.entity.FileStack.FileSnapshot
import com.sjl.bookmark.ui.adapter.*
import com.sjl.bookmark.ui.base.extend.BaseFileFragment
import com.sjl.bookmark.widget.reader.BookManager
import com.sjl.core.mvp.NoPresenter
import com.sjl.core.net.RxSchedulers
import com.sjl.core.util.security.MD5Utils
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import kotlinx.android.synthetic.main.fragment_file_category.*
import java.io.File
import java.io.FileFilter
import java.util.*

/**
 * 手机目录
 *
 * @author Kelly
 * @version 1.0.0
 * @filename FileCategoryFragment.java
 * @time 2018/12/10 14:38
 * @copyright(C) 2018 song
 */
class FileCategoryFragment : BaseFileFragment<NoPresenter>() {

    private lateinit var mFileStack: FileStack
    override fun getLayoutId(): Int {
        return R.layout.fragment_file_category
    }

    override fun initView() {}
    override fun initListener() {}
    override fun initData() {
        mFileStack = FileStack() //实例化文件栈
        setUpAdapter()
    }

    private fun setUpAdapter() {
        mAdapter = FileSystemAdapter(mActivity, R.layout.file_book_recycle_item, null)
        file_category_rv_content.layoutManager = LinearLayoutManager(context)
        file_category_rv_content.addItemDecoration(
            RecyclerViewDivider(
                    mActivity,
                LinearLayoutManager.VERTICAL
            )
        )
        file_category_rv_content.adapter = mAdapter
        /**
         * 文件条目点击
         */
        mAdapter.setOnItemClickListener(object : MultiItemTypeAdapter.OnItemClickListener {
            override fun onItemClick(view: View, holder: RecyclerView.ViewHolder, position: Int) {
                val file = mAdapter.getItem(position)
                if (file.isDirectory) {
                    //保存当前信息。
                    val snapshot = FileSnapshot()
                    snapshot.filePath = file_category_tv_path!!.text.toString()
                    snapshot.files = ArrayList(mAdapter.datas)
                    snapshot.scrollOffset = file_category_rv_content!!.computeVerticalScrollOffset()
                    mFileStack.push(snapshot)
                    //切换下一个文件
                    toggleFileTree(file)
                } else {

                    //如果是已加载的文件，则点击事件无效。
                    val id = mAdapter.getItem(position).absolutePath
                    if (DaoFactory.getCollectBookDao()
                            .getCollectBook(MD5Utils.strToMd5By16(id)) != null
                    ) {
                        return
                    }
                    //点击选中
                    mAdapter.setCheckedItem(position)
                    //反馈
                    if (mListener != null) {
                        mListener.onItemCheckedChange(mAdapter.getItemIsChecked(position))
                    }
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
        /**
         * 文件夹返回上一级
         */
        file_category_tv_back_last.setOnClickListener(View.OnClickListener {
            val snapshot = mFileStack.pop()
            val oldScrollOffset = file_category_rv_content!!.computeHorizontalScrollOffset()
            if (snapshot == null) return@OnClickListener
            file_category_tv_path.text = snapshot.filePath
            mAdapter.refreshItems(snapshot.files)
            file_category_rv_content!!.scrollBy(0, snapshot.scrollOffset - oldScrollOffset)
            //反馈
            if (mListener != null) {
                mListener.onCategoryChanged()
            }
        })
    }

    override fun onFirstUserVisible() {
        val root = Environment.getExternalStorageDirectory()
        toggleFileTree(root)
    }

    override fun onUserVisible() {}
    override fun onUserInvisible() {}
    private fun toggleFileTree(file: File) {
        //路径名
        file_category_tv_path.text = getString(R.string.nb_file_path, file.path)
        val subscribe = Observable.just(file).map(
            Function<File, List<File>> { file -> //通过过滤获取数据
                val files = file.listFiles(SimpleFileFilter())
                //转换成List
                val rootFiles = Arrays.asList(*files)
                //排序
                Collections.sort(rootFiles, FileComparator())
                rootFiles
            }).compose(RxSchedulers.applySchedulers())
            .subscribe(object : Consumer<List<File>> {
                @Throws(Exception::class)
                override fun accept(files: List<File>) {
                    //加入列表
                    mAdapter.refreshItems(files)
                    //反馈
                    if (mListener != null) {
                        mListener.onCategoryChanged()
                    }
                }
            }, object : Consumer<Throwable?> {
                @Throws(Exception::class)
                override fun accept(throwable: Throwable?) {
                }
            })
    }

    /**
     * 对文件列表List按名称排序(升序)
     */
    inner class FileComparator : Comparator<File> {
        override fun compare(o1: File, o2: File): Int {
            if (o1.isDirectory && o2.isFile) {
                return -1
            }
            return if (o2.isDirectory && o1.isFile) {
                1
            } else o1.name.compareTo(o2.name, ignoreCase = true)
        }
    }

    inner class SimpleFileFilter : FileFilter {
        override fun accept(pathname: File): Boolean {
            if (pathname.name.startsWith(".")) {
                return false
            }
            //文件夹内部数量为0
            if (pathname.isDirectory && pathname.list().size == 0) {
                return false
            }
            /**
             * 现在只支持TXT文件的显示
             */
            //文件内容为空,或者文件大小为0，或者不以txt为开头
            return !(!pathname.isDirectory &&
                    (pathname.length() == 0L || !pathname.name.endsWith(BookManager.SUFFIX_TXT)))
        }
    }
}