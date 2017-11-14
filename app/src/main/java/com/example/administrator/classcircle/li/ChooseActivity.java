package com.example.administrator.classcircle.li;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.administrator.classcircle.entity.ActivityLists;
import com.example.administrator.classcircle.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;


public class ChooseActivity extends BaseActivity {
   private ImageView activity_img;
    private EditText activity_title;
    private EditText activity_name;
    private EditText activity_explain;
    private EditText activity_className;
    private Button activity_submit;
    private static final int CHOOSE_PHOTO = 2;
    //从相册获取的图片的真实的路径
    private String choosealbum_uri;
    @Override
    protected int getLayoutRes() {
        return R.layout.activity_choose;
    }

    @Override
    protected void init() {
        initViews();

    }

    private void initViews() {
        activity_img= (ImageView) findViewById(R.id.activity_img);
        activity_submit= (Button) findViewById(R.id.activity_submit);
        activity_title= (EditText) findViewById(R.id.activity_title);
        activity_name= (EditText) findViewById(R.id.activity_name);
        activity_explain= (EditText) findViewById(R.id.activity_explain);
        activity_className= (EditText) findViewById(R.id.activity_class);
    }

    @Override
    protected void initListener() {

        activity_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //上传数据到数据库
                ActivityLists al=new ActivityLists();
                String title=activity_title.getText().toString();
                String name=activity_name.getText().toString();
                String explain=activity_explain.getText().toString();
                String className=activity_className.getText().toString();
                String imgUrl=choosealbum_uri;

                SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
                long time= System.currentTimeMillis();
                String t1=format.format(time);
                al.setCreateTime(t1);
                al.setExplain(explain);
                al.setFromClass(className);
                al.setImgUrl(imgUrl);
                al.setTitle(title);
                al.setName(name);

                al.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {

                    }
                });
               goTo(ActivityList.class);




            }
        });


        //从相册选择照片
        activity_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(ChooseActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE
                )!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(ChooseActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },1);
                }else{
                    openAlbum();
                }
            }
        });

    }
    private void openAlbum() {
        Intent intent=new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){

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
                String selection= MediaStore.Images.Media._ID+"="+id;
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
            activity_img.setImageBitmap(getimage(imagePath));
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
                    Toast.makeText(ChooseActivity.this,"you dinied the permission",Toast.LENGTH_LONG).show();

                }
                break;
        }
    }
    //缩放图片
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
}
