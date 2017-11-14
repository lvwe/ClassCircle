package com.example.administrator.classcircle.li;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.administrator.classcircle.entity.ClassId;
import com.example.administrator.classcircle.LoadCallBack.HttpResult;
import com.example.administrator.classcircle.R;
import com.example.administrator.classcircle.activity.MainActivity;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

public class AddClass extends BaseActivity {

    private static final int CHOOSE_PHOTO = 2;
    private static final int TAKE_PHOTO = 1;
    private ClassId classid=new ClassId();
  private ImageView head_image;
    private Button take_photo;
    private Button choose_photo;
    private EditText className;
    private Button toNextActivity;

    //拍照后的图片路径
    private Uri imageUrl;
    //从相册获取的图片的真实的路径
    private String choosealbum_uri;
    private BmobFile bmobFile;
    private String imagePath;
    private List<BmobFile> bmobFileList=new ArrayList<>();
    List<ClassId>  list=new ArrayList<>();
    HttpResult httpResult;
    Handler handler=new Handler();



    @Override
    protected int getLayoutRes() {
        return R.layout.activity_add_class;
    }

    @Override


    protected void init() {

        initview();
        // 显示标题栏左上角的返回图标
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // 显示标题栏cal
        getSupportActionBar().setTitle("班级");
    }

    private void initview() {

        head_image= (ImageView) findViewById(R.id.head_img);
        take_photo= (Button) findViewById(R.id.take_photo);
        choose_photo= (Button) findViewById(R.id.choose_photo);
        className= (EditText) findViewById(R.id.edt_classname);
        toNextActivity= (Button) findViewById(R.id.enter_mainActivity);
        initData();

    }

    private void initData() {



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
                finish();
            return true;
        }
return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initListener() {

        //进入主界面

        toNextActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //把新建班级信息传到数据库


                takeDataToDatabase();


              handler.postDelayed(new Runnable() {
                  @Override
                  public void run() {
        goTo(MainActivity.class);
                  }
              },5000 );

            }
        });

        //拍照
        take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建file对象，用于存储拍照后的照片
                File outputImage=new File(getExternalCacheDir(),"output_image.jpg");
                try{
                    if(outputImage.exists()){
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
                if(Build.VERSION.SDK_INT>=24){

                    imageUrl= FileProvider.getUriForFile(AddClass.this,"com.example.cameraalbumtest" +
                            ".fileprovider",outputImage);

                }else{
//                    Toast.makeText(AddClass.this,"wwww",Toast.LENGTH_LONG).show();

                    imageUrl=Uri.fromFile(outputImage);
                     imagePath=getRealFilePath(AddClass.this,imageUrl);
                }
                //启动相机
                Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUrl);
                startActivityForResult(intent,TAKE_PHOTO);

            }
        });


        //从相册选择照片
         choose_photo.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if(ContextCompat.checkSelfPermission(AddClass.this, Manifest.permission.WRITE_EXTERNAL_STORAGE
                 )!= PackageManager.PERMISSION_GRANTED){
                     ActivityCompat.requestPermissions(AddClass.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                     },1);
                 }else{
                     openAlbum();
                 }
             }
         });

    }

    private void takeDataToDatabase() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(imagePath!=null){


                    //把图片路径转为bombFile并上传到bmob服务器
                    File file=new File(imagePath);
                    final BmobFile bmobFile=new BmobFile(file);
                    //   classid.setImg(imagePath);
                    bmobFileList.add(bmobFile);
                    classid.setPhotoPath(bmobFileList);
                    String getName=className.getText().toString();
                    //班级id
                    int idNum=new Random().nextInt(1000000);
                    //班级创建时间
                    SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
                    long time= System.currentTimeMillis();
                    String t1=format.format(time);
//                    String a=String.valueOf(idNum);
//                    Log.e("AA",a);

                    classid.setCreateTime(t1);
                    classid.setIdNum(idNum);
                    classid.setClassName(getName);

                    bmobFile.uploadblock(new UploadFileListener() {

                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                 list.add(classid);
                                // Toast.makeText(AddClass.this, String.valueOf(list.size()), Toast.LENGTH_SHORT).show();
                                httpResult=new HttpResult(1,list);
                                showLoadingpager(httpResult);

                                //Toast.makeText(AddClass.this, "上传文件成功:" + bmobFile.getFileUrl(), Toast.LENGTH_SHORT).show();
                                //是继承了BmobObject的一个类
                                classid.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String s, BmobException e) {
                                        if(e==null){
                                            Log.d("bmob", "成功");

                                        }else{
                                            Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                                        }
                                    }
                                });
                            }else{
                                // Toast.makeText(AddClass.this, "上传文件失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onProgress(Integer value) {
                            // 返回的上传进度（百分比）
                        }
                    });

                }else if(choosealbum_uri!=null){
                    File file=new File(choosealbum_uri);
                    final BmobFile bmobFile=new BmobFile(file);
                    bmobFileList.add(bmobFile);
                    classid.setPhotoPath(bmobFileList);



                    String getName=className.getText().toString();
                    //班级id
                    int idNum=new Random().nextInt(1000000);
                    //班级创建时间
                    SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
                    //Date d1=new Date(time);
                    long time= System.currentTimeMillis();
                    String t1=format.format(time);
//                    String a=String.valueOf(idNum);
//                    Log.e("AA",a);
                    classid.setCreateTime(t1);
                    classid.setIdNum(idNum);
                    classid.setClassName(getName);


//                    final int[] i = {0};
//                    final Handler handler=new Handler(new Handler.Callback() {
//                        @Override
//                        public boolean handleMessage(Message msg) {
//                                if(msg.what==1){
//                                    i[0]++;
//                                }
//
//                            return true;
//                        }
//                    });
//                    handler.sendEmptyMessageDelayed(1,1000);
                    bmobFile.uploadblock(new UploadFileListener() {

                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                list.add(classid);
                               // Toast.makeText(AddClass.this, String.valueOf(list.size()), Toast.LENGTH_SHORT).show();
                                httpResult=new HttpResult(1,list);
                                showLoadingpager(httpResult);

                                //bmobFile.getFileUrl()--返回的上传文件的完整地址
                                // Toast.makeText(AddClass.this, "上传文件成功:" + bmobFile.getFileUrl(), Toast.LENGTH_SHORT).show();
                                //是继承了BmobObject的一个类
                                classid.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String s, BmobException e) {
                                        if(e==null){


                                            Log.d("bmob", "成功");

                                        }else{
                                            Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                                        }
                                    }
                                });
                            }else{
                                // Toast.makeText(AddClass.this, "上传文件失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onProgress(Integer value) {
                            // 返回的上传进度（百分比）
                            if(value<100){

                            }else{

                            }

                        }
                    });
                }


