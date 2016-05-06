package com.yydcdut.note;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.view.Window;
import android.view.WindowManager;

import java.io.File;

/**
 * Created by yuyidong on 15/12/15.
 */
public class DexActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION | 128);
        } else {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_no_animation, R.anim.activity_no_animation);
        setContentView(R.layout.activity_dex);
        new LoadDexTask().execute();
    }

    class LoadDexTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            try {
                MultiDex.install(getApplication());
            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            //delete file
            deleteFile();
            overridePendingTransition(R.anim.activity_no_animation, R.anim.activity_no_animation);
            finish();
            overridePendingTransition(R.anim.activity_no_animation, R.anim.activity_no_animation);
            System.exit(0);
        }
    }

    @Override
    public void onBackPressed() {
        //cannot backPressed
    }

    private void deleteFile() {
        String filePath = Environment.getExternalStorageDirectory().toString() + File.separator + "photo.note";
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }
}
