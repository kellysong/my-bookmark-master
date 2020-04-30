package com.sjl.bookmark.ui.activity;

import android.os.Handler;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.sjl.bookmark.R;
import com.sjl.bookmark.app.AppConstant;
import com.sjl.bookmark.entity.ThemeSkin;
import com.sjl.bookmark.kotlin.language.I18nUtils;
import com.sjl.bookmark.ui.adapter.ThemeSkinAdapter;
import com.sjl.core.mvp.BaseActivity;
import com.sjl.core.util.log.LogUtils;
import com.sjl.core.util.PreferencesHelper;
import com.sjl.core.util.file.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.feng.skin.manager.listener.ILoaderListener;
import cn.feng.skin.manager.loader.SkinManager;

/**
 * 更换主题
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ChangeSkinActivity.java
 * @time 2018/12/27 16:20
 * @copyright(C) 2018 song
 */
public class ChangeSkinActivity extends BaseActivity {
    @BindView(R.id.common_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.theme_recyclerview)
    RecyclerView mThemeRecyclerview;


    private String skinDir;
    private ThemeSkinAdapter mThemeSkinAdapter;
    private List<ThemeSkin> themeSkinList;

    @Override
    protected int getLayoutId() {
        return R.layout.change_skin_activity;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {
        bindingToolbar(mToolbar, I18nUtils.getString(R.string.title_change_subject));
    }


    @Override
    protected void initData() {
        skinDir = getSkinDir().getAbsolutePath();
        themeSkinList = new ArrayList<>();
        themeSkinList.add(new ThemeSkin(0, "默认", "", "#03A9F4"));
        themeSkinList.add(new ThemeSkin(1, "高贵棕", "skin_brown.skin", "#4e342e"));
        themeSkinList.add(new ThemeSkin(2, "酷炫黑", "skin_black.skin", "#212121"));

        themeSkinList.add(new ThemeSkin(3, "激情红", "skin_red.skin", "#F44336"));
        themeSkinList.add(new ThemeSkin(4, "舒适绿", "skin_green.skin", "#4CAF50"));
        themeSkinList.add(new ThemeSkin(5, "活力橙", "skin_orange.skin", "#FF9800"));
        themeSkinList.add(new ThemeSkin(6, "高雅灰", "skin_grey.skin", "#9E9E9E"));

        mThemeSkinAdapter = new ThemeSkinAdapter(R.layout.theme_skin_recycle_item, themeSkinList);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mThemeRecyclerview.setLayoutManager(layoutManager);
        mThemeRecyclerview.setAdapter(mThemeSkinAdapter);
        mThemeSkinAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                int skin = PreferencesHelper.getInstance(mContext).getInteger(AppConstant.SETTING.CURRENT_SELECT_SKIN, 0);
                if (skin == position) {
                    return;
                }
                PreferencesHelper.getInstance(mContext).put(AppConstant.SETTING.CURRENT_SELECT_SKIN, position);
                ThemeSkin item = (ThemeSkin) adapter.getItem(position);
                if (position == 0) {
                    SkinManager.getInstance().restoreDefaultTheme();
                } else {
                    loadSkin(item.getSkinFileName());
                }
                adapter.notifyDataSetChanged();
            }
        });

        mSwipeRefreshLayout.setColorSchemeResources(R.color.blueStatus);//设置下拉圆圈的颜色
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {//下拉刷新
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateSkin();
                        mSwipeRefreshLayout.setRefreshing(false);
                        int skin = PreferencesHelper.getInstance(mContext).getInteger(AppConstant.SETTING.CURRENT_SELECT_SKIN, 0);
                        ThemeSkin themeSkin = mThemeSkinAdapter.getData().get(skin);
                        loadSkin(themeSkin.getSkinFileName());
                        showShortToast("更新成功");
                    }
                }, 1000);

            }
        });
    }

    /**
     * 手动更新皮肤
     */
    private void updateSkin() {
        List<ThemeSkin> data = mThemeSkinAdapter.getData();
        if (data != null && !data.isEmpty()) {
            for (ThemeSkin themeSkin : data) {
                String skinFullName = skinDir + File.separator + themeSkin.getSkinFileName();
                File file = new File(skinFullName);
                if (file.exists()) {
                    boolean delete = file.delete();
                    LogUtils.i("delete:" + delete);
                }
            }
        }
    }

    /**
     * 加载皮肤
     *
     * @param skinName 皮肤插件名
     */
    private synchronized void loadSkin(String skinName) {
        String skinFullName = skinDir + File.separator + skinName;
        File file = new File(skinFullName);
        if (!file.exists()) {
            try {
                File parentDir = file.getParentFile();
                if (!parentDir.exists()) {
                    parentDir.mkdirs();
                }
                file.createNewFile();
                InputStream inputStream = mContext.getAssets().open("skin/" + skinName);
                FileUtils.fileCopy(inputStream, file);
            } catch (IOException e) {
                LogUtils.e("皮肤拷贝异常", e);
                return;
            }
        }
        SkinManager.getInstance().load(file.getAbsolutePath(),
                new ILoaderListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess() {
                        Toast.makeText(mContext, "主题更换成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed() {
                        Toast.makeText(mContext, "主题更换失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 得到皮肤目录
     *
     * @return
     */
    public File getSkinDir() {
        File skinDir = new File(FileUtils.getFilePath(), "skin");
        if (!skinDir.exists()) {
            skinDir.mkdirs();
        }
        return skinDir;
    }
}
