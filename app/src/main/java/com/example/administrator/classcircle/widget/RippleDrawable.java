package com.example.administrator.classcircle.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * Created by Administrator on 2017/10/31 0031.
 */

public class RippleDrawable extends Drawable {

    //最大透明度
    private static final int MAX_ALPHA_BG = 172;
    private static final int MAX_ALPHA_CIRCLE_BG = 255;
    // 透明度  0-255   drawable默认透明度为255;
    private int mAlpha = 255;
    //默认颜色为透明
    private int mRippleColor = 0;
    // 添加参数   抗锯齿
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float mRipplePointX, mRipplePointY;
    private float mRippleRadius = 0;

    //背景透明度     两种方式，1.改变画笔的透明度  2.拿到颜色的Alpha更改
    private int mBgAlpha = 0;
    private int mCircleAlpha = MAX_ALPHA_CIRCLE_BG;


    //    public RippleDrawable(Bitmap bitmap) {
    public RippleDrawable(int color) {
        mPaint.setAntiAlias(true); //抗锯齿
        mPaint.setDither(true);  //防抖动
        mPaint.setStyle(Paint.Style.FILL);  //设置画笔为填充方式
        setRippleColor(color);  //设置涟漪颜色
        /**
         *   ARGB 0xFF FF FF FF   透明度， 红，绿 蓝
         *   设置滤镜
         *   参数1 保留颜色
         *   参数2 填充的颜色
         */
//        setColorFilter(new LightingColorFilter(0xFFFF0000,0x00330000));

    }

    public void setRippleColor(int color) {
        //不建议直接设置      用户给的颜色不是全色，有可能透明，
        // 画笔透明度，drawable透明度，两者叠加效果难以控制
//        mPaint.setColor(color);
        mRippleColor = color;
        onColorOrAlphaChange();

    }


    private int changeColorAlpha(int color, int alpha) {
//        int r = Color.red(color);
        int a = (color >> 24) & 0xFF;
        a = (int) (a * (alpha / 255f));
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color) & 0xFF;
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private int getCircleAlpha(int preAlpha, int bgAlpha) {
        int dAlpha = preAlpha - bgAlpha;
        return (int) ((dAlpha * 255f) / (255f - bgAlpha));
    }

    @Override
    public void draw(Canvas canvas) {
        int preAlpha = mPaint.getAlpha();
        int bgAlpha = (int) (preAlpha * (mBgAlpha / 255f));
        int maxCircleAlpha = getCircleAlpha(preAlpha, bgAlpha);
        int circleAlpha = (int) (maxCircleAlpha * (mCircleAlpha / 255f));

        //绘制背景区域的颜色
        mPaint.setAlpha(bgAlpha);
        canvas.drawColor(mPaint.getColor());


//        canvas.drawColor(Color.RED);
//        canvas.drawBitmap(mBitmap,0,0,mPaint);
        mPaint.setAlpha(circleAlpha);

        canvas.drawCircle(mRipplePointX, mRipplePointY,
                mRippleRadius, mPaint);
        mPaint.setAlpha(preAlpha);

//        invalidateSelf();
    }

