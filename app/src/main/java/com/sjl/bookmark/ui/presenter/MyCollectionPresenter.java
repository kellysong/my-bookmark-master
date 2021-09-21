package com.sjl.bookmark.ui.presenter;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import com.sjl.bookmark.R;
import com.sjl.bookmark.api.MyBookmarkService;
import com.sjl.bookmark.app.MyApplication;
import com.sjl.bookmark.dao.impl.CollectDaoImpl;
import com.sjl.bookmark.entity.dto.ResponseDto;
import com.sjl.bookmark.entity.table.Collection;
import com.sjl.bookmark.net.HttpConstant;
import com.sjl.bookmark.ui.contract.MyCollectionContract;
import com.sjl.core.net.ErrorCode;
import com.sjl.core.net.RetrofitHelper;
import com.sjl.core.net.RxSchedulers;
import com.sjl.core.util.AppUtils;
import com.sjl.core.util.log.LogUtils;

import java.util.List;

import androidx.appcompat.app.AlertDialog;
import io.reactivex.functions.Consumer;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename MyCollectionPresenter.java
 * @time 2018/3/26 10:15
 * @copyright(C) 2018 song
 */
public class MyCollectionPresenter extends MyCollectionContract.Presenter {
    private CollectDaoImpl mCollectService;
    private String title;
    private int mPage = 1;
    private boolean mIsRefresh;

    public MyCollectionPresenter() {
        mCollectService = new CollectDaoImpl(MyApplication.getContext());
        this.mIsRefresh = true;
    }

    /**
     * 初始化收藏
     */
    @Override
    public void loadMyCollection() {
        List<Collection> collections = mCollectService.queryCollectByPage(title, mPage, HttpConstant.PAGE_SIZE);
        int loadType = mIsRefresh ? HttpConstant.LoadType.TYPE_REFRESH_SUCCESS : HttpConstant.LoadType.TYPE_LOAD_MORE_SUCCESS;
        mView.setMyCollection(collections, loadType);
    }


    /**
     * 查询收藏
     *
     * @param title
     */
    public void queryMyCollection(String title) {
        this.title = title;
        this.mPage = 1;
        this.mIsRefresh = true;
        loadMyCollection();
    }

    /**
     * 分页加载收藏
     */
    @Override
    public void loadMore() {
        mPage++;
        mIsRefresh = false;
        loadMyCollection();
    }

    /**
     * 删除收藏
     *
     * @param collection
     */
    @Override
    public void deleteCollection(Collection collection) {
        mCollectService.deleteCollection(collection);
    }

    /**
     * 数据为空，询问是否从服务器端恢复收藏的书签
     */
    @Override
    public void recoverCollectionDataFromServer() {
        if (!AppUtils.isConnected(mContext)) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.nb_common_tip);
        builder.setMessage(R.string.collection_recover_hint)
                .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ProgressDialog progressDialog = new ProgressDialog(mContext);
                        progressDialog.setMessage(mContext.getString(R.string.collection_recover_hint2));
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        requestCollectionData(progressDialog);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();

                    }
                });

        AlertDialog dialog = builder.create();
        //点击对话框外面,对话框不消失
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();


    }

    /**
     * 请求服务器数据
     *
     * @param progressDialog
     */
    private void requestCollectionData(final ProgressDialog progressDialog) {
        RetrofitHelper instance = RetrofitHelper.getInstance();
        MyBookmarkService apiService = instance.getApiService(MyBookmarkService.class);
        apiService.findAllCollection().compose(RxSchedulers.<ResponseDto<List<Collection>>>applySchedulers())
                .as(this.<ResponseDto<List<Collection>>>bindLifecycle())
                .subscribe(new Consumer<ResponseDto<List<Collection>>>() {
                    @Override
                    public void accept(ResponseDto<List<Collection>> dataResponse) throws Exception {
                        if (dataResponse.getCode() == 0) {
                            List<Collection> collectionList = dataResponse.getData();
                            mCollectService.batchSaveCollection(collectionList);
                            LogUtils.i("收藏恢复成功，共恢复" + collectionList.size() + "条");
                            loadMyCollection();//刷新列表数据
                            progressDialog.cancel();
                            Toast.makeText(mContext, mContext.getString(R.string.collection_recover_hint3,collectionList.size()), Toast.LENGTH_LONG).show();
                        } else if (dataResponse.getCode() == ErrorCode.ERROR_NO_DATA) {
                            progressDialog.cancel();
                            Toast.makeText(mContext, mContext.getString(R.string.collection_recover_hint4), Toast.LENGTH_LONG).show();
                        } else {
                            progressDialog.cancel();
                            Toast.makeText(mContext, mContext.getString(R.string.collection_recover_hint5)+"," + dataResponse.getMsg(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("收藏恢复异常", throwable);
                        progressDialog.cancel();
                        Toast.makeText(mContext, mContext.getString(R.string.collection_recover_hint5), Toast.LENGTH_LONG).show();
                    }
                });

    }

    /**
     * 更新收藏
     * @param item
     */
    public void updateCollection(Collection item) {
        mCollectService.updateCollection(item);
    }


}
