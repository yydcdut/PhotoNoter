package com.yydcdut.note.markdown;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.yydcdut.note.widget.ImageDialogView;
import com.yydcdut.rxmarkdown.RxMDConfiguration;
import com.yydcdut.rxmarkdown.RxMDEditText;

/**
 * Created by yuyidong on 16/8/17.
 */
public class ImageController {
    private ImageDialogView mImageDialogView;
    private RxMDEditText mRxMDEditText;
    private RxMDConfiguration mRxMDConfiguration;

    private AlertDialog mAlertDialog;

    public ImageController(RxMDEditText rxMDEditText, RxMDConfiguration rxMDConfiguration) {
        mRxMDEditText = rxMDEditText;
        mRxMDConfiguration = rxMDConfiguration;
        mImageDialogView = new ImageDialogView(mRxMDEditText.getContext());
        mImageDialogView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void doImage() {
        if (mAlertDialog == null) {
            initDialog();
        }
        mImageDialogView.clear();
        mAlertDialog.show();
    }

    public void handleResult(int requestCode, int resultCode, Intent data) {
        mImageDialogView.handleResult(requestCode, resultCode, data);
    }

    private void initDialog() {
        mAlertDialog = new AlertDialog.Builder(mRxMDEditText.getContext())
                .setView(mImageDialogView)
                .setPositiveButton("确定", (dialog, which) -> {
                    dialog.dismiss();
                    int width = mImageDialogView.getImageWidth();
                    int height = mImageDialogView.getImageHeight();
                    String path = mImageDialogView.getPath();
                    String description = mImageDialogView.getDescription();
                    doRealImage(width, height, path, description);
                })
                .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .create();
    }

    private void doRealImage(int width, int height, String path, String description) {
        int start = mRxMDEditText.getSelectionStart();
        if (TextUtils.isEmpty(description)) {
            mRxMDEditText.getText().insert(start, "![](" + path + "/" + width + "$" + height + ")");
            mRxMDEditText.setSelection(start + 2);
        } else {
            mRxMDEditText.getText().insert(start, "![" + description + "](" + path + "/" + width + "$" + height + ")");
        }
    }

}
