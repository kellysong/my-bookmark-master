package com.sjl.bookmark.ui.contract;

import com.sjl.bookmark.entity.zhuishu.SearchBookDto;
import com.sjl.core.mvp.BaseContract;
import com.sjl.core.mvp.BasePresenter;

import java.util.List;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookSearchContract.java
 * @time 2018/11/30 16:59
 * @copyright(C) 2018 song
 */
public interface BookSearchContract {
    interface View extends BaseContract.IBaseView {
        void finishHotWords(List<String> hotWords);

        void finishKeyWords(List<String> keyWords);

        void finishBooks(List<SearchBookDto.BooksBean> books);

        void errorBooks();
    }

    abstract class Presenter extends BasePresenter<View> {
        public abstract void searchHotWord();

        /**
         * 搜索提示
         * @param query
         */
        public abstract void searchKeyWord(String query);

        /**
         * 搜索书籍
         * @param query
         */
        public abstract void searchBook(String query);
    }
}
