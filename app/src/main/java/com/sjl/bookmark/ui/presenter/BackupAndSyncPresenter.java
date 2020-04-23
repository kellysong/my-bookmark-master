package com.sjl.bookmark.ui.presenter;

import android.preference.PreferenceFragment;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sjl.bookmark.api.MyBookmarkService;
import com.sjl.bookmark.app.AppConstant;
import com.sjl.bookmark.dao.impl.CollectDaoImpl;
import com.sjl.bookmark.entity.dto.ResponseDto;
import com.sjl.bookmark.entity.table.Collection;
import com.sjl.bookmark.ui.contract.BackupAndSyncContract;
import com.sjl.core.net.RetrofitHelper;
import com.sjl.core.net.RxLifecycleUtils;
import com.sjl.core.net.RxSchedulers;
import com.sjl.core.util.AppUtils;
import com.sjl.core.util.log.LogUtils;
import com.sjl.core.util.PreferencesHelper;
import com.sjl.core.util.SerializeUtils;
import com.sjl.core.widget.materialpreference.SwitchPreference;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 备份与同步
 */
public class BackupAndSyncPresenter extends BackupAndSyncContract.Presenter {
    private int recoverSize;
    private boolean isAutoBackup;

    @Override
    public void setClickPreferenceKey(String key) {
        if (TextUtils.equals(key, "立即同步")) {
            syncCollection();
        }else if (TextUtils.equals(key, "自动备份")) {
            isAutoBackup = !isAutoBackup;
            PreferencesHelper preferencesHelper = PreferencesHelper.getInstance(mContext);
            preferencesHelper.put(AppConstant.SETTING.AUTO_BACKUP_COLLECTION, isAutoBackup);
        } else if (TextUtils.equals(key, "本地备份")) {
            localBackupCollection();
        } else if (TextUtils.equals(key, "本地恢复")) {
            localRecoverCollection();
        }
    }

    @Override
    public void init() {
        PreferencesHelper preferencesHelper = PreferencesHelper.getInstance(mContext);
        isAutoBackup = (Boolean) preferencesHelper.get(AppConstant.SETTING.AUTO_BACKUP_COLLECTION, true);
        LogUtils.i("isAutoBackup=" + isAutoBackup);
        if (mView instanceof PreferenceFragment) {
            PreferenceFragment preferenceFragment = (PreferenceFragment) mView;
            SwitchPreference autoBackup = (SwitchPreference) preferenceFragment.findPreference("自动备份");
            autoBackup.setChecked(isAutoBackup);
        }
    }

    /**
     * 同步收藏
     */
    private void syncCollection() {
        if (!AppUtils.isConnected(mContext)) {
            Toast.makeText(mContext, "当前网络不可用，无法同步", Toast.LENGTH_SHORT).show();
            return;
        }
        mView.showLoading("正在同步...");
        CollectDaoImpl collectService = new CollectDaoImpl(mContext);
        List<Collection> collection = collectService.findAllCollection();
        if (AppUtils.isEmpty(collection)) {
            Toast.makeText(mContext, "当前没有可用数据，无法同步", Toast.LENGTH_SHORT).show();
            mView.hideLoading("当前没有可用数据，无法同步");
            return;
        }
        RetrofitHelper instance = RetrofitHelper.getInstance();
        MyBookmarkService apiService = instance.getApiService(MyBookmarkService.class);
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
        String collectionStr = gson.toJson(collection);
        apiService.syncCollection(collectionStr).compose(RxSchedulers.<ResponseDto<Object>>applySchedulers())
                .as(RxLifecycleUtils.<ResponseDto<Object>>bindLifecycle())
                .subscribe(new Consumer<ResponseDto<Object>>() {
                    @Override
                    public void accept(ResponseDto<Object> dataResponse) throws Exception {
                        LogUtils.i("同步收藏响应结果码：" + dataResponse.getResultCode());
                        if (dataResponse.getResultCode() == 0) {
                            mView.hideLoading("同步收藏成功");
                        } else {
                            mView.hideLoading("已同步至最新，无须同步");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("同步收藏异常", throwable);
                        mView.hideLoading("同步收藏失败");
                    }
                });

    }

    /**
     * 本地备份
     */
    private void localBackupCollection() {
        mView.showLoading("正在备份...");
        CollectDaoImpl collectService = new CollectDaoImpl(mContext);
        final List<Collection> collection = collectService.findAllCollection();
        if (AppUtils.isEmpty(collection)) {
            Toast.makeText(mContext, "当前没有可用数据，无法备份", Toast.LENGTH_SHORT).show();
            mView.hideLoading("当前没有可用数据，无法备份");
            return;
        }
        //开始序列化数据
        Observable.just(collection)
                .map(new Function<List<Collection>, Boolean>() {


                    @Override
                    public Boolean apply(List<Collection> collections) throws Exception {
                        boolean ret = SerializeUtils.serialize("collection", collections);
                        return ret;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(RxLifecycleUtils.<Boolean>bindLifecycle())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean ret) {
                        String msg = "";
                        if (ret) {
                            msg = "备份收藏成功，共" + collection.size() + "条";
                        } else {
                            msg = "备份收藏失败";
                        }
                        mView.hideLoading(msg);
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e("备份收藏异常", e);
                        mView.hideLoading("备份收藏异常");
                    }

                    @Override
                    public void onComplete() {
                        LogUtils.i("完成备份收藏");
                    }
                });
    }

    /**
     * 本地恢复
     */
    private void localRecoverCollection() {
        mView.showLoading("正在恢复...");
        //开始反序列化数据
        Observable.just("collection")
                .map(new Function<String, Boolean>() {


                    @Override
                    public Boolean apply(String fileName) throws Exception {
                        Object deserialize = SerializeUtils.deserialize(fileName);
                        if (deserialize != null) {
                            List<Collection> collectionList = (List<Collection>) deserialize;
                            CollectDaoImpl collectService = new CollectDaoImpl(mContext);
                            boolean ret = collectService.deleteAll(Collection.class);//清空收藏记录
                            if (ret) {
                                recoverSize = collectionList.size();
                                return collectService.batchSaveCollection(collectionList);//从本地恢复
                            }
                        }
                        return false;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(RxLifecycleUtils.<Boolean>bindLifecycle())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Boolean ret) {
                        String msg = "";
                        if (ret) {
                            msg = "恢复收藏成功，共" + recoverSize + "条";
                        } else {
                            msg = "恢复收藏失败";
                        }
                        mView.hideLoading(msg);
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e("恢复收藏异常", e);
                        mView.hideLoading("恢复收藏异常");
                    }

                    @Override
                    public void onComplete() {
                        LogUtils.i("完成恢复收藏");
                    }
                });

    }



}
