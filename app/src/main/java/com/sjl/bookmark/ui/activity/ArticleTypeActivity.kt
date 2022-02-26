package com.sjl.bookmark.ui.activity;

import android.content.Intent;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.sjl.bookmark.R;
import com.sjl.bookmark.entity.Category;
import com.sjl.bookmark.net.HttpConstant;
import com.sjl.bookmark.ui.adapter.ArticleTypePagerListAdapter;
import com.sjl.core.mvp.BaseActivity;

import java.util.List;

import butterknife.BindView;

/**
 * 文章类型
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ArticleTypeActivity.java
 * @time 2018/3/23 13:47
 * @copyright(C) 2018 song
 */
public class ArticleTypeActivity extends BaseActivity {
    @BindView(R.id.common_toolbar)
    Toolbar mToolBar;
    @BindView(R.id.tabArticleTypes)
    TabLayout mTabArticleTypes;
    @BindView(R.id.vpArticleTypes)
    ViewPager mVpArticleTypes;

    private ArticleTypePagerListAdapter mArticleTypePagerListAdapter;
    public List<Category.ChildrenBean> childrenData;
    private boolean searchMenuFlag = true;
    @Override
    protected int getLayoutId() {
        return R.layout.article_type_activity;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        String title = intent.getStringExtra(HttpConstant.CONTENT_TITLE_KEY);
        String openFlag = intent.getStringExtra(HttpConstant.CONTENT_OPEN_FLAG);
        if ("0".equals(openFlag)){
            searchMenuFlag = false;
        }else{
            searchMenuFlag = true;
        }
        bindingToolbar(mToolBar,title);
        childrenData = intent.getParcelableArrayListExtra(HttpConstant.CONTENT_CHILDREN_DATA_KEY);
        mArticleTypePagerListAdapter = new ArticleTypePagerListAdapter(getSupportFragmentManager(), childrenData);
        mVpArticleTypes.setOffscreenPageLimit(2);
        mVpArticleTypes.setAdapter(mArticleTypePagerListAdapter);
        mTabArticleTypes.setupWithViewPager(mVpArticleTypes);
    }
    /**
     * onCreate之后执行
     * 每次在display Menu之前，都会去调用，只要按一次Menu按鍵，就会调用一次。所以可以在这里动态的改变menu。
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem searchMenuItem = menu.getItem(0);
        if (!searchMenuFlag){
            searchMenuItem.setVisible(false);
        }else{
            searchMenuItem.setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.category_type_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuShare) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_type_url, getString(R.string.app_name),
                    childrenData.get(mTabArticleTypes.getSelectedTabPosition()).getName(), childrenData.get(mTabArticleTypes.getSelectedTabPosition()).getId()));
            intent.setType("text/plain");
            startActivity(Intent.createChooser(intent, getString(R.string.share_title)));
        } else if (item.getItemId() == R.id.menuSearch) {
            startActivity(new Intent(this,ArticleSearchActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }


}
