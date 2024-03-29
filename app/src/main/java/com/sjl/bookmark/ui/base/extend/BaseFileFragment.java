package com.sjl.bookmark.ui.base.extend;

import com.sjl.bookmark.ui.adapter.FileSystemAdapter;
import com.sjl.core.mvp.BaseFragment;
import com.sjl.core.mvp.BasePresenter;

import java.io.File;
import java.util.List;

/**
 *
 * FileSystemActivity的基础Fragment类
 */

public abstract class BaseFileFragment<T extends BasePresenter> extends BaseFragment<T> {

    protected FileSystemAdapter mAdapter;
    protected OnFileCheckedListener mListener;
    protected boolean isCheckedAll;

    /**
     * 设置当前列表为全选
     * @param checkedAll true全选，false不全选
     */
    public void setCheckedAll(boolean checkedAll){
        if (mAdapter == null) return;

        isCheckedAll = checkedAll;
        mAdapter.setCheckedAll(checkedAll);
    }

    public void setChecked(boolean checked){
        isCheckedAll = checked;
    }

    /**
     * 当前fragment是否全选
     * @return
     */
    public boolean isCheckedAll() {
        return isCheckedAll;
    }

    /**
     * 获取被选中的数量
     * @return
     */
    public int getCheckedCount(){
        if (mAdapter == null) return 0;
        return mAdapter.getCheckedCount();
    }

    /**
     * 获取被选中的文件列表
     * @return
     */
    public List<File> getCheckedFiles(){
        return mAdapter != null ? mAdapter.getCheckedFiles() : null;
    }

    //获取文件的总数
    public int getFileCount(){
        return mAdapter != null ? mAdapter.getItemCount() : null;
    }

    /**
     * 获取可点击的文件的数量
     * @return
     */
    public int getCheckableCount(){
        if (mAdapter == null) return 0;
        return mAdapter.getCheckableCount();
    }

    /**
     * 删除选中的文件
     */
    public void deleteCheckedFiles(){
        //删除选中的文件
        List<File> files = getCheckedFiles();
        //删除显示的文件列表
        mAdapter.removeItems(files);
        //删除选中的文件
        for (File file : files){
            if (file.exists()){
                file.delete();
            }
        }
    }

    //设置文件点击监听事件
    public void setOnFileCheckedListener(OnFileCheckedListener listener){
        mListener = listener;
    }

    /**
     * 文件点击监听
     */
    public interface OnFileCheckedListener {
        /**
         * 单个条目选择状态监听
         * @param isChecked
         */
        void onItemCheckedChange(boolean isChecked);

        /**
         * 整个目录下文件状态监听
         */
        void onCategoryChanged();
    }
}
