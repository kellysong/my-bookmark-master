package com.sjl.bookmark.ui.presenter;

import android.content.Intent;

import com.sjl.bookmark.ui.activity.MainActivity;
import com.sjl.bookmark.ui.contract.CheckLockContract;
import com.sjl.bookmark.widget.LockPatternUtils;
import com.sjl.bookmark.widget.LockPatternView;

import java.util.List;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename CheckLockPresenter.java
 * @time 2018/3/5 11:05
 * @copyright(C) 2018 song
 */
public class CheckLockPresenter extends CheckLockContract.Presenter {


    public void check(List<LockPatternView.Cell> pattern) {
        if (pattern == null) return;

        LockPatternUtils instances = LockPatternUtils.getInstances(mContext);
        if (instances.checkPattern(pattern)) {
            Intent intent = new Intent(mContext, MainActivity.class);
            mContext.startActivity(intent);
            mView.kill();
        } else {
            mView.lockDisplayError();
        }
    }
}
