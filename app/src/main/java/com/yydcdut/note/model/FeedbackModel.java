package com.yydcdut.note.model;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.yydcdut.note.NoteApplication;
import com.yydcdut.note.utils.LocalStorageUtils;
import com.yydcdut.note.utils.YLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by yuyidong on 15/11/3.
 */
public class FeedbackModel {
    private Context mContext = null;

    private FeedbackModel() {
        mContext = NoteApplication.getContext();
    }

    private static FeedbackModel sInstance;

    public synchronized static FeedbackModel getInstance() {
        if (sInstance == null) {
            sInstance = new FeedbackModel();
        }
        return sInstance;
    }


    public synchronized Map sendFeedback(String feedback_id, String content) {
        JSONObject json = null;
        try {
            JSONObject totalInfoJson = getdeviceInfo(this.mContext);
            totalInfoJson.put("content", content);
            totalInfoJson.put("feedback_id", feedback_id);
            totalInfoJson.put("reply_id", System.currentTimeMillis() + "");
            totalInfoJson.put("device_uuid", LocalStorageUtils.getInstance().getDeviceUuid());
            totalInfoJson.put("type", "new_feedback");
            json = httpConnection(totalInfoJson, "http://fb.umeng.com/api/v2/feedback/reply/new");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this.setData(json);
    }

    private JSONObject httpConnection(JSONObject infoJosn, String url) throws IOException {
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) (new URL(url)).openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setReadTimeout(10000);
            String deviceInfo = infoJosn.toString();
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(httpURLConnection.getOutputStream());
            bufferedOutputStream.write(deviceInfo.getBytes());
            bufferedOutputStream.flush();
            if (httpURLConnection.getResponseCode() != 200) {
                return null;
            }
            JSONObject responseJson = getResponse(httpURLConnection.getInputStream());
            httpURLConnection.disconnect();
            return responseJson;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
        return null;
    }

    private JSONObject getResponse(InputStream inputStream) throws IOException, JSONException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();

        String buffer;
        while ((buffer = bufferedReader.readLine()) != null) {
            sb.append(buffer);
        }

