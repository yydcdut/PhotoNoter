package com.yydcdut.note.presenters.gallery.impl;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.annotation.NonNull;

import com.yydcdut.note.R;
import com.yydcdut.note.bean.gallery.GalleryApp;
import com.yydcdut.note.injector.ContextLife;
import com.yydcdut.note.model.gallery.SelectPhotoModel;
import com.yydcdut.note.presenters.gallery.IGalleryPresenter;
import com.yydcdut.note.views.BaseActivity;
import com.yydcdut.note.views.IView;
import com.yydcdut.note.views.gallery.IGalleryView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by yuyidong on 16/4/5.
 */
public class GalleryPresenterImpl implements IGalleryPresenter {
    private IGalleryView mIGalleryView;

    private Context mContext;

    @Inject
    public GalleryPresenterImpl(@ContextLife("Activity") Context context) {
        mContext = context;
    }

    @Override
    public void attachView(@NonNull IView iView) {
        mIGalleryView = (IGalleryView) iView;
    }

    @Override
    public List<GalleryApp> getGalleryAppList() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        PackageManager pm = mContext.getPackageManager();
        List<ResolveInfo> info = pm.queryIntentActivities(intent, 0);
        List<GalleryApp> galleryAppList = new ArrayList<>(info.size());
        for (int i = 0; i < info.size(); i++) {
            ActivityInfo activityInfo = info.get(i).activityInfo;
            galleryAppList.add(new GalleryApp(
                    activityInfo.loadIcon(mContext.getPackageManager()),
                    activityInfo.packageName,
                    activityInfo.loadLabel(mContext.getPackageManager()) + ""));
        }
        return galleryAppList;
    }

    @Override
    public void jump2SelectedDetailActivity() {
        if (SelectPhotoModel.getInstance().getCount() != 0) {
            mIGalleryView.jump2SelectedDetailActivity();
        }
    }

    @Override
    public void onReturnData(int requestCode, int resultCode, Intent data) {
        if (requestCode == BaseActivity.REQUEST_CODE && resultCode == BaseActivity.CODE_RESULT_CHANGED) {
            mIGalleryView.notifyDataChanged(0);//// TODO: 16/4/5  
            if (SelectPhotoModel.getInstance().getCount() == 0) {
                mIGalleryView.setPreviewMenuTitle(mContext.getResources().getString(R.string.action_view));
            } else {
                mIGalleryView.setPreviewMenuTitle(mContext.getResources().getString(R.string.action_view) + "(" + SelectPhotoModel.getInstance().getCount() + ")");
            }
        }
    }

    @Override
    public void detachView() {

    }
}
