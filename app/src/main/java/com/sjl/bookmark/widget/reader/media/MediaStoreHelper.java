package com.sjl.bookmark.widget.reader.media;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import com.sjl.core.net.RxSchedulers;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * 获取媒体库的数据。
 */

public class MediaStoreHelper {

    /**
     * 获取媒体库中所有的书籍文件
     * <p>
     * 暂时只支持 TXT
     *
     * @param activity
     * @param resultCallback
     */
    public static void getAllBookFile(FragmentActivity activity, MediaResultCallback resultCallback) {
        // 将文件的获取处理交给 LoaderManager。
        activity.getSupportLoaderManager()
                .initLoader(LoaderCreator.ALL_BOOK_FILE, null, new MediaLoaderCallbacks(activity, resultCallback));
    }

    public interface MediaResultCallback {
        void onResultCallback(List<File> files);
    }

    /**
     * Loader 回调处理
     */
    static class MediaLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {
        protected WeakReference<Context> mContext;
        protected WeakReference<MediaResultCallback> mResultCallback;

        public MediaLoaderCallbacks(Context context, MediaResultCallback resultCallback) {
            mContext = new WeakReference<>(context);
            mResultCallback = new WeakReference<>(resultCallback);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return LoaderCreator.create(mContext.get(), id, args);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            LocalFileLoader localFileLoader = (LocalFileLoader) loader;
            Observable.just(data).map(new Function<Cursor, List<File>>() {
                @Override
                public List<File> apply(Cursor cursor) throws Exception {
                    List<File> files = localFileLoader.parseData(data);
                    return files;
                }
            }).compose(RxSchedulers.applySchedulers()).subscribe(new Consumer<List<File>>() {
                @Override
                public void accept(List<File> files) throws Exception {

                    if (mResultCallback == null) {
                        return;
                    }
                    MediaResultCallback mediaResultCallback = mResultCallback.get();
                    if (mediaResultCallback != null) {
                        mediaResultCallback.onResultCallback(files);
                    }
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {

                }
            });

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }
    }
}
