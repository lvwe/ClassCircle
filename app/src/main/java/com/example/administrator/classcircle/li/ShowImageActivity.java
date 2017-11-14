package com.example.administrator.classcircle.li;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.example.administrator.classcircle.entity.ClassAlbum;
import com.example.administrator.classcircle.entity.ClassId;
import com.example.administrator.classcircle.LoadCallBack.HttpResult;
import com.example.administrator.classcircle.R;
import com.example.administrator.classcircle.fragment.FragmentShowImage;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class ShowImageActivity extends BaseActivity {


    private ViewPager viewPager;
    private List<FragmentShowImage> pageview;
    private List<ClassAlbum> classAlbumList = new ArrayList<>();


    @Override
    protected int getLayoutRes() {
        return R.layout.activity_show_image;
    }

    @Override
    protected void init() {


    }

    private String getImageUrl() {
        Intent intent = getIntent();
        String str = intent.getStringExtra("image");
        // Toast.makeText(this, "wawa", Toast.LENGTH_SHORT).show();

        return str;
    }

    @Override
    protected void initListener() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        //将view装入数组
        initalbumList();


    }


    private void initalbumList() {

        BmobQuery<ClassId> bmobQuery = new BmobQuery<>();
        bmobQuery.findObjects(new FindListener<ClassId>() {
            @Override
            public void done(List<ClassId> list, BmobException e) {
                if (e == null) {


                    //获取所有的班级图片的路径
                    for (int i = 0; i < list.size(); i++) {
                        ClassId classId;
                        classId = list.get(i);
                        Intent intent = getIntent();
                        int classNum = Integer.parseInt(intent.getStringExtra("classNum"));
                        //根据班级号码找信息
                        if (classId.getIdNum() == classNum) {
                            //找出所有图片
                            if (classId.getPhotoPath().size() > 0) {

                                //classId.getPhotoPath().size()
                                for (int j = 0; j < classId.getPhotoPath().size(); j++) {
                                    ClassAlbum classAlbum = new ClassAlbum();
                                    //Toast.makeText(ShowImageActivity.this, "123", Toast.LENGTH_SHORT).show();
                                    classAlbum.setAlbum1(classId.getPhotoPath().get(j).getUrl());
                                    classAlbumList.add(classAlbum);
                                    int a = classAlbumList.size();
                                    //getalbum(classAlbumList);


                                }
                                // Toast.makeText(ShowImageActivity.this, String.valueOf(2), Toast.LENGTH_LONG).show();
                                pageview = new ArrayList<>();
                                for (int k = 0; k < classAlbumList.size(); k++) {
                                    FragmentShowImage fragment = new FragmentShowImage();
                                    fragment.setImgUrl(classAlbumList.get(k).getAlbum1(), ShowImageActivity.this);
                                    // Toast.makeText(ShowImageActivity.this, getImageUrl(), Toast.LENGTH_SHORT).show();
                                    pageview.add(fragment);

                                }

                                viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
                                    @Override
                                    public Fragment getItem(int position) {
                                        return pageview.get(position);
                                    }

                                    @Override
                                    public int getCount() {
                                        return pageview.size();
                                    }
                                });
                                viewPager.setCurrentItem(Integer.parseInt(intent.getStringExtra("position")));
                                HttpResult resultCode = new HttpResult(1, list);
                                LoadingPager(resultCode, 0);
                            }

                        }

                    }
                }
            }
        });


    }

    private void getalbum(List<ClassAlbum> classAlbumList) {
        this.classAlbumList = classAlbumList;


    }

}
