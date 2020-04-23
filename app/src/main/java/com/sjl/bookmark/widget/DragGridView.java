package com.sjl.bookmark.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.sjl.bookmark.R;
import com.sjl.core.util.log.LogUtils;
import com.sjl.core.util.ViewUtils;

import java.util.LinkedList;
import java.util.List;


/**
 * 可拖动的书架GridView
 */
public class DragGridView extends GridView {

    /**
     * DragGridView的item长按响应的时间， 默认是500毫秒，也可以自行设置
     */
    private long dragResponseMS = 500;

    /**
     * 是否可以拖拽，默认不可以
     */
    private boolean isDrag = false;

    private int mDownX;
    private int mDownY;
    private int moveX;
    private int moveY;
    /**
     * 正在拖拽的position，不能根据这个索引删除条目
     */
    private int mDragPosition;

    /**
     * 刚开始拖拽的item对应的View
     */
    private View mStartDragItemView = null;

    /**
     * 用于拖拽的镜像，这里直接用一个ImageView
     */
    private ImageView mDragImageView;

    /**
     * 震动器
     */
    private Vibrator mVibrator;

    private WindowManager mWindowManager;
    /**
     * item镜像的布局参数
     */
    private WindowManager.LayoutParams mWindowLayoutParams;

    /**
     * 我们拖拽的item对应的Bitmap
     */
    private Bitmap mDragBitmap;

    /**
     * 按下的点到所在item的上边缘的距离
     */
    private int mPoint2ItemTop;

    /**
     * 按下的点到所在item的左边缘的距离
     */
    private int mPoint2ItemLeft;

    /**
     * DragGridView距离屏幕顶部的偏移量
     */
    private int mOffset2Top;

    /**
     * DragGridView距离屏幕左边的偏移量
     */
    private int mOffset2Left;

    /**
     * 状态栏的高度
     */
    private int mStatusHeight;

    /**
     * DragGridView自动向下滚动的边界值
     */
    private int mDownScrollBorder;

    /**
     * DragGridView自动向上滚动的边界值
     */
    private int mUpScrollBorder;
    /**
     * DragGridView自动滚动的速度
     */
    private static final int speed = 20;

    private boolean mAnimationEnd = true;

    private DragGridListener mDragAdapter;
    private int mNumColumns;
    private int mColumnWidth;
    private boolean mNumColumnsSet;
    private int mHorizontalSpacing;

    private Bitmap background;
    private Bitmap bookshelf_dock;
    private boolean isShowDeleteButton = false;
    private Context context;
    private View firstView;
    private TextView firstItemTextView;
    private final int[] firstLocation = new int[2];
    private int i = 0;

    public DragGridView(Context context) {
        this(context, null);
    }

    public DragGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mStatusHeight = getStatusHeight(context);

        background = BitmapFactory.decodeResource(getResources(),
                R.mipmap.bookshelf_layer_center);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int mScreenWidth = dm.widthPixels;
        int mScreenHeight = dm.heightPixels;

