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

public class JsonData {

    /**
     * data : {"updatedAt":"2017-09-28 15:11:34","uploadFile":{"group":"","filename":"IMG_20170920_171850.jpg","url":"http://bmob-cdn-14149.b0.upaiyun.com/2017/09/28/13cf8fe1e64f49d2a0cc7cda0c8b1ab9.jpg"},"objectId":"31851069e6","createdAt":"2017-09-28 15:11:34","name":"a001"}
     * action : updateTable
     * objectId :
     * tableName : Data
     * appKey : 367b261b079c03625a6f45aa2815be7d
     */

    public DataBean data;
    public String action;
    public String objectId;
    public String tableName;
    public String appKey;

    public static JsonData objectFromData(String str) {

        return new Gson().fromJson(str, JsonData.class);
    }

    public static JsonData objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), JsonData.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<JsonData> arrayJsonDataFromData(String str) {

        Type listType = new TypeToken<ArrayList<JsonData>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<JsonData> arrayJsonDataFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<JsonData>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }

    public static class DataBean {
        /**
         * updatedAt : 2017-09-28 15:11:34
         * uploadFile : {"group":"","filename":"IMG_20170920_171850.jpg","url":"http://bmob-cdn-14149.b0.upaiyun.com/2017/09/28/13cf8fe1e64f49d2a0cc7cda0c8b1ab9.jpg"}
         * objectId : 31851069e6
         * createdAt : 2017-09-28 15:11:34
         * name : a001
         */

        public String updatedAt;
        public UploadFileBean uploadFile;
        public String objectId;
        public String createdAt;
        public String name;

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

        public static class UploadFileBean {
            /**
             * group :
             * filename : IMG_20170920_171850.jpg
             * url : http://bmob-cdn-14149.b0.upaiyun.com/2017/09/28/13cf8fe1e64f49d2a0cc7cda0c8b1ab9.jpg
             */

            public String group;
            public String filename;
            public String url;

            public static UploadFileBean objectFromData(String str) {

                return new Gson().fromJson(str, UploadFileBean.class);
            }

            public static UploadFileBean objectFromData(String str, String key) {

                try {
                    JSONObject jsonObject = new JSONObject(str);

                    return new Gson().fromJson(jsonObject.getString(str), UploadFileBean.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            public static List<UploadFileBean> arrayUploadFileBeanFromData(String str) {

                Type listType = new TypeToken<ArrayList<UploadFileBean>>() {
                }.getType();

                return new Gson().fromJson(str, listType);
            }

            public static List<UploadFileBean> arrayUploadFileBeanFromData(String str, String key) {

                try {
                    JSONObject jsonObject = new JSONObject(str);
                    Type listType = new TypeToken<ArrayList<UploadFileBean>>() {
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
