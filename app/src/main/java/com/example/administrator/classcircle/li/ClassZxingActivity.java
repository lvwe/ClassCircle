package com.example.administrator.classcircle.li;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.administrator.classcircle.entity.ClassId;
import com.example.administrator.classcircle.R;
import com.example.administrator.classcircle.utils.EncodingUtils;
import com.example.administrator.classcircle.ZxingDependence.CaptureActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


public class ClassZxingActivity extends BaseActivity {
    private EditText   mEditText;
    private boolean isshowing=true;
    private PopupWindow mpopuWindow;
    private  boolean HASSHOWPOPUPWINDOW=false;
    private Button searchClass;


    @Override
    protected int getLayoutRes() {
        return R.layout.activity_class_zxing;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void init() {
        mEditText = (EditText) findViewById(R.id.et_text);
        searchClass= (Button) findViewById(R.id.searchClass);

        searchClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result=mEditText.getText().toString();
                //int num=Integer.parseInt(result);
                if(result.length()==0){
                    Toast.makeText(ClassZxingActivity.this, "输入不能为空", 		Toast.LENGTH_SHORT).show();
                    return;
                }

                Pattern pattern = Pattern.compile("[0-9]*");
                Matcher isNum = pattern.matcher(result);
                if( isNum.matches() && result.length()<9){
                    int num = Integer.parseInt(result);
                    if(num>100000 && num < 999999){

                        //判断班级id是否存在
                        getClassIdNum(result,num);
                    }else if(num<100000 ){
                        Toast.makeText(ClassZxingActivity.this, "班级id只能是6位数哦，亲，请重新输入哈", Toast.LENGTH_SHORT).show();


                    }else if(num > 999999){
                        Toast.makeText(ClassZxingActivity.this, "班级id只能是6位数哦，亲，请重新输入哈", Toast.LENGTH_SHORT).show();

                    } else{
                        Toast.makeText(ClassZxingActivity.this, "找不到该班级id呢亲，请认真输入好吗", Toast.LENGTH_SHORT).show();

                    }
                }else {
                    Toast.makeText(ClassZxingActivity.this, "您输入的班级id格式有误呢亲", Toast.LENGTH_SHORT).show();


                }

            }
        });


    }


    @Override
    protected void initListener() {




    }
    //显示右上角的菜单栏并设置点击事件
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
getMenuInflater().inflate(R.menu.main_option,menu);


        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN && HASSHOWPOPUPWINDOW){
            isshowing=!isshowing;
            HASSHOWPOPUPWINDOW=!HASSHOWPOPUPWINDOW;
            mpopuWindow.dismiss();
        }


        return super.onTouchEvent(event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.item_06:

                if(isshowing){
                    showPopupWindow();
                    isshowing=!isshowing;
                }



                break;
            case R.id.item_07:
                HASSHOWPOPUPWINDOW=!HASSHOWPOPUPWINDOW;
                isshowing=!isshowing;
                mpopuWindow.dismiss();
                break;
        }



        return super.onOptionsItemSelected(item);
    }

    //扫描二维码 //https://cli.im/text?2dd0d2b267ea882d797f03abf5b97d88二维码生成网站
    public void scan(View view) {
        startActivityForResult(new Intent(this, CaptureActivity.class), 0);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,
                resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                String result = bundle.getString("result");


                Pattern pattern = Pattern.compile("[0-9]*");
                 Matcher isNum = pattern.matcher(result);
                 if( isNum.matches() ){
                     int num = Integer.parseInt(result);
                     if(num>100000 &&num < 999999){

                         //判断班级id是否存在
                         getClassIdNum(result,num);
                     }
                 } else {
                    Intent intent=new Intent(ClassZxingActivity.this,showTextzxingContentActivity.class);
                    intent.putExtra("result",result);
                    startActivity(intent);


                }


                //Toast.makeText(ClassZxingActivity.this, "扫描不出结果", Toast.LENGTH_LONG).show();
            //    mTextView.setText(result);


            }
        }
    }

    public void make(View view) {

    }

    public void getClassIdNum(final String result, final int num) {

        BmobQuery<ClassId> bmobQuery = new BmobQuery<>();
        bmobQuery.addQueryKeys("IdNum");
        bmobQuery.findObjects(new FindListener<ClassId>() {
            @Override
            public void done(List<ClassId> list, BmobException e) {
                if (e == null) {
                 //   HttpResult resultCode=new HttpResult(1,list);
                    List<Integer> listIdNum = new ArrayList<>();
                    int count = list.size();

//                    if(listIdNum.get(0)==list.get(0).getIdNum()){
//                        return;
//                    }
                    for (int i = 0; i < count; i++) {
                        listIdNum.add(list.get(i).getIdNum());
   //Toast.makeText(ClassZxingActivity.this,String.valueOf(listIdNum.get(i)),Toast.LENGTH_LONG).show();
                    }

                    joinPager(num,result,listIdNum);



                } else {
                    Toast.makeText(ClassZxingActivity.this, "no data be found", Toast.LENGTH_LONG).show();
                }


            }
        });
    }
    public void joinPager(int num,String result,List<Integer> a){
        for(int i = 0; i< a.size(); i++){
           // Toast.makeText(ClassZxingActivity.this, String.valueOf(list.get(i)), Toast.LENGTH_LONG).show();
            if(num==a.get(i)){
               // Toast.makeText(ClassZxingActivity.this, String.valueOf(list.get(i)), Toast.LENGTH_LONG).show();
                Intent intent=new Intent(ClassZxingActivity.this,JoinActivity.class);
                intent.putExtra("result",result);

                startActivity(intent);

            }
        }
    }
    private void showPopupWindow(){

        HASSHOWPOPUPWINDOW=!HASSHOWPOPUPWINDOW;
        View view= LayoutInflater.from(ClassZxingActivity.this).inflate(R.layout.puplayout,null);
        mpopuWindow=new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                700);
        mpopuWindow.setContentView(view);

        final ImageView   mImageView = (ImageView) view.findViewById(R.id.img_shouw);
        final CheckBox   mCheckBox = (CheckBox) view.findViewById(R.id.cb_logo);
        Button make= (Button) view.findViewById(R.id.makeZxing);

        make.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isshowing=!isshowing;
                HASSHOWPOPUPWINDOW=!HASSHOWPOPUPWINDOW;
                mpopuWindow.dismiss();
                String input = mEditText.getText().toString();
                if (input.equals("")) {
                    Toast.makeText(ClassZxingActivity.this, "输入不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    Bitmap qrCode = EncodingUtils.createQRCode(input, 500, 500, mCheckBox.isChecked() ?
                            BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher) : null);
                    //CheckBox选中就设置Logo
                    getClassIdNum("390689",390689);
                    mImageView.setImageBitmap(qrCode);


                }
            }
        });
        View view1=LayoutInflater.from(ClassZxingActivity.this).inflate(R.layout.activity_class_zxing,null);
        mpopuWindow.setAnimationStyle(R.style.contextMenuAnim);
        mpopuWindow.showAtLocation(view1, Gravity.BOTTOM,0,0);
       // Toast.makeText(this, "no now", Toast.LENGTH_SHORT).show();
    }
}