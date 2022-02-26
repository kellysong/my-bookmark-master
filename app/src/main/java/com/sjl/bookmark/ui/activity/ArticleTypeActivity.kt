package com.sjl.bookmark.ui.activity

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import com.sjl.bookmark.R
import com.sjl.bookmark.entity.Category.ChildrenBean
import com.sjl.bookmark.net.HttpConstant
import com.sjl.bookmark.ui.adapter.ArticleTypePagerListAdapter
import com.sjl.core.mvp.BaseActivity
import com.sjl.core.mvp.NoPresenter
import kotlinx.android.synthetic.main.article_type_activity.*
import kotlinx.android.synthetic.main.toolbar_default.*



/**
 * 文章类型
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ArticleTypeActivity.java
 * @time 2018/3/23 13:47
 * @copyright(C) 2018 song
 */
class ArticleTypeActivity : BaseActivity<NoPresenter>() {

    private lateinit var mArticleTypePagerListAdapter: ArticleTypePagerListAdapter
    var childrenData: List<ChildrenBean>? = null
    private var searchMenuFlag: Boolean = true
    override fun getLayoutId(): Int {
        return R.layout.article_type_activity
    }

    override fun initView() {}
    override fun initListener() {}
    override fun initData() {
        val intent: Intent = intent
        val title: String = intent.getStringExtra(HttpConstant.CONTENT_TITLE_KEY)
        val openFlag: String = intent.getStringExtra(HttpConstant.CONTENT_OPEN_FLAG)
        searchMenuFlag = !("0" == openFlag)
        bindingToolbar(common_toolbar, title)
        childrenData = intent.getParcelableArrayListExtra(HttpConstant.CONTENT_CHILDREN_DATA_KEY)
        mArticleTypePagerListAdapter =
            ArticleTypePagerListAdapter(supportFragmentManager, childrenData)
        vpArticleTypes.offscreenPageLimit = 2
        vpArticleTypes.adapter = mArticleTypePagerListAdapter
        tabArticleTypes.setupWithViewPager(vpArticleTypes)
    }

    /**
     * onCreate之后执行
     * 每次在display Menu之前，都会去调用，只要按一次Menu按鍵，就会调用一次。所以可以在这里动态的改变menu。
     *
     * @param menu
     * @return
     */
    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val searchMenuItem: MenuItem = menu.getItem(0)
        searchMenuItem.isVisible = searchMenuFlag
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.category_type_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menuShare) {
            val intent: Intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(
                Intent.EXTRA_TEXT, getString(
                    R.string.share_type_url,
                    getString(R.string.app_name),
                    childrenData!!.get(tabArticleTypes!!.selectedTabPosition).name,
                    childrenData!!.get(
                        tabArticleTypes!!.selectedTabPosition
                    ).id
                )
            )
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, getString(R.string.share_title)))
        } else if (item.itemId == R.id.menuSearch) {
            startActivity(Intent(this, ArticleSearchActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }
}