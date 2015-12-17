package com.yydcdut.note.model.rx;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.EvernoteUtil;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.client.conn.mobile.FileData;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Notebook;
import com.evernote.edam.type.Resource;
import com.evernote.edam.type.ResourceAttributes;
import com.evernote.edam.type.User;
import com.evernote.thrift.TException;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.yydcdut.note.BuildConfig;
import com.yydcdut.note.R;
import com.yydcdut.note.bean.user.EvernoteUser;
import com.yydcdut.note.bean.user.IUser;
import com.yydcdut.note.bean.user.QQUser;
import com.yydcdut.note.injector.ContextLife;
import com.yydcdut.note.model.rx.exception.RxException;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.utils.ImageManager.ImageLoaderManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by yuyidong on 15/12/4.
 */
public class RxUser {
    private static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.PRODUCTION;
    private static final boolean SUPPORT_APP_LINKED_NOTEBOOKS = true;

    private static final String NULL = "";

    private static final String NAME = "User";

    private static final String Q_NAME = "q_name";
    private static final String EVERNOTE_NAME = "evernote_name";
    private static final String NAME_DEFAULT = "";

    private static final String Q_NET_IMAGE_PATH = "q_net_image_id";
    private static final String Q_NET_IMAGE_PATH_DEFAULT = "";

    private SharedPreferences mSharedPreferences;

    private IUser mQQUser = null;
    private IUser mEvernoteUser = null;
    private Context mContext;

    private WeakReference<Activity> mQQActivity;
    private WeakReference<Activity> mEvernoteActivity;
    private Tencent mTencent;
    private EvernoteSession mEvernoteSession;

    @Singleton
    @Inject
    public RxUser(@ContextLife("Application") Context context) {
        mContext = context;
        mSharedPreferences = mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public Observable<Boolean> isLoginQQ() {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                String name = mSharedPreferences.getString(Q_NAME, NAME_DEFAULT);
                String netImagePath = mSharedPreferences.getString(Q_NET_IMAGE_PATH, Q_NET_IMAGE_PATH_DEFAULT);
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(netImagePath)) {
                    subscriber.onNext(false);
                } else {
                    subscriber.onNext(true);
                }
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
    }

