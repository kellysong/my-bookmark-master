package com.sjl.bookmark.ui.activity;

import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.sjl.bookmark.R;
import com.sjl.bookmark.dao.impl.DaoFactory;
import com.sjl.bookmark.entity.zhuishu.table.CollectBook;
import com.sjl.bookmark.kotlin.language.I18nUtils;
import com.sjl.bookmark.ui.base.extend.BaseFileFragment;
import com.sjl.bookmark.ui.base.extend.BaseTabActivity;
import com.sjl.bookmark.ui.fragment.FileCategoryFragment;
import com.sjl.bookmark.ui.fragment.LocalBookFragment;
import com.sjl.core.net.RxBus;
import com.sjl.core.util.datetime.TimeUtils;
import com.sjl.core.util.security.MD5Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;

/**
 * 书籍本地导入
 *
 * @author Kelly
 * @version 1.0.0
 * @filename FileSystemActivity.java
 * @time 2018/11/30 17:02
 * @copyright(C) 2018 song
 */
public class FileSystemActivity extends BaseTabActivity {
    @BindView(R.id.common_toolbar)
    Toolbar mToolBar;
    @BindView(R.id.file_system_cb_selected_all)
    CheckBox mCbSelectAll;
    @BindView(R.id.tv_cb_text)
    TextView mCbText;
    @BindView(R.id.file_system_btn_delete)
    Button mBtnDelete;
    @BindView(R.id.file_system_btn_add_book)
    Button mBtnAddBook;


    private LocalBookFragment mLocalFragment;
    private FileCategoryFragment mCategoryFragment;
    private BaseFileFragment mCurFragment;

