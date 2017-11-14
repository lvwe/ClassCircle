package com.example.administrator.classcircle;

import android.app.ActivityManager;
import android.content.pm.PackageManager;
import android.util.Log;

import com.example.administrator.classcircle.entity.FileInfo;
import com.example.administrator.classcircle.LoadCallBack.CustomCallBack;
import com.example.administrator.classcircle.LoadCallBack.EmptyCallBack;
import com.example.administrator.classcircle.LoadCallBack.ErrorCallBack;
import com.example.administrator.classcircle.LoadCallBack.LoadingCallBack;
import com.example.administrator.classcircle.LoadCallBack.TimeOutCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.kingja.loadsir.core.LoadSir;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import cn.bmob.v3.Bmob;

/**
 * Created by Administrator on 2017/9/12 0012.
 */

public class Application extends android.app.Application {
    private static final String TAG = "Application";

    //固定大小线程池  5
    public static final Executor MAIN_EXECUTOR = Executors.newFixedThreadPool(5);
    static Application sAppContent;

    static Map<String, FileInfo> mFileInfoMap = new HashMap<String, FileInfo>();
    //采用HashMap结构，文件地址--->>>FileInfo 映射结构，重复加入FileInfo

    @Override
    public void onCreate() {
        super.onCreate();
        initBmob();
        initEMClient();
        initLoadSir();
        this.sAppContent = this;
    }

    public static void addFileInfo(FileInfo fileInfo) {
        if (!mFileInfoMap.containsKey(fileInfo.getFilePath()))
            mFileInfoMap.put(fileInfo.getFilePath(), fileInfo);
    }
    public void updateFileInfo(FileInfo fileInfo){
        mFileInfoMap.put(fileInfo.getFilePath(), fileInfo);
    }
    public static void deleteFileInfo(FileInfo fileInfo){
        if (mFileInfoMap.containsKey(fileInfo.getFilePath())){
            mFileInfoMap.remove(fileInfo.getFilePath());
        }
    }

    public static boolean isExist(FileInfo fileInfo) {
        Log.d(TAG, "isExist: ----mFileInfoMap"+mFileInfoMap);
        Log.d(TAG, "isExist: ----fileInfo.getFilePath"+fileInfo.getFilePath());
        Log.d(TAG, "isExist: -----containsKey"+mFileInfoMap.containsKey(fileInfo.getFilePath()));

        if (mFileInfoMap == null){
            return false;
        }
        return mFileInfoMap.containsKey(fileInfo.getFilePath());
    }

    public boolean isFileInfoMapExist(){
        if (mFileInfoMap == null && mFileInfoMap.size() < 0){
            return false;
        }
        return true;
    }

    public static Map<String, FileInfo> getFileInfoMap(){
        return  mFileInfoMap;
    }

    public long getAllFileInfoSize(){
        long total = 0;
        for (FileInfo fileInfo : mFileInfoMap.values()){
            if (fileInfo != null){
                total = total+fileInfo.getSize();
            }
        }
        return total;
    }

    public static Application getAppContent() {
        return sAppContent;
    }

    private void initLoadSir() {
                LoadSir.beginBuilder()
                .addCallback(new ErrorCallBack())//'添加各种状态页
                .addCallback(new EmptyCallBack())
                .addCallback(new LoadingCallBack())
                .addCallback(new TimeOutCallBack())
                .addCallback(new CustomCallBack())
                .setDefaultCallback(LoadingCallBack.class)//设置默认状态页
                .commit();
    }

    private void initEMClient() {
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
// 如果APP启用了远程的service，此application:onCreate会被调用2次
// 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
// 默认的APP会在以包名为默认的process name下运行，如果查到的process name不是APP的process name就立即返回
        if (processAppName == null ||!processAppName.equalsIgnoreCase(getPackageName())) {
            Log.e(TAG, "enter the service process!");

            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }
        EMOptions options = new EMOptions();
// 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        options.setAutoLogin(true);
//初始化
        EMClient.getInstance().init(getApplicationContext(), options);
//在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(true);
    }

    private void initBmob() {
        //第一：默认初始化
        Bmob.initialize(this, "367b261b079c03625a6f45aa2815be7d");
        Log.d(TAG, "initBmob: --------------------");
        // 注:自v3.5.2开始，数据sdk内部缝合了统计sdk，开发者无需额外集成，传渠道参数即可，不传默认没开启数据统计功能
        //Bmob.initialize(this, "Your Application ID","bmob");

        //第二：自v3.4.7版本开始,设置BmobConfig,允许设置请求超时时间、文件分片上传时每片的大小、文件的过期时间(单位为秒)，
        //BmobConfig config =new BmobConfig.Builder(this)
        ////设置appkey
        //.setApplicationId("Your Application ID")
        ////请求超时时间（单位为秒）：默认15s
        //.setConnectTimeout(30)
        ////文件分片上传时每片的大小（单位字节），默认512*1024
        //.setUploadBlockSize(1024*1024)
        ////文件的过期时间(单位为秒)：默认1800s
        //.setFileExpiration(2500)
        //.build();
        //Bmob.initialize(config);
    }

    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }
}
