package com.example.administrator.classcircle.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.administrator.classcircle.entity.ClassId;
import com.example.administrator.classcircle.LoadCallBack.HttpResult;
import com.example.administrator.classcircle.R;
import com.example.administrator.classcircle.utils.StatusBarUtils;
import com.example.administrator.classcircle.ZxingDependence.EncodingUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class TwoCodeActivity extends BaseActivity {
    private RelativeLayout title_bar;

    private ImageView mClassHead;
    private TextView mClassName;
    private TextView mCreateTime;
    private ImageView mTwoCode;
    private TextView mTitle;
    private TextView scanme;

    final String input = SplashActivity.mClassID;
    public static Bitmap bm = null;
    public static Bitmap mybitmap;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            mClassHead.setImageBitmap((Bitmap) msg.obj);
            // mybitmap=(Bitmap) msg.obj;;
            if (input.equals("")) {
                Toast.makeText(TwoCodeActivity.this, "输入不能为空", Toast.LENGTH_SHORT).show();
            } else {
                Bitmap qrCode = EncodingUtils.createQRCode(input, 600, 600,
                        bm);
                mTwoCode.setImageBitmap(qrCode);
            }
            return true;
        }
    });

    private Bitmap getBitmap(Bitmap obj) {
        return obj;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_two_code;
    }

    @Override
    protected void init() {
        StatusBarUtils.compat(this,getResources().getColor(R.color.colorPrimary));
        mClassHead = (ImageView) findViewById(R.id.id_twoCode_img);
        mClassName = (TextView) findViewById(R.id.id_twoCode_tvName);
        scanme = (TextView) findViewById(R.id.tv_scanme);
        //scanme.setVisibility(View.GONE);
        // mClassName.setVisibility(View.GONE);
        mCreateTime = (TextView) findViewById(R.id.id_twoCode_createTime);
        title_bar = (RelativeLayout) findViewById(R.id.title_bar);

        //  mCreateTime.setVisibility(View.GONE);
        mTwoCode = (ImageView) findViewById(R.id.id_twoCode_img_code);
        mTitle = (TextView) findViewById(R.id.id_header_tv);
        mTitle.setText("二维码名片");
        title_bar.setVisibility(View.GONE);


        BmobQuery<ClassId> bmobQuery = new BmobQuery<>();
        bmobQuery.findObjects(new FindListener<ClassId>() {
            @Override
            public void done(List<ClassId> list, BmobException e) {
                if (e == null) {
                    HttpResult result = new HttpResult(1, list);
                    LoadingPager(result, 1);

                    int size = list.size();
                    for (int i = 0; i < size; i++) {

                        if (Integer.parseInt(input) == list.get(i).getIdNum()) {
                            Bitmap b = getInternetPicture(list.get(i).getPhotoPath().get(0).getUrl());
                            Glide.with(TwoCodeActivity.this).load(list.get(i).getPhotoPath().get(0).getUrl()).into(mClassHead);
                            scanme.setVisibility(View.VISIBLE);
                            mClassName.setVisibility(View.VISIBLE);
                            mCreateTime.setVisibility(View.VISIBLE);
                            mClassHead.setVisibility(View.VISIBLE);
                            mTwoCode.setVisibility(View.VISIBLE);
                            title_bar.setVisibility(View.VISIBLE);

                        }
                    }
                }
            }
        });
    }

    @Override
    protected void initListener() {

    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();

        }
        return super.onOptionsItemSelected(item);
    }

    public Bitmap getInternetPicture(String UrlPath) {

        // 1、确定网址
        // http://pic39.nipic.com/20140226/18071023_164300608000_2.jpg
        final String urlpath = UrlPath;
        // 2、获取Uri
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL uri = new URL(urlpath);
                    // 3、获取连接对象、此时还没有建立连接
                    HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
                    // 4、初始化连接对象
                    // 设置请求的方法，注意大写
                    connection.setRequestMethod("GET");
                    // 读取超时
                    connection.setReadTimeout(5000);
                    // 设置连接超时
                    connection.setConnectTimeout(5000);
                    // 5、建立连接
                    connection.connect();

                    // 6、获取成功判断,获取响应码
                    if (connection.getResponseCode() == 200) {
                        // 7、拿到服务器返回的流，客户端请求的数据，就保存在流当中
                        InputStream is = connection.getInputStream();
                        // 8、从流中读取数据，构造一个图片对象GoogleAPI
                        bm = BitmapFactory.decodeStream(is);
                        mybitmap = bm;
                        if (bm != null) {
                            Log.e("AAA", "nt null");
                        }
                        // 9、把图片设置到UI主线程
                        // ImageView中,获取网络资源是耗时操作需放在子线程中进行,通过创建消息发送消息给主线程刷新控件；
                        Message m = new Message();
                        m.obj = bm;
                        handler.sendMessage(m);
                        Log.i("", "网络请求成功");

                    } else {
                        Log.v("tag", "网络请求失败");
                        bm = null;
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        if (bm == null) {
            Log.e("bbb", "no");
        } else {
            Log.e("vvv", "no");
        }
        return bm;

    }
}