    @Override
    protected int getLayoutId() {
        return R.layout.file_system_activity;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {
        bindingToolbar(mToolBar, I18nUtils.getString(R.string.title_book_import));
        //全选和不全选
        mCbSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置全选状态
                boolean isChecked = mCbSelectAll.isChecked();
                mCurFragment.setCheckedAll(isChecked);
                //改变菜单状态
                changeMenuStatus();
            }
        });
        mCbText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置全选状态
                boolean isChecked = mCbSelectAll.isChecked();
                if (isChecked){
                    mCbSelectAll.setChecked(false);
                    mCurFragment.setCheckedAll(false);
                }else{
                    mCbSelectAll.setChecked(true);
                    mCurFragment.setCheckedAll(true);
                }
                //改变菜单状态
                changeMenuStatus();
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mCurFragment = mLocalFragment;
                } else {
                    mCurFragment = mCategoryFragment;
                }
                //改变菜单状态
                changeMenuStatus();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //添加本地书籍到书架
        mBtnAddBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取选中的文件
                List<File> files = mCurFragment.getCheckedFiles();
                //转换成CollBook,并存储
                List<CollectBook> collBooks = convertCollectBook(files);
                DaoFactory.getCollectBookDao().saveCollectBooks(collBooks);
                //设置HashMap为false
                mCurFragment.setCheckedAll(false);
                //改变菜单状态
                changeMenuStatus();
                //改变是否可以全选
                changeCheckedAllStatus();
                //提示加入书架成功
                showShortToast(FileSystemActivity.this.getResources().getString(R.string.nb_file_add_succeed, collBooks.size()));
                //刷新书架
                RxBus.getInstance().post(true);
            }
        });

        mBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出，确定删除文件吗。
                new AlertDialog.Builder(FileSystemActivity.this)
                        .setTitle(R.string.delete_file)
                        .setMessage(R.string.delete_file_hint)
                        .setPositiveButton(getResources().getString(R.string.nb_common_sure), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //删除选中的文件
                                mCurFragment.deleteCheckedFiles();
                                //提示删除文件成功
                                showShortToast(getString(R.string.delete_file_success));
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.nb_common_cancel), null)
                        .show();
            }
        });

        mLocalFragment.setOnFileCheckedListener(mListener);//监听文件选择
        mCategoryFragment.setOnFileCheckedListener(mListener);
    }

    @Override
    protected void initData() {
        mCurFragment = mLocalFragment;
    }

    @Override
    protected List<Fragment> createTabFragments() {
        mLocalFragment = new LocalBookFragment();
        mCategoryFragment = new FileCategoryFragment();
        return buildFragmentList(mLocalFragment, mCategoryFragment);
    }


    @Override
    protected List<String> createTabTitles() {
        return Arrays.asList(getString(R.string.smart_import), getString(R.string.cellphone_dir));
    }

    private BaseFileFragment.OnFileCheckedListener mListener = new BaseFileFragment.OnFileCheckedListener() {
        @Override
        public void onItemCheckedChange(boolean isChecked) {
            changeMenuStatus();
        }

        @Override
        public void onCategoryChanged() {
            //状态归零
            mCurFragment.setCheckedAll(false);
            //改变菜单
            changeMenuStatus();
            //改变是否能够全选
            changeCheckedAllStatus();
        }
    };

    /**
     * 将文件转换成CollectBook
     *
     * @param files:需要加载的文件列表
     * @return
     */
    private List<CollectBook> convertCollectBook(List<File> files) {

        List<CollectBook> collBooks = new ArrayList<>(files.size());
        for (File file : files) {
            //判断文件是否存在
            if (!file.exists()) continue;

            CollectBook collBook = new CollectBook();
            collBook.set_id(MD5Utils.strToMd5By16(file.getAbsolutePath()));
            collBook.setTitle(file.getName().replace(".txt", ""));
            collBook.setAuthor("");
            collBook.setShortIntro(getString(R.string.nb_book_detail_none));
            collBook.setCover(file.getAbsolutePath());
            collBook.setLocal(true);
            collBook.setLastChapter(getString(R.string.nb_book_detail_start_read));
            collBook.setUpdated(TimeUtils.formatDateToStr(file.lastModified(), TimeUtils.DATE_FORMAT_7));
            collBook.setLastRead(TimeUtils.
                    formatDateToStr(System.currentTimeMillis(), TimeUtils.DATE_FORMAT_7));
            collBooks.add(collBook);
        }
        return collBooks;
    }

    /**
     * 改变底部选择栏的状态
     */
    private void changeMenuStatus() {

        //点击、删除状态的设置
        if (mCurFragment.getCheckedCount() == 0) {
            mBtnAddBook.setText(getString(R.string.nb_file_add_shelf));
            //设置某些按钮的是否可点击
            setMenuClickable(false);

            if (mCbSelectAll.isChecked()) {
                mCurFragment.setChecked(false);
                mCbSelectAll.setChecked(mCurFragment.isCheckedAll());
            }

        } else {
            mBtnAddBook.setText(getString(R.string.nb_file_add_shelves, mCurFragment.getCheckedCount()));
            setMenuClickable(true);

            //全选状态的设置

            //如果选中的全部的数据，则判断为全选
            if (mCurFragment.getCheckedCount() == mCurFragment.getCheckableCount()) {
                //设置为全选
                mCurFragment.setChecked(true);
                mCbSelectAll.setChecked(mCurFragment.isCheckedAll());
            }
            //如果曾今是全选则替换
            else if (mCurFragment.isCheckedAll()) {
                mCurFragment.setChecked(false);
                mCbSelectAll.setChecked(mCurFragment.isCheckedAll());
            }
        }

        //重置全选的文字
        if (mCurFragment.isCheckedAll()) {
            mCbText.setText(getString(R.string.cancel));
        } else {
            mCbText.setText(getString(R.string.select_all));
        }

    }

    private void setMenuClickable(boolean isClickable) {

        //设置是否可删除
        mBtnDelete.setEnabled(isClickable);
        mBtnDelete.setClickable(isClickable);

        //设置是否可添加书籍
        mBtnAddBook.setEnabled(isClickable);
        mBtnAddBook.setClickable(isClickable);
    }

    /**
     * 改变全选按钮的状态
     */
    private void changeCheckedAllStatus() {
        //获取可选择的文件数量
        int count = mCurFragment.getCheckableCount();

        //设置是否能够全选
        if (count > 0) {
            mCbSelectAll.setClickable(true);
            mCbSelectAll.setEnabled(true);
        } else {
            mCbSelectAll.setClickable(false);
            mCbSelectAll.setEnabled(false);
        }
    }
}
