package com.sjl.bookmark.ui.contract;

import com.sjl.bookmark.entity.zhuishu.BookDetailDto;
import com.sjl.bookmark.entity.zhuishu.HotCommentDto;
import com.sjl.bookmark.entity.zhuishu.table.CollectBook;
import com.sjl.bookmark.entity.zhuishu.table.RecommendBook;
import com.sjl.core.mvp.BaseContract;
import com.sjl.core.mvp.BasePresenter;

import java.util.List;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookDetailContract.java
 * @time 2018/12/7 10:09
 * @copyright(C) 2018 song
 */
public interface BookDetailContract {
    interface View extends BaseContract.IBaseView {
        void finishRefresh(BookDetailDto.BookDetail bookDetail);

        void finishHotComment(List<HotCommentDto.HotComment> hotCommentList);

        void finishRecommendBookList(List<RecommendBook> recommendBookList);

        void waitToBookShelf();

        void errorToBookShelf();

        void succeedToBookShelf();

        void showError();

        void complete();

    }

    abstract class Presenter extends BasePresenter<View> {

        public abstract void refreshBookDetail(String bookId);

        /**
         * 添加到书架上
         *
         * @param collectBook
         */
        public abstract void addToBookShelf(CollectBook collectBook);
    }
}
