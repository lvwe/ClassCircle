package com.example.administrator.classcircle.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/9/23 0023.
 */

public class DownloadUtil {
    private static final String TAG = "DownloadUtil";
    private static DownloadUtil sDownloadUtil;
    private OkHttpClient mOkHttpClient = null;
    public static File mFile;

    public static DownloadUtil get(){
        if (sDownloadUtil == null){
            sDownloadUtil = new DownloadUtil();
        }
        return sDownloadUtil;
    }

    private DownloadUtil(){
        mOkHttpClient = new OkHttpClient();
    }

    public void download(final String url, final String saveDir, final OnDownloadListener listener ){

        Request request = new Request.Builder().url(url).build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //下载失败
                listener.onDownloadFailed();
            }

            @Override
            public void onResponse(Call call, Response response)  {
                //下载成功
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                //储存下载文件的目录

                try {
                    String savePath = isExistDir(saveDir);

                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    mFile = new File(savePath,getNameFromUrl(url));
                    Log.d(TAG, "onResponse: ---------------"+ mFile);
                    fos = new FileOutputStream(mFile);
                    long sum = 0;
                    while((len = is.read(buf)) != 1){
                        fos.write(buf, 0 , len);
                        sum = len+sum;
                        int progress = (int) (sum * 1.0f / total *100);
                        //下载中
                        listener.onDownloadProgress(progress);
                    }
                    fos.flush();
                    //下载完成
                    listener.onDownloadSuccess();
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onDownloadFailed();
                }finally {
                    if (is != null){
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (fos != null){
                            try {
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }






                listener.onDownloadSuccess();
            }
        });
    }
    public static String isExistDir(String saveDir) throws IOException {
        //下载位置
        File downloadFile = new File(Environment.getExternalStorageDirectory(),saveDir);
        if (!downloadFile.mkdirs()){
            downloadFile.createNewFile();
        }
        String savePath = downloadFile.getAbsolutePath();
        return savePath;
    }

    private String getNameFromUrl(String url){
        return url.substring(url.lastIndexOf("/")+1);
    }

    public interface OnDownloadListener{

        void onDownloadSuccess();

        void onDownloadProgress(int progress);

        void onDownloadFailed();
    }





}
