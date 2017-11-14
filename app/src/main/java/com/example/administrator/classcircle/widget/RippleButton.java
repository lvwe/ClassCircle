package com.example.administrator.classcircle.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

/**
 * Created by Administrator on 2017/10/31 0031.
 */

public class RippleButton extends Button {



    private RippleDrawable mRippleDrawable;

    public RippleButton(Context context) {
        this(context,null);
    }

    public RippleButton(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RippleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        mRippleDrawable = new RippleDrawable(BitmapFactory.
//                decodeResource(getResources(), R.mipmap.ic_launcher));
//        setBackgroundDrawable(new RippleDrawable());

        mRippleDrawable = new RippleDrawable(0x30000000);
        //设置刷新接口  View中已经实现
        mRippleDrawable.setCallback(this);
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        //验证Drawable是否OK
        return who == mRippleDrawable || super.verifyDrawable(who);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 设置Drawable绘制和刷新的区域
        mRippleDrawable.setBounds(0,0,getWidth(),getHeight());
    }

    /**
     * 动态改变   不断调用onDraw方法
     * invalidate();
     * @param canvas
     */

    @Override
    protected void onDraw(Canvas canvas) {
        //绘制自己的Drawable
        mRippleDrawable.draw(canvas);
        super.onDraw(canvas);

    }

    /**
     * 自定义控件中
     * TouchEvent 返回为false将不会调用后面的事件
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //触发onDraw方法
        mRippleDrawable.onTouch(event);
//        invalidate();
//        return super.onTouchEvent(event);
        super.onTouchEvent(event);
        return true;
    }
}
