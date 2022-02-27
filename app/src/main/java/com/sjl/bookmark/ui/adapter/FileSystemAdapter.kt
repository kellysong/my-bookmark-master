package com.sjl.bookmark.ui.adapter

import android.content.Context
import android.view.View
import android.widget.CheckBox
import com.sjl.bookmark.R
import com.sjl.bookmark.dao.impl.DaoFactory
import com.sjl.core.util.datetime.TimeUtils
import com.sjl.core.util.file.FileUtils
import com.sjl.core.util.security.MD5Utils
import com.zhy.adapter.recyclerview.CommonAdapter
import com.zhy.adapter.recyclerview.base.ViewHolder
import java.io.File
import java.util.*

/**
 * 文件系统适配器
 *
 * @author Kelly
 * @version 1.0.0
 * @filename FileSystemAdapter.java
 * @time 2018/12/10 15:11
 * @copyright(C) 2018 song
 */
class FileSystemAdapter(context: Context, layoutId: Int, datas: List<File>?) : CommonAdapter<File>(context, layoutId, datas) {
    //记录item是否被选中的Map
    val checkMap = HashMap<File, Boolean>()
    var checkedCount = 0
        private set

    override fun convert(holder: ViewHolder, file: File, position: Int) {
        //判断是文件还是文件夹
        if (file.isDirectory) {
            setFolder(holder, file)
        } else {
            setFile(holder, file)
        }
    }

    private fun setFile(holder: ViewHolder, file: File) {
        //选择
        val id = MD5Utils.strToMd5By16(file.absolutePath)
        val mCbSelect = holder.getView<CheckBox>(R.id.file_cb_select)
        if (DaoFactory.getCollectBookDao().getCollectBook(id) != null) {
            holder.setImageResource(R.id.file_iv_icon, R.drawable.ic_file_loaded)
            holder.getView<View>(R.id.file_iv_icon).visibility = View.VISIBLE
            mCbSelect.visibility = View.GONE
        } else {
            val isSelected = checkMap[file]!!
            mCbSelect.isChecked = isSelected
            holder.getView<View>(R.id.file_iv_icon).visibility = View.GONE
            mCbSelect.visibility = View.VISIBLE
        }
        holder.getView<View>(R.id.file_ll_brief).visibility = View.VISIBLE
        holder.getView<View>(R.id.file_tv_sub_count).visibility = View.GONE
        holder.setText(R.id.file_tv_name, file.name)
        holder.setText(R.id.file_tv_size, FileUtils.formatFileSize(file.length()))
        holder.setText(R.id.file_tv_date, TimeUtils.formatDateToStr(file.lastModified(), TimeUtils.DATE_FORMAT_4))
    }

    fun setFolder(holder: ViewHolder, folder: File) {
        //图片
        holder.getView<View>(R.id.file_iv_icon).visibility = View.VISIBLE
        holder.getView<View>(R.id.file_cb_select).visibility = View.GONE
        holder.setImageResource(R.id.file_iv_icon, R.drawable.ic_dir)
        //名字
        holder.setText(R.id.file_tv_name, folder.name)
        //介绍
        holder.getView<View>(R.id.file_ll_brief).visibility = View.GONE
        holder.getView<View>(R.id.file_tv_sub_count).visibility = View.VISIBLE
        holder.setText(R.id.file_tv_sub_count, context.getString(R.string.nb_file_sub_count, folder.list().size))
    }

    override fun refreshItems(list: List<File>) {
        checkMap.clear()
        for (file in list) {
            checkMap[file] = false
        }
        super.refreshItems(list)
    }

    fun removeItems(value: List<File?>) {
        //删除在HashMap中的文件
        for (file in value) {
            checkMap.remove(file)
            //因为，能够被移除的文件，肯定是选中的
            --checkedCount
        }
        //删除列表中的文件
        remove(value)
    }

    //设置点击切换
    fun setCheckedItem(pos: Int) {
        val file = getItem(pos)
        if (isFileLoaded(file!!.absolutePath)) return
        val isSelected = checkMap[file]!!
        if (isSelected) {
            checkMap[file] = false
            --checkedCount
        } else {
            checkMap[file] = true
            ++checkedCount
        }
        notifyDataSetChanged()
    }

    fun setCheckedAll(isChecked: Boolean) {
        val entrys: Set<MutableMap.MutableEntry<File, Boolean>> = checkMap.entries
        checkedCount = 0
        for (entry in entrys) {
            //必须是文件，必须没有被收藏
            if (entry.key.isFile && !isFileLoaded(entry.key.absolutePath)) {
                entry.setValue(isChecked)
                //如果选中，则增加点击的数量
                if (isChecked) {
                    ++checkedCount
                }
            }
        }
        notifyDataSetChanged()
    }

    /**
     * 如果是已加载的文件，则点击事件无效。
     * @param filePath
     * @return
     */
    private fun isFileLoaded(filePath: String): Boolean {
        return DaoFactory.getCollectBookDao().getCollectBook(MD5Utils.strToMd5By16(filePath)) != null
    }

    val checkableCount: Int
        get() {
            val files = datas
            var count = 0
            for (file in files) {
                if (!isFileLoaded(file!!.absolutePath) && file.isFile) ++count
            }
            return count
        }

    fun getItemIsChecked(pos: Int): Boolean {
        val file = getItem(pos)
        return checkMap[file]!!
    }

    val checkedFiles: List<File?>
        get() {
            val files: MutableList<File?> = ArrayList()
            val entrys: Set<Map.Entry<File?, Boolean>> = checkMap.entries
            for ((key, value) in entrys) {
                if (value) {
                    files.add(key)
                }
            }
            return files
        }
}