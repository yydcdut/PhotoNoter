package com.yydcdut.note.mvp.v.impl;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.yydcdut.note.R;
import com.yydcdut.note.adapter.EditCategoryAdapter;
import com.yydcdut.note.bean.Category;
import com.yydcdut.note.controller.BaseActivity;
import com.yydcdut.note.mvp.p.IEditCategoryPresenter;
import com.yydcdut.note.mvp.p.impl.EditCategoryPresenterImpl;
import com.yydcdut.note.mvp.v.IEditCategoryView;
import com.yydcdut.note.utils.LollipopCompat;
import com.yydcdut.note.view.CircleProgressBarLayout;
import com.yydcdut.sdlv.Menu;
import com.yydcdut.sdlv.MenuItem;
import com.yydcdut.sdlv.SlideAndDragListView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuyidong on 15/10/13.
 */
public class EditCategoryActivity extends BaseActivity implements IEditCategoryView, SlideAndDragListView.OnDragListener,
        SlideAndDragListView.OnSlideListener, SlideAndDragListView.OnMenuItemClickListener {

    @InjectView(R.id.lv_edit_category)
    SlideAndDragListView mListView;
    @InjectView(R.id.layout_progress)
    CircleProgressBarLayout mProgressLayout;

    private EditCategoryAdapter mCategoryAdapter;

    private IEditCategoryPresenter mEditCategoryPresenter;

    @Override
    public boolean setStatusBar() {
        return true;
    }

    @Override
    public int setContentView() {
        return R.layout.activity_edit_category;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.inject(this);
        mEditCategoryPresenter = new EditCategoryPresenterImpl();
        mEditCategoryPresenter.attachView(this);
        initToolBarUI();
        initListView();
    }

    private void initToolBarUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.edit_category));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_check_white_24dp);
        LollipopCompat.setElevation(toolbar, getResources().getDimension(R.dimen.ui_elevation));
    }

    private void initListView() {
        Menu menu = new Menu((int) getResources().getDimension(R.dimen.slv_item_height), new ColorDrawable(Color.WHITE), true);
        menu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_width) * 3 / 2)
                .setBackground(new ColorDrawable(getResources().getColor(R.color.red_colorPrimary)))
                .setText(getResources().getString(R.string.delete))
                .setTextColor(Color.WHITE)
                .setTextSize((int) getResources().getDimension(R.dimen.txt_small) / 2)
                .build());
        menu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_width) * 3 / 2)
                .setBackground(new ColorDrawable(getResources().getColor(R.color.fab_blue)))
                .setText(getResources().getString(R.string.rename))
                .setDirection(MenuItem.DIRECTION_RIGHT)
                .setTextColor(Color.WHITE)
                .setTextSize((int) getResources().getDimension(R.dimen.txt_small) / 2)
                .build());
        mListView.setMenu(menu);
        mListView.setAdapter(mCategoryAdapter);
        mListView.setOnSlideListener(this);
        mListView.setOnMenuItemClickListener(this);
    }

    @Override
    public void onDragViewStart(int position) {
        mCategoryAdapter.setCurrentPosition(position);
    }

    @Override
    public void onDragViewMoving(int position) {
        mCategoryAdapter.setCurrentPosition(position);
    }

    @Override
    public void onDragViewDown(int position) {
        mCategoryAdapter.setCurrentPosition(-1);
    }

    @Override
    public void onSlideOpen(View view, View parentView, int position, int direction) {

    }

    @Override
    public void onSlideClose(View view, View parentView, int position, int direction) {

    }

    @Override
    public boolean onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction) {
        switch (direction) {
            case MenuItem.DIRECTION_LEFT:
                mEditCategoryPresenter.deleteCategory(itemPosition);
                return true;
            case MenuItem.DIRECTION_RIGHT:
                showRenameDialog(itemPosition);
                return true;
        }
        return false;
    }

    @Override
    public void showProgressBar() {
        mProgressLayout.show();
    }

    @Override
    public void hideProgressBar() {
        mProgressLayout.hide();
    }

    @Override
    public void finishActivity() {
        finish();
    }

    private void showRenameDialog(final int position) {
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_edittext, null);
        final EditText editText = (EditText) v.findViewById(R.id.edit_dialog);
        new AlertDialog.Builder(this, R.style.note_dialog)
                .setTitle(R.string.rename)
                .setCancelable(false)
                .setView(v)
                .setPositiveButton(R.string.dialog_btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mEditCategoryPresenter.renameCategory(position, editText.getText().toString());
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.dialog_btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void updateListView() {
        mCategoryAdapter.notifyDataSetChanged();
    }

    @Override
    public void showSnackbar(String messgae) {
        Snackbar.make(mListView, messgae, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showCategoryList(List<Category> categoryList) {
        mCategoryAdapter = new EditCategoryAdapter(this, categoryList);
        mListView.setOnDragListener(this, categoryList);
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mEditCategoryPresenter.doJob();
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEditCategoryPresenter.detachView();
    }
}