    public Observable<IUser> getQQ() {
        return Observable.create(new Observable.OnSubscribe<IUser>() {
            @Override
            public void call(Subscriber<? super IUser> subscriber) {
                if (mQQUser == null) {
                    String name = mSharedPreferences.getString(Q_NAME, NAME_DEFAULT);
                    String netImagePath = mSharedPreferences.getString(Q_NET_IMAGE_PATH, Q_NET_IMAGE_PATH_DEFAULT);
                    if (TextUtils.isEmpty(name) || TextUtils.isEmpty(netImagePath)) {
                        subscriber.onError(new RxException("没有登录！！！"));
                    } else {
                        mQQUser = new QQUser(name, netImagePath);
                    }
                    subscriber.onNext(mQQUser);
                    subscriber.onCompleted();
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    public Observable<Boolean> logoutQQ() {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putString(Q_NAME, NULL);
                editor.putString(Q_NET_IMAGE_PATH, NULL);
                editor.commit();
                mQQUser = null;
                subscriber.onNext(true);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
    }

    public Observable<IUser> loginQQ(Activity activity) {
        if (mQQActivity != null) {
            mQQActivity.clear();
            mQQActivity = null;
        }
        mQQActivity = new WeakReference<>(activity);
        return Observable.create(new Observable.OnSubscribe<UserInfo>() {
            @Override
            public void call(Subscriber<? super UserInfo> subscriber) {
                if (mTencent == null) {
                    mTencent = Tencent.createInstance(BuildConfig.TENCENT_KEY, mContext);
                }
                mTencent.login(mQQActivity.get(), "all", new BaseUiListener(subscriber));
            }
        })
                .subscribeOn(Schedulers.io())
                .lift(new Observable.Operator<IUser, UserInfo>() {
                    @Override
                    public Subscriber<? super UserInfo> call(Subscriber<? super IUser> subscriber) {
                        return new Subscriber<UserInfo>() {
                            @Override
                            public void onCompleted() {
                                subscriber.onCompleted();
                            }

                            @Override
                            public void onError(Throwable e) {
                                subscriber.onError(e);
                            }

                            @Override
                            public void onNext(UserInfo userInfo) {
                                userInfo.getUserInfo(new UserInfoUiListener(subscriber));
                            }
                        };
                    }
                })
                .map(iUser -> {
                    /** todo:
                     * E/ImageLoader: null
                     android.os.NetworkOnMainThreadException
                     at android.os.StrictMode$AndroidBlockGuardPolicy.onNetwork(StrictMode.java:1117)
                     at java.net.InetAddress.lookupHostByName(InetAddress.java:385)
                     at java.net.InetAddress.getAllByNameImpl(InetAddress.java:236)
                     at java.net.InetAddress.getAllByName(InetAddress.java:214)
                     at libcore.net.http.HttpConnection.<init>(HttpConnection.java:70)
                     at libcore.net.http.HttpConnection.<init>(HttpConnection.java:50)
                     at libcore.net.http.HttpConnection$Address.connect(HttpConnection.java:340)
                     at libcore.net.http.HttpConnectionPool.get(HttpConnectionPool.java:87)
                     at libcore.net.http.HttpConnection.connect(HttpConnection.java:128)
                     at libcore.net.http.HttpEngine.openSocketConnection(HttpEngine.java:316)
                     at libcore.net.http.HttpEngine.connect(HttpEngine.java:311)
                     at libcore.net.http.HttpEngine.sendSocketRequest(HttpEngine.java:290)
                     at libcore.net.http.HttpEngine.sendRequest(HttpEngine.java:240)
                     at libcore.net.http.HttpURLConnectionImpl.getResponse(HttpURLConnectionImpl.java:282)
                     at libcore.net.http.HttpURLConnectionImpl.getResponseCode(HttpURLConnectionImpl.java:495)
                     at com.nostra13.universalimageloader.core.download.BaseImageDownloader.getStreamFromNetwork(BaseImageDownloader.java:117)
                     at com.nostra13.universalimageloader.core.download.BaseImageDownloader.getStream(BaseImageDownloader.java:88)
                     at com.nostra13.universalimageloader.core.decode.BaseImageDecoder.getImageStream(BaseImageDecoder.java:98)
                     at com.nostra13.universalimageloader.core.decode.BaseImageDecoder.decode(BaseImageDecoder.java:74)
                     at com.nostra13.universalimageloader.core.LoadAndDisplayImageTask.decodeImage(LoadAndDisplayImageTask.java:265)
                     at com.nostra13.universalimageloader.core.LoadAndDisplayImageTask.tryLoadBitmap(LoadAndDisplayImageTask.java:238)
                     at com.nostra13.universalimageloader.core.LoadAndDisplayImageTask.run(LoadAndDisplayImageTask.java:136)
                     at com.nostra13.universalimageloader.core.ImageLoader.displayImage(ImageLoader.java:267)
                     at com.nostra13.universalimageloader.core.ImageLoader.loadImage(ImageLoader.java:483)
                     at com.nostra13.universalimageloader.core.ImageLoader.loadImage(ImageLoader.java:444)
                     at com.nostra13.universalimageloader.core.ImageLoader.loadImageSync(ImageLoader.java:557)
                     at com.nostra13.universalimageloader.core.ImageLoader.loadImageSync(ImageLoader.java:498)
                     at com.yydcdut.note.utils.ImageManager.ImageLoaderManager.loadImageSync(ImageLoaderManager.java:37)
                     at com.yydcdut.note.model.rx.RxUser.lambda$loginQQ$139(RxUser.java:181)
                     at com.yydcdut.note.model.rx.RxUser.access$lambda$0(RxUser.java)
                     at com.yydcdut.note.model.rx.RxUser$$Lambda$1.call(Unknown Source)
                     at rx.internal.operators.OperatorMap$1.onNext(OperatorMap.java:55)
                     at com.yydcdut.note.model.rx.RxUser$UserInfoUiListener.onComplete(RxUser.java:294)
                     at com.tencent.connect.common.BaseApi$TempRequestListener$1.handleMessage(ProGuard:426)
                     at android.os.Handler.dispatchMessage(Handler.java:99)
                     at android.os.Looper.loop(Looper.java:137)
                     at android.app.ActivityThread.main(ActivityThread.java:5039)
                     at java.lang.reflect.Method.invokeNative(Native Method)
                     at java.lang.reflect.Method.invoke(Method.java:511)
                     at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:833)
                     at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:600)
                     at dalvik.system.NativeStart.main(Native Method)
                     */
                    Bitmap bitmap = ImageLoaderManager.loadImageSync(getQQQQ().getNetImagePath());
                    FilePathUtils.saveImage(FilePathUtils.getQQImagePath(), bitmap);
                    return iUser;
                });
    }

    private IUser getQQQQ() {
        if (mQQUser == null) {
            String name = mSharedPreferences.getString(Q_NAME, NAME_DEFAULT);
            String netImagePath = mSharedPreferences.getString(Q_NET_IMAGE_PATH, Q_NET_IMAGE_PATH_DEFAULT);
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(netImagePath)) {
                return null;
            } else {
                mQQUser = new QQUser(name, netImagePath);
            }
        }
        return mQQUser;
    }

    /**
     * 当自定义的监听器实现IUiListener接口后，必须要实现接口的三个方法，
     * onComplete  onCancel onError
     * 分别表示第三方登录成功，取消 ，错误。
     */
    private class BaseUiListener implements IUiListener {
        private Subscriber<? super UserInfo> mSubscriber;

        public BaseUiListener(Subscriber<? super UserInfo> subscriber) {
            mSubscriber = subscriber;
        }

        public void onCancel() {
            mSubscriber.onError(new RxException("取消登录"));
            mSubscriber.onCompleted();
        }

        /*
            {
                "access_token": "15D69FFB81BC403D9DB3DFACCF2FDDFF",
	            "authority_cost": 2490,
	            "expires_in": 7776000,
	            "login_cost": 775,
	            "msg": "",
	            "openid": "563559BEF3E2F97B693A6F88308F8D21",
	            "pay_token": "0E13A21128EAFB5E39048E5DE9478AD4",
	            "pf": "desktop_m_qq-10000144-android-2002-",
	            "pfkey": "11157020df5d6a8ebeaa150e2a7c68ce",
	            "query_authority_cost": 788,
	            "ret": 0
            }
        */
        public void onComplete(Object response) {
            /*
              到此已经获得OpenID以及其他你想获得的内容了
              QQ登录成功了，我们还想获取一些QQ的基本信息，比如昵称，头像
              sdk给我们提供了一个类UserInfo，这个类中封装了QQ用户的一些信息，我么可以通过这个类拿到这些信息
             */
            QQToken qqToken = mTencent.getQQToken();
            UserInfo info = new UserInfo(mContext, qqToken);
            mSubscriber.onNext(info);
        }

        @Override
        public void onError(UiError uiError) {
            mSubscriber.onError(new RxException(uiError.errorMessage));
            mSubscriber.onCompleted();
        }
    }

    private class UserInfoUiListener implements IUiListener {
        private Subscriber<? super IUser> mSubscriber;

        public UserInfoUiListener(Subscriber<? super IUser> subscriber) {
            mSubscriber = subscriber;
        }

        /*
                         {
                            "city": "成都",
                            "figureurl": "http://qzapp.qlogo.cn/qzapp/1104732115/563559BEF3E2F97B693A6F88308F8D21/30",
                            "figureurl_1": "http://qzapp.qlogo.cn/qzapp/1104732115/563559BEF3E2F97B693A6F88308F8D21/50",
                            "figureurl_2": "http://qzapp.qlogo.cn/qzapp/1104732115/563559BEF3E2F97B693A6F88308F8D21/100",
                            "figureurl_qq_1": "http://q.qlogo.cn/qqapp/1104732115/563559BEF3E2F97B693A6F88308F8D21/40",
                            "figureurl_qq_2": "http://q.qlogo.cn/qqapp/1104732115/563559BEF3E2F97B693A6F88308F8D21/100",
                            "gender": "男",
                            "is_lost": 0,
                            "is_yellow_vip": "0",
                            "is_yellow_year_vip": "0",
                            "level": "0",
                            "msg": "",
                            "nickname": "生命短暂，快乐至上。",
                            "province": "四川",
                            "ret": 0,
                            "vip": "0",
                            "yellow_vip_level": "0"
                           }
                        */
        public void onComplete(final Object response) {

            JSONObject json = (JSONObject) response;
            String name = null;
            String image = null;
            try {
                name = json.getString("nickname");
                image = json.getString("figureurl_qq_2");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString(Q_NAME, name);
            editor.putString(Q_NET_IMAGE_PATH, image);
            editor.commit();
            mQQUser = new QQUser(name, image);
            mSubscriber.onNext(mQQUser);
            mSubscriber.onCompleted();
        }

        public void onCancel() {
            mSubscriber.onError(new RxException("取消登录"));
            mSubscriber.onCompleted();
        }

        public void onError(UiError arg0) {
            mSubscriber.onError(new RxException(arg0.errorMessage));
            mSubscriber.onCompleted();
        }
    }

    public Observable<Boolean> isLoginEvernote() {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                String name = mSharedPreferences.getString(EVERNOTE_NAME, NAME_DEFAULT);
                if (TextUtils.isEmpty(name)) {
                    subscriber.onNext(false);
                } else {
                    subscriber.onNext(true);
                }
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
    }

    public Observable<IUser> getEvernote() {
        return Observable.create(new Observable.OnSubscribe<IUser>() {
            @Override
            public void call(Subscriber<? super IUser> subscriber) {
                if (mEvernoteUser == null) {
                    String name = mSharedPreferences.getString(EVERNOTE_NAME, NAME_DEFAULT);
                    if (TextUtils.isEmpty(name)) {
                        subscriber.onError(new RxException("没有登录！！！"));
                    } else {
                        mEvernoteUser = new EvernoteUser(name);
                    }
                }
                subscriber.onNext(mEvernoteUser);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
    }

    public Observable<Boolean> logoutEvernote() {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putString(EVERNOTE_NAME, NULL);
                editor.commit();
                if (mEvernoteSession == null) {
                    mEvernoteSession = new EvernoteSession.Builder(mContext)
                            .setLocale(Locale.SIMPLIFIED_CHINESE)
                            .setEvernoteService(EVERNOTE_SERVICE)
                            .setSupportAppLinkedNotebooks(SUPPORT_APP_LINKED_NOTEBOOKS)
                            .setForceAuthenticationInThirdPartyApp(true)
                            .build(BuildConfig.EVERNOTE_CONSUMER_KEY, BuildConfig.EVERNOTE_CONSUMER_SECRET)
                            .asSingleton();
                }
                if (mEvernoteSession.isLoggedIn()) {
                    mEvernoteSession.logOut();
                }
                mEvernoteUser = null;
                subscriber.onNext(true);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
    }

    public Observable<Boolean> loginEvernote(Activity activity) {
        if (mEvernoteActivity != null) {
            mEvernoteActivity.clear();
            mEvernoteActivity = null;
        }
        mEvernoteActivity = new WeakReference<>(activity);
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                if (mEvernoteSession == null) {
                    mEvernoteSession = new EvernoteSession.Builder(mContext)
                            .setLocale(Locale.SIMPLIFIED_CHINESE)
                            .setEvernoteService(EVERNOTE_SERVICE)
                            .setSupportAppLinkedNotebooks(SUPPORT_APP_LINKED_NOTEBOOKS)
                            .setForceAuthenticationInThirdPartyApp(true)
                            .build(BuildConfig.EVERNOTE_CONSUMER_KEY, BuildConfig.EVERNOTE_CONSUMER_SECRET)
                            .asSingleton();
                }
                mEvernoteSession.authenticate(mEvernoteActivity.get());
                subscriber.onNext(true);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
    }

    public Observable<IUser> saveEvernote() {
        return Observable.create(new Observable.OnSubscribe<IUser>() {
            @Override
            public void call(Subscriber<? super IUser> subscriber) {
                if (mEvernoteSession.isLoggedIn()) {
                    try {
                        User user = mEvernoteSession.getEvernoteClientFactory().getUserStoreClient().getUser();
                        mEvernoteUser = new EvernoteUser(user.getUsername());
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putString(EVERNOTE_NAME, mEvernoteUser.getName());
                        editor.commit();
                        subscriber.onNext(mEvernoteUser);
                    } catch (EDAMUserException e) {
                        e.printStackTrace();
                        subscriber.onError(e);
                    } catch (EDAMSystemException e) {
                        e.printStackTrace();
                        subscriber.onError(e);
                    } catch (TException e) {
                        e.printStackTrace();
                        subscriber.onError(e);
                    }
                } else {
                    subscriber.onError(new RxException("没有登录"));
                }
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
    }

    public Observable<Boolean> updateNote2Evernote(String bigPhotoPathWithoutFile, String photoName, String noteTitle, String noteContent) {
        return Observable.create(new Observable.OnSubscribe<List<Notebook>>() {
            @Override
            public void call(Subscriber<? super List<Notebook>> subscriber) {
                if (mEvernoteSession == null) {
                    mEvernoteSession = new EvernoteSession.Builder(mContext)
                            .setLocale(Locale.SIMPLIFIED_CHINESE)
                            .setEvernoteService(EVERNOTE_SERVICE)
                            .setSupportAppLinkedNotebooks(SUPPORT_APP_LINKED_NOTEBOOKS)
                            .setForceAuthenticationInThirdPartyApp(true)
                            .build(BuildConfig.EVERNOTE_CONSUMER_KEY, BuildConfig.EVERNOTE_CONSUMER_SECRET)
                            .asSingleton();
                }
                EvernoteNoteStoreClient noteStoreClient = mEvernoteSession.getEvernoteClientFactory().getNoteStoreClient();
                try {
                    List<Notebook> notebookList = noteStoreClient.listNotebooks();
                    subscriber.onNext(notebookList);
                } catch (EDAMUserException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                } catch (EDAMSystemException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                } catch (TException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .flatMap(notebooks -> Observable.from(notebooks))
                .filter(notebook -> notebook.getName().equals(mContext.getResources().getString(R.string.app_name)))
                .lift(new Observable.Operator<String, Notebook>() {
                    @Override
                    public Subscriber<? super Notebook> call(Subscriber<? super String> subscriber) {
                        return new Subscriber<Notebook>() {
                            private int mInTimes = 0;

                            @Override
                            public void onCompleted() {
                                if (mInTimes == 0) {
                                    Notebook notebook = new Notebook();
                                    notebook.setName(mContext.getResources().getString(R.string.app_name));
                                    EvernoteNoteStoreClient noteStoreClient = mEvernoteSession.getEvernoteClientFactory().getNoteStoreClient();
                                    try {
                                        Notebook appNoteBook = noteStoreClient.createNotebook(notebook);
                                        subscriber.onNext(appNoteBook.getGuid());
                                    } catch (EDAMUserException e) {
                                        e.printStackTrace();
                                        subscriber.onError(e);
                                    } catch (EDAMSystemException e) {
                                        e.printStackTrace();
                                        subscriber.onError(e);
                                    } catch (TException e) {
                                        e.printStackTrace();
                                        subscriber.onError(e);
                                    }
                                }
                                subscriber.onCompleted();
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(Notebook notebook) {
                                mInTimes++;
                                subscriber.onNext(notebook.getGuid());
                            }
                        };
                    }
                })
                .map(s -> {
                    Note note = new Note();
                    note.setNotebookGuid(s);
                    return note;
                })
                .lift(new Observable.Operator<Boolean, Note>() {
                    @Override
                    public Subscriber<? super Note> call(Subscriber<? super Boolean> subscriber) {
                        return new Subscriber<Note>() {
                            @Override
                            public void onCompleted() {
                                subscriber.onCompleted();
                            }

                            @Override
                            public void onError(Throwable e) {
                                subscriber.onError(e);
                            }

                            @Override
                            public void onNext(Note note) {
                                note.setTitle(noteTitle);
                                InputStream in = null;
                                // Hash the data in the image file. The hash is used to reference the file in the ENML note content.
                                try {
                                    in = new BufferedInputStream(new FileInputStream(bigPhotoPathWithoutFile));
                                    FileData data = new FileData(EvernoteUtil.hash(in), new File(bigPhotoPathWithoutFile));
                                    ResourceAttributes attributes = new ResourceAttributes();
                                    attributes.setFileName(photoName);

                                    // Create a new Resource
                                    Resource resource = new Resource();
                                    resource.setData(data);
                                    resource.setMime("image/jpeg");
                                    resource.setAttributes(attributes);

                                    note.addToResources(resource);
                                    // Set the note's ENML content
                                    String content = EvernoteUtil.NOTE_PREFIX
                                            + noteContent
                                            + EvernoteUtil.createEnMediaTag(resource)
                                            + EvernoteUtil.NOTE_SUFFIX;

                                    note.setContent(content);
                                    EvernoteNoteStoreClient noteStoreClient = mEvernoteSession.getEvernoteClientFactory().getNoteStoreClient();
                                    noteStoreClient.createNote(note);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                    subscriber.onError(e);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    subscriber.onError(e);
                                } catch (EDAMNotFoundException e) {
                                    e.printStackTrace();
                                    subscriber.onError(e);
                                } catch (TException e) {
                                    e.printStackTrace();
                                    subscriber.onError(e);
                                } catch (EDAMUserException e) {
                                    e.printStackTrace();
                                    subscriber.onError(e);
                                } catch (EDAMSystemException e) {
                                    e.printStackTrace();
                                    subscriber.onError(e);
                                } finally {
                                    if (in != null) {
                                        try {
                                            in.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                subscriber.onNext(true);
                            }
                        };
                    }
                });
    }

    public void initEvernoteSession() {
        if (mEvernoteSession == null) {
            mEvernoteSession = new EvernoteSession.Builder(mContext)
                    .setLocale(Locale.SIMPLIFIED_CHINESE)
                    .setEvernoteService(EVERNOTE_SERVICE)
                    .setSupportAppLinkedNotebooks(SUPPORT_APP_LINKED_NOTEBOOKS)
                    .setForceAuthenticationInThirdPartyApp(true)
                    .build(BuildConfig.EVERNOTE_CONSUMER_KEY, BuildConfig.EVERNOTE_CONSUMER_SECRET)
                    .asSingleton();
        }
    }
}
