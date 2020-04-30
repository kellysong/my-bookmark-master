package com.sjl.bookmark.ui.activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.sjl.bookmark.R;
import com.sjl.bookmark.entity.table.Collection;
import com.sjl.bookmark.net.HttpConstant;
import com.sjl.bookmark.ui.adapter.MyCollectionAdapter;
import com.sjl.bookmark.ui.contract.MyCollectionContract;
import com.sjl.bookmark.ui.presenter.MyCollectionPresenter;
import com.sjl.core.mvp.BaseActivity;
import com.sjl.core.util.log.LogUtils;
import com.sjl.core.util.ViewUtils;

import java.util.List;

import butterknife.BindView;

import static com.sjl.bookmark.app.MyApplication.getContext;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename MyCollectionSearchActivity.java
 * @time 2018/4/10 15:51
 * @copyright(C) 2018 song
 */
public class MyCollectionSearchActivity extends BaseActivity<MyCollectionPresenter> implements MyCollectionContract.View, MyCollectionAdapter.OnItemClickListener,
        MyCollectionAdapter.RequestLoadMoreListener, View.OnClickListener {
    @BindView(R.id.cet_word)
    EditText etSearch;//输入框
    @BindView(R.id.btn_search)
    TextView mSearch;//取消和搜索
    @BindView(R.id.tv_msg)
    TextView mMsg;//消息提示

    @BindView(R.id.rv_search_collection_content)
    RecyclerView mRecyclerView;

    MyCollectionAdapter myCollectionAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.my_collection_search_activity;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {
        mSearch.setOnClickListener(this);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                LogUtils.i("onTextChanged");
                String keywords = s.toString().trim();
                if (TextUtils.isEmpty(keywords)) {//没有输入
                    mMsg.setVisibility(View.VISIBLE);
                    mMsg.setText("搜索收藏内容");
                    mRecyclerView.setVisibility(View.GONE);
                } else {
                    //有输入，实时搜索
                    mPresenter.queryMyCollection(keywords);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                LogUtils.i("afterTextChanged");
            }
        });
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    String keywords = etSearch.getText().toString().trim();
                    if (!TextUtils.isEmpty(keywords)) {//回车搜索
                        ViewUtils.hideKeyBoard(MyCollectionSearchActivity.this, etSearch);
                        mPresenter.queryMyCollection(keywords);
                    }
                    return true;
                }
                return false;
            }
        });
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
        myCollectionAdapter.setOnLoadMoreListener(this, mRecyclerView);
    }

    @Override
    public void setMyCollection(List<Collection> collections, int loadType) {
        switch (loadType) {
            case HttpConstant.LoadType.TYPE_REFRESH_SUCCESS:
                if (collections != null && collections.size() > 0) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mMsg.setVisibility(View.GONE);
                    myCollectionAdapter.setNewData(collections);
                    myCollectionAdapter.loadMoreComplete(); //加载完成
                } else {
                    mRecyclerView.setVisibility(View.GONE);
                    mMsg.setVisibility(View.VISIBLE);
                    mMsg.setText("暂无条目");
                }
                break;
            case HttpConstant.LoadType.TYPE_LOAD_MORE_SUCCESS://加载更多
                if (collections != null && collections.size() > 0) {
                    myCollectionAdapter.addData(collections);
                    myCollectionAdapter.loadMoreComplete(); //加载完成
                } else {
                    LogUtils.i("搜索收藏分页完毕");
                    myCollectionAdapter.loadMoreEnd(false); //数据全部加载完毕
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search://取消搜索
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Collection item = myCollectionAdapter.getItem(position);
        if (item.getType() == 0) {
            BrowserActivity.startWithParams(this, item.getTitle(), item.getHref());
        } else if (item.getType() == 1) {

        }
    }

    @Override
    public void onLoadMoreRequested() {
        mPresenter.loadMore();
    }
}
