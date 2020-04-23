package com.sjl.bookmark.ui.presenter;

import com.sjl.bookmark.app.MyApplication;
import com.sjl.bookmark.dao.impl.HistoryExpressService;
import com.sjl.bookmark.entity.table.HistoryExpress;
import com.sjl.bookmark.ui.contract.ExpressContract;

import java.util.List;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ExpressPresenter.java
 * @time 2018/4/26 15:36
 * @copyright(C) 2018 song
 */
public class ExpressPresenter extends ExpressContract.Presenter {

    private HistoryExpressService historyExpressService;

    public ExpressPresenter() {
        historyExpressService = new HistoryExpressService(MyApplication.getContext());
    }

    /**
     * 获取未验收的快递
     */
    @Override
    public void getUnCheckList() {
        List<HistoryExpress> unCheckList = historyExpressService.getUnCheckList();
        mView.setHistoryExpress(unCheckList);
    }

    /**
     * 获取所有历史快递，包括已验收和未验收
     */
    @Override
    public void getHistoryExpresses() {
        List<HistoryExpress> historyExpresses = historyExpressService.queryHistoryExpresses();
        mView.setHistoryExpress(historyExpresses);
    }
}
