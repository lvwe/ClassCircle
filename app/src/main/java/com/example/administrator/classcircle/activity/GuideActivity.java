package com.example.administrator.classcircle.activity;

import android.animation.Animator;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.administrator.classcircle.R;

public class GuideActivity extends BaseActivity {

    private ImageView mImageView;
    private Button mButton;
    private int index;
    private boolean mExitActivity;
    private int[] imgs = new int[]{
            R.drawable.guid1,
            R.drawable.guid2,
            R.drawable.guid3,
            R.drawable.guid4
    };

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_guide;
    }

    @Override
    protected void init() {
        mImageView = (ImageView) findViewById(R.id.id_guide_iv_bg);
        mButton = (Button) findViewById(R.id.id_guide_btn_start);
        startImgAnimation();
    }

    private void startImgAnimation() {
        int imgId = imgs[index % imgs.length];
        mImageView.setImageResource(imgId);
        index++;
        mImageView.setScaleY(1f);
        mImageView.setScaleX(1f);
        mImageView.animate().scaleY(1.2f).scaleX(1.2f).setDuration(3000)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (!mExitActivity) {
                            startImgAnimation();
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                }).start();
    }

    @Override
    protected void initListener() {
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTo(LoginActivity.class, true);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mExitActivity = true;
    }
}
