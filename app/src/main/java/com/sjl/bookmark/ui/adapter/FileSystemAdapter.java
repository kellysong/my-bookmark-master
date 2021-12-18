package com.sjl.bookmark.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;

import com.sjl.bookmark.R;
import com.sjl.bookmark.dao.impl.DaoFactory;
import com.sjl.core.util.file.FileUtils;
import com.sjl.core.util.security.MD5Utils;
import com.sjl.core.util.datetime.TimeUtils;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 文件系统适配器
 *
 * @author Kelly
 * @version 1.0.0
 * @filename FileSystemAdapter.java
 * @time 2018/12/10 15:11
 * @copyright(C) 2018 song
 */
public class FileSystemAdapter extends CommonAdapter<File> {
    //记录item是否被选中的Map
    private HashMap<File, Boolean> mCheckMap = new HashMap<>();
    private int mCheckedCount = 0;

    public FileSystemAdapter(Context context, int layoutId, List<File> datas) {
        super(context, layoutId, datas);
    }

    @Override
    protected void convert(ViewHolder holder, File file, int position) {
        //判断是文件还是文件夹
        if (file.isDirectory()){
            setFolder(holder,file);
        }
        else {
            setFile(holder,file);
        }
    }
    private void setFile(ViewHolder holder, File file){
        //选择
        String id = MD5Utils.strToMd5By16(file.getAbsolutePath());
        CheckBox mCbSelect = holder.getView(R.id.file_cb_select);
        if (DaoFactory.getCollectBookDao().getCollectBook(id) != null){
            holder.setImageResource(R.id.file_iv_icon,R.drawable.ic_file_loaded);

            holder.getView(R.id.file_iv_icon).setVisibility(View.VISIBLE);
            mCbSelect.setVisibility(View.GONE);
        }
        else {
            boolean isSelected = mCheckMap.get(file);
            mCbSelect.setChecked(isSelected);
            holder.getView(R.id.file_iv_icon).setVisibility(View.GONE);
            mCbSelect.setVisibility(View.VISIBLE);
        }

        holder.getView(R.id.file_ll_brief).setVisibility(View.VISIBLE);
        holder.getView(R.id.file_tv_sub_count).setVisibility(View.GONE);

        holder.setText(R.id.file_tv_name,file.getName());
        holder.setText(R.id.file_tv_size, FileUtils.formatFileSize(file.length()));
        holder.setText(R.id.file_tv_date, TimeUtils.formatDateToStr(file.lastModified(), TimeUtils.DATE_FORMAT_4));
    }

    public void setFolder(ViewHolder holder, File folder){
        //图片
        holder.getView(R.id.file_iv_icon)
                .setVisibility(View.VISIBLE);
        holder.getView(R.id.file_cb_select)
                .setVisibility(View.GONE);
        holder.setImageResource(R.id.file_iv_icon,R.drawable.ic_dir);
        //名字
        holder.setText(R.id.file_tv_name,folder.getName());
        //介绍
        holder.getView(R.id.file_ll_brief)
                .setVisibility(View.GONE);
        holder.getView(R.id.file_tv_sub_count).setVisibility(View.VISIBLE);

        holder.setText(R.id.file_tv_sub_count,getContext().getString(R.string.nb_file_sub_count,folder.list().length));
    }

    @Override
    public void refreshItems(List<File> list) {
        mCheckMap.clear();
        for(File file : list){
            mCheckMap.put(file, false);
        }
        super.refreshItems(list);
    }




    public void removeItems(List<File> value) {
        //删除在HashMap中的文件
        for (File file : value) {
            mCheckMap.remove(file);
            //因为，能够被移除的文件，肯定是选中的
            --mCheckedCount;
        }
        //删除列表中的文件
        remove(value);
    }

    //设置点击切换
    public void setCheckedItem(int pos) {
        File file = getItem(pos);
        if (isFileLoaded(file.getAbsolutePath())) return;

        boolean isSelected = mCheckMap.get(file);
        if (isSelected) {
            mCheckMap.put(file, false);
            --mCheckedCount;
        } else {
            mCheckMap.put(file, true);
            ++mCheckedCount;
        }
        notifyDataSetChanged();
    }

    public void setCheckedAll(boolean isChecked) {
        Set<Map.Entry<File, Boolean>> entrys = mCheckMap.entrySet();
        mCheckedCount = 0;
        for (Map.Entry<File, Boolean> entry : entrys) {
            //必须是文件，必须没有被收藏
            if (entry.getKey().isFile() && !isFileLoaded(entry.getKey().getAbsolutePath())) {
                entry.setValue(isChecked);
                //如果选中，则增加点击的数量
                if (isChecked) {
                    ++mCheckedCount;
                }
            }
        }
        notifyDataSetChanged();
    }

    /**
     * 如果是已加载的文件，则点击事件无效。
     * @param filePath
     * @return
     */
    private boolean isFileLoaded(String filePath) {
        if (DaoFactory.getCollectBookDao().getCollectBook(MD5Utils.strToMd5By16(filePath)) != null) {
            return true;
        }
        return false;
    }

    public int getCheckableCount() {
        List<File> files = getDatas();
        int count = 0;
        for (File file : files) {
            if (!isFileLoaded(file.getAbsolutePath()) && file.isFile())
                ++count;
        }
        return count;
    }

    public boolean getItemIsChecked(int pos) {
        File file = getItem(pos);
        return mCheckMap.get(file);
    }

    public List<File> getCheckedFiles() {
        List<File> files = new ArrayList<>();
        Set<Map.Entry<File, Boolean>> entrys = mCheckMap.entrySet();
        for (Map.Entry<File, Boolean> entry : entrys) {
            if (entry.getValue()) {
                files.add(entry.getKey());
            }
        }
        return files;
    }

    public int getCheckedCount() {
        return mCheckedCount;
    }

    public HashMap<File, Boolean> getCheckMap() {
        return mCheckMap;
    }
}
