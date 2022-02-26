package com.sjl.bookmark.ui.fragment

import android.content.Intent
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import androidx.viewpager.widget.ViewPager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseQuickAdapter.RequestLoadMoreListener
import com.chad.library.adapter.base.BaseViewHolder
import com.lwj.widget.viewpagerindicator.ViewPagerIndicator
import com.sjl.bookmark.R
import com.sjl.bookmark.app.AppConstant
import com.sjl.bookmark.entity.Article
import com.sjl.bookmark.entity.Article.DatasBean
import com.sjl.bookmark.entity.Category.ChildrenBean
import com.sjl.bookmark.entity.TopBanner
import com.sjl.bookmark.net.HttpConstant
import com.sjl.bookmark.ui.activity.ArticleTypeActivity
import com.sjl.bookmark.ui.activity.BrowserActivity
import com.sjl.bookmark.ui.adapter.ArticleAdapter
import com.sjl.bookmark.ui.adapter.ImagePagerAdapter
import com.sjl.bookmark.ui.contract.HomeContract
import com.sjl.bookmark.ui.presenter.HomePresenter
import com.sjl.core.entity.EventBusDto
import com.sjl.core.mvp.BaseFragment
import com.sjl.core.net.RxBus
import com.sjl.core.util.log.LogUtils
import com.uber.autodispose.AutoDispose
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import com.youth.banner.Banner
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.home_fragment.*
import java.util.*

/**
 * 首页
 *
 * @author Kelly
 * @version 1.0.0
 * @filename HomeFragment.java
 * @time 2018/3/21 11:23
 * @copyright(C) 2018 song
 */
