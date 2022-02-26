package com.sjl.bookmark.ui.presenter

import android.app.ProgressDialog
import android.content.DialogInterface
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.sjl.bookmark.R
import com.sjl.bookmark.api.MyBookmarkService
import com.sjl.bookmark.app.MyApplication
import com.sjl.bookmark.dao.impl.CollectDaoImpl
import com.sjl.bookmark.entity.dto.ResponseDto
import com.sjl.bookmark.entity.table.Collection
import com.sjl.bookmark.net.HttpConstant
import com.sjl.bookmark.ui.contract.MyCollectionContract
import com.sjl.core.net.ErrorCode
import com.sjl.core.net.RetrofitHelper
import com.sjl.core.net.RxSchedulers
import com.sjl.core.util.AppUtils
import com.sjl.core.util.log.LogUtils
import com.uber.autodispose.ObservableSubscribeProxy
import io.reactivex.functions.Consumer

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename MyCollectionPresenter.java
 * @time 2018/3/26 10:15
 * @copyright(C) 2018 song
 */
class MyCollectionPresenter : MyCollectionContract.Presenter() {
    private val mCollectService: CollectDaoImpl
    private var title: String? = null
    private var mPage: Int = 1
    private var mIsRefresh: Boolean

    /**
     * 初始化收藏
     */
    override fun loadMyCollection() {
        val collections: List<Collection> =
            mCollectService.queryCollectByPage(title, mPage, HttpConstant.PAGE_SIZE)
        val loadType: Int =
            if (mIsRefresh) HttpConstant.LoadType.TYPE_REFRESH_SUCCESS else HttpConstant.LoadType.TYPE_LOAD_MORE_SUCCESS
        mView.setMyCollection(collections, loadType)
    }

    /**
     * 查询收藏
     *
     * @param title
     */
    fun queryMyCollection(title: String?) {
        this.title = title
        mPage = 1
        mIsRefresh = true
        loadMyCollection()
    }

    /**
     * 分页加载收藏
     */
    override fun loadMore() {
        mPage++
        mIsRefresh = false
        loadMyCollection()
    }

    /**
     * 删除收藏
     *
     * @param collection
     */
    override fun deleteCollection(collection: Collection) {
        mCollectService.deleteCollection(collection)
    }

    /**
     * 数据为空，询问是否从服务器端恢复收藏的书签
     */
    override fun recoverCollectionDataFromServer() {
        if (!AppUtils.isConnected(mContext)) {
            return
        }
        val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
        builder.setTitle(R.string.nb_common_tip)
        builder.setMessage(R.string.collection_recover_hint)
            .setPositiveButton(R.string.sure, object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, id: Int) {
                    val progressDialog: ProgressDialog = ProgressDialog(mContext)
                    progressDialog.setMessage(mContext.getString(R.string.collection_recover_hint2))
                    progressDialog.setCancelable(false)
                    progressDialog.show()
                    requestCollectionData(progressDialog)
                }
            })
            .setNegativeButton(R.string.cancel, object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, id: Int) {
                    dialog.dismiss()
                }
            })
        val dialog: AlertDialog = builder.create()
        //点击对话框外面,对话框不消失
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    /**
     * 请求服务器数据
     *
     * @param progressDialog
     */
    private fun requestCollectionData(progressDialog: ProgressDialog) {
        val instance: RetrofitHelper = RetrofitHelper.getInstance()
        val apiService: MyBookmarkService = instance.getApiService(
            MyBookmarkService::class.java
        )
        apiService.findAllCollection().compose(RxSchedulers.applySchedulers())
            .`as`<ObservableSubscribeProxy<ResponseDto<List<Collection>>>>(
                bindLifecycle<ResponseDto<List<Collection>>>()
            )
            .subscribe(object : Consumer<ResponseDto<List<Collection>>> {
                @Throws(Exception::class)
                override fun accept(dataResponse: ResponseDto<List<Collection>>) {
                    if (dataResponse.code == 0) {
                        val collectionList: List<Collection?> = dataResponse.data
                        mCollectService.batchSaveCollection(collectionList)
                        LogUtils.i("收藏恢复成功，共恢复" + collectionList.size + "条")
                        loadMyCollection() //刷新列表数据
                        progressDialog.cancel()
                        Toast.makeText(
                            mContext,
                            mContext.getString(
                                R.string.collection_recover_hint3,
                                collectionList.size
                            ),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (dataResponse.code == ErrorCode.ERROR_NO_DATA) {
                        progressDialog.cancel()
                        Toast.makeText(
                            mContext,
                            mContext.getString(R.string.collection_recover_hint4),
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        progressDialog.cancel()
                        Toast.makeText(
                            mContext,
                            mContext.getString(R.string.collection_recover_hint5) + "," + dataResponse.msg,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }, object : Consumer<Throwable?> {
                @Throws(Exception::class)
                override fun accept(throwable: Throwable?) {
                    LogUtils.e("收藏恢复异常", throwable)
                    progressDialog.cancel()
                    Toast.makeText(
                        mContext,
                        mContext.getString(R.string.collection_recover_hint5),
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    /**
     * 更新收藏
     * @param item
     */
    fun updateCollection(item: Collection?) {
        mCollectService.updateCollection(item)
    }

    init {
        mCollectService = CollectDaoImpl(MyApplication.getContext())
        mIsRefresh = true
    }
}