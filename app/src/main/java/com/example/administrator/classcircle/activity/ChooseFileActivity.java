package com.example.administrator.classcircle.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.classcircle.utils.ShowFileUtils;
import com.example.administrator.classcircle.entity.FileInfo;
import com.example.administrator.classcircle.R;
import com.example.administrator.classcircle.widget.ShowSelectedFileInfoDialog;
import com.example.administrator.classcircle.widget.ShowSendFileInfoDialog;
import com.example.administrator.classcircle.utils.ToastUtils;
import com.example.administrator.classcircle.Application;
import com.example.administrator.classcircle.entity.Data;
import com.example.administrator.classcircle.fragment.FileInfoFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.UploadBatchListener;

/**
 * Created by Administrator on 2017/10/25 0025.
 */

public class ChooseFileActivity extends BaseActivity {
    private static final String TAG = "ChooseFileActivity";

    private TextView mTvBack;
    private ImageView mIvSearch;
    private TextView mTvTitle;

    private android.support.design.widget.TabLayout mTabLayout;
    private ViewPager mViewPager;
    private Button mBtnSelect;
    private Button mBtnNext;

    FileInfoFragment mCurrentFragment;
    FileInfoFragment mApkFragment;
    FileInfoFragment mMp3Fragment;
    FileInfoFragment mMp4Fragment;
    FileInfoFragment mJpgFragment;

    public static final int REQUEST_CODE_GET_FILE_INFOS = 200;
    public static final int FILE_SELECT_CODE = 201;
    private ShowSelectedFileInfoDialog mShowSelectedFileInfoDialog;
    private ShowSendFileInfoDialog mShowSendFileInfoDialog;
    private String mPath;
    private boolean isUploading = false;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_choose_file;
    }

    @Override
    protected void init() {
        mShowSelectedFileInfoDialog = new ShowSelectedFileInfoDialog(ChooseFileActivity.this);
        mShowSendFileInfoDialog = new ShowSendFileInfoDialog(ChooseFileActivity.this);
        mTvBack = (TextView) findViewById(R.id.tv_back);
        mIvSearch = (ImageView) findViewById(R.id.iv_search);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mBtnSelect = (Button) findViewById(R.id.btn_select);
        mBtnNext = (Button) findViewById(R.id.btn_next);
        mTvTitle.setText("选择文件");
        mTvTitle.setVisibility(View.VISIBLE);
        getSelectView();
        if (ContextCompat.checkSelfPermission(this, Manifest.
                permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions
                    (this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_GET_FILE_INFOS);
        } else {
            initData();
        }
        ShowSelectedFileInfoDialog.mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == 0) {
                    update();
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    protected void initListener() {

        mTvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mIvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnSelect.setEnabled(true);
                mBtnSelect.setBackgroundResource(R.drawable.selector_bottom_text_common);
                mBtnSelect.setTextColor(getResources().getColor(R.color.colorPrimary));
//                Intent.ACTION_GET_CONTENT
//                action_get_content是通过intent中设置的type属性来判断具体调用哪个程序的。
//                intent.setType("audio/*");
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                try {
                    startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
                } catch (android.content.ActivityNotFoundException ex) {
                    ToastUtils.show(ChooseFileActivity.this, "Please install a File Manager");
                }
            }
        });

        mBtnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mShowSelectedFileInfoDialog != null) {
                    mShowSelectedFileInfoDialog.show();
                }
