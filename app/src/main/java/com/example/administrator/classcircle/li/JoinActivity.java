package com.example.administrator.classcircle.li;

import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.administrator.classcircle.entity.ClassId;
import com.example.administrator.classcircle.LoadCallBack.HttpResult;
import com.example.administrator.classcircle.R;
import com.example.administrator.classcircle.activity.*;
import com.example.administrator.classcircle.activity.SplashActivity;
import com.example.administrator.classcircle.entity.User;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class JoinActivity extends BaseActivity {
    private static final String TAG = "JoinActivity";
    HttpResult resultCode;
    private TextView tv_classid;
    private LinearLayout showClassAlbum;
    private ImageView class_img;
    private ImageView class_album1;
    private ImageView class_album2;
    private ImageView class_album3;
    private Button confire;
    private TextView class_name;
    private EditText marke;
    private ClassId classId = new ClassId();
    private Bitmap bitmap1 = null;
    //bmob上的班级id
    private String objectId;
    private int realNum;
    //private String url;
    private String classNum;



    @Override
    protected int getLayoutRes() {
        return R.layout.activity_join;
    }

    @Override
    protected void init() {

        // 显示标题栏左上角的返回图标
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // 显示标题栏cal
        getSupportActionBar().setTitle("班级");

        initViews();
        //获取传过来的班级号码
        getIdNum();

//根据传过来的班级号码数据后台找出对应的班级信息并显示出来
        BmobQuery<ClassId> bmobQuery = new BmobQuery<>();
        bmobQuery.findObjects(new FindListener<ClassId>() {
            @Override
            public void done(final List<ClassId> list, BmobException e) {
                Log.d(TAG, "done: ---"+e.toString()+"--"+e.getErrorCode());
                if (e == null) {
                    Log.d(TAG, "done: ---"+list.get(0));
                     resultCode=new HttpResult(1,list);

                    //获取该班级id的所有信息
                    for (int i = 0; i < list.size(); i++) {
                        int classnum = Integer.parseInt(classNum);
                        if (list.get(i).getIdNum() == classnum) {
                            realNum = i;
                            class_name.setText(list.get(realNum).getClassName());
                            //String size = String.valueOf(list.size());
                            //Toast.makeText(JoinActivity.this, size, Toast.LENGTH_LONG).show();

                            if (list.get(realNum).getPhotoPath().size() >0) {

                                String path = list.get(realNum).getPhotoPath().get(0).getUrl();
                                Log.d(TAG, "done: -----+"+path);
                                //Toast.makeText(JoinActivity.this, "d", Toast.LENGTH_SHORT).show();
                                Glide.with(JoinActivity.this).load(path).into(class_img);
                            }
                        }
                    }

                }
            }


        });

    }


    private void getIdNum() {
        Intent i = getIntent();
        classNum = i.getStringExtra("result");
        tv_classid.setText(classNum);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void initViews() {


        showClassAlbum = (LinearLayout) findViewById(R.id.showClassAlbum);
        confire = (Button) findViewById(R.id.confire);
        tv_classid= (TextView) findViewById(R.id.tv_classid);
        class_img = (ImageView) findViewById(R.id.class_img);
        class_album1 = (ImageView) findViewById(R.id.class_ablum1);
        class_album2 = (ImageView) findViewById(R.id.class_ablum2);
        class_album3 = (ImageView) findViewById(R.id.class_ablum3);
        Glide.with(this).load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1506008904277&di=181fcf10c202388c8d70bd9832d981c1&imgtype=0&src=http%3A%2F%2Fpic.58pic.com" +
                "%2F58pic%2F15%2F35%2F99%2F00p58PICwCH_1024.jpg").into(class_album1);
        Glide.with(this).load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1506008904276&di=de8da7b76a43bacb9211169053579937&imgtype=0&src=http%3A%2F%2Fwww.zhlzw.com%2FUploadFiles%2FArticl" +
                "e_UploadFiles%2F201204%2F20120412123907568.jpg").into(class_album2);
        Glide.with(this).load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1506008922973&di=29b9f9c22e63396f5eb532606f13eb88&imgtype=0&src=http%3A%2F%2Fwww.zhlzw.com%2FUploadFiles%" +
                "2FArticle_UploadFiles%2F201204%2F20120412123924436.jpg").into(class_album3);

        marke = (EditText) findViewById(R.id.marke);
        class_name = (TextView) findViewById(R.id.class_name);
        // to next activity
        confire.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                User user = new User();
                user.setClassId(tv_classid.getText().toString().trim());
                String objId = SplashActivity.mObjID;
                Log.d(TAG, "onClick: ----btn"+objId);

                if (objId != null && objId.length() > 0){
                    user.update(objId, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null){
                                SplashActivity.mClassID = classNum;
                                Intent i = new Intent(JoinActivity.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            }else {
                                Toast.makeText(JoinActivity.this,"加入失败",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    Toast.makeText(JoinActivity.this,"加入失败",Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    @Override
    protected void initListener() {
        showClassAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(JoinActivity.this, AlbumActivity.class);
                intent.putExtra("classNum", classNum);
                LoadingPager(resultCode,0);
                startActivity(intent);


            }
        });

    }



}