class HomeFragment : BaseFragment<HomePresenter>(), HomeContract.View,
    BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemChildClickListener,
    OnRefreshListener, RequestLoadMoreListener {

    lateinit var mViewpager: ViewPager
    var mViewPagerIndicator: ViewPagerIndicator? = null
    private var mBannerAdapter: ImagePagerAdapter? = null
    private lateinit var mArticleAdapter: ArticleAdapter
    private val mBannerAds: Banner? = null
    private lateinit var mHomeBannerHeadView: View
    override fun onFirstUserVisible() {}
    override fun onUserVisible() {}
    override fun onUserInvisible() {}
    override fun getLayoutId(): Int {
        return R.layout.home_fragment
    }

    override fun initView() {}
    override fun initListener() {
        RxBus.getInstance()
            .toObservable(AppConstant.RxBusFlag.FLAG_1, EventBusDto::class.java)
            .observeOn(AndroidSchedulers.mainThread())
            .`as`(
                AutoDispose.autoDisposable(
                    AndroidLifecycleScopeProvider.from(
                        this,
                        Lifecycle.Event.ON_DESTROY
                    )
                )
            ) //Lifecycle.Event.ON_DESTROY不加，跳转页面时,fragment回调onStop导致订阅会失效无法接收事件
            .subscribe({ s ->
                LogUtils.i("触发清除浏览记录，开始更新HomeFragment列表：$s")
                if (s.eventCode == 0) {
                    mArticleAdapter.refreshBrowseTrack()
                    mArticleAdapter.notifyDataSetChanged() //方法可能不生效
                }
            }) { throwable -> LogUtils.e(throwable) }
    }

    override fun initData() {
        swipeRefreshLayout.setColorSchemeResources(R.color.blueStatus)
        /**设置RecyclerView */
        rvHomeArticles.layoutManager = LinearLayoutManager(context)
        mArticleAdapter = ArticleAdapter(R.layout.home_article_recycle_item, null)
        rvHomeArticles.adapter = mArticleAdapter
        /**设置BannerHead BaseView */
        mHomeBannerHeadView = LayoutInflater.from(context).inflate(R.layout.home_head_banner, null)
        mViewpager = mHomeBannerHeadView.findViewById<View>(R.id.viewpager) as ViewPager
        mViewPagerIndicator =
            mHomeBannerHeadView.findViewById<View>(R.id.indicator_line) as ViewPagerIndicator

//        mBannerAds = (Banner) mHomeBannerHeadView.findViewById(R.id.banner_ads);
        mArticleAdapter.addHeaderView(mHomeBannerHeadView)
        /**设置事件监听 */
        mArticleAdapter.onItemClickListener = this
        mArticleAdapter.onItemChildClickListener = this
        swipeRefreshLayout.setOnRefreshListener(this)
        mArticleAdapter.setOnLoadMoreListener(this, rvHomeArticles)
        /**请求数据 */
        mPresenter.loadHomeData()
    }

    override fun setHomeBanners(banners: List<TopBanner>) {
        val images: MutableList<String?> = ArrayList()
        val titles: MutableList<String?> = ArrayList()
        //左右添加多一张图片
        for (banner in banners) {
            images.add(banner.imagePath)
            titles.add(banner.title)
        }
        initBanner(images, banners)
        /*  mBannerAds.setImages(images)
                .setBannerTitles(titles)
//                .setIndicatorGravity(BannerConfig.CENTER)
                .setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE)
                .setImageLoader(new GlideImageLoader())
                .start();

        mBannerAds.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                TopBanner banner = banners.get(position);
                BrowserActivity.startWithParams(mActivity, banner.getTitle(), banner.getUrl());
            }
        });*/
    }

    private fun initBanner(images: List<String?>, banners: List<TopBanner>) {
        if (mBannerAdapter == null) { //new一次适配器，否则下拉刷新后出现图片、指示器滑动过快
            mBannerAdapter = ImagePagerAdapter(activity, images, mViewpager)
        } else {
            mBannerAdapter?.setData(images)
        }
        mViewpager.adapter = mBannerAdapter
        mViewPagerIndicator!!.setViewPager(mViewpager, images.size)
        mBannerAdapter!!.setOnItemClickListener { position ->
            val newPosition = position % images.size
            val banner = banners[newPosition]
            BrowserActivity.startWithParams(mActivity, banner.title, banner.url)
        }
        mBannerAdapter?.startLoop(3)
        mViewpager.currentItem = 0
    }

    override fun setHomeArticles(article: Article, loadType: Int) {
        setLoadDataResult(mArticleAdapter, swipeRefreshLayout, article.datas, loadType)
    }

    override fun collectArticleSuccess(position: Int, bean: DatasBean) {}
    override fun showFaild(message: String?) {
        swipeRefreshLayout.isRefreshing = false
    }

    override fun showLoading() {
        swipeRefreshLayout.isRefreshing = true
    }

    override fun hideLoading() {}
    protected fun setLoadDataResult(
        articleAdapter: BaseQuickAdapter<Article.DatasBean, BaseViewHolder>,
        refreshLayout: SwipeRefreshLayout,
        list: MutableList<DatasBean>,
        loadType: Int
    ) {
        LogUtils.i("list:" + list.size)
        when (loadType) {
            HttpConstant.LoadType.TYPE_REFRESH_SUCCESS -> {
                articleAdapter.setNewData(list)
                refreshLayout.isRefreshing = false
            }
            HttpConstant.LoadType.TYPE_REFRESH_ERROR -> refreshLayout.isRefreshing = false
            HttpConstant.LoadType.TYPE_LOAD_MORE_SUCCESS -> if (list != null) {
                articleAdapter.addData(list)
            }
            HttpConstant.LoadType.TYPE_LOAD_MORE_ERROR -> articleAdapter.loadMoreFail()
            else -> {}
        }
        if (list == null || list.isEmpty() || list.size < HttpConstant.PAGE_SIZE_15) { //后台有时候返回19条，用20有问题
            LogUtils.i("没有文章数据了")
            articleAdapter.loadMoreEnd(false) //数据全部加载完毕
        } else {
            articleAdapter.loadMoreComplete() //注意不是加载结束，而是本次数据加载结束并且还有下页数据
        }
    }

    /**
     * Item子控件的点击事件
     *
     * @param adapter
     * @param view
     * @param position
     */
    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View, position: Int) {
        if (view.id == R.id.tvChapterName) {
            val chapterName = mArticleAdapter.getItem(position)!!.chapterName
            val intent = Intent(mActivity, ArticleTypeActivity::class.java)
            intent.putExtra(HttpConstant.CONTENT_TITLE_KEY, chapterName)
            val children: MutableList<ChildrenBean?> = ArrayList()
            children.add(
                ChildrenBean(
                    mArticleAdapter.getItem(position)!!.chapterId,
                    chapterName
                )
            )
            intent.putParcelableArrayListExtra(
                HttpConstant.CONTENT_CHILDREN_DATA_KEY,
                children as ArrayList<out Parcelable?>
            )
            intent.putExtra(HttpConstant.CONTENT_OPEN_FLAG, "0")
            mActivity.startActivity(intent)
        }
    }

    /**
     * Item点击事件
     *
     * @param adapter
     * @param view
     * @param position
     */
    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View, position: Int) {
        val item = mArticleAdapter.getItem(position)
        mArticleAdapter.addBrowseTrack(item!!.id.toString(), position)
        BrowserActivity.startWithParams(mActivity, item.title, item.link)
    }

    /**
     * 下拉刷新
     */
    override fun onRefresh() {
        mPresenter!!.refresh()
    }

    /**
     * 上拉加载
     */
    override fun onLoadMoreRequested() {
        mPresenter!!.loadMore()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (mBannerAdapter != null) {
            mBannerAdapter!!.stopLoop()
        }
    }
}