package com.example.administrator.classcircle.li;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.administrator.classcircle.entity.ClassAlbum;
import com.example.administrator.classcircle.entity.ClassId;
import com.example.administrator.classcircle.LoadCallBack.HttpResult;
import com.example.administrator.classcircle.R;
import com.example.administrator.classcircle.adapter.AlbumAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class AlbumActivity extends BaseActivity {
    private RecyclerView recy_classAlbum;
    public static List<ClassAlbum> albumList = new ArrayList<>();
    public static  List<BmobFile> bmobFileList=new ArrayList<>();
    private AlbumAdapter albumAdapter;
    final static int PHOTO_RESULT_CODE = 1001;
    final static int PHOTO_RESULT_CUT_CODE = 1004;
    static Uri imgSelectUri;
    static String imgFilePath;
    public static String objectId;
    static int idNum;
    static int voteNum;
    static int voteRank;
    ProgressDialog mproDialog;



    @Override
    protected int getLayoutRes() {
        return R.layout.activity_album;
    }

    @Override
    protected void init() {
        initalbumList();
        //标题栏
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("班级相册");

        recy_classAlbum = (RecyclerView) findViewById(R.id.classAlbum);


        //Toast.makeText(this, String.valueOf(albumList.size()), Toast.LENGTH_SHORT).show();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_add_photos,menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void initalbumList() {

        BmobQuery<ClassId> bmobQuery = new BmobQuery<>();
        bmobQuery.findObjects(new FindListener<ClassId>() {
            @Override
            public void done(List<ClassId> list, BmobException e) {
                if (e == null) {
                    if(mproDialog!=null){
                        mproDialog.dismiss();
                    }
                    bmobFileList.clear();
                    albumList.clear();
                    HttpResult result=new HttpResult(1,list);
                    LoadingPager(result,0);
                    //获取所有的班级图片的路径
                    for (int i = 0; i < list.size(); i++) {
                        ClassId classId;
                        classId = list.get(i);
                        Intent intent = getIntent();
                        int classNum = Integer.parseInt(intent.getStringExtra("classNum"));
                        //根据班级号码找信息
                        if (classId.getIdNum() == classNum) {
                            objectId=classId.getObjectId();
                            idNum=classId.getIdNum();
                            voteNum=classId.getVoteNum();
                            voteRank=classId.getVoteRank();

                            //找出所有图片
                            if (classId.getPhotoPath().size() > 0) {

                                bmobFileList.addAll(classId.getPhotoPath());
                                // Toast.makeText(AlbumActivity.this, "a", Toast.LENGTH_SHORT).show();
                                //classId.getPhotoPath().size()
                                for (int j = 0; j < classId.getPhotoPath().size(); j++) {
                                    ClassAlbum classAlbum = new ClassAlbum();
                                    classAlbum.setAlbum1(classId.getPhotoPath().get(j).getUrl());
                                    albumList.add(classAlbum);
                                    //   int a = albumList.size();

                                }
                                //Toast.makeText(AlbumActivity.this, String.valueOf(albumList.size()), Toast.LENGTH_SHORT).show();
                                albumAdapter = new AlbumAdapter(albumList, AlbumActivity.this,intent.getStringExtra("classNum"));
                                StaggeredGridLayoutManager staggeredGridLayoutManager=new StaggeredGridLayoutManager(4,StaggeredGridLayoutManager.VERTICAL);
                                // LinearLayoutManager manager = new LinearLayoutManager(AlbumActivity.this);
                                recy_classAlbum.setLayoutManager(staggeredGridLayoutManager);
                                recy_classAlbum.setAdapter(albumAdapter);
                            }

                        }

                    }
                }
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.add_photos:
                openAlbum();
                break;
        }



        return super.onOptionsItemSelected(item);
    }


    private void openAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PHOTO_RESULT_CODE);
    }

    @Override
    protected void initListener() {

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (PHOTO_RESULT_CODE == requestCode && data != null) {
            imgSelectUri = data.getData();
            String[] filePathColumn = new String[]{MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(imgSelectUri, filePathColumn, null, null, null);
            imgFilePath = null;
            while (cursor.moveToNext()) {
                imgFilePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
            }
            // showToast("图片路径"+imgFilePath);
            cursor.close();
            // crop(imgSelectUri);
            uploadBmobImg();
        }
// else if (PHOTO_RESULT_CUT_CODE == requestCode) {
//            showToast("crop(imgSelectUri)");
//            mBitmap = null;
//            //bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgSelectUri);
//            mBitmap = data.getParcelableExtra("data");
//            uploadBmobImg();
//            Log.d(TAG, "onActivityResult: =====uploadBmobImg");
////            mHeadImg.setImageBitmap(bmp);
//        }
        else {
            Toast.makeText(this, "choose photos fail", Toast.LENGTH_SHORT).show();
        }
    }
    private void uploadBmobImg() {
        final BmobFile bmobFile = new BmobFile(new File(imgFilePath));
        bmobFileList.add(bmobFile);
        mproDialog=new ProgressDialog(this);
        mproDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mproDialog.setMessage("处理中...");
        mproDialog.setTitle("上传图片");
        mproDialog.setCancelable(true);
        mproDialog.setCanceledOnTouchOutside(false);
        mproDialog.setIcon(R.drawable.icon);
        mproDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mproDialog.dismiss();
            }
        });

        mproDialog.show();
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Toast.makeText(AlbumActivity.this, "success", Toast.LENGTH_SHORT).show();
                    ClassId classId=new ClassId();
                    classId.setPhotoPath(bmobFileList);
                    classId.setIdNum(idNum);
                    classId.setVoteNum(voteNum);
                    classId.setVoteRank(voteRank);
                    classId.update(objectId, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){

                                Toast.makeText(AlbumActivity.this, "success2", Toast.LENGTH_SHORT).show();
                                initalbumList();
                            }else{
                                Toast.makeText(AlbumActivity.this, "fail", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(AlbumActivity.this, "fail2", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onProgress(Integer value) {
                super.onProgress(value);
            }
        });
    }


}
