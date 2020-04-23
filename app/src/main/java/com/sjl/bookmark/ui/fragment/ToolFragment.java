package com.sjl.bookmark.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sinpo.xnfc.NFCardActivity;
import com.sjl.bookmark.R;
import com.sjl.bookmark.entity.ModuleMenu;
import com.sjl.bookmark.kotlin.language.I18nUtils;
import com.sjl.bookmark.ui.activity.AccountIndexActivity;
import com.sjl.bookmark.ui.activity.BookShelfActivity;
import com.sjl.bookmark.ui.activity.BrowserActivity;
import com.sjl.bookmark.ui.activity.ExpressActivity;
import com.sjl.bookmark.ui.activity.NewsListActivity;
import com.sjl.bookmark.ui.activity.WifiQueryActivity;
import com.sjl.core.entity.EventBusDto;
import com.sjl.core.mvp.BaseFragment;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.feng.skin.manager.loader.SkinManager;

/**
 * 功能菜单fragment
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ToolFragment.java
 * @time 2018/3/27 17:10
 * @copyright(C) 2018 song
 */
public class ToolFragment extends BaseFragment {
    @BindView(R.id.rv_tool)
    RecyclerView rvContent;
    private List<ModuleMenu> moduleMenus;

    @Override
    protected int getLayoutId() {
        return R.layout.tool_fragment;
    }


    @Override
    protected void initData() {
        GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 3);
        rvContent.setLayoutManager(layoutManager);
        moduleMenus = new ArrayList<>();
        moduleMenus.add(new ModuleMenu(I18nUtils.getString(R.string.tool_account_manager), R.mipmap.menu_password, AccountIndexActivity.class));
        moduleMenus.add(new ModuleMenu(I18nUtils.getString(R.string.tool_wifi_query), R.mipmap.menu_wifi, WifiQueryActivity.class));
        moduleMenus.add(new ModuleMenu(I18nUtils.getString(R.string.tool_balance_query), R.mipmap.menu_card, NFCardActivity.class));
        moduleMenus.add(new ModuleMenu(I18nUtils.getString(R.string.tool_shenzhen_subway), R.mipmap.menu_subway, "http://www.szmc.net/page/html5.html"));
        moduleMenus.add(new ModuleMenu(I18nUtils.getString(R.string.tool_my_express), R.mipmap.menu_express_query, ExpressActivity.class));
        moduleMenus.add(new ModuleMenu(I18nUtils.getString(R.string.tool_cartoon), R.mipmap.menu_caricature, "https://m.ac.qq.com/search/index"));
        moduleMenus.add(new ModuleMenu(I18nUtils.getString(R.string.tool_novel_read), R.mipmap.menu_book, BookShelfActivity.class));
        moduleMenus.add(new ModuleMenu(I18nUtils.getString(R.string.tool_zhihu_daily), R.mipmap.menu_news, NewsListActivity.class));

        rvContent.setAdapter(new CommonAdapter<ModuleMenu>(mActivity, R.layout.tool_recycle_item, moduleMenus) {

            @Override
            protected void convert(ViewHolder holder, final ModuleMenu moduleMenu, final int position) {
                holder.setText(R.id.tv_title, moduleMenu.getTitle());
                holder.setImageResource(R.id.iv_icon, moduleMenu.getDrawableId());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (position) {
                            case 2:
                                int color = SkinManager.getInstance().getColorPrimary();
                                int statusBarColor;
                                if (color != -1) {
                                    statusBarColor = color;
                                } else {
                                    statusBarColor = getResources().getColor(R.color.colorPrimary);
                                }
                                Bundle bundle = new Bundle();
                                bundle.putInt("nfcColor", statusBarColor);//适配主题更换是，nfc模块标题栏和状态栏颜色
                                bundle.putInt("isActivityOpen", 1);
                                bundle.putString("title",I18nUtils.getString(R.string.tool_balance_query));
                                openActivity(moduleMenu.getClz(), bundle);
                                break;
                            case 3:
                            case 5:
                                BrowserActivity.startWithParams(mActivity, moduleMenu.getTitle(),
                                        moduleMenu.getWebUrl());
                                break;
                            default:
                                openActivity(moduleMenu.getClz());
                                break;
                        }
                    }
                });
            }
        });
    }


    @Override
    protected void onFirstUserVisible() {

    }

    @Override
    protected void onUserVisible() {

    }

    @Override
    protected void onUserInvisible() {

    }


    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    public void onEventComing(EventBusDto eventCenter) {

    }
}
