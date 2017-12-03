package com.pf.gdtest.util;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.pf.gdtest.ApplicationConfig;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

/**
 * @author zhaopf
 * @version 1.0
 * @QQ 1308108803
 * @date 2017/12/3
 * 全局工具类
 */
public class GlobalUtils {
    /**
     * 获取本地外存存储路径
     *
     * @param subPath 子路径 - 前后要加"/"
     * @return String
     */
    public static String getAppLocalFilePath(String subPath) {
        subPath = subPath == null ? "" : subPath;

        String rootPath = "";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            rootPath = Environment.getExternalStorageDirectory().toString();
        } else {
            rootPath = Environment.getDataDirectory().toString();
        }

        return rootPath + ApplicationConfig.FOLDER_NAME + subPath;
    }

    /**
     * 获取版本名字
     * <p>
     * 对应build.gradle中的versionName      <br>
     *
     * @param context
     * @return String
     */
    public static String getVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }


    /**
     * 返回版本号
     * <p>
     * 对应build.gradle中的versionCode   <br>
     *
     * @param context
     * @return String
     */
    public static String getVersionCode(Context context) {
        String versionCode = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionCode = String.valueOf(packInfo.versionCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionCode;
    }


    /**
     * 获取设备的唯一标识
     * <p>
     * 需要读取手机信息的权限      <br>
     *
     * @param context
     * @return String
     */
    public static String getDeviceId(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            // 没有权限
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "获取手机信息权限未开启", Toast.LENGTH_SHORT).show();
                return null;
            }
            String deviceId = tm.getDeviceId();
            if (deviceId == null) {
                return "";
            } else {
                return deviceId;
            }
        } catch (Exception e) {
            Toast.makeText(context, "获取手机信息权限未开启", Toast.LENGTH_SHORT).show();
            return "";
        }

    }

    /**
     * 获取手机品牌
     * <p>
     * 需要读取手机信息的权限    <br>
     *
     * @return String
     */
    public static String getPhoneBrand(Context context) {
        try {
            return android.os.Build.BRAND;
        } catch (Exception e) {
            Toast.makeText(context, "获取手机信息权限未开启", Toast.LENGTH_SHORT).show();
            return "";
        }
    }

    /**
     * 获取手机型号
     * <p>
     * 需要读取手机信息的权限    <br>
     *
     * @return String
     */
    public static String getPhoneModel(Context context) {
        try {
            return android.os.Build.MODEL;

        } catch (Exception e) {
            Toast.makeText(context, "获取手机信息权限未开启", Toast.LENGTH_SHORT).show();
            return "";
        }
    }

    /**
     * 获取手机Android API等级
     * <p>
     * 需要读取手机信息的权限      <br>
     *
     * @return String
     */
    public static int getBuildLevel(Context context) {
        try {
            return android.os.Build.VERSION.SDK_INT;
        } catch (Exception e) {
            Toast.makeText(context, "获取手机信息权限未开启", Toast.LENGTH_SHORT).show();
            return 0;
        }
    }

    /**
     * 获取手机Android 版本
     * <p>
     * 需要读取手机信息的权限      <br>
     *
     * @return String
     */
    public static String getBuildVersion(Context context) {
        try {
            return android.os.Build.VERSION.RELEASE;
        } catch (Exception e) {
            Toast.makeText(context, "获取手机信息权限未开启", Toast.LENGTH_SHORT).show();
            return "";
        }
    }

    /**
     * 获取当前App进程的id
     *
     * @return int
     */
    public static int getAppProcessId() {
        return android.os.Process.myPid();
    }

    /**
     * 获取当前App进程的Name
     *
     * @param context   上下文
     * @param processId 进程id
     * @return String
     */
    public static String getAppProcessName(Context context, int processId) {
        String processName = null;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        // 获取所有运行App的进程集合
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = context.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == processId) {
                    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));

                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                Log.e(GlobalUtils.class.getName(), e.getMessage(), e);
            }
        }
        return processName;
    }


    /**
     * 判断sd卡是否存在
     * <p>
     * 需要读取手机信息的权限      <br>
     *
     * @return true:存在；false：不存在
     */
    public static boolean isSdcardExisting(Context context) {
        try {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Toast.makeText(context, "获取手机信息权限未开启", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * 尝试打开wifi
     * <p>
     * 需要 'ACCESS_WIFI_STATE','CHANGE_WIFI_STATE' 的权限       <br>
     *
     * @param context
     * @param manager
     * @return boolean true：开启成功 ,false：开启失败
     */
    private static boolean tryOpenMAC(Context context, WifiManager manager) {
        boolean softOpenWifi = false;
        try {
            int state = manager.getWifiState();
            if (state != WifiManager.WIFI_STATE_ENABLED && state != WifiManager.WIFI_STATE_ENABLING) {
                manager.setWifiEnabled(true);
                softOpenWifi = true;
            }
        } catch (SecurityException s) {
            Toast.makeText(context, "请授予权限后继续操作", Toast.LENGTH_SHORT).show();
        }
        return softOpenWifi;
    }

    /**
     * 尝试关闭wifi
     * <p>
     * 需要 'ACCESS_WIFI_STATE','CHANGE_WIFI_STATE' 的权限     <br>
     *
     * @param context
     * @param manager
     * @return boolean true：关闭成功 ,false：关闭失败
     */
    private static void tryCloseMAC(Context context, WifiManager manager) {
        try {
            manager.setWifiEnabled(false);
        } catch (SecurityException e) {
            Toast.makeText(context, "请授予权限后继续操作", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取mac地址
     * <p>
     * 这方法在没有wifi的情况下获取不到     <br>
     *
     * @return String
     */
    public static String macAddress() {

        String address = "";
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            // 把当前机器上的访问网络接口的存入 Enumeration集合中
            while (interfaces.hasMoreElements()) {
                NetworkInterface netWork = interfaces.nextElement();
                // 如果存在硬件地址并可以使用给定的当前权限访问，则返回该硬件地址（通常是 MAC）。
                byte[] by = netWork.getHardwareAddress();
                if (by == null || by.length == 0) {
                    continue;
                }
                StringBuilder builder = new StringBuilder();
                for (byte b : by) {
                    builder.append(String.format("%02X:", b));
                }
                if (builder.length() > 0) {
                    builder.deleteCharAt(builder.length() - 1);
                }
                String mac = builder.toString();
                Log.e("pf", "interfaceName=" + netWork.getName() + ", mac=" + mac);
                // 从路由器上在线设备的MAC地址列表，可以印证设备Wifi的 name 是 wlan0
                if (netWork.getName().equals("wlan0")) {
                    Log.e("pf", "interfaceName =" + netWork.getName() + ", mac=" + mac);
                    address = mac;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return address;
    }

    /**
     * 获取mac地址
     * <p>
     * 在没有连接wifi的情况下通过打开wifi来获取     <br>
     *
     * @param internal 获取失败的再次获取，减少获取失败的概率
     * @param context  上下文
     * @return String
     */
    public static String getMacFromDevice(int internal, Context context) {
        String mac = "";
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mac = macAddress();
        if (!StringUtil.isEmpty(mac)) {
            return mac;
        }

        //获取失败，尝试打开wifi获取
        boolean isOkWifi = tryOpenMAC(context, wifiManager);
        for (int index = 0; index < internal; index++) {
            //如果第一次没有成功，第二次做100毫秒的延迟。
            if (index != 0) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            mac = macAddress();
            if (!StringUtil.isEmpty(mac)) {
                break;
            }
        }

        //尝试关闭wifi
        if (isOkWifi) {
            tryCloseMAC(context, wifiManager);
        }
        return mac;
    }
}