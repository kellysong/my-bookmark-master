package com.sjl.bookmark.ui.contract

import android.widget.EditText
import com.sjl.bookmark.entity.ExpressCompany
import com.sjl.bookmark.entity.ExpressName
import com.sjl.core.mvp.BaseContract.IBaseView
import com.sjl.core.mvp.BasePresenter

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ExpressSearchContract.java
 * @time 2018/11/26 11:24
 * @copyright(C) 2018 song
 */
interface ExpressSearchContract {
    interface View : IBaseView {
        fun showSuggestionCompany(expressName: ExpressName)
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun initCompany(): Map<String, ExpressCompany>
        abstract fun getSuggestionList(postId: String)
        abstract fun getSuggestionList(etPostId: EditText)
    }
}