package com.sjl.bookmark.ui.contract;

import android.widget.EditText;

import com.sjl.bookmark.entity.ExpressCompany;
import com.sjl.bookmark.entity.ExpressName;
import com.sjl.core.mvp.BaseContract;
import com.sjl.core.mvp.BasePresenter;

import java.util.Map;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ExpressSearchContract.java
 * @time 2018/11/26 11:24
 * @copyright(C) 2018 song
 */
public interface ExpressSearchContract {
    interface View extends BaseContract.IBaseView {
        void showSuggestionCompany(ExpressName expressName);
    }

    abstract class Presenter extends BasePresenter<View> {
        public abstract Map<String, ExpressCompany> initCompany();


        public abstract void getSuggestionList(String postId);

        public abstract void getSuggestionList(EditText etPostId);
    }
}
