package com.example.administrator.classcircle.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.administrator.classcircle.R;
import com.example.administrator.classcircle.utils.FileUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import cn.bluemobi.dylan.photoview.library.PhotoView;
import cn.bluemobi.dylan.photoview.library.PhotoViewAttacher;

public class ShowImgFileActivity extends BaseActivity {
    private static final String TAG = "ShowImgFileActivity";

    private PhotoView mPhotoView;
    private PhotoViewAttacher mAttacher;
    private FrameLayout mFrameLayoutLoading;
    private ImageView mImageView;
    private TextView mTextView;
    private ProgressBar mProgressBar;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_show_img_file;
    }

    @Override
    protected void init() {
        this.setTheme(R.style.AppTheme_NoTitle);
        mPhotoView = (PhotoView) findViewById(R.id.photoView);
        mFrameLayoutLoading = (FrameLayout) findViewById(R.id.frame_layout_loading_progress);
        mImageView = (ImageView) findViewById(R.id.img_view);
        mTextView = (TextView) findViewById(R.id.text_view_loading);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar_loading);
        mFrameLayoutLoading.setVisibility(View.VISIBLE);

        loadedImageToPhotoView();
        if (mAttacher == null){
            mAttacher = new PhotoViewAttacher(mPhotoView);
        }
    }

    private void loadedImageToPhotoView() {
        Intent intent = getIntent();
        String fileUrl = intent.getStringExtra("FILE_URL");
        if (FileUtils.getExtension(fileUrl).equals("gif")){
            Log.d(TAG, "loadedImageToPhotoView: -----"+FileUtils.getExtension(fileUrl));
            Log.d(TAG, "loadedImageToPhotoView: ---  +"+fileUrl );
            mPhotoView.setVisibility(View.GONE);
            mFrameLayoutLoading.setVisibility(View.GONE);
            Glide.with(this).load(fileUrl).listener(mListener).into(mImageView);
            /*Picasso.with(this).load(fileUrl).into(mImageView, new Callback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "onSuccess: -----");
                    mFrameLayoutLoading.setVisibility(View.GONE);


                }

                @Override
                public void onError() {
                    Log.d(TAG, "onError: ----");
                }
            });*/

        }else {
            Log.d(TAG, "loadedImageToPhotoView: ---  +" + fileUrl);
            Glide.with(this).load(fileUrl).listener(mListener).into(mPhotoView);
            mImageView.setVisibility(View.GONE);
        }
    }
    RequestListener mListener = new RequestListener() {
        @Override
        public boolean onException(Exception e, Object model, Target target, boolean isFirstResource) {
            Log.d(TAG, "onException: ----"+e.getMessage());
            mProgressBar.setVisibility(View.GONE);
            mTextView.setText(e.toString());
            return false;
        }

        //这个用于监听图片是否加载完成
        @Override
        public boolean onResourceReady(Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
            Log.d(TAG, "onResourceReady: -------model"+model);
            mFrameLayoutLoading.setVisibility(View.GONE);

            getBaseContext().setTheme(R.style.AppTheme_FullScreen);
            return false;
        }
    };

    @Override
    protected void initListener() {

    }
}