//                Intent intent = new Intent(ChooseFileActivity.this, ShowFile.class);
//                startActivity(intent);
            }
        });

        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUploading) {
                    if (mShowSendFileInfoDialog != null) {
                        mShowSendFileInfoDialog.show();
                    }
                } else {
                    if (mShowSendFileInfoDialog != null) {
                        mShowSendFileInfoDialog.show();
                        uploadToBmob();
                    }
                }

            }
        });
    }

    private void uploadToBmob() {
        isUploading = true;
        final Map<String, FileInfo> mFileInfoMap = Application.getAppContent().getFileInfoMap();

        final String filePath[] = new String[mFileInfoMap.size()];
        int j = 0;

        for (FileInfo fileInfo : mFileInfoMap.values()) {
            filePath[j] = fileInfo.getFilePath();
            j++;
        }
        BmobFile.uploadBatch(filePath, new UploadBatchListener() {
            @Override
            public void onSuccess(List<BmobFile> files, List<String> urls) {
                //1、files-上传完成后的BmobFile集合，是为了方便大家对其上传后的数据进行操作，例如你可以将该文件保存到表中
                //2、urls-上传文件的完整url地址
                if (urls.size() == filePath.length) {//如果数量相等，则代表文件全部上传完成
                    //do something
                    showToast("上传完成");
                    uploadToBmobTableData(files);
                }
            }

            @Override
            public void onProgress(int curIndex, int curPercent, int total, int totalPercent) {
                //1、curIndex--表示当前第几个文件正在上传
                //2、curPercent--表示当前上传文件的进度值（百分比）
                //3、total--表示总的上传文件数
                //4、totalPercent--表示总的上传进度（百分比）
                Log.d(TAG, "onProgress: --" + curIndex + "--" + curPercent + "--" + total + "--" + totalPercent);


                if (mFileInfoMap != null || total != 0) {
                    List<FileInfo> fileInfoList = new ArrayList<FileInfo>();
                    for (FileInfo fileInfo : mFileInfoMap.values()) {
                        fileInfoList.add(fileInfo);
                    }
                    if (fileInfoList.size() == 0){
                        return;
                    }else {
                        fileInfoList.get(curIndex - 1).setProcceed(curPercent);
                        if (curPercent == 100) {
                            fileInfoList.get(curIndex - 1).setResult(FileInfo.FLAG_SUCCESS);
                        }
                        Application.getAppContent().updateFileInfo(fileInfoList.get(curIndex - 1));
                        ShowSendFileInfoDialog.mHandler.sendEmptyMessage(3);
                    }

                }
            }

            @Override
            public void onError(int statuscode, String errormsg) {
                showToast("错误码" + statuscode + ",错误描述：" + errormsg);
                isUploading = false;
            }
        });
    }

    /**
     * 批量添加到bmob；
     * @param files
     */
    private void uploadToBmobTableData(List<BmobFile> files) {
        List<BmobObject> data = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            Data data1 = new Data();
            data1.setName(SplashActivity.mLoginUserName);
            data1.setClassId(SplashActivity.mClassID);
            data1.setUploadFile(files.get(i));
            data.add(data1);
        }

        new BmobBatch().insertBatch(data).doBatch(new QueryListListener<BatchResult>() {

            @Override
            public void done(List<BatchResult> o, BmobException e) {
                if (e == null) {
                    for (int i = 0; i < o.size(); i++) {
                        BatchResult result = o.get(i);
                        BmobException ex = result.getError();
                        if (ex == null) {
                            Log.d(TAG, ("第" + i + "个数据批量添加成功：" + result.getCreatedAt() + "," + result.getObjectId() + "," + result.getUpdatedAt()));
                            isUploading = false;
                        } else {
                            Log.d(TAG, ("第" + i + "个数据批量添加失败：" + ex.getMessage() + "," + ex.getErrorCode()));
                        }
                    }
                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                    isUploading = false;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        FileInfo fileInfo = new FileInfo();
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri mUri = data.getData();
                    mPath = ShowFileUtils.getPath(this, mUri);
                    File f = new File(mPath);
                    try {
                        if (ShowFileUtils.getFileSizeSe(f) > 10485760) {
                            ToastUtils.show(ChooseFileActivity.this, "文件不可以大于10M");
                            return;
                        }
                        /**
                         * 添加到FileInfo  list中；
                         */
                        long size = ShowFileUtils.getFileSizeSe(f);
                        Log.d(TAG, "onActivityResult: -----name" + mPath);
                        fileInfo.setSize(size);
                        fileInfo.setFilePath(mPath);



                        if (Application.getAppContent().isExist(fileInfo)) {
                            ToastUtils.show(ChooseFileActivity.this, "文件已存在");
                        } else {
                            Application.getAppContent().addFileInfo(fileInfo);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    getFileName(mPath);

                    Log.d(TAG, "onActivityResult: ++++++++++++++++++" + mPath);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //    dat=file:///storage/emulated/0/3296887429.mp4
    private void getFileName(String path) {
        String[] str = path.split("/");
        String fileName = str[str.length - 1].toString();
//        mEditText.setText(fileName+"/size:"+ ShowFileUtils.checkFileSize(path));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_GET_FILE_INFOS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initData();
            } else {
                //Permission Denied
                ToastUtils.show(getBaseContext(), "权限被拒绝");
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void initData() {
        mApkFragment = FileInfoFragment.newInstance(FileInfo.TYPE_APK);
        mJpgFragment = FileInfoFragment.newInstance(FileInfo.TYPE_JPG);
        mMp3Fragment = FileInfoFragment.newInstance(FileInfo.TYPE_MP3);
        mMp4Fragment = FileInfoFragment.newInstance(FileInfo.TYPE_MP4);
        mCurrentFragment = mApkFragment;

        String[] title = getResources().getStringArray(R.array.array_res);
        mViewPager.setAdapter(new ResPagerAdapter(getSupportFragmentManager(), title));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {

                } else if (position == 1) {

                } else if (position == 2) {

                } else if (position == 3) {

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mViewPager.setOffscreenPageLimit(4);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void update() {
        if (mApkFragment != null) mApkFragment.updateFileInfoAdapter();
        if (mJpgFragment != null) mJpgFragment.updateFileInfoAdapter();
        if (mMp3Fragment != null) mMp3Fragment.updateFileInfoAdapter();
        if (mMp4Fragment != null) mMp4Fragment.updateFileInfoAdapter();

        //更新已选中Button
        getSelectView();
    }

    public View getSelectView() {
        if (Application.getAppContent().getFileInfoMap() != null
                && Application.getAppContent().getFileInfoMap().size() > 0) {
            setSelectedViewStyle(true);
            int size = Application.getAppContent().getFileInfoMap().size();
            mBtnSelect.setText(getResources().getString(R.string.str_has_selected_detail, size));
        } else {
            setSelectedViewStyle(false);
            mBtnSelect.setText(getResources().getString(R.string.str_has_selected));
        }
        return mBtnSelect;
    }

    private void setSelectedViewStyle(boolean isEnable) {
        if (isEnable) {
            mBtnSelect.setEnabled(true);
            mBtnSelect.setBackgroundResource(R.drawable.selctor_bottom_text_common);
            mBtnSelect.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else {
            mBtnSelect.setEnabled(false);
            mBtnSelect.setBackgroundResource(R.drawable.shape_bottom_text_unenable);
            mBtnSelect.setTextColor(getResources().getColor(R.color.darker_gray));
        }
    }

    class ResPagerAdapter extends FragmentPagerAdapter {

        String[] mTitleArray;

        public ResPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public ResPagerAdapter(FragmentManager fm, String[] titleArray) {
            super(fm);
            mTitleArray = titleArray;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {//应用
                mCurrentFragment = mApkFragment;
            } else if (position == 1) {//图片
                mCurrentFragment = mJpgFragment;
            } else if (position == 2) {//MP3
                mCurrentFragment = mMp3Fragment;
            } else if (position == 3) {//MP4
                mCurrentFragment = mMp4Fragment;
            }
            return mCurrentFragment;
        }

        @Override
        public int getCount() {
            return mTitleArray.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitleArray[position];
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSelectView();
    }
}
