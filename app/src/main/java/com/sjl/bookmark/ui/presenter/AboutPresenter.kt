package com.sjl.bookmark.ui.presenter

import com.sjl.bookmark.app.MyApplication
import com.sjl.bookmark.ui.contract.AboutContract
import org.greenrobot.eventbus.EventBus

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename AboutPresenter.java
 * @time 2018/3/2 14:55
 * @copyright(C) 2018 song
 */
class AboutPresenter : AboutContract.Presenter() {
    override fun getCurrentVersion() {
        EventBus.getDefault().post(MyApplication.getAppVersion())
    }
}