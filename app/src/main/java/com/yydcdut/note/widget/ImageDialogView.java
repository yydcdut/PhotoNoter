package com.yydcdut.note.widget;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.widget.PopupMenu;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yydcdut.note.R;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by yuyidong on 16/8/17.
 */
public class ImageDialogView extends LinearLayout implements PopupMenu.OnMenuItemClickListener {
    private static final int REQUEST_CAMERA = 10;
    private static final int REQUEST_GALLERY = 11;

    private static final String DEFAULT_PATH = "drawable://" + R.drawable.ic_logo;

    private int mCurrentCameraPictureIndex = 0;

    @Bind(R.id.img_image)
    ImageView mTargetImageView;

    private String mPath;

    @Bind(R.id.edit_width)
    EditText mWidthEditText;
    @Bind(R.id.edit_height)
    EditText mHeightEditText;
    @Bind(R.id.edit_description)
    EditText mDescriptionEditText;

    public ImageDialogView(Context context) {
        super(context);
        init(context);
    }

    public ImageDialogView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public ImageDialogView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.dialog_image, this, true);
        ButterKnife.bind(v, this);
    }

    @OnClick(R.id.img_image)
    public void targetImageClick(View v) {
        PopupMenu popup = new PopupMenu(getContext(), mTargetImageView);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_popup, popup.getMenu());
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }

    public void handleResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
            mPath = "file:/" + getContext().getExternalCacheDir().getAbsolutePath() + File.separator + "tmp" + mCurrentCameraPictureIndex + ".jpg";
            ImageLoader.getInstance().displayImage(mPath, mTargetImageView);
            mCurrentCameraPictureIndex++;
        } else if (requestCode == REQUEST_GALLERY && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            Uri uri = data.getData();
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = mTargetImageView.getContext().getContentResolver().query(uri, proj, null, null, null);
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                mPath = "file:/" + cursor.getString(column_index);
            }
            cursor.close();
            ImageLoader.getInstance().displayImage(mPath, mTargetImageView);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Activity activity = null;
        if (getContext() instanceof Activity) {
            activity = (Activity) getContext();
        } else {
            return false;
        }
        switch (item.getItemId()) {
            case R.id.action_gallery:
                Intent albumIntent = new Intent(Intent.ACTION_PICK, null);
                albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                activity.startActivityForResult(albumIntent, REQUEST_GALLERY);
                break;
            case R.id.action_camera:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = new File(getContext().getExternalCacheDir().getAbsolutePath() + File.separator + "tmp" + mCurrentCameraPictureIndex + ".jpg");
                if (file.exists()) {
                    file.delete();
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                activity.startActivityForResult(intent, REQUEST_CAMERA);
                break;
        }
        return false;
    }

    public void clear() {
        ImageLoader.getInstance().displayImage(DEFAULT_PATH, mTargetImageView);
        mWidthEditText.setText("200");
        mHeightEditText.setText("200");
        mPath = DEFAULT_PATH;
    }

    public int getImageWidth() {
        return Integer.parseInt(mWidthEditText.getText().toString());
    }

    public int getImageHeight() {
        return Integer.parseInt(mHeightEditText.getText().toString());
    }

    public String getPath() {
        return mPath;
    }

    public String getDescription() {
        return mDescriptionEditText.getText().toString();
    }

}
