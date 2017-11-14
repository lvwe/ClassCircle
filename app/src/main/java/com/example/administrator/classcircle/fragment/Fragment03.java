package com.example.administrator.classcircle.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.classcircle.activity.ChooseFileActivity;
import com.example.administrator.classcircle.R;
import com.example.administrator.classcircle.activity.NoticeActivity;
import com.example.administrator.classcircle.activity.SplashActivity;
import com.example.administrator.classcircle.entity.User;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import java.io.File;
import java.util.List;
import java.util.Random;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by Administrator on 2017/9/5 0005.
 */

public class Fragment03 extends BaseFragment implements View.OnClickListener{
    private static final String TAG = "Fragment03";
    private ImageView mHeadImg;
    static Uri imgSelectUri;
    static String imgFilePath;
    private TextView mMyName;
    private TextView mMyClass;
    private TextView mMyClassName;
    private TextView mMyClassRole;
    private TextView mSex;
    private  TextView mPersonalInfo;
    private View mViewNotice;
    private View mViewCollect;
    private Button mButtonLogout;

    final static int PHOTO_RESULT_CODE = 1001;
    final static int PHOTO_REQUEST_CAMERA = 1002;
    final static int PHOTO_REQUEST_GALLERY = 1003;
    final static int PHOTO_RESULT_CUT_CODE = 1004;

    private TextView tvTitle;
    public static String mIsStu;
    private String ObjId;



    private static final int MAX_PROGRESS = 100;
    private int progress = 0;
    private Handler progressDialogHandler;
    private int PROGRESSDIALOG_FLAG = 1;
    private int uploadValue = 0;
    private Bitmap mBitmap;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_03;
    }

    @Override
    protected void initView() {
        tvTitle = (TextView) mRootView.findViewById(R.id.id_header_tv);
        tvTitle.setText(getString(R.string.my));
        mHeadImg = (ImageView) mRootView.findViewById(R.id.id_my_img);
        mHeadImg.setOnClickListener(this);
        mButtonLogout = (Button) mRootView.findViewById(R.id.btn_logout);
        mButtonLogout.setOnClickListener(this);

        mMyName = (TextView) mRootView.findViewById(R.id.id_my_name);
        mMyClass = (TextView) mRootView.findViewById(R.id.id_my_class);
        mMyClassRole = (TextView) mRootView.findViewById(R.id.id_my_class_role);
        mMyClassName = (TextView) mRootView.findViewById(R.id.id_my_class_name);
        mSex = (TextView) mRootView.findViewById(R.id.id_my_sex);
        mPersonalInfo = (TextView) mRootView.findViewById(R.id.id_my_personalInfo);
        mViewNotice = mRootView.findViewById(R.id.id_my_notice);
        mViewCollect = mRootView.findViewById(R.id.id_my_collect);
        mViewNotice.setOnClickListener(this);
        mViewCollect.setOnClickListener(this);
        mViewNotice.setVisibility(View.GONE);
        mViewCollect.setVisibility(View.GONE);
        mMyClassRole.setText("学生");

    }


    @Override
    protected void initData() {
        if (SplashActivity.mClassID == null){
            mMyClassRole.setText("无");
            mMyName.setText("无");
            mMyClassName.setText("无");
            mMyClass.setText("无");
            mSex.setText("");
            mViewNotice.setVisibility(View.GONE);
            mViewCollect.setVisibility(View.GONE);
        }else {

            mMyClass.setText(Fragment02.mClassName);
            queryMyInfo();
        }
    }

    private void queryMyInfo() {

        BmobQuery<User> query = new BmobQuery<User>();
        query.addWhereEqualTo("userName", SplashActivity.mLoginUserName);
        Log.d(TAG, "uploadBmobImg: --------------"+SplashActivity.mLoginUserName);
        query.setLimit(50);
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null){
                    for (User user : list){
                        mIsStu = user.getIsStudent();
                        if (mIsStu.equals("true")){
                            mViewNotice.setVisibility(View.GONE);
                            mViewCollect.setVisibility(View.VISIBLE);
                            mMyClassRole.setText("学生");
                        }else {
                            mViewNotice.setVisibility(View.VISIBLE);
                            mViewCollect.setVisibility(View.VISIBLE);
                            mMyClassRole.setText("管理员");
                        }

                        ObjId = user.getObjectId();
                        String picUrl = user.getPic().getFileUrl();
                        if (picUrl != null) {
                            Glide.with(getContext()).load(picUrl).placeholder(R.drawable.weather).into(mHeadImg);
                        }else {
                            mHeadImg.setImageResource(R.drawable.weather);
                        }
                        String userName = user.getUserName();
                        mMyName.setText(userName);
                        String myClassCard= null;
                        if (user.getMyClassCard() == null){
                            mMyClassName.setText("无");
                        }else {
                            mMyClassName.setText(user.getMyClassCard());
                        }
                        Log.d(TAG, "done: --=="+user.getPic().getFileUrl());
                        Log.d(TAG, "done: ----------------------"+ObjId+"stu"+user.getIsStudent());
                    }
                }
            }
        });
    }

    @Override
    protected void initListener() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_my_img:
                if (SplashActivity.mClassID == null){
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PHOTO_RESULT_CODE);
                break;
            case R.id.id_my_notice:
                goTo(NoticeActivity.class);
                break;
            case R.id.id_my_collect:
