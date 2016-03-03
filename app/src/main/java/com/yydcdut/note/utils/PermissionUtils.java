package com.yydcdut.note.utils;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.yydcdut.note.R;
import com.yydcdut.note.presenters.IPresenter;
import com.yydcdut.note.utils.permission.Permission;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuyidong on 16/1/4.
 */
public class PermissionUtils {
    private static final String TAG = PermissionUtils.class.getSimpleName();

    public static final int CODE_CAMERA = 0;
    public static final int CODE_ADJUST_CAMERA = -1;
    public static final int CODE_STORAGE = 1;
    public static final int CODE_LOCATION = 2;
    public static final int CODE_AUDIO = 3;
    public static final int CODE_PHONE_STATE = 4;

    public static final String[] PERMISSION_CAMERA = new String[]{
            Manifest.permission.CAMERA
    };
    public static final String[] PERMISSION_STORAGE = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    public static final String[] PERMISSION_LOCATION = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    public static final String[] PERMISSION_AUDIO = new String[]{
            Manifest.permission.RECORD_AUDIO
    };
    public static final String[] PERMISSION_PHONE_STATE = new String[]{
            Manifest.permission.READ_PHONE_STATE
    };

    public static boolean hasPermission4Camera(@NonNull Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean hasPermission4Storage(@NonNull Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        int permission0 = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission1 = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission0 == PackageManager.PERMISSION_GRANTED && permission1 == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean hasPermission4Location(@NonNull Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        int permission0 = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        int permission1 = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permission0 == PackageManager.PERMISSION_GRANTED && permission1 == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean hasPermission4Audio(@NonNull Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        int permission = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean hasPermission4PhoneState(@NonNull Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        int permission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public static void requestPermissionsWithDialog(final @NonNull Activity activity, String explanation,
                                                    final String[] permissions, final int code) {
        //explanation
        AlertDialog dialog = new AlertDialog.Builder(activity, R.style.note_dialog)
                .setTitle(R.string.permission_title)
                .setMessage(explanation)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(activity, permissions, code);
                    }
                })
                .create();
        dialog.show();
    }

    public static void requestPermissionsWithDialog(final @NonNull Fragment fragment, String explanation,
                                                    final String[] permissions, final int code) {
        //explanation
        AlertDialog dialog = new AlertDialog.Builder(fragment.getActivity(), R.style.note_dialog)
                .setTitle(R.string.permission_title)
                .setMessage(explanation)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FragmentCompat.requestPermissions(fragment, permissions, code);
                    }
                })
                .create();
        dialog.show();
    }

    public static void requestPermissions(final @NonNull Activity activity, String explanation, final String[] permissions,
                                          final int code, final OnRequestPermissionDeniedByUserListener listener) {
        boolean shouldShowRationale = false;
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                shouldShowRationale = true;
                break;
            }
        }
        if (shouldShowRationale) {
            //explanation
            AlertDialog dialog = new AlertDialog.Builder(activity, R.style.note_dialog)
                    .setTitle(R.string.permission_title)
                    .setMessage(explanation)
                    .setCancelable(false)
                    .setPositiveButton(R.string.dialog_btn_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(activity, permissions, code);
                        }
                    })
                    .setNegativeButton(R.string.dialog_btn_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (listener != null) {
                                listener.onDenied(code);
                            }
                        }
                    })
                    .create();
            dialog.show();
        } else {
            ActivityCompat.requestPermissions(activity, permissions, code);
        }
    }

    public static void requestPermissions(final @NonNull Fragment fragment, String explanation, final String[] permissions,
                                          final int code, final OnRequestPermissionDeniedByUserListener listener) {
        boolean shouldShowRationale = false;
        for (String permission : permissions) {
            if (FragmentCompat.shouldShowRequestPermissionRationale(fragment, permission)) {
                shouldShowRationale = true;
                break;
            }
        }
        if (shouldShowRationale) {
            //explanation
            AlertDialog dialog = new AlertDialog.Builder(fragment.getActivity(), R.style.note_dialog)
                    .setTitle(R.string.permission_title)
                    .setMessage(explanation)
                    .setCancelable(false)
                    .setPositiveButton(R.string.dialog_btn_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FragmentCompat.requestPermissions(fragment, permissions, code);
                        }
                    })
                    .setNegativeButton(R.string.dialog_btn_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (listener != null) {
                                listener.onDenied(code);
                            }
                        }
                    })
                    .create();
            dialog.show();
        } else {
            FragmentCompat.requestPermissions(fragment, permissions, code);
        }
    }

    /**
     * 权限的回调
     */
    public static void permissionResult(@NonNull IPresenter iPresenter, @NonNull String[] permissions,
                                        @NonNull int[] grantResults, @NonNull int requestCode) {
        if (!(iPresenter instanceof OnPermissionCallBacks)) {
            throw new IllegalArgumentException("Activity must implement PermissionCallbacks.");
        }
        OnPermissionCallBacks callbacks = (OnPermissionCallBacks) iPresenter;
        ArrayList<String> granted = new ArrayList<>();
        ArrayList<String> denied = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            String perm = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                granted.add(perm);
            } else {
                denied.add(perm);
            }
        }
        if (!granted.isEmpty()) {
            callbacks.onPermissionsGranted(granted);
        }
        if (!denied.isEmpty()) {
            callbacks.onPermissionsDenied(denied);
        }
        if (!granted.isEmpty()) {
            invokeMethod(iPresenter, requestCode);
        }
    }

    private static void invokeMethod(@NonNull IPresenter iPresenter, @NonNull int requestCode) {
        Class clazz = iPresenter.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Permission.class)) {
                Permission ann = method.getAnnotation(Permission.class);
                if (ann.value() == requestCode) {
                    if (method.getParameterTypes().length > 0) {
                        throw new RuntimeException("Cannot execute non-void method " + method.getName());
                    }
                    try {
                        // Make method accessible if private
                        if (!method.isAccessible()) {
                            method.setAccessible(true);
                        }
                        method.invoke(iPresenter);
                    } catch (IllegalAccessException e) {
                        YLog.e(TAG, "IllegalAccessException--->" + e);
                    } catch (InvocationTargetException e) {
                        YLog.e(TAG, "InvocationTargetException--->" + e);
                    }
                }
            }
        }
    }


    public interface OnPermissionCallBacks extends ActivityCompat.OnRequestPermissionsResultCallback {
        void onPermissionsGranted(List<String> permissions);

        void onPermissionsDenied(List<String> permissions);
    }

    public interface OnRequestPermissionDeniedByUserListener {
        void onDenied(int requestCode);
    }
}
