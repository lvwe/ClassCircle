package com.example.administrator.classcircle.utils;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2017/10/8 0008.
 */

public class AnimationUtils {

    /**
     * 创建动画
     *
     * @param activity
     * @return
     */
    public static ViewGroup createAnimLayout(Activity activity) {

        ViewGroup rootView = (ViewGroup) activity.getWindow().getDecorView();
        LinearLayout animLayout = new LinearLayout(activity);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        animLayout.setLayoutParams(lp);
        animLayout.setBackgroundResource(android.R.color.transparent);
        rootView.addView(animLayout);
        return animLayout;
    }

    public static void setAddTaskAnimation(Activity activity, View startView,
                                           View targetView, final AddTaskAnimationListener animationListener) {
        // 1.创建动画层
        ViewGroup animMaskLayout = createAnimLayout(activity);

        final ImageView imageView = new ImageView(activity);
        animMaskLayout.addView(imageView);

        // 2.创建Animation
        int[] startLocArray = new int[2];
        int[] endLocArray = new int[2];
        startView.getLocationInWindow(startLocArray);
        targetView.getLocationInWindow(endLocArray);
        //３．设置ImageView的LayoutParams


        ViewGroup.LayoutParams startViewLayoutParams = startView.getLayoutParams();
        ViewGroup.LayoutParams targetViewLayoutParams = targetView.getLayoutParams();
        LinearLayout.LayoutParams params = new LinearLayout.
                LayoutParams(startViewLayoutParams.width,startViewLayoutParams.height);
        params.leftMargin = startLocArray[0];
        params.topMargin = startLocArray[1];
        imageView.setLayoutParams(params);
        //设置ImageView的背景
        if (startView != null && (startView instanceof ImageView)){
            ImageView iv = (ImageView) startView;
            imageView.setImageDrawable(iv.getDrawable() == null ? null : iv.getDrawable());
        }
        //计算位移
        int xOffSet = endLocArray[0] - startLocArray[0] +targetViewLayoutParams.width /2 ;
        int yOffSet = endLocArray[1] - startLocArray[1] + targetViewLayoutParams.height / 2;
        TranslateAnimation translateAnimationX = new TranslateAnimation(0,xOffSet,0,0);
        translateAnimationX.setInterpolator(new LinearInterpolator());//加速
        translateAnimationX.setRepeatCount(0); //重复次数
        translateAnimationX.setFillAfter(true);

        TranslateAnimation translateAnimationY = new TranslateAnimation(0,0,0,yOffSet);
        translateAnimationY.setInterpolator(new LinearInterpolator());//加速
        translateAnimationY.setRepeatCount(0); //重复次数
        translateAnimationY.setFillAfter(true);

        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f,0.2f,1.0f,0.2f);
        scaleAnimation.setInterpolator(new LinearInterpolator());
//        scaleAnimation.setRepeatCount(5);
        scaleAnimation.setFillAfter(true); //动画最后一帧 的状态




        final AnimationSet set  = new AnimationSet(false);

        set.setFillAfter(false);
        set.addAnimation(scaleAnimation);

        set.addAnimation(translateAnimationY);
        set.addAnimation(translateAnimationX);
        set.setDuration(800);


        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (animationListener != null){
                    animationListener.onAnimationStart(animation);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (animationListener != null){
                    animationListener.onAnimationEnd(animation);
                }
//                imageView.clearAnimation();
//                set.reset();
//                imageView.invalidate();
                imageView.setVisibility(View.GONE);


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.setAnimation(set);



    }

    public interface AddTaskAnimationListener{
        void onAnimationStart(Animation animation);
        void onAnimationEnd(Animation animation);
    }
}
