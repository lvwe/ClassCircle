package com.example.administrator.classcircle.fragment;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.bumptech.glide.Glide;
import com.example.administrator.classcircle.R;

import cn.bluemobi.dylan.photoview.library.PhotoView;
import cn.bluemobi.dylan.photoview.library.PhotoViewAttacher;


/**
 * Created by Administrator on 2017/9/19 0019.
 */

public class FragmentShowImage extends BaseFragment {
    private PhotoViewAttacher mAttacher;
    private PhotoView photoView;
    private String ImgUrl;

    private Activity mActivity;
    private PopupWindow mpopuWindow;
    private PopupWindow mpopuWindow1;
    private boolean isShowPowindow=true;


    public void setImgUrl(String ImgUrl,Activity mActivity){
        this.ImgUrl=ImgUrl;
        this.mActivity=mActivity;
        //Toast.makeText(mActivity, "1", Toast.LENGTH_SHORT).show();
        //bitmap= BitmapFactory.decodeFile(ImgUrl);
        //photoView.setImageBitmap(bitmap);

    }






    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_image;
    }

    @Override
    protected void initView() {

        photoView= (PhotoView) mRootView.findViewById(R.id.fragment_photoview);
        mAttacher = new PhotoViewAttacher(photoView);
        mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                if(isShowPowindow){
                    showPopupWindow();

                    isShowPowindow=!isShowPowindow;
                }else{
                    // mpopuWindow.dismiss();
                    mpopuWindow1.dismiss();
                    isShowPowindow=!isShowPowindow;
                }
            }
        });



        //Toast.makeText(mActivity, "2", Toast.LENGTH_SHORT).show();
        Glide.with(mActivity).load(ImgUrl).into(photoView);
        mAttacher.update();

    }

    @Override
    protected void initData() {
//       FirstshowPopupWindow();
//        FirstshowPopupWindowdown();


    }

    @Override
    protected void initListener() {


    }


    public void showPopupWindow(){
        View view1= LayoutInflater.from(mActivity).inflate(R.layout.puplayout_top,null);
        //View view3= LayoutInflater.from(mActivity).inflate(R.layout.puplayout_buttom,null);
        // mpopuWindow=new PopupWindow(view1, ViewGroup.LayoutParams.MATCH_PARENT,
        //     200);
        mpopuWindow1=new PopupWindow(view1, ViewGroup.LayoutParams.MATCH_PARENT,
                200);


        ImageView backt;
        backt= (ImageView) view1.findViewById(R.id.backto);
        backt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();

            }
        });
        //mpopuWindow.setContentView(view1);
        mpopuWindow1.setContentView(view1);
        View view2= LayoutInflater.from(mActivity).inflate(R.layout.fragment_image,null);
        //View view4=LayoutInflater.from(mActivity).inflate(R.layout.fragment_image,null);
        //mpopuWindow.setAnimationStyle(R.style.contextMenuAnim);
        mpopuWindow1.setAnimationStyle(R.style.popupwindowfromtop);
        // mpopuWindow.showAtLocation(view2, Gravity.BOTTOM,0,0);
        mpopuWindow1.showAtLocation(view2, Gravity.TOP,0,0);
    }
    public void showPopupWindowTop(){




    }


}
