package com.sjl.bookmark.ui.fragment;

import android.view.View;

import com.sjl.bookmark.R;
import com.sjl.bookmark.dao.impl.DaoFactory;
import com.sjl.bookmark.ui.adapter.FileSystemAdapter;
import com.sjl.bookmark.ui.adapter.RecyclerViewDivider;
import com.sjl.bookmark.ui.base.extend.BaseFileFragment;
import com.sjl.bookmark.widget.reader.media.MediaStoreHelper;
import com.sjl.core.util.security.MD5Utils;
import com.sjl.core.widget.RefreshLayout;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import java.io.File;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

/**
 * 本地书籍
 *
 * @author Kelly
 * @version 1.0.0
 * @filename LocalBookFragment.java
 * @time 2018/12/10 14:38
 * @copyright(C) 2018 song
 */
public class LocalBookFragment extends BaseFileFragment {
    @BindView(R.id.refresh_layout)
    RefreshLayout mRlRefresh;
    @BindView(R.id.local_book_rv_content)
    RecyclerView mRvContent;



    @Override
    protected int getLayoutId() {
        return R.layout.fragment_local_book;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {
        setUpAdapter();
    }

    private void setUpAdapter() {
        mAdapter = new FileSystemAdapter(getContext(), R.layout.file_book_recycle_item, null);
        mRvContent.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvContent.addItemDecoration(new RecyclerViewDivider(getContext(), LinearLayoutManager.VERTICAL));
        mRvContent.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                //如果是已加载的文件，则点击事件无效。
                String id = MD5Utils.strToMd5By16(mAdapter.getDataItem(position).getAbsolutePath());
                if (DaoFactory.getCollectBookDao().getCollectBook(id) != null) {
                    return;
                }

                //点击选中
                mAdapter.setCheckedItem(position);

                //反馈
                if (mListener != null) {
                    mListener.onItemCheckedChange(mAdapter.getItemIsChecked(position));
                }
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });

    }

    @Override
    protected void onFirstUserVisible() {
        long start = System.currentTimeMillis();
        MediaStoreHelper.getAllBookFile(getActivity(), new MediaStoreHelper.MediaResultCallback() {
            @Override
            public void onResultCallback(List<File> files) {
                if (isDetached()){
                    return;
                }
                System.out.println("txt文件加载耗时:"+(System.currentTimeMillis()-start)/1000.0+"s");
                if (files.isEmpty()) {
                    mRlRefresh.showEmpty();
                } else {
                    mAdapter.refreshItems(files);//加载列表
                    mRlRefresh.showFinish();
                    //反馈
                    if (mListener != null) {
                        mListener.onCategoryChanged();
                    }
                }
            }
        });
    }

    @Override
    protected void onUserVisible() {

    }

    @Override
    protected void onUserInvisible() {

    }
}
