package com.example.administrator.classcircle.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.classcircle.entity.ClassId;
import com.example.administrator.classcircle.entity.Msg;
import com.example.administrator.classcircle.activity.SplashActivity;
import com.example.administrator.classcircle.adapter.ChatAdapter;
import com.example.administrator.classcircle.entity.JsonData;
import com.example.administrator.classcircle.entity.JsonNotice;
import com.example.administrator.classcircle.activity.ModifyMyClassCardActivity;
import com.example.administrator.classcircle.R;
import com.example.administrator.classcircle.activity.MyClassMemberActivity;
import com.example.administrator.classcircle.activity.ShowFileActivity;
import com.example.administrator.classcircle.activity.ShowNoticeActivity;
import com.example.administrator.classcircle.activity.TwoCodeActivity;
import com.example.administrator.classcircle.entity.User;
import com.example.administrator.classcircle.li.AlbumActivity;
import com.example.administrator.classcircle.li.ChatActivity;
import com.example.administrator.classcircle.li.CreateClassActivity;

import com.example.administrator.classcircle.utils.GlideCircleTransform;
import com.example.administrator.classcircle.utils.ThreadUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.ValueEventListener;

/**
 * Created by Administrator on 2017/9/5 0005.
 */

public class Fragment02 extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "Fragment02";

    private ImageView mImageTop;
    private ImageView mImageBg;
    private ImageView mImageTwoCode;
    private ImageView mImageViewFirstDesc, mImageViewSecDesc;
    private View mViewNotice;
    private View mViewFile;
    private View mViewAlbum;
    private View mViewMore;
    private View mViewMyCard;
    private View mViewGroup;
    private TextView mTvMarquee;
    private LinearLayout mLinearLayoutMarquee;
    private LinearLayout mLinearLayoutAllElement;

    private TextView mTvClassName;
    private TextView mTvClassId;
    public static TextView mTvMyName;
    private TextView mTvCountPeople;
    public static final int FRAGMENT_02_REQUEST_CODE = 1101;
    public static final int FRAGMENT_02_RESULT_CODE = 1102;
    public static BmobRealTimeData mBmobRealTimeData;
    private String mUrl;
    private Bitmap mBitmap;
    public static String mClassName;

    private RecyclerView mRecyclerView;
    private ChatAdapter chatAdapter;
    private Button btn_send;
    private EditText edt_content;
    private List<Msg> msgList = new ArrayList<>();
    private BmobRealTimeData brt = new BmobRealTimeData();


    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_02;
    }

    @Override
    protected void initView() {
        initLayoutChat();
        mLinearLayoutAllElement = (LinearLayout) mRootView.findViewById(R.id.fragment02_linearLayout);
        mImageTop = (ImageView) mRootView.findViewById(R.id.f2_img_top);
        mImageTop.setOnClickListener(this);
        mImageBg = (ImageView) mRootView.findViewById(R.id.f2_img_bg);
        mImageBg.setOnClickListener(this);
        mImageTwoCode = (ImageView) mRootView.findViewById(R.id.f2_two_code);
        mImageTwoCode.setOnClickListener(this);
        mViewNotice = mRootView.findViewById(R.id.f2_notice);
        mViewNotice.setOnClickListener(this);
        mViewFile = mRootView.findViewById(R.id.f2_file);
        mViewFile.setOnClickListener(this);
        mViewAlbum = mRootView.findViewById(R.id.f2_album);
        mViewAlbum.setOnClickListener(this);
        mViewMore = mRootView.findViewById(R.id.f2_more);
        mViewMore.setOnClickListener(this);
        mViewMyCard = mRootView.findViewById(R.id.f2_my_card);
        mViewMyCard.setOnClickListener(this);
        mViewGroup = mRootView.findViewById(R.id.f2_group_people);
        mViewGroup.setOnClickListener(this);

        mImageViewFirstDesc = (ImageView) mRootView.findViewById(R.id.f2_img_first);
        mImageViewSecDesc = (ImageView) mRootView.findViewById(R.id.f2_img_sec);

        mTvClassName = (TextView) mRootView.findViewById(R.id.f2_class_name);
        mTvClassId = (TextView) mRootView.findViewById(R.id.f2_class_id);
        mTvMyName = (TextView) mRootView.findViewById(R.id.f2_my_name);
        mTvCountPeople = (TextView) mRootView.findViewById(R.id.f2_count_of_class_people);
        mTvMarquee = (TextView) mRootView.findViewById(R.id.f2_marquee);
        mLinearLayoutMarquee = (LinearLayout) mRootView.findViewById(R.id.f2_linearLayout_marquee);

    }

    private void initLayoutChat() {
        addListennerToMsg();
        btn_send = (Button) mRootView.findViewById(R.id.btn_sendMsg);
        edt_content = (EditText) mRootView.findViewById(R.id.edt_content);
        edt_content.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Intent intent = new Intent(getContext(), ChatActivity.class);
                    startActivity(intent);
                }
            }
        });
        edt_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ChatActivity.class);
                startActivity(intent);
            }
        });


        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recyeclerview_content);

        LinearLayoutManager massage = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(massage);

        BmobQuery<Msg> bmobquery = new BmobQuery<>();
        bmobquery.findObjects(new FindListener<Msg>() {
            @Override
            public void done(List<Msg> list, BmobException e) {
                if (e == null) {
                    msgList.addAll(list);
                    chatAdapter = new ChatAdapter(msgList, getActivity());
                    mRecyclerView.setAdapter(chatAdapter);
                    mRecyclerView.scrollToPosition(msgList.size() - 1);
                }

            }
        });


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = edt_content.getText().toString();
                if (!"".equals(content)) {
                    Msg m = new Msg();
                    m.setStudentId(SplashActivity.mLoginUserName);
                    m.setContent(content);
                    m.setImgUrl(SplashActivity.mImgUrl);
                    m.setName(SplashActivity.mLoginUserName);
                    msgList.add(m);
                    chatAdapter.notifyItemInserted(msgList.size() - 1);
                    mRecyclerView.scrollToPosition(msgList.size() - 1);
                    edt_content.setText("");
                    m.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {

                            }
                        }
                    });
                }
            }
        });
    }

    private void addListennerToMsg() {
        brt.start(new ValueEventListener() {
            @Override
            public void onConnectCompleted(Exception e) {
                if (e == null) {
                    brt.subTableUpdate("Msg");
                }
            }

            @Override
            public void onDataChange(JSONObject jsonObject) {

                BmobQuery<Msg> bmobquery = new BmobQuery<>();
                bmobquery.findObjects(new FindListener<Msg>() {
                    @Override
                    public void done(List<Msg> list, BmobException e) {
                        if (e == null) {
                            msgList.clear();
                            msgList.addAll(list);
                            chatAdapter.notifyDataSetChanged();
                            mRecyclerView.scrollToPosition(msgList.size() - 1);
                            edt_content.setText("");
                            //  Toast.makeText(mActivity, "haha", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });
    }


    @Override
    protected void initData() {
        try {
            getDataFromBmob();
        } catch (SocketException e) {
            showToast("查询超时");
        }
        CheckDataChangeFromBmob();
    }

    private void getDataFromBmob() throws SocketException {
        String classId = SplashActivity.mClassID;
        getClassInfo(classId);

        BmobQuery<User> bmobQuery = new BmobQuery();
        bmobQuery.addWhereEqualTo("classId", classId);
        bmobQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    Log.d(TAG, "done: --查询成功size" + list.size());
                    mTvCountPeople.setText(list.size() + "名成员");
                    setTvMyName(list);
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getPic() != null) {
                            Log.d(TAG, "done: +++i " + list.get(i).getPic().getFileUrl());
                            Glide.with(getContext())
                                    .load(list.get(i).getPic().getFileUrl())
                                    .transform(new GlideCircleTransform(getContext()))
                                    .into(mImageViewFirstDesc);
                            for (int j = i + 1; j < list.size(); j++) {
                                if (list.get(j).getPic() != null) {

                                    Log.d(TAG, "done: +++j " + list.get(j).getPic().getFileUrl());
                                    Glide.with(getContext())
                                            .load(list.get(j).getPic().getFileUrl())
                                            .transform(new GlideCircleTransform(getContext()))
                                            .into(mImageViewSecDesc);
                                    return;
                                }
                            }
                        }
                    }
                    Log.d(TAG, "done: --===查询成功 ");
                } else {
                    Log.d(TAG, "done: --===查询失败 " + e.toString());
//                    其他错误均返回此code
                    if (e.getErrorCode() == 9015) {
                        try {
                            getDataFromBmob();
                        } catch (SocketException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });

    }

    private void getClassInfo(String mClassID) {
        BmobQuery<ClassId> bmobQuery = new BmobQuery();
        bmobQuery.addWhereEqualTo("IdNum", Integer.valueOf(mClassID));
        bmobQuery.findObjects(new FindListener<ClassId>() {
            @Override
            public void done(List<ClassId> list, BmobException e) {
                if (e == null) {
                    int idNum = 0;
                    mUrl = null;
                    for (ClassId classId : list) {
                        mClassName = classId.getClassName();
                        idNum = classId.getIdNum();
                        mUrl = classId.getPhotoPath().get(0).getFileUrl();
                    }
                    Log.d(TAG, "done: ---" + mClassName + "--" + idNum);
                    mTvClassName.setText(mClassName);
                    mTvClassId.setText(String.valueOf(idNum));
                    Glide.with(getContext())
                            .load(mUrl)
                            .placeholder(R.drawable.weather)
                            .centerCrop()
                            .into(mImageBg);
                } else {
                    Log.d(TAG, "done: ====查询失败" + e.toString());
                    mTvClassName.setVisibility(View.GONE);
                    mTvClassId.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setTvMyName(List<User> list) {
        for (User user : list) {
            if (user.getUserName().equals(SplashActivity.mLoginUserName)) {
                if (user.getMyClassCard() != null) {
                    mTvMyName.setText(user.getMyClassCard());
                } else {
                    mTvMyName.setText(SplashActivity.mLoginUserName);
                }
            }
        }
    }

    /**
     * 监听表的变化
     */

    private void CheckDataChangeFromBmob() {
        mBmobRealTimeData = new BmobRealTimeData();
        mBmobRealTimeData.start(new ValueEventListener() {
            @Override
            public void onConnectCompleted(Exception e) {
                if (mBmobRealTimeData.isConnected()) {
                    // 监听表更新
                    mBmobRealTimeData.subTableUpdate("Data");
                    mBmobRealTimeData.subTableUpdate("Notice");
                    mBmobRealTimeData.unsubTableDelete("Notice");
                }
            }

            /**
             * 表的数据变化返回的是Json数据
             * @param jsonObject
             */
            @Override
            public void onDataChange(JSONObject jsonObject) {
                Log.d("bmob--------------", "(" + jsonObject.optString("action") + ")" + "数据：" + jsonObject);
                //{"data":{"objectId":"2ed0356c78"},"action":"updateTable","objectId":"","tableName":"Notice","appKey":"367b261b079c03625a6f45aa2815be7d"}
                try {
                    JSONObject object = new JSONObject(jsonObject.toString());
                    String tableName = object.getString("tableName");
                    String data = object.toString();
                    Log.d(TAG, "onDataChange: ======" + tableName);
                    Log.d(TAG, "onDataChange: -----" + data.contains("createdAt"));
                    if (tableName.equals("Notice") && data.contains("createdAt")) {
                        //解析JsonNotice
                        final String marquee = decodeNoticeJson(jsonObject.toString());
                        ThreadUtils.runUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Log.d(TAG, "run: ---------Notice" + marquee);
                                mLinearLayoutMarquee.setVisibility(View.VISIBLE);
                                mTvMarquee.setText(marquee);
                                mTvMarquee.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                                mTvMarquee.setSelected(true);
                            }
                        });
                    } else if (tableName.equals("Data") && data.contains("createdAt")) {
                        //解析JsonData
                        final String marquee = decodeDataJson(jsonObject.toString());
                        ThreadUtils.runUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "run: ---------Data" + marquee);
                                mLinearLayoutMarquee.setVisibility(View.VISIBLE);
                                mTvMarquee.setText(marquee);
                                mTvMarquee.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 解析Data表发生变化返回Json
     *
     * @param s Data表发生变法返回的Json
     * @return
     */
    private String decodeDataJson(String s) {
        Gson gson = new Gson();
        JsonData jsonData = gson.fromJson(s, JsonData.class);
        String tableName = jsonData.tableName;
        String name = jsonData.data.name;
        String time = jsonData.data.updatedAt;
        String fileName = jsonData.data.uploadFile.filename;
        return "大 家 好 :" + name + "在 " + time + "上 传 了 文 件" + fileName + " 快 去 看 看 吧^_^!";
    }

    /**
     * 解析Notice表发生变化返回Json
     *
     * @param s Notice表发生变法返回的Json
     * @return
     */
    private String decodeNoticeJson(String s) {
        Gson gson = new Gson();
        JsonNotice jsonNotice = gson.fromJson(s, JsonNotice.class);
        String tableName = jsonNotice.tableName;
        String time = jsonNotice.data.createdAt;
        String name = jsonNotice.data.uploadName;
        String fileName = jsonNotice.data.uploadOther.filename;
        String content = jsonNotice.data.uploadContent;
        String con = content.substring(0, 5);
        if (fileName == null) {
            return "大 家 好: " + name + "在" + time + "发 布 了 通 知 ：" + con + "···，文 件" + fileName + " 快 去 看 看 吧 ^_^ !";
        }
        return "大 家 好:" + name + "在 " + time + "发 布 了 通 知 ：" + con + "···，" + " 快 去 看 看 吧 ^_^ !";

    }

    @Override
    protected void initListener() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.f2_img_top:
                showDialog();
                break;
            case R.id.f2_img_bg:
                break;
            case R.id.f2_two_code:
                goTo(TwoCodeActivity.class);
                break;
            case R.id.f2_notice:
                goTo(ShowNoticeActivity.class);
                break;
            case R.id.f2_file:
                goTo(ShowFileActivity.class);
                break;
            case R.id.f2_album:
                Intent intent = new Intent(getContext(), AlbumActivity.class);
                intent.putExtra("classNum", SplashActivity.mClassID);
                startActivity(intent);
                break;
            case R.id.f2_more:
                showToast("f2_more");
                break;
            case R.id.f2_my_card:
                goTo(ModifyMyClassCardActivity.class);
                break;
            case R.id.f2_group_people:
                goTo(MyClassMemberActivity.class);
                break;
        }
    }

    private void showDialog() {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(getContext());
        normalDialog.setIcon(R.drawable.weather);
        normalDialog.setTitle("我的班圈");
        normalDialog.setMessage("选择操作");
        normalDialog.setPositiveButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        normalDialog.setNegativeButton("退出班级",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        DeleteClassIdFromUser();
                    }
                });
        // 显示
        normalDialog.show();
    }

    private void DeleteClassIdFromUser() {
        User user = new User();
        user.setClassId("");
        user.update(SplashActivity.mObjID, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    goTo(CreateClassActivity.class);
                    this.onFinish();
                } else {
                    showToast("退出失败");
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        mTvMarquee.setSelected(true);
    }
}