//                Toast.makeText(AddClass.this,classid.getClassName(),Toast.LENGTH_LONG).show();




            }
        }).start();
    }

    private void showLoadingpager(HttpResult httpResult) {
        LoadingPager(httpResult,5);
    }

    private void openAlbum() {
        Intent intent=new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case TAKE_PHOTO:
                if(resultCode==RESULT_OK){
                    //将拍照后的照片显示出来

//                    try {
                        Bitmap bitmap=BitmapFactory.decodeFile(imagePath);
//                        Bitmap  bitmap= BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUrl));
                        head_image.setImageBitmap(bitmap);
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
                }
                break;
            case CHOOSE_PHOTO:
                if(resultCode==RESULT_OK){
                    //判断手机系统版本号
                    if(Build.VERSION.SDK_INT>=19){
                        //4.4及以上系统使用这个方法处理照片
                        handleTmageOnkitkat(data);
                    }else{
                        //4.4以下系统使用这个方法处理照片
                        handleImageBeforKitkat(data);
                    }
                }
                break;
        }
    }

    private void handleImageBeforKitkat(Intent data) {
        Uri uri=data.getData();
        String imagePath=getImagePath(uri,null);
        displayImage(imagePath);
    }

    @TargetApi(19)
    private void handleTmageOnkitkat(Intent data) {
        String imagePath=null;
        Uri uri=data.getData();
        if(DocumentsContract.isDocumentUri(this,uri)){
            //如果是document类型的Uri，则通过document id处理
            String docId=DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id=docId.split(":")[1];
                String selection=MediaStore.Images.Media._ID+"="+id;
                imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);


            }   else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                       Uri contentUri= ContentUris.withAppendedId(Uri.parse("content://downloads/" +
                               "public_downloads"),Long.valueOf(docId));
                imagePath=getImagePath(contentUri,null);
            }else if("content".equalsIgnoreCase(uri.getScheme())){
                //如果是content类型的uri，则使用普通处理
                imagePath=getImagePath(uri,null);
            }else if("file".equalsIgnoreCase(uri.getScheme())){
                //如果是file类型的uri，直接获取图片路径
                imagePath=uri.getPath();
            }

        }
       displayImage(imagePath);
    }

    private void displayImage(String imagePath) {
        choosealbum_uri=imagePath;
        if(imagePath!=null){
            Bitmap bitmap=BitmapFactory.decodeFile(imagePath);
            head_image.setImageBitmap(getimage(imagePath));
           // Glide.with(this).load(imagePath).into(head_image);
        }
    }

    private String getImagePath(Uri uri, String selection) {
        String path=null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cuisor=getContentResolver().query(uri,null,selection,null,null);
        if(cuisor!=null){
            if(cuisor.moveToFirst()){
                path=cuisor.getString(cuisor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cuisor.close();
        }
        return path;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else{
                    Toast.makeText(AddClass.this,"you dinied the permission",Toast.LENGTH_LONG).show();

                }
                break;
        }
    }
    //把图片的uri转为string格式
    public  String getRealFilePath( Context context,  Uri uri ) {
        if ( null == uri ) return null;
        String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }
    //压缩图片
    private Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while ( baos.toByteArray().length / 1024>100) {    //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            options -= 10;//每次都减少10
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中

        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    private Bitmap getimage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath,newOpts);//此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }


}