    //标示用户手指是否抬起
    private boolean mTouchRelease;
    public void onTouch(MotionEvent event) {
//        event.getAction()      封装了action down，up，move等事件
        switch (event.getActionMasked()) {  //只保留点击的操作
            case MotionEvent.ACTION_DOWN:
                onTouchDown(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                onTouchMove(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                onTouchUp(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_CANCEL:
                onTouchCancel(event.getX(), event.getY());
                break;
        }
    }

    public void onTouchDown(float x, float y) {
        //手指按下 没有抬起
        mTouchRelease = false;
        mDownPointX = x;
        mDownPointY = y;
        mRippleRadius = 0;
        invalidateSelf();
        startEnterRunnable();

    }

    public void onTouchUp(float x, float y) {
//        unscheduleSelf(mEnterRunnable);
        //手指抬起
        mTouchRelease = true;
        //当进入动画完成时  启动退出动画
        if (mEnterDown) {
            startExitRunnable();
        }
    }

    public void onTouchMove(float x, float y) {

    }

    public void onTouchCancel(float x, float y) {
        //手指抬起
        mTouchRelease = true;
        // 当进入动画完成时  启动退出动画
        if (mEnterDown) {
            startExitRunnable();
        }

    }
// XXX
    private void startEnterRunnable() {
        mCircleAlpha = 255;
        mProgress = 0;
        mEnterDown = false;
        //取消事务的操作
        unscheduleSelf(mExitRunnable);
        unscheduleSelf(mEnterRunnable);
        //注入一个进去动画
        scheduleSelf(mEnterRunnable, SystemClock.uptimeMillis());
    }

    private void startExitRunnable() {
        mExitProgress = 0;
        unscheduleSelf(mEnterRunnable);
        unscheduleSelf(mExitRunnable);
        scheduleSelf(mExitRunnable, SystemClock.uptimeMillis());
    }

    private boolean mEnterDown;
    //进入动画的精度值
    private float mProgress = 0;
    //每次递增的进度值
    private float mEnterIncrement = 16f / 360;
    //进入动画查值器  用于实现从快到慢的效果
    private Interpolator mEnterInterpolator = new DecelerateInterpolator(2);
    //动画的回调
    private Runnable mEnterRunnable = new Runnable() {
        @Override
        public void run() {
            mProgress = mProgress + mEnterIncrement;
            if (mProgress > 1) {
                onEnterProgress(1);
                onEnterDown();
                return;
            }
            float realProgress = mEnterInterpolator.getInterpolation(mProgress);
            onEnterProgress(realProgress);
            //延迟16毫秒，保证界面刷新频率接近 60fps
            scheduleSelf(this, SystemClock.uptimeMillis() + 16);
        }
    };

    private void onEnterDown() {
        mEnterDown = true;
        //当用户手放开时，启动退出动画
        if (mTouchRelease) {
            startExitRunnable();
        }

    }

    private void onEnterProgress(float progress) {
        mRippleRadius = getProgressValue(mStartRadius, mEndRadius, progress);
        mRipplePointX = getProgressValue(mDownPointX, mCenterPointX, progress);
        mRipplePointY = getProgressValue(mDownPointY, mCenterPointY, progress);

        mBgAlpha = (int) getProgressValue(0, MAX_ALPHA_BG, progress);
        //刷新界面
        invalidateSelf();
    }

    //退出动画的精度值
    private float mExitProgress = 0;
    //每次递增的进度值
    private float mExitIncrement = 16f / 300;
    //退出动画查值器  用于实现从慢到快的效果
    private Interpolator mExitInterpolator = new AccelerateInterpolator(2);
    //动画的回调
    private Runnable mExitRunnable = new Runnable() {
        @Override
        public void run() {
            //进入时，首先判断进入动画是否具有
            if (!mEnterDown){
                return;
            }

            mExitProgress = mExitProgress + mExitIncrement;
            if (mExitProgress > 1) {
                onExitProgress(1);
                onExitDown();
                return;
            }
            float realProgress = mExitInterpolator.getInterpolation(mExitProgress);
            onExitProgress(realProgress);
            //延迟16毫秒，保证界面刷新频率接近 60fps
            scheduleSelf(this, SystemClock.uptimeMillis() + 16);
        }
    };

    /**
     * 退出动画完成时触发
     */
    private void onExitDown() {

    }

    /**
     * 退出动画刷新方法
     *
     * @param progress
     */
    private void onExitProgress(float progress) {
        //背景减淡
        mBgAlpha = (int) getProgressValue(MAX_ALPHA_BG, 0, progress);
        //圆的透明度减淡
        mCircleAlpha = (int) getProgressValue(MAX_ALPHA_CIRCLE_BG, 0, progress);


        //刷新界面
        invalidateSelf();
    }

    private float getProgressValue(float start, float end, float progress) {
        return start + (end - start) * progress;
    }


    //按下的坐标点
    private float mDownPointX, mDownPointY;
    //控件的中心点
    private float mCenterPointX, mCenterPointY;
    //开始和结束的半径
    private float mStartRadius, mEndRadius;

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        mCenterPointX = bounds.centerX();
        mCenterPointY = bounds.centerY();

        float maxRadius = Math.max(mCenterPointX, mCenterPointY);
        mStartRadius = maxRadius * 0f;
        mEndRadius = maxRadius * 0.8f;
    }

    /**
     * 设置drawable的透明度
     *
     * @param alpha
     */
    @Override
    public void setAlpha(int alpha) {
        mAlpha = alpha;
        onColorOrAlphaChange();
    }

    @Override
    public int getAlpha() {
        return mAlpha;
    }

    private void onColorOrAlphaChange() {
        mPaint.setColor(mRippleColor);
        //不是全透明
        if (mAlpha != 255) {
            //运算透明度   得到画笔的透明度
//        int paintAlpha = mPaint.getAlpha();
            int paintAlpha = Color.alpha(mRippleColor);
            int realAlpha = (int) (paintAlpha * (mAlpha / 255f));
            mPaint.setAlpha(realAlpha);
            //得到颜色   已经经过一次运算颜色已经改变
            mPaint.getColor();
        }
        //刷新当前Drawable
        invalidateSelf();
    }

    /**
     * 设置drawable的颜色滤镜
     * 相对与图片而言
     *
     * @param colorFilter
     */
    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        if (mPaint.getColorFilter() != colorFilter) {
            mPaint.setColorFilter(colorFilter);
            //刷新当前Drawable
            invalidateSelf();
        }
    }

    //得到drawable的透明度
    @Override
    public int getOpacity() {
        //完全不透明
        if (mPaint.getAlpha() == 255) {
            return PixelFormat.OPAQUE;

        } else if (mPaint.getAlpha() == 0) {
            return PixelFormat.TRANSPARENT;
        } else {
            return PixelFormat.TRANSLUCENT;
        }
    }
}
