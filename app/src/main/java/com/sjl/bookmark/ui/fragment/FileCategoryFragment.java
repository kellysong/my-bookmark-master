package com.sjl.bookmark.ui.fragment;

import android.os.Environment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sjl.bookmark.R;
import com.sjl.bookmark.dao.impl.DaoFactory;
import com.sjl.bookmark.entity.FileStack;
import com.sjl.bookmark.ui.adapter.FileSystemAdapter;
import com.sjl.bookmark.ui.adapter.RecyclerViewDivider;
import com.sjl.bookmark.ui.base.extend.BaseFileFragment;
import com.sjl.bookmark.widget.reader.BookManager;
import com.sjl.core.util.security.MD5Utils;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;

/**
 * 手机目录
 *
 * @author Kelly
 * @version 1.0.0
 * @filename FileCategoryFragment.java
 * @time 2018/12/10 14:38
 * @copyright(C) 2018 song
 */
public class FileCategoryFragment extends BaseFileFragment {

    @BindView(R.id.file_category_tv_path)
    TextView mTvPath;
    @BindView(R.id.file_category_tv_back_last)
    TextView mTvBackLast;
    @BindView(R.id.file_category_rv_content)
    RecyclerView mRvContent;

    private FileStack mFileStack;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_file_category;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {
        mFileStack = new FileStack();//实例化文件栈
        setUpAdapter();
    }
    private void setUpAdapter(){
        mAdapter = new FileSystemAdapter(getContext(),R.layout.file_book_recycle_item,null);
        mRvContent.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvContent.addItemDecoration(new RecyclerViewDivider(getContext(), LinearLayoutManager.VERTICAL));
        mRvContent.setAdapter(mAdapter);
        /**
         * 文件条目点击
         */
        mAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                File file = mAdapter.getDataItem(position);
                if (file.isDirectory()){
                    //保存当前信息。
                    FileStack.FileSnapshot snapshot = new FileStack.FileSnapshot();
                    snapshot.filePath = mTvPath.getText().toString();
                    snapshot.files = new ArrayList<File>(mAdapter.getDatas());
                    snapshot.scrollOffset = mRvContent.computeVerticalScrollOffset();
                    mFileStack.push(snapshot);
                    //切换下一个文件
                    toggleFileTree(file);
                }
                else {

                    //如果是已加载的文件，则点击事件无效。
                    String id = mAdapter.getDataItem(position).getAbsolutePath();
                    if (DaoFactory.getCollectBookDao().getCollectBook(MD5Utils.strToMd5By16(id)) != null){
                        return;
                    }
                    //点击选中
                    mAdapter.setCheckedItem(position);
                    //反馈
                    if (mListener != null){
                        mListener.onItemCheckedChange(mAdapter.getItemIsChecked(position));
                    }
                }
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });

        /**
         * 文件夹返回上一级
         */
        mTvBackLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileStack.FileSnapshot snapshot = mFileStack.pop();
                int oldScrollOffset = mRvContent.computeHorizontalScrollOffset();
                if (snapshot == null) return;
                mTvPath.setText(snapshot.filePath);
                mAdapter.refreshItems(snapshot.files);
                mRvContent.scrollBy(0,snapshot.scrollOffset - oldScrollOffset);
                //反馈
                if (mListener != null){
                    mListener.onCategoryChanged();
                }
            }
        });
        File root = Environment.getExternalStorageDirectory();
        toggleFileTree(root);
    }


    @Override
    protected void onFirstUserVisible() {

    }

    @Override
    protected void onUserVisible() {

    }

    @Override
    protected void onUserInvisible() {

    }

    private void toggleFileTree(File file){
        //路径名
        mTvPath.setText(getString(R.string.nb_file_path,file.getPath()));
        //通过过滤获取数据
        File[] files = file.listFiles(new SimpleFileFilter());
        //转换成List
        List<File> rootFiles = Arrays.asList(files);
        //排序
        Collections.sort(rootFiles,new FileComparator());
        //加入列表
        mAdapter.refreshItems(rootFiles);
        //反馈
        if (mListener != null){
            mListener.onCategoryChanged();
        }
    }



    /**
     * 对文件列表List按名称排序(升序)
     */
    public class FileComparator implements Comparator<File> {
        @Override
        public int compare(File o1, File o2){
            if (o1.isDirectory() && o2.isFile()) {
                return -1;
            }
            if (o2.isDirectory() && o1.isFile()) {
                return 1;
            }
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    }

    public class SimpleFileFilter implements FileFilter {
        @Override
        public boolean accept(File pathname) {
            if (pathname.getName().startsWith(".")){
                return false;
            }
            //文件夹内部数量为0
            if (pathname.isDirectory() && pathname.list().length == 0){
                return false;
            }

            /**
             * 现在只支持TXT文件的显示
             */
            //文件内容为空,或者文件大小为0，或者不以txt为开头
            if (!pathname.isDirectory() &&
                    (pathname.length() == 0 || !pathname.getName().endsWith(BookManager.SUFFIX_TXT))){
                return false;
            }
            return true;
        }
    }
}