        bufferedReader.close();
        return new JSONObject(sb.toString());
    }

    private Map<String, String> setData(JSONObject json) {
        HashMap map = new HashMap();
        try {
            if (this.judgeStatus(json)) {
                String feedback_id = json.getJSONObject("data").getString("feedback_id");
                long created_id = json.getJSONObject("data").getLong("created_at");
                map.put("feedback_id", feedback_id);
                map.put("created_at", Long.valueOf(created_id));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

    private boolean judgeStatus(JSONObject json) {
        if (json == null) {
            return false;
        } else {
            try {
                String status = json.getString("status");
                if (status != null && status.equals("200")) {
                    return true;
                }
            } catch (JSONException var3) {
                var3.printStackTrace();
            }
            return false;
        }
    }

    public JSONObject getdeviceInfo(Context var0) {
        JSONObject var1 = new JSONObject();
        try {
            var1.put("device_id", ModelUtils.f(var0));
            var1.put("device_model", Build.MODEL);
            var1.put("appkey", ModelUtils.p(var0));
            var1.put("channel", ModelUtils.t(var0));
            var1.put("app_version", ModelUtils.d(var0));
            var1.put("version_code", ModelUtils.c(var0));
            var1.put("sdk_type", "Android");
            var1.put("sdk_version", "5.4.0.20150727");
            var1.put("os", "Android");
            var1.put("os_version", Build.VERSION.RELEASE);
            var1.put("country", ModelUtils.o(var0)[0]);
            var1.put("language", ModelUtils.o(var0)[1]);
            var1.put("timezone", ModelUtils.n(var0));
            var1.put("resolution", ModelUtils.r(var0));
            var1.put("access", ModelUtils.j(var0)[0]);
            var1.put("access_subtype", ModelUtils.j(var0)[1]);
            var1.put("carrier", ModelUtils.h(var0));
            var1.put("cpu", ModelUtils.a());
            var1.put("package", ModelUtils.u(var0));
            var1.put("uid", LocalStorageUtils.getInstance().getUmengUid());
            var1.put("mac", ModelUtils.q(var0));
            var1.put("protocol_version", "2.0");
            return var1;
        } catch (Exception var3) {
            var3.printStackTrace();
            return var1;
        }
    }

    private String getUmengUID() {
        try {
            JSONObject deviceInfo = getdeviceInfo(this.mContext);
            StringBuilder sb = new StringBuilder("http://fb.umeng.com/api/v2/user/getuid");
            sb.append("?");
            Iterator iterator = deviceInfo.keys();
            String uid;
            while (iterator.hasNext()) {
                String entry = (String) iterator.next();
                uid = deviceInfo.get(entry).toString();
                sb.append(URLEncoder.encode(entry, "UTF-8") + "=" + URLEncoder.encode(uid, "UTF-8") + "&");
            }

            if (38 == sb.charAt(sb.length() - 1)) {
                sb.deleteCharAt(sb.length() - 1);
            }

            JSONObject json = httpConnection(sb.toString());
            if (this.judgeStatus(json)) {
                uid = json.getJSONObject("data").getString("uid");
                LocalStorageUtils.getInstance().setUmengUid(uid);
                return uid;
            }
        } catch (Exception var6) {
            var6.printStackTrace();
        }
        return "";
    }

    private JSONObject httpConnection(String url) throws IOException {
        if (TextUtils.isEmpty(LocalStorageUtils.getInstance().getUmengUid())) {
            getUmengUID();
        }
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) (new URL(url)).openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setReadTimeout(10000);
            if (httpURLConnection.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + httpURLConnection.getResponseCode());
            }
            JSONObject json = getResponse(httpURLConnection.getInputStream());
            httpURLConnection.disconnect();
            return json;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    static class ModelUtils {
        protected static final String TAG = ModelUtils.class.getName();

        public ModelUtils() {
        }

        public static boolean a(String var0, Context var1) {
            PackageManager var2 = var1.getPackageManager();
            boolean var3 = false;

            try {
                var2.getPackageInfo(var0, 1);
                var3 = true;
            } catch (PackageManager.NameNotFoundException var5) {
                var3 = false;
            }

            return var3;
        }

        public static boolean a(Context var0) {
            Locale var1 = var0.getResources().getConfiguration().locale;
            return var1.toString().equals(Locale.CHINA.toString());
        }

        public static boolean b(Context var0) {
            return var0.getResources().getConfiguration().orientation == 1;
        }

        public static String c(Context var0) {
            try {
                PackageInfo var1 = var0.getPackageManager().getPackageInfo(var0.getPackageName(), 0);
                int var2 = var1.versionCode;
                return String.valueOf(var2);
            } catch (PackageManager.NameNotFoundException var3) {
                return "Unknown";
            }
        }

        public static String d(Context var0) {
            try {
                PackageInfo var1 = var0.getPackageManager().getPackageInfo(var0.getPackageName(), 0);
                return var1.versionName;
            } catch (PackageManager.NameNotFoundException var2) {
                return "Unknown";
            }
        }

        public static boolean a(Context var0, String var1) {
            PackageManager var2 = var0.getPackageManager();
            return var2.checkPermission(var1, var0.getPackageName()) == 0;
        }

        public static String e(Context var0) {
            PackageManager var1 = var0.getPackageManager();

            ApplicationInfo var2;
            try {
                var2 = var1.getApplicationInfo(var0.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException var4) {
                var2 = null;
            }

            String var3 = (String) ((String) (var2 != null ? var1.getApplicationLabel(var2) : ""));
            return var3;
        }

        public static String[] a(GL10 var0) {
            try {
                String[] var1 = new String[2];
                String var2 = var0.glGetString(7936);
                String var3 = var0.glGetString(7937);
                var1[0] = var2;
                var1[1] = var3;
                return var1;
            } catch (Exception var4) {
                YLog.e(TAG, "Could not read gpu infor:--->" + var4);
                return new String[0];
            }
        }

        public static String a() {
            String var0 = null;
            FileReader var1 = null;
            BufferedReader var2 = null;

            try {
                var1 = new FileReader("/proc/cpuinfo");
                if (var1 != null) {
                    try {
                        var2 = new BufferedReader(var1, 1024);
                        var0 = var2.readLine();
                        var2.close();
                        var1.close();
                    } catch (IOException var4) {
                        YLog.e(TAG, "Could not read from file /proc/cpuinfo--->" + var4);
                    }
                }
            } catch (FileNotFoundException var5) {
                YLog.e(TAG, "Could not open file /proc/cpuinfo--->" + var5);
            }

            if (var0 != null) {
                int var3 = var0.indexOf(58) + 1;
                var0 = var0.substring(var3);
            }

            return var0.trim();
        }

        public static String f(Context var0) {
            TelephonyManager var1 = (TelephonyManager) var0.getSystemService(Context.TELEPHONY_SERVICE);
            if (var1 == null) {
                YLog.e(TAG, "No IMEI.");
            }

            String var2 = "";

            try {
                if (a(var0, "android.permission.READ_PHONE_STATE")) {
                    var2 = var1.getDeviceId();
                }
            } catch (Exception var4) {
                YLog.e(TAG, "No IMEI.--->" + var4);
            }

            if (TextUtils.isEmpty(var2)) {
                YLog.e(TAG, "No IMEI.");
                var2 = q(var0);
                if (TextUtils.isEmpty(var2)) {
                    YLog.e(TAG, "Failed to take mac as IMEI. Try to use Secure.ANDROID_ID instead.");
                    var2 = Settings.Secure.getString(var0.getContentResolver(), "android_id");
                    YLog.e(TAG, "getDeviceId: Secure.ANDROID_ID: " + var2);
                    return var2;
                }
            }

            return var2;
        }

        public static String h(Context var0) {
            try {
                TelephonyManager var1 = (TelephonyManager) var0.getSystemService(Context.TELEPHONY_SERVICE);
                return var1 == null ? "Unknown" : var1.getNetworkOperatorName();
            } catch (Exception var2) {
                var2.printStackTrace();
                return "Unknown";
            }
        }

        public static String i(Context var0) {
            try {
                DisplayMetrics var1 = new DisplayMetrics();
                WindowManager var2 = (WindowManager) ((WindowManager) var0.getSystemService(Context.WINDOW_SERVICE));
                var2.getDefaultDisplay().getMetrics(var1);
                int var3 = var1.widthPixels;
                int var4 = var1.heightPixels;
                String var5 = var4 + "*" + var3;
                return var5;
            } catch (Exception var6) {
                var6.printStackTrace();
                return "Unknown";
            }
        }

        public static String[] j(Context var0) {
            String[] var1 = new String[]{"Unknown", "Unknown"};

            try {
                PackageManager var2 = var0.getPackageManager();
                if (var2.checkPermission("android.permission.ACCESS_NETWORK_STATE", var0.getPackageName()) != 0) {
                    var1[0] = "Unknown";
                    return var1;
                }

                ConnectivityManager var3 = (ConnectivityManager) var0.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (var3 == null) {
                    var1[0] = "Unknown";
                    return var1;
                }

                NetworkInfo var4 = var3.getNetworkInfo(1);
                if (var4.getState() == NetworkInfo.State.CONNECTED) {
                    var1[0] = "Wi-Fi";
                    return var1;
                }

                NetworkInfo var5 = var3.getNetworkInfo(0);
                if (var5.getState() == NetworkInfo.State.CONNECTED) {
                    var1[0] = "2G/3G";
                    var1[1] = var5.getSubtypeName();
                    return var1;
                }
            } catch (Exception var6) {
                var6.printStackTrace();
            }

            return var1;
        }

        public static boolean k(Context var0) {
            return "Wi-Fi".equals(j(var0)[0]);
        }

        public static Location l(Context var0) {
            return null;
        }

        public static boolean m(Context var0) {
            try {
                ConnectivityManager var1 = (ConnectivityManager) var0.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo var2 = var1.getActiveNetworkInfo();
                return var2 != null ? var2.isConnectedOrConnecting() : false;
            } catch (Exception var3) {
                return true;
            }
        }

        public static boolean b() {
            return Environment.getExternalStorageState().equals("mounted");
        }

        public static int n(Context var0) {
            try {
                Locale var1 = x(var0);
                Calendar var2 = Calendar.getInstance(var1);
                if (var2 != null) {
                    return var2.getTimeZone().getRawOffset() / 3600000;
                }
            } catch (Exception var3) {
                YLog.e(TAG, "error in getTimeZone--->" + var3);
            }

            return 8;
        }

        public static String[] o(Context var0) {
            String[] var1 = new String[2];

            try {
                Locale var2 = x(var0);
                if (var2 != null) {
                    var1[0] = var2.getCountry();
                    var1[1] = var2.getLanguage();
                }

                if (TextUtils.isEmpty(var1[0])) {
                    var1[0] = "Unknown";
                }

                if (TextUtils.isEmpty(var1[1])) {
                    var1[1] = "Unknown";
                }

                return var1;
            } catch (Exception var3) {
                YLog.e(TAG, "error in getLocaleInfo--->" + var3);
                return var1;
            }
        }

        private static Locale x(Context var0) {
            Locale var1 = null;

            try {
                Configuration var2 = new Configuration();
                Settings.System.getConfiguration(var0.getContentResolver(), var2);
                if (var2 != null) {
                    var1 = var2.locale;
                }
            } catch (Exception var3) {
                YLog.e(TAG, "fail to read user config locale");
            }

            if (var1 == null) {
                var1 = Locale.getDefault();
            }

            return var1;
        }

        public static String p(Context var0) {
            Object var1 = null;

            try {
                PackageManager var2 = var0.getPackageManager();
                ApplicationInfo var3 = var2.getApplicationInfo(var0.getPackageName(), 128);
                if (var3 != null) {
                    String var4 = var3.metaData.getString("UMENG_APPKEY");
                    if (var4 != null) {
                        return var4.trim();
                    }

                    YLog.e(TAG, "Could not read UMENG_APPKEY meta-data from AndroidManifest.xml.");
                }
            } catch (Exception var5) {
                YLog.e(TAG, "Could not read UMENG_APPKEY meta-data from AndroidManifest.xml.--->" + var5);
            }

            return null;
        }

        public static String q(Context var0) {
            try {
                WifiManager var1 = (WifiManager) var0.getSystemService(Context.WIFI_SERVICE);
                if (a(var0, "android.permission.ACCESS_WIFI_STATE")) {
                    WifiInfo var2 = var1.getConnectionInfo();
                    return var2.getMacAddress();
                }

                YLog.e(TAG, "Could not get mac address.[no permission android.permission.ACCESS_WIFI_STATE");
            } catch (Exception var3) {
                YLog.e(TAG, "Could not get mac address." + var3.toString());
            }

            return "";
        }

        public static String r(Context var0) {
            try {
                DisplayMetrics var1 = new DisplayMetrics();
                WindowManager var2 = (WindowManager) ((WindowManager) var0.getSystemService(Context.WINDOW_SERVICE));
                var2.getDefaultDisplay().getMetrics(var1);
                int var3 = -1;
                int var4 = -1;
                if ((var0.getApplicationInfo().flags & 8192) == 0) {
                    var3 = a((Object) var1, (String) "noncompatWidthPixels");
                    var4 = a((Object) var1, (String) "noncompatHeightPixels");
                }

                if (var3 == -1 || var4 == -1) {
                    var3 = var1.widthPixels;
                    var4 = var1.heightPixels;
                }

                StringBuffer var5 = new StringBuffer();
                var5.append(var3);
                var5.append("*");
                var5.append(var4);
                return var5.toString();
            } catch (Exception var6) {
                YLog.e(TAG, "read resolution fail--->" + var6);
                return "Unknown";
            }
        }

        private static int a(Object var0, String var1) {
            try {
                Field var2 = DisplayMetrics.class.getDeclaredField(var1);
                var2.setAccessible(true);
                return var2.getInt(var0);
            } catch (Exception var3) {
                var3.printStackTrace();
                return -1;
            }
        }

        public static String s(Context var0) {
            try {
                return ((TelephonyManager) var0.getSystemService(Context.TELEPHONY_SERVICE)).getNetworkOperatorName();
            } catch (Exception var2) {
                YLog.e(TAG, "read carrier fail--->" + var2);
                return "Unknown";
            }
        }

        public static String a(Date var0) {
            SimpleDateFormat var1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            String var2 = var1.format(var0);
            return var2;
        }

        public static String c() {
            Date var0 = new Date();
            SimpleDateFormat var1 = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            String var2 = var1.format(var0);
            return var2;
        }

        public static Date a(String var0) {
            try {
                SimpleDateFormat var1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                Date var2 = var1.parse(var0);
                return var2;
            } catch (Exception var3) {
                return null;
            }
        }

        public static int a(Date var0, Date var1) {
            if (var0.after(var1)) {
                Date var2 = var0;
                var0 = var1;
                var1 = var2;
            }

            long var8 = var0.getTime();
            long var4 = var1.getTime();
            long var6 = var4 - var8;
            return (int) (var6 / 1000L);
        }

        public static String t(Context var0) {
            String var1 = "Unknown";

            try {
                PackageManager var2 = var0.getPackageManager();
                ApplicationInfo var3 = var2.getApplicationInfo(var0.getPackageName(), 128);
                if (var3 != null && var3.metaData != null) {
                    Object var4 = var3.metaData.get("UMENG_CHANNEL");
                    if (var4 != null) {
                        String var5 = var4.toString();
                        if (var5 != null) {
                            var1 = var5;
                        } else {
                            YLog.i(TAG, "Could not read UMENG_CHANNEL meta-data from AndroidManifest.xml.");
                        }
                    }
                }
            } catch (Exception var6) {
                YLog.e(TAG, "Could not read UMENG_CHANNEL meta-data from AndroidManifest.xml.");
                var6.printStackTrace();
            }

            return var1;
        }

        public static String u(Context var0) {
            return var0.getPackageName();
        }

        public static String v(Context var0) {
            return var0.getPackageManager().getApplicationLabel(var0.getApplicationInfo()).toString();
        }

        public static boolean w(Context var0) {
            try {
                return (var0.getApplicationInfo().flags & 2) != 0;
            } catch (Exception var2) {
                return false;
            }
        }
    }
}