        bookshelf_dock = BitmapFactory.decodeResource(getResources(), R.mipmap.bookshelf_dock);
        //填充满屏幕
        bookshelf_dock = Bitmap.createScaledBitmap(bookshelf_dock, mScreenWidth, bookshelf_dock.getHeight(), true);
        if (!mNumColumnsSet) {
            mNumColumns = AUTO_FIT;
        }
        this.context = context;
    }

    private Handler mHandler = new Handler();

    //用来处理是否为长按的Runnable
    private Runnable mLongClickRunnable = new Runnable() {

        @Override
        public void run() {
            isDrag = true; //设置可以拖拽
            mVibrator.vibrate(50); //震动一下
            mStartDragItemView.setVisibility(View.INVISIBLE);//隐藏该item

            //根据我们按下的点显示item镜像
            createDragImage(mDragBitmap, mDownX, mDownY);
            setShowDeleteButton(true);
            mDragAdapter.showDeleteButton();

        }
    };

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);

        if (adapter instanceof DragGridListener) {
            mDragAdapter = (DragGridListener) adapter;//回调方法的关键,拿到了该接口被实现后的实例
        } else {
            throw new IllegalStateException("the adapter must be implements DragGridListener");
        }
    }


    @Override
    public void setNumColumns(int numColumns) {
        super.setNumColumns(numColumns);
        mNumColumnsSet = true;
        this.mNumColumns = numColumns;
    }


    @Override
    public void setColumnWidth(int columnWidth) {
        super.setColumnWidth(columnWidth);
        mColumnWidth = columnWidth;
    }


    @Override
    public void setHorizontalSpacing(int horizontalSpacing) {
        super.setHorizontalSpacing(horizontalSpacing);
        this.mHorizontalSpacing = horizontalSpacing;
    }

    /**
     * 防止GridView getView被多次调用
     */
    public boolean isOnMeasure;

    /**
     * 若设置为AUTO_FIT，计算有多少列
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        isOnMeasure = true;

        if (mNumColumns == AUTO_FIT) {
            int numFittedColumns;
            if (mColumnWidth > 0) {
                int gridWidth = Math.max(MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft()
                        - getPaddingRight(), 0);
                numFittedColumns = gridWidth / mColumnWidth;
                if (numFittedColumns > 0) {
                    while (numFittedColumns != 1) {
                        if (numFittedColumns * mColumnWidth + (numFittedColumns - 1)
                                * mHorizontalSpacing > gridWidth) {
                            numFittedColumns--;
                        } else {
                            break;
                        }
                    }
                } else {
                    numFittedColumns = 1;
                }
            } else {
                numFittedColumns = 2;
            }
            mNumColumns = numFittedColumns;
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        isOnMeasure = false;
        super.onLayout(changed, l, t, r, b);
    }


    /**
     * 设置响应拖拽的毫秒数，默认是1000毫秒
     *
     * @param dragResponseMS
     */
    public void setDragResponseMS(long dragResponseMS) {
        this.dragResponseMS = dragResponseMS;
    }


    /**
     * 是否点击在GridView的item上面
     *
     * @param dragView
     * @param x
     * @param y
     * @return
     */
    private boolean isTouchInItem(View dragView, int x, int y) {
        if (dragView == null) {
            return false;
        }
        int leftOffset = dragView.getLeft();
        int topOffset = dragView.getTop();
        if (x < leftOffset || x > leftOffset + dragView.getWidth()) {
            return false;
        }

        if (y < topOffset || y > topOffset + dragView.getHeight()) {
            return false;
        }

        return true;
    }



    /**
     * return true：消费了该事件，事件到此结束；
     * return false：没有消费事件，事件会以冒泡方式传递到 最上层的 view 或者 activity，
     * 如果最上边的 view 或者 activity没有处理，还是 返回 false，该事件将消失。接下来的所有事件都会被 最上层的view 的  onTouchEvent捕获；
     * return super.onTouchEvent(event)：默认情况，和 return false一样；
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        LogUtils.w("gridView onTouchEvent， isDrag：" + isDrag);
        if (!isDrag) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mDownX = (int) ev.getX();
                    mDownY = (int) ev.getY();
                    //根据按下的X,Y坐标获取所点击item的position
                    mDragPosition = pointToPosition(mDownX, mDownY);
                    // Log.d("mDagPosition is", "" + mDragPosition);

                    if (mDragPosition == AdapterView.INVALID_POSITION) {
                        return super.onTouchEvent(ev);//不能返回true，否则不点击书籍也会回调条目点击事件

                    }
                    //使用Handler延迟dragResponseMS执行mLongClickRunnable,
                    // 大于20dp才执行mLongClickRunnable避免与Drawlayout发生冲突
                    int panding = (int) ViewUtils.dp2px(context, 20);
                    if (mDownX > panding) {
                        mHandler.postDelayed(mLongClickRunnable, dragResponseMS);
                    }
                    //根据position获取该item所对应的View
                    mStartDragItemView = getChildAt(mDragPosition - getFirstVisiblePosition());

                    //
                    mPoint2ItemTop = mDownY - mStartDragItemView.getTop();
                    mPoint2ItemLeft = mDownX - mStartDragItemView.getLeft();

                    mOffset2Top = (int) (ev.getRawY() - mDownY);
                    mOffset2Left = (int) (ev.getRawX() - mDownX);

                    //获取DragGridView自动向上滚动的偏移量，小于这个值，DragGridView向下滚动
                    mDownScrollBorder = getHeight() / 5;
                    //获取DragGridView自动向下滚动的偏移量，大于这个值，DragGridView向上滚动
                    mUpScrollBorder = getHeight() * 4 / 5;


                    //开启mDragItemView绘图缓存
                    mStartDragItemView.setDrawingCacheEnabled(true);

                    //获取mDragItemView在缓存中的Bitmap对象
                    mDragBitmap = Bitmap.createBitmap(mStartDragItemView.getDrawingCache());
                    //这一步很关键，释放绘图缓存，避免出现重复的镜像
                    mStartDragItemView.destroyDrawingCache();


                    break;
                case MotionEvent.ACTION_MOVE:
                    int moveX = (int) ev.getX();
                    int moveY = (int) ev.getY();

                    //如果我们在按下的item上面移动，只要不超过item的边界我们就不移除mRunnable
                    if (!isTouchInItem(mStartDragItemView, moveX, moveY)) {
                        mHandler.removeCallbacks(mLongClickRunnable);
                    }
                    int dY = (moveY - mDownY);
                    int dx = (moveX - mDownX);
                    LogUtils.i("dY:" + dY);
                    /**
                     * 判断是否是上下滑动
                     */
                    if (Math.abs(dY) > Math.abs(dx) || isDrag) {//还有问题:TODO
                        LogUtils.w("移除长按...");
                        mHandler.removeCallbacks(mLongClickRunnable);
                    }

                    break;
                case MotionEvent.ACTION_UP:
                    mHandler.removeCallbacks(mLongClickRunnable);
                    mHandler.removeCallbacks(mScrollRunnable);
                    isDrag = false;
                    break;
            }
        } else if (isDrag && mDragImageView != null) {

            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_MOVE:
                    moveX = (int) ev.getX();
                    moveY = (int) ev.getY();
                    if (swipeRefreshLayout != null) {
                        swipeRefreshLayout.setEnabled(false);//禁止下拉
                    }
                    //拖动item
                    onDragItem(moveX, moveY);
                    break;
                case MotionEvent.ACTION_UP:
                    onStopDrag();
                    isDrag = false;
                    if (swipeRefreshLayout != null) {
                        swipeRefreshLayout.setEnabled(true);//允许下拉
                    }
                    break;
            }
            return true;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 创建拖动的镜像
     *
     * @param bitmap
     * @param downX  按下的点相对父控件的X坐标
     * @param downY  按下的点相对父控件的X坐标
     */
    private void createDragImage(Bitmap bitmap, int downX, int downY) {
        mWindowLayoutParams = new WindowManager.LayoutParams();
        mWindowLayoutParams.format = PixelFormat.TRANSLUCENT; //图片之外的其他地方透明
        mWindowLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        mWindowLayoutParams.x = downX - mPoint2ItemLeft + mOffset2Left;
        mWindowLayoutParams.y = downY - mPoint2ItemTop + mOffset2Top - mStatusHeight;
        mWindowLayoutParams.alpha = 1.0f; //透明度
        // mWindowLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        //  mWindowLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowLayoutParams.width = (int) (1.05 * mStartDragItemView.getWidth());
        mWindowLayoutParams.height = (int) (1.05 * mStartDragItemView.getHeight());
        mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

        mDragImageView = new ImageView(getContext());
        mDragImageView.setImageBitmap(bitmap);
        mWindowManager.addView(mDragImageView, mWindowLayoutParams);
    }

    /**
     * 从界面上面移动拖动镜像
     */
    private void removeDragImage() {
        if (mDragImageView != null) {
            mWindowManager.removeView(mDragImageView);
            mDragImageView = null;
        }
    }

    /**
     * 拖动item，在里面实现了item镜像的位置更新，item的相互交换以及GridView的自行滚动
     *
     * @param moveX
     * @param moveY
     */
    private void onDragItem(int moveX, int moveY) {
        mWindowLayoutParams.x = moveX - mPoint2ItemLeft + mOffset2Left;
        mWindowLayoutParams.y = moveY - mPoint2ItemTop + mOffset2Top - mStatusHeight;
        mWindowManager.updateViewLayout(mDragImageView, mWindowLayoutParams); //更新镜像的位置
        onSwapItem(moveX, moveY);

        //GridView自动滚动             //已知bug：当上下滚动过快时item的相互交换速度跟不上会crash
        mHandler.post(mScrollRunnable);
    }


    /**
     * 当moveY的值大于向上滚动的边界值，触发GridView自动向上滚动
     * 当moveY的值小于向下滚动的边界值，触发GridView自动向下滚动
     * 否则不进行滚动
     */
    private Runnable mScrollRunnable = new Runnable() {

        @Override
        public void run() {
            int scrollY;
            if (getFirstVisiblePosition() == 0 || getLastVisiblePosition() == getCount() - 1) {
                mHandler.removeCallbacks(mScrollRunnable);
            }

            if (moveY > mUpScrollBorder) {
                scrollY = speed;
                mHandler.postDelayed(mScrollRunnable, 25);
            } else if (moveY < mDownScrollBorder) {
                scrollY = -speed;
                mHandler.postDelayed(mScrollRunnable, 25);
            } else {
                scrollY = 0;
                mHandler.removeCallbacks(mScrollRunnable);
            }

            smoothScrollBy(scrollY, 10);
        }
    };


    /**
     * 交换item,并且控制item之间的显示与隐藏效果
     *
     * @param moveX
     * @param moveY
     */
    private void onSwapItem(int moveX, int moveY) {
        //获取我们手指移动到的那个item的position
        final int tempPosition = pointToPosition(moveX, moveY);

        //假如tempPosition 改变了并且tempPosition不等于-1,则进行交换
        if (tempPosition != mDragPosition && tempPosition != AdapterView.INVALID_POSITION && mAnimationEnd) {

            mDragAdapter.setHideItem(tempPosition);

            mDragAdapter.reorderItems(mDragPosition, tempPosition);


            final ViewTreeObserver observer = getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    observer.removeOnPreDrawListener(this);
                    animateReorder(mDragPosition, tempPosition);
                    mDragPosition = tempPosition;  //交换结束更新mDragPosition
                    return true;
                }
            });

        }

    }

    /**
     * 创建移动动画
     *
     * @param view
     * @param startX
     * @param endX
     * @param startY
     * @param endY
     * @return
     */
    private AnimatorSet createTranslationAnimations(View view, float startX,
                                                    float endX, float startY, float endY) {
        ObjectAnimator animX = ObjectAnimator.ofFloat(view, "translationX",
                startX, endX);
        ObjectAnimator animY = ObjectAnimator.ofFloat(view, "translationY",
                startY, endY);
        AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.playTogether(animX, animY);
        return animSetXY;
    }


    /**
     * item的交换动画效果
     *
     * @param oldPosition
     * @param newPosition
     */
    private void animateReorder(final int oldPosition, final int newPosition) {
        boolean isForward = newPosition > oldPosition;
        List<Animator> resultList = new LinkedList<Animator>();
        if (isForward) {
            for (int pos = oldPosition; pos < newPosition; pos++) {
                View view = getChildAt(pos - getFirstVisiblePosition());
                // Log.d("oldPosition",""+ pos);

                //双数
                if ((pos + 1) % mNumColumns == 0) {
                    resultList.add(createTranslationAnimations(view,
                            -view.getWidth() * (mNumColumns - 1), 0,
                            view.getHeight(), 0));
                } else {
                    resultList.add(createTranslationAnimations(view,
                            view.getWidth(), 0, 0, 0));
                }
            }
        } else {
            for (int pos = oldPosition; pos > newPosition; pos--) {
                View view = getChildAt(pos - getFirstVisiblePosition());
                if ((pos + mNumColumns) % mNumColumns == 0) {
                    resultList.add(createTranslationAnimations(view,
                            view.getWidth() * (mNumColumns - 1), 0,
                            -view.getHeight(), 0));
                } else {
                    resultList.add(createTranslationAnimations(view,
                            -view.getWidth(), 0, 0, 0));
                }
            }
        }

        AnimatorSet resultSet = new AnimatorSet();
        resultSet.playTogether(resultList);
        resultSet.setDuration(300);
        resultSet.setInterpolator(new AccelerateDecelerateInterpolator());
        resultSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mAnimationEnd = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimationEnd = true;
                mDragAdapter.finishDragGrid();
            }
        });

        resultSet.start();

    }

    /**
     * 停止拖拽我们将之前隐藏的item显示出来，并将镜像移除
     */
    private void onStopDrag() {
        View view = getChildAt(mDragPosition - getFirstVisiblePosition());
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
        mDragAdapter.setHideItem(-1);
        removeDragImage();
    }

    /**
     * 获得状态栏的高度
     *
     * @param context
     * @return
     */
    public static int getStatusHeight(Context context) {
        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        i++;
        int backgroundHeightPadding = ViewUtils.dp2px(context, 4);
        int dockHeightPadding = ViewUtils.dp2px(context, 3);
        int count = getChildCount();
        int top = count > 0 ? getChildAt(0).getTop() : 0;
        int backgroundWidth = background.getWidth();
        int backgroundHeight = background.getHeight() - backgroundHeightPadding;
        int width = getWidth();
        int height = getHeight();

        for (int y = top; y < height; y += backgroundHeight) {
            for (int x = 0; x < width; x += backgroundWidth) {
                canvas.drawBitmap(background, x, y, null);
            }
            if (y > top) {
                canvas.drawBitmap(bookshelf_dock, 0, y - dockHeightPadding, null);
            }
        }
        if (i == 1) {
            firstView = getChildAt(0);
            firstItemTextView = (TextView) firstView.findViewById(R.id.tv_name);
            firstItemTextView.getLocationInWindow(firstLocation);
        }

        super.dispatchDraw(canvas);
    }


    public boolean isShowDeleteButton() {
        return isShowDeleteButton;
    }

    public void setShowDeleteButton(boolean showDeleteButton) {
        isShowDeleteButton = showDeleteButton;
    }


    private ImageView getDragImageView() {
        return mDragImageView;
    }

    public int[] getFirstLocation() {
        return firstLocation;
    }

    private SwipeRefreshLayout swipeRefreshLayout;

    public void setSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout) {
        this.swipeRefreshLayout = swipeRefreshLayout;
    }
}
