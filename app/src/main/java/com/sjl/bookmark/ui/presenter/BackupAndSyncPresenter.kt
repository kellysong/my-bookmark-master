package com.sjl.bookmark.ui.presenter

import android.preference.PreferenceFragment
import android.text.TextUtils
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sjl.bookmark.R
import com.sjl.bookmark.api.MyBookmarkService
import com.sjl.bookmark.app.AppConstant
import com.sjl.bookmark.dao.impl.CollectDaoImpl
import com.sjl.bookmark.entity.dto.ResponseDto
import com.sjl.bookmark.entity.table.Collection
import com.sjl.bookmark.ui.contract.BackupAndSyncContract
import com.sjl.core.net.RetrofitHelper
import com.sjl.core.net.RxLifecycleUtils
import com.sjl.core.net.RxSchedulers
import com.sjl.core.util.AppUtils
import com.sjl.core.util.PreferencesHelper
import com.sjl.core.util.SerializeUtils
import com.sjl.core.util.log.LogUtils
import com.sjl.core.widget.materialpreference.SwitchPreference
import com.uber.autodispose.ObservableSubscribeProxy
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers

/**
 * 备份与同步
 */
class BackupAndSyncPresenter : BackupAndSyncContract.Presenter() {
    private var recoverSize: Int = 0
    private var isAutoBackup: Boolean = false
    override fun setClickPreferenceKey(key: String) {
        if (TextUtils.equals(key, "立即同步")) {
            syncCollection()
        } else if (TextUtils.equals(key, "自动备份")) {
            isAutoBackup = !isAutoBackup
            val preferencesHelper: PreferencesHelper = PreferencesHelper.getInstance(mContext)
            preferencesHelper.put(AppConstant.SETTING.AUTO_BACKUP_COLLECTION, isAutoBackup)
        } else if (TextUtils.equals(key, "本地备份")) {
            localBackupCollection()
        } else if (TextUtils.equals(key, "本地恢复")) {
            localRecoverCollection()
        }
    }

    override fun init() {
        val preferencesHelper: PreferencesHelper = PreferencesHelper.getInstance(mContext)
        isAutoBackup =
            preferencesHelper.get(AppConstant.SETTING.AUTO_BACKUP_COLLECTION, true) as Boolean
        LogUtils.i("isAutoBackup=" + isAutoBackup)
        if (mView is PreferenceFragment) {
            val preferenceFragment: PreferenceFragment = mView as PreferenceFragment
            val autoBackup: SwitchPreference =
                preferenceFragment.findPreference("自动备份") as SwitchPreference
            autoBackup.isChecked = isAutoBackup
        }
    }

