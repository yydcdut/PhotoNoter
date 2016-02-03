package com.yydcdut.note.model.rx;

import android.text.TextUtils;

import com.yydcdut.note.presenters.setting.IFeedbackPresenter;
import com.yydcdut.note.utils.LocalStorageUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by yuyidong on 15/12/1.
 */
public class RxFeedBack {
    private LocalStorageUtils mLocalStorageUtils;

    private static final String FEEDBACK = "Feedback";
    private static final String CONTACT = "Contact";
    private String mType;
    private String mFeedbackId;
    private String mContent;
    private String mEmail;
    private JSONObject mDeviceInfo;

    @Singleton
    @Inject
    public RxFeedBack(LocalStorageUtils localStorageUtils) {
        mLocalStorageUtils = localStorageUtils;
    }

    public RxFeedBack setType(int type) {
        switch (type) {
            case IFeedbackPresenter.TYPE_CONTACT:
                mType = FEEDBACK;
                break;
            case IFeedbackPresenter.TYPE_FEEDBACK:
                mType = CONTACT;
                break;
            default:
                mType = "????";
                break;
        }
        return this;
    }

    public RxFeedBack setFeedBackId(String feedback_id) {
        mFeedbackId = feedback_id;
        return this;
    }

    public RxFeedBack setContent(String content) {
        mContent = content;
        return this;
    }

    public RxFeedBack setEmail(String email) {
        mEmail = email;
        return this;
    }

    public RxFeedBack setDeviceInfo(JSONObject jsonObject) {
        mDeviceInfo = jsonObject;
        return this;
    }

    public Observable<Map<String, String>> doObservable() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                if (TextUtils.isEmpty(mLocalStorageUtils.getUmengUid())) {
                    try {
                        StringBuilder sb = new StringBuilder("http://fb.umeng.com/api/v2/user/getuid");
                        sb.append("?");
                        Iterator iterator = mDeviceInfo.keys();
                        String uid;
                        while (iterator.hasNext()) {
                            String entry = (String) iterator.next();
                            uid = mDeviceInfo.get(entry).toString();
                            sb.append(URLEncoder.encode(entry, "UTF-8") + "=" + URLEncoder.encode(uid, "UTF-8") + "&");
                        }

                        if (38 == sb.charAt(sb.length() - 1)) {
                            sb.deleteCharAt(sb.length() - 1);
                        }

                        JSONObject json = httpConnection(sb.toString());
                        if (judgeStatus(json)) {
                            uid = json.getJSONObject("data").getString("uid");
                            mLocalStorageUtils.setUmengUid(uid);
                            subscriber.onNext(uid);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        subscriber.onError(e);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        subscriber.onError(e);
                    } catch (IOException e) {
                        e.printStackTrace();
                        subscriber.onError(e);
                    } finally {
                        subscriber.onCompleted();
                    }
                } else {
                    subscriber.onNext(mLocalStorageUtils.getUmengUid());
                    subscriber.onCompleted();
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .lift(new Observable.Operator<JSONObject, String>() {
                    @Override
                    public Subscriber<? super String> call(Subscriber<? super JSONObject> subscriber) {
                        return new Subscriber<String>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                subscriber.onError(e);
                            }

                            @Override
                            public void onNext(String s) {
                                StringBuilder sb = new StringBuilder(mEmail);
                                sb.append("<---联系方式   ")
                                        .append(mType)
                                        .append("   反馈内容--->")
                                        .append(mContent);
                                try {
                                    mDeviceInfo.put("content", sb.toString());
                                    mDeviceInfo.put("feedback_id", mFeedbackId);
                                    mDeviceInfo.put("reply_id", System.currentTimeMillis() + "");
                                    mDeviceInfo.put("device_uuid", mLocalStorageUtils.getDeviceUuid());
                                    mDeviceInfo.put("type", "new_feedback");
                                    mDeviceInfo.put("uid", s);
                                    subscriber.onNext(mDeviceInfo);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    subscriber.onError(e);
                                }
                            }
                        };
                    }
                })
                .lift(new Observable.Operator<JSONObject, JSONObject>() {
                    @Override
                    public Subscriber<? super JSONObject> call(Subscriber<? super JSONObject> subscriber) {
                        return new Subscriber<JSONObject>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                subscriber.onError(e);
                            }

                            @Override
                            public void onNext(JSONObject jsonObject) {
                                try {
                                    JSONObject json = httpConnection(jsonObject, "http://fb.umeng.com/api/v2/feedback/reply/new");
                                    subscriber.onNext(json);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    subscriber.onError(e);
                                }
                            }
                        };
                    }
                })
                .map(jsonObject -> setData(jsonObject));
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


}
