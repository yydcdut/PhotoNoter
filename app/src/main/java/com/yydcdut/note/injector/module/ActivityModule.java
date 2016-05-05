package com.yydcdut.note.injector.module;

import android.app.Activity;
import android.content.Context;

import com.yydcdut.note.injector.ContextLife;
import com.yydcdut.note.injector.PerActivity;
import com.yydcdut.note.markdown.MarkdownParser;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yuyidong on 15/11/22.
 */
@Module
public class ActivityModule {
    private Activity mActivity;
    private MarkdownParser mMarkdownParser;

    public ActivityModule(Activity activity) {
        mActivity = activity;
        mMarkdownParser = new MarkdownParser();
    }

    @Provides
    @PerActivity
    @ContextLife("Activity")
    public Context provideContext() {
        return mActivity;
    }

    @Provides
    @PerActivity
    public Activity provideActivity() {
        return mActivity;
    }

    @Provides
    public MarkdownParser provideMarkdownParser() {
        return mMarkdownParser;
    }

}
