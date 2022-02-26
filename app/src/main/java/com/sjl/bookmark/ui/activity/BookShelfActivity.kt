package com.sjl.bookmark.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sjl.bookmark.R;
import com.sjl.bookmark.entity.zhuishu.table.CollectBook;
import com.sjl.bookmark.kotlin.language.I18nUtils;
import com.sjl.bookmark.ui.adapter.ShelfAdapter;
import com.sjl.bookmark.ui.contract.BookShelfContract;
import com.sjl.bookmark.ui.presenter.BookShelfPresenter;
import com.sjl.bookmark.widget.DragGridView;
import com.sjl.core.mvp.BaseActivity;
import com.sjl.core.net.RxBus;
import com.sjl.core.util.log.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookShelfActivity.java
 * @time 2018/11/30 14:39
 * @copyright(C) 2018 song
 */
public class BookShelfActivity extends BaseActivity<BookShelfPresenter> implements BookShelfContract.View {
    @BindView(R.id.common_toolbar)
    Toolbar mToolBar;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.bookShelf)
    DragGridView dragGridView;
    @BindView(R.id.srl_bookShelf)
    SwipeRefreshLayout swipeRefreshLayout;

    private List<CollectBook> bookLists;
    private ShelfAdapter shelfAdapter;
    /**
     * 点击书本的位置
     */
    private int itemPosition;

    @Override
    protected int getLayoutId() {
        setStatusBar(0xff000000);
        return R.layout.book_shelf_activity;
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initListener() {
        bindingToolbar(mToolBar, I18nUtils.getString(R.string.tool_novel_read));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookShelfActivity.this, FileSystemActivity.class);
                startActivity(intent);
            }
        });
        /**
         * 点击书籍跳转到阅读页面
         */
        dragGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemPosition = position;
                List<CollectBook> bookList = shelfAdapter.getBookList();//持有引用
                if (bookList == null || bookList.size() == 0 || itemPosition > bookList.size() - 1) {//说明是绘制的书架背景
                    return;
                }
                final CollectBook collectBook = bookList.get(position);
                //如果是本地文件，首先判断这个文件是否存在
                if (collectBook.isLocal()) {
                    //id表示本地文件的路径
                    String path = collectBook.getCover();
                    File file = new File(path);
                    //判断这个本地文件是否存在
                    if (file.exists() && file.length() != 0) {
                        shelfAdapter.setItemToFirst(itemPosition);
                        BookReadActivity.startActivity(BookShelfActivity.this, collectBook, true);//此时collectBook的排序id已经是最大
                    } else {
                        String tip = BookShelfActivity.this.getString(R.string.nb_bookshelf_book_not_exist);
                        //提示(从目录中移除这个文件)
                        new AlertDialog.Builder(BookShelfActivity.this)
                                .setTitle(getResources().getString(R.string.nb_common_tip))
                                .setMessage(tip)
                                .setPositiveButton(getResources().getString(R.string.nb_common_sure),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                mPresenter.deleteBook(collectBook);
                                            }
                                        })
                                .setNegativeButton(getResources().getString(R.string.nb_common_cancel), null)
                                .show();
                    }
                } else {
                    shelfAdapter.setItemToFirst(itemPosition);
                    BookReadActivity.startActivity(BookShelfActivity.this, collectBook, true);
                }
            }
        });
        dragGridView.setSwipeRefreshLayout(swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        refreshShelfBook();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });
        //监听书架添加书籍
        Disposable subscribe = RxBus.getInstance()
                .toObservable(Boolean.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean s) throws Exception {
                        if (s) {
                            LogUtils.i("书架更新，新增收藏" + s);
                        } else {
                            LogUtils.i("书架更新，移除收藏" + s);
                        }
                        mPresenter.getRecommendBook();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        addDisposable(subscribe);

    }


    @Override
    protected void initData() {
        bookLists = new ArrayList<>();
        shelfAdapter = new ShelfAdapter(this, bookLists);
        dragGridView.setAdapter(shelfAdapter);
        shelfAdapter.setItemDeleteListener(new ShelfAdapter.OnDeleteItemListener() {
            @Override
            public void item(int deletePosition) {
                itemPosition = deletePosition;
                CollectBook collectBook = shelfAdapter.getBookList().get(deletePosition);
                mPresenter.deleteBook(collectBook);

            }
        });
        shelfAdapter.setDragGridView(dragGridView);
        showLoadingDialog();
        mPresenter.getRecommendBook();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.book_shelf_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_search) {
            Intent intent = new Intent(this, BookSearchActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_clear) {
            new AlertDialog.Builder(BookShelfActivity.this)
                    .setTitle(getResources().getString(R.string.nb_common_tip))
                    .setMessage(R.string.book_shelf_hint)
                    .setPositiveButton(getResources().getString(R.string.nb_common_sure),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    showLoadingDialog(getString(R.string.book_delete_hint));
                                    mPresenter.deleteAllBook();
                                    dialog.dismiss();
                                }
                            })
                    .setNegativeButton(getResources().getString(R.string.nb_common_cancel), null)
                    .show();
        } else if (id == R.id.action_refresh) {
            refreshShelfBook();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 刷新书架书籍
     */
    private void refreshShelfBook() {
        List<CollectBook> bookList = shelfAdapter.getBookList();
        showLoadingDialog(getString(R.string.book_update_hint));
        if (bookList != null && !bookList.isEmpty()) {//书架已经有数据
            mPresenter.getRecommendBook();
        } else {
            mPresenter.refreshCollectBooks();
        }
    }


    @Override
    public void showErrorMsg(String msg) {
        showToast(msg);
        hideLoadingDialog();
    }

    @Override
    public void showRecommendBook(List<CollectBook> collBookBeans) {
        shelfAdapter.setItems(collBookBeans);//刷新图书
        hideLoadingDialog();
    }

    @Override
    public void refreshBook() {
        CollectBook remove = shelfAdapter.getBookList().remove(itemPosition);
        LogUtils.i("删除的书本是:" + remove.getTitle());
        if (shelfAdapter.getBookList().size() == 0) {//没有书籍了隐藏删除按钮，防止重新加载书籍时显示删除按钮
            dragGridView.setShowDeleteButton(false);
        }
        shelfAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        dragGridView.setShowDeleteButton(false);
        shelfAdapter.notifyDataSetChanged();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (dragGridView.isShowDeleteButton()) {
                dragGridView.setShowDeleteButton(false);
                shelfAdapter.notifyDataSetChanged();
                return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

}
