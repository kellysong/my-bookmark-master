package com.sjl.bookmark.ui.contract;

import com.sjl.bookmark.entity.zhuishu.table.BookChapter;
import com.sjl.bookmark.widget.reader.bean.TxtChapter;
import com.sjl.core.mvp.BaseContract;
import com.sjl.core.mvp.BasePresenter;

import java.util.List;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookReadContract.java
 * @time 2018/12/4 15:51
 * @copyright(C) 2018 song
 */
public interface BookReadContract {
    interface View extends BaseContract.IBaseView {
        void showCategory(List<BookChapter> bookChapterList);

        void finishChapter();

        void errorChapter();
    }

    abstract class Presenter extends BasePresenter<View> {
        /**
         * 加载书籍章节目录
         * @param bookId
         */
        public abstract void loadCategory(String bookId);

        /**
         * 加载章节内容
         * @param bookId
         * @param bookChapterList
         */
        public abstract void loadChapter(String bookId, List<TxtChapter> bookChapterList);
    }
}