    /**
     * 同步收藏
     */
    private fun syncCollection() {
        if (!AppUtils.isConnected(mContext)) {
            Toast.makeText(mContext, R.string.network_unavailable, Toast.LENGTH_SHORT).show()
            return
        }
        mView.showLoading(mContext.getString(R.string.synchronizing))
        val collectService: CollectDaoImpl = CollectDaoImpl(mContext)
        val collection: List<Collection?> = collectService.findAllCollection()
        if (AppUtils.isEmpty(collection)) {
            mView.hideLoading(mContext.getString(R.string.sync_hint))
            return
        }
        val instance: RetrofitHelper = RetrofitHelper.getInstance()
        val apiService: MyBookmarkService = instance.getApiService(
            MyBookmarkService::class.java
        )
        val gson: Gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create()
        val collectionStr: String = gson.toJson(collection)
        apiService.syncCollection(collectionStr).compose(RxSchedulers.applySchedulers())
            .`as`<ObservableSubscribeProxy<ResponseDto<Any>>>(RxLifecycleUtils.bindLifecycle<ResponseDto<Any>>())
            .subscribe(object : Consumer<ResponseDto<Any>> {
                @Throws(Exception::class)
                override fun accept(dataResponse: ResponseDto<Any>) {
                    LogUtils.i("同步收藏响应结果码：" + dataResponse.code)
                    if (dataResponse.code == 0) {
                        mView.hideLoading(mContext.getString(R.string.sync_success))
                    } else {
                        mView.hideLoading(mContext.getString(R.string.sync_hint2))
                    }
                }
            }, object : Consumer<Throwable?> {
                @Throws(Exception::class)
                override fun accept(throwable: Throwable?) {
                    LogUtils.e("同步收藏异常", throwable)
                    mView.hideLoading(mContext.getString(R.string.sync_failed))
                }
            })
    }
    /**
     * LogUtils.i("同步收藏响应结果码：" + dataResponse.getCode());
     * if (dataResponse.getCode() == 0)
     * mView.hideLoading(mContext.getString(R.string.sync_success));
     * } else
     *
     * {
     * mView.hideLoading(mContext.getString(R.string.sync_hint2));
     * }
     */
    /**
     * 本地备份
     */
    private fun localBackupCollection() {
        mView.showLoading(mContext.getString(R.string.backup_hint))
        val collectService: CollectDaoImpl = CollectDaoImpl(mContext)
        val collection: List<Collection>? = collectService.findAllCollection()
        if (AppUtils.isEmpty(collection)) {
            mView.hideLoading(mContext.getString(R.string.backup_hint2))
            return
        }
        //开始序列化数据
        Observable.just(collection)
            .map(object : Function<List<Collection>?, Boolean> {
                @Throws(Exception::class)
                override fun apply(collections: List<Collection>): Boolean {
                    val ret: Boolean = SerializeUtils.serialize("collection", collections)
                    return ret
                }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .`as`(RxLifecycleUtils.bindLifecycle())
            .subscribe(object : Observer<Boolean> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(ret: Boolean) {
                    var msg: String? = ""
                    if (ret) {
                        msg = mContext.getString(R.string.backup_hint4, collection?.size)
                    } else {
                        msg = mContext.getString(R.string.backup_hint3)
                    }
                    mView.hideLoading(msg)
                }

                override fun onError(e: Throwable) {
                    LogUtils.e("备份收藏异常", e)
                    mView.hideLoading(mContext.getString(R.string.backup_hint3))
                }

                override fun onComplete() {
                    LogUtils.i("完成备份收藏")
                }
            })
    }

    /**
     * 本地恢复
     */
    private fun localRecoverCollection() {
        mView.showLoading(mContext.getString(R.string.recovering))
        //开始反序列化数据
        Observable.just("collection")
            .map(object : Function<String, Boolean> {
                @Throws(Exception::class)
                override fun apply(fileName: String): Boolean {
                    val deserialize: Any? = SerializeUtils.deserialize(fileName)
                    if (deserialize != null) {
                        val collectionList: List<Collection> = deserialize as List<Collection>
                        val collectService: CollectDaoImpl = CollectDaoImpl(mContext)
                        val ret: Boolean = collectService.deleteAll(Collection::class.java) //清空收藏记录
                        if (ret) {
                            recoverSize = collectionList.size
                            return collectService.batchSaveCollection(collectionList) //从本地恢复
                        }
                    }
                    return false
                }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .`as`(RxLifecycleUtils.bindLifecycle())
            .subscribe(object : Observer<Boolean> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(ret: Boolean) {
                    var msg: String? = ""
                    if (ret) {
                        msg = mContext.getString(R.string.recover_hint, recoverSize)
                    } else {
                        msg = mContext.getString(R.string.recover_hint2)
                    }
                    mView.hideLoading(msg)
                }

                override fun onError(e: Throwable) {
                    LogUtils.e("恢复收藏异常", e)
                    mView.hideLoading(mContext.getString(R.string.recover_hint2))
                }

                override fun onComplete() {
                    LogUtils.i("完成恢复收藏")
                }
            })
    }
}