//                    goTo(CollectDataActivity.class);
                goTo(ChooseFileActivity.class);
                break;

            case R.id.btn_logout:
                Log.d(TAG, "onClick: ----==btnLogout");
                logoutEmclient();
                break;
        }
    }

    private void logoutEmclient() {
        EMClient.getInstance().logout(true, new EMCallBack() {
            @Override
            public void onSuccess() {
                getActivity().finish();
            }

            @Override
            public void onError(int i, String s) {
                showToast("退出失败");
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    public void crop(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        intent.putExtra("outputFormat", "JPEG");// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PHOTO_RESULT_CUT_CODE);
        showToast("PHOTO_RESULT_CUT");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (PHOTO_RESULT_CODE == requestCode && data != null) {
            imgSelectUri = data.getData();
            String[] filePathColumn = new String[]{MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(imgSelectUri, filePathColumn, null, null, null);
            imgFilePath = null;
            while (cursor.moveToNext()) {
                imgFilePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
            }
            cursor.close();
            crop(imgSelectUri);
        } else if (PHOTO_RESULT_CUT_CODE == requestCode) {
            mBitmap = null;
            //bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgSelectUri);
            mBitmap = data.getParcelableExtra("data");
            uploadBmobImg();
            Log.d(TAG, "onActivityResult: =====uploadBmobImg");
//            mHeadImg.setImageBitmap(bmp);
        }
            /*try {
                *//*
                *//**//*错误Bitmap too large to be uploaded into a texture (4208x3120, max=4096x4096)
                * http://www.cnblogs.com/Ringer/p/4096050.html
                * 一个解决的方法是禁止硬件加速
                    <application android:hardwareAccelerated="false" ...>
                * *//*
                Bitmap bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgSelectUri);
                Log.d("------", "onActivityResult: -----"+bmp);
                imgHead.setImageBitmap(bmp);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Toast.makeText(AddActivity.this,"设置图片",Toast.LENGTH_SHORT).show();*/

            /*if(requestCode == PHOTO_RESULT_CUT){
                Bundle bundle = data.getExtras();
                Bitmap bitmap = bundle.getParcelable("data");
                imgHead.setImageBitmap(bitmap);
            }*/

        else {
//            showToast("选择图片失败");
        }
    }

    private void uploadBmobImg() {
        showProgressBarDialog(ProgressDialog.STYLE_HORIZONTAL);
        final BmobFile bmobFile = new BmobFile(new File(imgFilePath));

        final User user = new User();
        user.setPic(bmobFile);
        user.setClassId("666666");
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null){
                    user.update(ObjId, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null){
                                showToast("success");
                                Log.d(TAG, "done: ---success");
                                progress = 101;
                                mHeadImg.setImageBitmap(mBitmap);
                            }else {
                                showToast(e.toString());
                                Log.d(TAG, "done: --"+e.toString());
                                progress = 101;
                            }
                        }
                    });
                }else {
                    Log.d(TAG, "done: ----update image fail");
                    progress = 101;
                }
            }

            @Override
            public void onProgress(Integer value) {
                super.onProgress(value);
                uploadValue = value;
                Log.d(TAG, "onProgress: +++progress"+progress);
                Log.d(TAG, "onProgress: ---+"+value);
            }
        });
    }

    private void showProgressBarDialog(int style) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setIcon(R.drawable.file_download);

        progressDialog.setTitle("上传图片中");
        progressDialog.setMessage("请稍后");
        progressDialog.setProgressStyle(style);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        progressDialog.setMax(MAX_PROGRESS);

        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialogHandler.removeMessages(PROGRESSDIALOG_FLAG);
                progress = 0;
                progressDialog.setProgress(progress);
            }
        });
        progressDialog.show();

        progressDialogHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (progress >= MAX_PROGRESS) {
                    //消失 并重置初始值
                    progressDialog.dismiss();
                    progress = 0;
                } else {
                    //  progress++;
                    // progressDialog.incrementProgressBy(1);
                    // 随机设置下一次递增进度 (50 +毫秒)
                    progress = uploadValue;
                    progressDialog.setProgress(progress);
                    progressDialogHandler.sendEmptyMessageDelayed(1, 50 + new Random().nextInt(500));
                }
            }

        };
        // 设置进度初始值
        progress = (progress > 0) ? progress : 0;
        progressDialog.setProgress(progress);
        // 发送消息
        progressDialogHandler.sendEmptyMessage(PROGRESSDIALOG_FLAG);

    }
}
