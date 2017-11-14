package com.example.administrator.classcircle.entity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/28 0028.
 */

public class JsonNotice {


    /**
     * data : {"uploadName":"a001","updatedAt":"2017-09-28 14:49:55","uploadContent":"嘎嘎嘎就看看姐姐","objectId":"13a152c2b4","createdAt":"2017-09-28 14:49:55","uploadOther":{"group":"","filename":"IMG_20170920_171802.jpg","url":"http://bmob-cdn-14149.b0.upaiyun.com/2017/09/28/5b1981b2216a4918bc22c9794bfc64d8.jpg"}}
     * action : updateTable
     * objectId :
     * tableName : Notice
     * appKey : 367b261b079c03625a6f45aa2815be7d
     */

    public DataBean data;
    public String action;
    public String objectId;
    public String tableName;
    public String appKey;

    public static JsonNotice objectFromData(String str) {

        return new Gson().fromJson(str, JsonNotice.class);
    }

    public static JsonNotice objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), JsonNotice.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<JsonNotice> arrayJsonNoticeFromData(String str) {

        Type listType = new TypeToken<ArrayList<JsonNotice>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<JsonNotice> arrayJsonNoticeFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<JsonNotice>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }

    public static class DataBean {
        /**
         * uploadName : a001
         * updatedAt : 2017-09-28 14:49:55
         * uploadContent : 嘎嘎嘎就看看姐姐
         * objectId : 13a152c2b4
         * createdAt : 2017-09-28 14:49:55
         * uploadOther : {"group":"","filename":"IMG_20170920_171802.jpg","url":"http://bmob-cdn-14149.b0.upaiyun.com/2017/09/28/5b1981b2216a4918bc22c9794bfc64d8.jpg"}
         */

        public String uploadName;
        public String updatedAt;
        public String uploadContent;
        public String objectId;
        public String createdAt;
        public UploadOtherBean uploadOther;

        public static DataBean objectFromData(String str) {

            return new Gson().fromJson(str, DataBean.class);
        }

        public static DataBean objectFromData(String str, String key) {

            try {
                JSONObject jsonObject = new JSONObject(str);

                return new Gson().fromJson(jsonObject.getString(str), DataBean.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        public static List<DataBean> arrayDataBeanFromData(String str) {

            Type listType = new TypeToken<ArrayList<DataBean>>() {
            }.getType();

            return new Gson().fromJson(str, listType);
        }

        public static List<DataBean> arrayDataBeanFromData(String str, String key) {

            try {
                JSONObject jsonObject = new JSONObject(str);
                Type listType = new TypeToken<ArrayList<DataBean>>() {
                }.getType();

                return new Gson().fromJson(jsonObject.getString(str), listType);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return new ArrayList();


        }

        public static class UploadOtherBean {
            /**
             * group :
             * filename : IMG_20170920_171802.jpg
             * url : http://bmob-cdn-14149.b0.upaiyun.com/2017/09/28/5b1981b2216a4918bc22c9794bfc64d8.jpg
             */

            public String group;
            public String filename;
            public String url;

            public static UploadOtherBean objectFromData(String str) {

                return new Gson().fromJson(str, UploadOtherBean.class);
            }

            public static UploadOtherBean objectFromData(String str, String key) {

                try {
                    JSONObject jsonObject = new JSONObject(str);

                    return new Gson().fromJson(jsonObject.getString(str), UploadOtherBean.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            public static List<UploadOtherBean> arrayUploadOtherBeanFromData(String str) {

                Type listType = new TypeToken<ArrayList<UploadOtherBean>>() {
                }.getType();

                return new Gson().fromJson(str, listType);
            }

            public static List<UploadOtherBean> arrayUploadOtherBeanFromData(String str, String key) {

                try {
                    JSONObject jsonObject = new JSONObject(str);
                    Type listType = new TypeToken<ArrayList<UploadOtherBean>>() {
                    }.getType();

                    return new Gson().fromJson(jsonObject.getString(str), listType);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return new ArrayList();


            }
        }
    }
}
