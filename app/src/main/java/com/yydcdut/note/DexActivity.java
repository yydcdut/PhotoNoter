package com.yydcdut.note;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.view.Window;
import android.view.WindowManager;

import com.yydcdut.note.utils.YLog;

import java.io.File;

/**
 * Created by yuyidong on 15/12/15.
 */
public class DexActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        overridePendingTransition(R.anim.activity_no_animation, R.anim.activity_no_animation);
        setContentView(R.layout.activity_dex);
        new LoadDexTask().execute();
    }

    class LoadDexTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            try {
                YLog.wtf("loadDex", "install begin");
                MultiDex.install(getApplication());
//                Thread.sleep(5000);
//                Thread.sleep(5000);
//                Thread.sleep(5000);
                YLog.wtf("loadDex", "install finish");
            } catch (Exception e) {
                YLog.wtf("loadDex", "Exception" + e.getLocalizedMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            //delete file
            deleteFile();
            YLog.wtf("loadDex", "get install finish");
            finish();
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
