package com.example.administrator.classcircle.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.example.administrator.classcircle.R;
import com.example.administrator.classcircle.entity.FileInfo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/6 0006.
 */

public class ShowFileUtils {
    private static final String TAG = ShowFileUtils.class.getSimpleName();

    public static final int TYPE_APK = 1;
    public static final int TYPE_JPEG = 2;
    public static final int TYPE_MP3 = 3;
    public static final int TYPE_MP4 = 4;

    public static final String DEFAULT_ROOT_PATH = "/mnt/download/kuaichuan/";
    public static final String DEFAULT_SCREENSHOT_PATH = "/mnt/kc_screenshot";

    public static final DecimalFormat FORMAT = new DecimalFormat("####.##");
    public static final DecimalFormat FORMAT_ONE = new DecimalFormat("####.#");

    /**
     * 储存卡获取 指定文件
     * @param context
     * @param extension
     * @return
     */
    public static List<FileInfo> getSpecificTypeFiles(Context context, String[] extension) {
        List<FileInfo> fileInfoList = new ArrayList<FileInfo>();
        //内存卡文件的uri  三种方法
//        Uri uri1 = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//        Uri uri2 = MediaStore.Images.Media.getContentUri("external");
//        Uri uri3 = Uri.parse("content://media/external/images/media");
        Uri fileUri = MediaStore.Files.getContentUri("external");
        //筛选列
        String[] projection = new String[]{
                MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.TITLE
        };
        //筛选条件 语句
        String selection = "";
        for (int i = 0; i < extension.length; i++) {
            if (i != 0) {
                selection = selection + " OR ";
            }
            selection = selection + MediaStore.Files.FileColumns.DATA + " LIKE '%" + extension[i] + "'";
            Log.d(TAG, "getSpecificTypeFiles: sql-- " + selection);
        }
        //排序
        String sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED;
        Cursor cursor = context.getContentResolver().query(fileUri, projection, selection, null, sortOrder);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                try {
                    String data = cursor.getString(0);
                    FileInfo fileInfo = new FileInfo();
                    fileInfo.setFilePath(data);
                    long size = 0;
                    try {
                        File file = new File(data);
                        size = file.length();
                        fileInfo.setSize(size);
                    } catch (Exception e) {
                    }
                    fileInfoList.add(fileInfo);
                    Log.d(TAG, "getSpecificTypeFiles: ---fileInfo " + fileInfo.getFilePath());
                } catch (Exception e) {
                    Log.d(TAG, "getSpecificTypeFiles: ---- " + e.getMessage());
                }
            }
        }
        return fileInfoList;
    }

    /**
     * 查找指定文件名的文件
     *
     * @param context
     * @param fileName
     * @return
     */
    public static FileInfo getFileInfo(Context context, String fileName) {
        List<FileInfo> fileInfoList = getSpecificTypeFiles(context, new String[]{fileName});
        if (fileInfoList == null && fileInfoList.size() == 0) {
            return null;
        }
        return fileInfoList.get(0);
    }

    /**
     * 转化完整信息的 FileInfo
     *
     * @param context
     * @param fileInfoList
     * @param type
     * @return
     */
    public static List<FileInfo> getDetailFileInfo(Context context, List<FileInfo> fileInfoList, int type) {
        if (fileInfoList == null && fileInfoList.size() <= 0) {
            return fileInfoList;
        }
        for (FileInfo fileInfo : fileInfoList) {
            if (fileInfo != null) {
                fileInfo.setName(getFileNames(fileInfo.getFilePath()));
                fileInfo.setSizeDesc(getFileSize(fileInfo.getSize()));
                if (type == FileInfo.TYPE_APK) {
                    fileInfo.setBitmap(ShowFileUtils.drawableToBitmap(
                            ShowFileUtils.getApkThumbnail(context,fileInfo.getFilePath())));
                }else if (type == FileInfo.TYPE_MP4){
                    fileInfo.setBitmap(ShowFileUtils.
                            getScreenshotBitmap(context,fileInfo.getFilePath(),FileInfo.TYPE_MP4));
                }else if (type == FileInfo.TYPE_MP3){
                    //MP3 不需要缩略图
                }else if (type == FileInfo.TYPE_JPG){
                    //由Glide图片加载框架加载
                }
                fileInfo.setFileType(type);
            }
        }
        return fileInfoList;
    }

    /**
     * 根据文件路径获取文件名称
     *
     * @param filePath
     * @return
     */
    public static String getFileNames(String filePath) {
        if (filePath == null || filePath.equals("")) {
            return "";
        }
        return filePath.substring(filePath.lastIndexOf("/") + 1);
    }

    public static String getFileSize(long size) {
        //字节数转化为KB、MB、GB
        if (size < 0) {
            return "0B";
        }
        double value = 0f;
        if ((size / 1024) < 1) {
            return size + "B";
        } else if ((size / (1024 * 1024)) < 1) {
            value = size / 1024f;
            return FORMAT.format(value) + "KB";
        } else if ((size / (1024 * 1024 * 1024)) < 1) {
            value = (size * 100 / (1024 * 1024)) / 100f;
            return FORMAT.format(value) + "MB";
        } else {
            value = (size * 1001 / (10241 * 10241 * 10241)) / 100f;
            return FORMAT.format(value) + "GB";
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        //获取drawable的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        //取drawable的颜色格式
        Bitmap.Config config = drawable.
                getOpacity() != PixelFormat.OPAQUE ? Bitmap.
                Config.ARGB_8888 : Bitmap.Config.RGB_565;
        //建立相应的bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        //建立对应的bitmap画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        //将drawable内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public static boolean bitmapToSDCard(Bitmap bitmap, String resPath) {
        if (bitmap == null) {
            return false;
        }
        File resFile = new File(resPath);
        try {
            FileOutputStream fos = new FileOutputStream(resFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Bitmap compressBitmap(Bitmap srcBitmap, int maxKByteCount) {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            srcBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            int option = 98;
            while (baos.toByteArray().length / 1024 >= maxKByteCount && option > 0) {
                baos.reset();
                srcBitmap.compress(Bitmap.CompressFormat.PNG, option, baos);
                option -= 2;
            }

        } catch (Exception e) {

        }
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(bais, null, null);
        return bitmap;
    }

    public static boolean compressBitmap(Bitmap srcBitmap, int maxKByteCount, String targetPath) {
        boolean result = false;
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            srcBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            int option = 98;
            while (baos.toByteArray().length / 1024 >= maxKByteCount && option > 0) {
                baos.reset();
                srcBitmap.compress(Bitmap.CompressFormat.PNG, option, baos);
                option -= 2;
            }
            byte[] bitmapByte = baos.toByteArray();
            File targetFile = new File(targetPath);
            if (!targetFile.exists()) {
                targetFile.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(targetFile);
            fos.write(bitmapByte);
            result = true;
            try {
                fos.close();
                baos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!srcBitmap.isRecycled()){
                srcBitmap.recycle();
                srcBitmap = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Drawable getApkThumbnail(Context context, String apkPath) {
        if (context == null) {
            return null;
        }
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            //获取apk图片
            applicationInfo.sourceDir = apkPath;
            applicationInfo.publicSourceDir = apkPath;
            if (applicationInfo != null) {
                Drawable apkIcon = applicationInfo.loadIcon(packageManager);
                return apkIcon;
            }
        } catch (Exception e) {

        }
        return null;
    }

    public static Bitmap getScreenshotBitmap(Context context, String filePath, int type){
        Bitmap bitmap = null;
        switch (type){
            case TYPE_APK:
                Drawable drawable = getApkThumbnail(context,filePath);
                if (drawable != null){
                    bitmap = drawableToBitmap(drawable);
                }else {
                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.apk);
                }
                break;
            case TYPE_JPEG:
                try {
                    bitmap = BitmapFactory.decodeStream(new FileInputStream(new File(filePath)));
                }catch (FileNotFoundException e){
                    bitmap = BitmapFactory.decodeResource(context.getResources(),R.mipmap.jpg);
                }
                break;
            case TYPE_MP3:
                bitmap = BitmapFactory.decodeResource(context.getResources(),R.mipmap.mp3);
                break;
            case TYPE_MP4:
                try {
                    bitmap  = ScreenshotUtils.createVideoThumbnail(filePath);
                }catch (Exception e){
                    bitmap = BitmapFactory.decodeResource(context.getResources(),R.mipmap.mp4);
                }
                bitmap = ScreenshotUtils.extractThumbnail(bitmap,100, 100);
                break;
        }
        return bitmap;
    }


    public static boolean isApkFile(String filePath) {
        if(filePath == null || filePath.equals("")){
            return false;
        }
        if(filePath.lastIndexOf(FileInfo.EXTEND_APK) > 0){
            return true;
        }
        return false;
    }

    public static boolean isJpgFile(String filePath) {
        if(filePath == null || filePath.equals("")){
            return false;
        }
        if(filePath.lastIndexOf(FileInfo.EXTEND_JPG) > 0 || filePath.lastIndexOf(FileInfo.EXTEND_JPEG) > 0){
            return true;
        }
        return false;
    }

    public static boolean isMp4File(String filePath) {
        if(filePath == null || filePath.equals("")){
            return false;
        }
        if(filePath.lastIndexOf(FileInfo.EXTEND_MP4) > 0){
            return true;
        }
        return false;
    }

    public static boolean isMp3File(String filePath) {
        if(filePath == null || filePath.equals("")){
            return false;
        }
        if(filePath.lastIndexOf(FileInfo.EXTEND_MP3) > 0){
            return true;
        }
        return false;
    }

    /**
     *   uri 转 path
     *   访问资源的命名机制。
         存放资源的主机名。
         资源自身的名称，由路径表示。

     Android的Uri由以下三部分组成： "content://"、数据的路径、标示ID(可选)
     如：
        所有联系人的Uri： content://contacts/people
        某个联系人的Uri: content://contacts/people/5
        所有图片Uri: content://media/external
        某个图片的Uri：content://media/external/images/media/4
     * @param context
     * @param uri
     * @return
     */
    public static String getPath(Context context, Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;
            try {
//                ContentResolver直译为内容解析器，
//                Android中程序间数据的共享是通过Provider/Resolver进行的。
//                提供数据（内容）的就叫Provider，Resovler提供接口对这个内容进行解读。
//                第二个参数，projection，这个参数告诉Provider要返回的内容（列Column）
//                第三个参数，selection，设置条件，相当于SQL语句中的where。null表示不进行筛选
//                第四个参数，selectionArgs，要配合第三个参数使用，如果你在第三个参数里面有？，那么你在selectionArgs写的数据就会替换掉
//                第五个参数，sortOrder，按照什么进行排序，相当于SQL语句中的Order by。
                cursor = context.getContentResolver().query(uri, projection,null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        file:///storage/emulated/0/3296887429.mp4
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static long getFileSizeSe(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
            Log.e("获取文件大小", "文件不存在!");
        }
        return size;
    }
}
