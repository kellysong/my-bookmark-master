package com.sjl.bookmark.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.sjl.bookmark.R;
import com.sjl.bookmark.app.AppConstant;
import com.sjl.bookmark.entity.table.Collection;
import com.sjl.bookmark.kotlin.language.I18nUtils;
import com.sjl.bookmark.net.HttpConstant;
import com.sjl.bookmark.ui.adapter.MyCollectionAdapter;
import com.sjl.bookmark.ui.contract.MyCollectionContract;
import com.sjl.bookmark.ui.presenter.MyCollectionPresenter;
import com.sjl.bookmark.widget.PopWindow;
import com.sjl.bookmark.widget.RecyclerViewLocation;
import com.sjl.core.mvp.BaseActivity;
import com.sjl.core.util.log.LogUtils;
import com.sjl.core.util.VibrateHelper;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.sjl.bookmark.app.MyApplication.getContext;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename MyCollectionActivity.java
 * @time 2018/3/25 17:07
 * @copyright(C) 2018 song
 */
public class MyCollectionActivity extends BaseActivity<MyCollectionPresenter> implements MyCollectionContract.View, MyCollectionAdapter.OnItemClickListener,
        MyCollectionAdapter.OnItemLongClickListener,
        MyCollectionAdapter.RequestLoadMoreListener, View.OnClickListener {
    @BindView(R.id.common_toolbar)
    Toolbar mToolBar;

    @BindView(R.id.fl_content)
    FrameLayout mContent;

    @BindView(R.id.rv_collection)
    RecyclerView mRecyclerView;
    @BindView(R.id.ll_empty_view)
    LinearLayout mEmptyView;

    @BindView(R.id.tv_select_num)
    TextView mTvSelectNum;
    @BindView(R.id.btn_delete)
    Button mBtnDelete;
    @BindView(R.id.select_all)
    TextView mSelectAll;
    @BindView(R.id.ll_mycollection_bottom_dialog)
    LinearLayout mBottomDialog;

    MyCollectionAdapter myCollectionAdapter;

    private PopWindow popWindow;//长按popWindow
    private boolean mEditMode = false;//false非编辑模式，true编辑模式
    private boolean isSelectAll = false;//false非全选,true全选
    private int index = 0;//通统计选中选项
    private int position;//点击条目索引

    @Override
    protected int getLayoutId() {
        return R.layout.my_collection_activity;
    }

    @Override
    protected void initView() {

    }


    @Override
    protected void initListener() {
        bindingToolbar(mToolBar, I18nUtils.getString(R.string.my_collection));
        mBtnDelete.setOnClickListener(this);
        mSelectAll.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        /**设置RecyclerView*/
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        myCollectionAdapter = new MyCollectionAdapter(R.layout.my_collection_recycle_item, null);
        /**隐藏文章类型*/
        mRecyclerView.setAdapter(myCollectionAdapter);

        /**设置事件监听*/
        myCollectionAdapter.setOnItemClickListener(this);
        myCollectionAdapter.setOnItemLongClickListener(this);//长按点击事件
        myCollectionAdapter.setOnLoadMoreListener(this, mRecyclerView);
        mPresenter.loadMyCollection();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_collection_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuSearch) {
            openActivity(MyCollectionSearchActivity.class);
        } else if (item.getItemId() == R.id.menuAdd) {
            openActivityForResult(MyNoteActivity.class, AppConstant.REQUEST_CODE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setMyCollection(List<Collection> collections, int loadType) {
        switch (loadType) {
            case HttpConstant.LoadType.TYPE_REFRESH_SUCCESS:
                if (collections != null && collections.size() > 0) {
                    mContent.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                    myCollectionAdapter.setNewData(collections);
                    myCollectionAdapter.loadMoreComplete(); //加载完成
                } else {
                    mContent.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                    mPresenter.recoverCollectionDataFromServer();//没有数据，从服务器加载
                }
                break;
            case HttpConstant.LoadType.TYPE_LOAD_MORE_SUCCESS:
                if (collections != null && collections.size() > 0) {
                    myCollectionAdapter.addData(collections);
                    myCollectionAdapter.loadMoreComplete(); //加载完成
                } else {
                    LogUtils.i("收藏分页完毕");
                    myCollectionAdapter.loadMoreEnd(false); //数据全部加载完毕
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (mEditMode) {//编辑模式
            CheckBox checkBox = (CheckBox) adapter.getViewByPosition(position, R.id.cb_item);
            Collection item = myCollectionAdapter.getItem(position);
            if (checkBox.isChecked()) {
                checkBox.setChecked(false);
                item.setSelectItem(false);
                index--;
            } else {
                checkBox.setChecked(true);
                item.setSelectItem(true);
                index++;
            }
            int size = myCollectionAdapter.getData().size();
            if (index == size) {
                isSelectAll = true;
                mSelectAll.setText("取消全选");
            } else {
                isSelectAll = false;
                mSelectAll.setText("全选");
            }
            setBtnBackground(index);
            return;
        }
        Collection item = myCollectionAdapter.getItem(position);
        if (item.getType() == 0) {
            BrowserActivity.startWithParams(this, item.getTitle(), item.getHref());
        } else if (item.getType() == 1) {
            this.position = position;
            MyNoteActivity.startWithParams(this, item);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.i("笔记回调成功,requestCode:" + requestCode + ",resultCode:" + resultCode);
        if (requestCode == AppConstant.REQUEST_CODE && resultCode == AppConstant.RESULT_CODE) {
            Bundle bundle = data.getExtras();//笔记添加或者修改回调
            if (bundle != null) {
                boolean noteFlag = bundle.getBoolean("noteFlag", false);
                Collection collection = (Collection) bundle.getSerializable("collection");
                if (collection == null) {
                    return;
                }
                if (noteFlag) {
                    myCollectionAdapter.getData().set(position, collection);
                    myCollectionAdapter.refreshNotifyItemChanged(position);//局部刷新
                } else {//添加
                    myCollectionAdapter.addData(0,collection);
                    showContentView();
                    //滚动到顶部
                    RecyclerViewLocation.moveToPosition((LinearLayoutManager) mRecyclerView.getLayoutManager(), 0,false);
                }

            }
        }
    }

    @Override
    public void onLoadMoreRequested() {
        mPresenter.loadMore();//加载更多
    }

    private static int ON_TOP = 1;

    private static int CANCEL_TOP = 0;


    @Override
    public boolean onItemLongClick(BaseQuickAdapter adapter, View view, final int position) {
        if (mEditMode) {
            return true;//编辑模式
        }
        VibrateHelper.vSimple(this, 80);//震动80毫秒


        final Collection item = myCollectionAdapter.getItem(position);


        popWindow = new PopWindow(this, new PopWindow.PopWindowOnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow.dismiss();
                popWindow.backgroundAlpha(MyCollectionActivity.this, 1f);
                switch (v.getId()) {
                    case R.id.tv_share://分享
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_article_url, getString(R.string.app_name), item.getTitle(), item.getHref()));
                        intent.setType("text/plain");
                        startActivity(intent);
                        break;
                    case R.id.tv_delete://删除
                        index--;
                        mPresenter.deleteCollection(item);
                        myCollectionAdapter.remove(position);
                        showEmptyView();
                        break;
                    case R.id.tv_more://更多
                        mEditMode = true;
                        mBottomDialog.setVisibility(View.VISIBLE);
                        myCollectionAdapter.setEditMode(mEditMode);
                        break;
                    case R.id.tv_top://置顶
                        if (popWindow.isExecuteTopFlag()){//置顶
                            //置顶
                            item.setTop(ON_TOP);
                            item.setTime(System.currentTimeMillis());
                        }else {
                            //取消
                            item.setTop(CANCEL_TOP);
                            item.setTime(0);
                        }
                        Observable.just(item)
                                .map(new Function<Collection, Boolean>() {

                                    @Override
                                    public Boolean apply(Collection collection) throws Exception {
                                        //更新数据库，后期分页会查询出来，要不查询出来可以在页面退出的时候再更新数据库
                                        mPresenter.updateCollection(item);
                                        List<Collection> data = myCollectionAdapter.getData();
                                        Collections.sort(data);
                                        return true;
                                    }
                                })
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .as(MyCollectionActivity.this.<Boolean>bindLifecycle())
                                .subscribe(new Consumer<Boolean>() {
                                    @Override
                                    public void accept(Boolean next) throws Exception {

                                        myCollectionAdapter.notifyDataSetChanged();
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {

                                    }
                                });

                        break;
                    default:
                        break;
                }
            }
        });
        popWindow.setTopStates(item.getTop());
        popWindow.showPopupWindow(view);
        return true;//这样不会触发点击事件
    }


    /**
     * 显示空布局
     */
    private void showEmptyView() {
        List<Collection> data = myCollectionAdapter.getData();
        if (data == null || data.size() == 0) {
            mContent.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 显示内容布局
     */
    private void showContentView() {
        List<Collection> data = myCollectionAdapter.getData();
        if (data != null || data.size() > 0) {
            mContent.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VibrateHelper.stop();
    }

    @Override
    public void onBackPressed() {
        if (mEditMode) {
            index = 0;
            mEditMode = false;
            myCollectionAdapter.setEditMode(mEditMode);
            setBtnBackground(index);
            mBottomDialog.setVisibility(View.GONE);
            return;
        }
        super.onBackPressed();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_delete:
                batchDeleteCollection();
                break;
            case R.id.select_all:
                selectAllCollection();
                break;
            default:
                break;
        }
    }


    /**
     * 批量删除
     */
    private void batchDeleteCollection() {
        final AlertDialog builder = new AlertDialog.Builder(this)
                .create();
        builder.show();
        if (builder.getWindow() == null) return;
        builder.getWindow().setContentView(R.layout.dialog_confirm);//设置弹出框加载的布局
        TextView msg = (TextView) builder.findViewById(R.id.tv_msg);
        Button cancel = (Button) builder.findViewById(R.id.btn_cancle);
        Button sure = (Button) builder.findViewById(R.id.btn_sure);
        if (index == 1) {
            msg.setText("删除后不可恢复，是否删除该条目？");
        } else {
            msg.setText("删除后不可恢复，是否删除这" + index + "个条目？");
        }
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        //确认删除
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = myCollectionAdapter.getData().size();
                for (int i = size, j = 0; i > j; i--) {
                    Collection collection = myCollectionAdapter.getData().get(i - 1);
                    if (collection.isSelectItem()) {
                        mPresenter.deleteCollection(collection);
                        myCollectionAdapter.getData().remove(collection);
                        index--;
                    }
                }
                setBtnBackground(index);
                if (index == 0) {
                    mBottomDialog.setVisibility(View.GONE);
                }
                mEditMode = false;
                myCollectionAdapter.setEditMode(mEditMode);
                builder.dismiss();
                showEmptyView();
            }
        });
    }


    /**
     * 全选和反选
     */
    private void selectAllCollection() {
        int size = myCollectionAdapter.getData().size();
        if (!isSelectAll) {
            for (int i = 0, j = size; i < j; i++) {
                myCollectionAdapter.getData().get(i).setSelectItem(true);
            }
            index = myCollectionAdapter.getData().size();
            mBtnDelete.setEnabled(true);
            mSelectAll.setText("取消全选");
            isSelectAll = true;
        } else {
            for (int i = 0, j = size; i < j; i++) {
                myCollectionAdapter.getData().get(i).setSelectItem(false);
            }
            index = 0;
            mBtnDelete.setEnabled(false);
            mSelectAll.setText("全选");
            isSelectAll = false;
        }
        myCollectionAdapter.notifyDataSetChanged();
        setBtnBackground(index);
    }


    /**
     * 根据选择的数量是否为0来判断按钮的是否可点击.
     *
     * @param size
     */
    private void setBtnBackground(int size) {
        if (size != 0) {
            mBtnDelete.setBackgroundResource(R.drawable.button_shape);
            mBtnDelete.setEnabled(true);
            mBtnDelete.setTextColor(Color.WHITE);
        } else {
            mBtnDelete.setBackgroundResource(R.drawable.button_noclickable_shape);
            mBtnDelete.setEnabled(false);
            mBtnDelete.setTextColor(ContextCompat.getColor(this, R.color.color_b7b8bd));
        }
        mTvSelectNum.setText(String.valueOf(size));
    }
}
