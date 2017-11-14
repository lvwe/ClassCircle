package com.example.administrator.classcircle.utils;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.example.administrator.classcircle.C;

/**
 * Created by Administrator on 2017/10/1 0001.
 */

public class NetworkUtil {

    public static boolean isNetworkConnected(Context context){
        if (context != null){
            ConnectivityManager connectivityManager = (ConnectivityManager) context.
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null){
                return networkInfo.isAvailable();
            }
        }
        return false;
    }

    public static boolean isWifiConnected(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = manager.getActiveNetworkInfo();
        if (wifiNetworkInfo != null && wifiNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI){
            return wifiNetworkInfo.isAvailable();
        }
        return false;
    }

    public static boolean isMobileConnect(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileNetworkInfo = manager.getActiveNetworkInfo();
        if (mobileNetworkInfo != null && mobileNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE){
            return mobileNetworkInfo.isAvailable();
        }
        return false;
    }

    /**
     *
     * @param context
     * @return  NO_MOBILE_NETWORK
     * 0 NO_MOBILE_NETWORK
     * 1 wifi
     * 2 2g
     * 3 3g
     * 4 4g
     */
    public static int getAPNType(Context context){
        int networkType = 0;
        ConnectivityManager manager = (ConnectivityManager) context.
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo == null){
            return C.NO_MOBILE_NETWORK;
        }else {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI){
                return 1;
            }else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE){
                int subNetwork = networkInfo.getSubtype();
                TelephonyManager telephonyManager = (TelephonyManager) context.
                        getSystemService(Context.TELEPHONY_SERVICE);
                if (subNetwork == TelephonyManager.NETWORK_TYPE_LTE
                        && !telephonyManager.isNetworkRoaming()) {
                    networkType = 4;
                    //3G   联通的3G为UMTS或HSDPA 电信的3G为EVDO
                } else if (subNetwork == TelephonyManager.NETWORK_TYPE_UMTS
                        || subNetwork == TelephonyManager.NETWORK_TYPE_HSDPA
                        || subNetwork == TelephonyManager.NETWORK_TYPE_EVDO_0
                        && !telephonyManager.isNetworkRoaming()) {
                    networkType = 3;
                    //2G 移动和联通的2G为GPRS或EGDE，电信的2G为CDMA
                } else if (subNetwork == TelephonyManager.NETWORK_TYPE_GPRS
                        || subNetwork == TelephonyManager.NETWORK_TYPE_EDGE
                        || subNetwork == TelephonyManager.NETWORK_TYPE_CDMA
                        && !telephonyManager.isNetworkRoaming()) {
                    networkType = 2;
                } else {
                    networkType = 2;
                }
            }
        }
        return networkType;
    }

    /**
     *
     * @param context
     * @return 判断GPS是否打开
     */
    public static boolean isGPSEnable(Context context){
        LocationManager locationManager = (LocationManager) context.
                getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}
