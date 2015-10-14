package com.yydcdut.note.controller.home;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.yydcdut.note.R;
import com.yydcdut.note.controller.BaseActivity;
import com.yydcdut.note.utils.LocalStorageUtils;
import com.yydcdut.note.utils.LollipopUtils;

/**
 * Created by yuyidong on 15/7/16.
 */
public class SplashActivity extends BaseActivity {

    @Override
    public void initUiAndListener() {
        if (!LocalStorageUtils.getInstance().getSplashOpen()) {
            return;
        }
        View logoView = findViewById(R.id.layout_splash);
        View backgroundView = findViewById(R.id.img_splash_bg);

        AnimatorSet animation = new AnimatorSet();
        animation.setDuration(2000);
        animation.playTogether(
                ObjectAnimator.ofFloat(logoView, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(logoView, "translationY", 300, 0),
                ObjectAnimator.ofFloat(backgroundView, "scaleX", 1.3f, 1.05f),
                ObjectAnimator.ofFloat(backgroundView, "scaleY", 1.3f, 1.05f)
        );
        animation.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!LocalStorageUtils.getInstance().notGotoIntroduce()) {
                    Intent intent = new Intent(SplashActivity.this, IntroduceActivity.class);
                    startActivity(intent);
                    SplashActivity.this.finish();
                } else {
                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(intent);
                    SplashActivity.this.finish();
                }
            }
        }, 3000);

    }

    @Override
    public int setContentView() {
        LollipopUtils.setFullWindow(getWindow());
        return R.layout.activity_splash;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!LocalStorageUtils.getInstance().getSplashOpen()) {
            Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
        super.onCreate(savedInstanceState);
    }
}
