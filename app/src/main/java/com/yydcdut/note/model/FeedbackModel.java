package com.yydcdut.note.model;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.yydcdut.note.injector.ContextLife;
import com.yydcdut.note.utils.LocalStorageUtils;
import com.yydcdut.note.utils.PhoneUtils;
import com.yydcdut.note.utils.YLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by yuyidong on 15/11/3.
 */
public class FeedbackModel {
    private LocalStorageUtils mLocalStorageUtils;
    private Context mContext;

    @Singleton
    @Inject
    public FeedbackModel(@ContextLife("Application") Context context, LocalStorageUtils localStorageUtils) {
        mLocalStorageUtils = localStorageUtils;
        mContext = context;
        YLog.i("yuyidong", "FeedbackModel   --->" + this.toString());
    }

    public synchronized Map sendFeedback(String feedback_id, String content) {
        if (TextUtils.isEmpty(mLocalStorageUtils.getUmengUid())) {
            getUmengUID();
        }
        JSONObject json = null;
        try {
            JSONObject totalInfoJson = getdeviceInfo(mContext);
            totalInfoJson.put("content", content);
            totalInfoJson.put("feedback_id", feedback_id);
            totalInfoJson.put("reply_id", System.currentTimeMillis() + "");
            totalInfoJson.put("device_uuid", mLocalStorageUtils.getDeviceUuid());
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
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_id", PhoneUtils.f(var0));
            jsonObject.put("device_model", Build.MODEL);
            jsonObject.put("appkey", PhoneUtils.p(var0));
            jsonObject.put("channel", PhoneUtils.t(var0));
            jsonObject.put("app_version", PhoneUtils.d(var0));
            jsonObject.put("version_code", PhoneUtils.c(var0));
            jsonObject.put("sdk_type", "Android");
            jsonObject.put("sdk_version", "5.4.0.20150727");
            jsonObject.put("os", "Android");
            jsonObject.put("os_version", Build.VERSION.RELEASE);
            jsonObject.put("country", PhoneUtils.o(var0)[0]);
            jsonObject.put("language", PhoneUtils.o(var0)[1]);
            jsonObject.put("timezone", PhoneUtils.n(var0));
            jsonObject.put("resolution", PhoneUtils.r(var0));
            jsonObject.put("access", PhoneUtils.j(var0)[0]);
            jsonObject.put("access_subtype", PhoneUtils.j(var0)[1]);
            jsonObject.put("carrier", PhoneUtils.h(var0));
            jsonObject.put("cpu", PhoneUtils.a());
            jsonObject.put("package", PhoneUtils.u(var0));
            jsonObject.put("uid", mLocalStorageUtils.getUmengUid());
            jsonObject.put("mac", PhoneUtils.q(var0));
            jsonObject.put("protocol_version", "2.0");
            return jsonObject;
        } catch (Exception var3) {
            var3.printStackTrace();
            return jsonObject;
        }
    }

    private String getUmengUID() {
        try {
            JSONObject deviceInfo = getdeviceInfo(mContext);
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
                mLocalStorageUtils.setUmengUid(uid);
                return uid;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private JSONObject httpConnection(String url) throws IOException {
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


}
