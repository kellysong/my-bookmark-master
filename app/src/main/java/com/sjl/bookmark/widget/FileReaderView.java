package com.sjl.bookmark.widget;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.tencent.smtt.sdk.TbsReaderView;

import java.io.File;

/**
 * 文件阅读器
 *
 * 插件加载失败
 首次打开相关文件的时候需要下载相关文件的插件，因此需要保持网络可用状态，否则下载插件失败会出现这个错误。

 NoSuchMethodException: onCallBackAction
 这个错误我也是懵逼，不管成功打开与否，都会有这玩意，所以暂时先忽略。
 */
public class FileReaderView extends FrameLayout implements TbsReaderView.ReaderCallback {
    private static final String TAG = "FileReaderView";
    private TbsReaderView mTbsReaderView;

    public FileReaderView(Context context) {
        this(context, null, 0);
    }

    public FileReaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FileReaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTbsReaderView = getTbsReaderView(context);
        this.addView(mTbsReaderView, new LinearLayout.LayoutParams(-1, -1));
    }


    private TbsReaderView getTbsReaderView(Context context) {
        return new TbsReaderView(context, this);
    }


    @Override
    public void onCallBackAction(Integer integer, Object o, Object o1) {
        Log.e(TAG, "文件打开回调：" + integer);

    }

    /**
     * 务必在onDestroy方法中调用此方法，否则第二次打开无法浏览
     */
    public void stop() {
        if (mTbsReaderView != null) {
            mTbsReaderView.onStop();
        }
    }


    /**
     * 打开文件预览,x5内核加载失败的情况下或者未加载完毕也会预览文件失败
     *
     * @param filePath
     */
    public void show(String filePath) {
        //增加下面一句解决没有TbsReaderTemp文件夹存在导致加载文件失败
        final String tbsReaderTemp = Environment.getExternalStorageDirectory() + "/TbsReaderTemp";
        File bsReaderTempFile = new File(tbsReaderTemp);
        if (!bsReaderTempFile.exists()) {
            bsReaderTempFile.mkdir();
        }
        Bundle bundle = new Bundle();
        bundle.putString("filePath", filePath);
        bundle.putString("tempPath", tbsReaderTemp);
        String fileType = getFileType(filePath);
        boolean result = mTbsReaderView.preOpen(fileType, false);
        if (result) {
            mTbsReaderView.openFile(bundle);
        } else {
            Log.e(TAG, "不支持打开该文件或者文件路径无效！");
        }
    }

    /***
     * 获取文件类型
     */
    private String getFileType(String paramString) {
        String str = "";
        if (TextUtils.isEmpty(paramString)) {
            return str;
        }
        int i = paramString.lastIndexOf('.');
        if (i <= -1) {
            return str;
        }
        str = paramString.substring(i + 1);
        Log.d(TAG, "paramString.substring(i + 1)------>" + str);
        return str;
    }
}
