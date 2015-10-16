package com.yydcdut.note.controller.setting;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yydcdut.note.NoteApplication;
import com.yydcdut.note.R;
import com.yydcdut.note.bean.Category;
import com.yydcdut.note.controller.BaseActivity;
import com.yydcdut.note.model.CategoryDBModel;
import com.yydcdut.note.utils.LollipopCompat;
import com.yydcdut.note.utils.RandomColor;
import com.yydcdut.note.view.CircleProgressBarLayout;
import com.yydcdut.note.view.TextDrawable;
import com.yydcdut.sdlv.Menu;
import com.yydcdut.sdlv.MenuItem;
import com.yydcdut.sdlv.SlideAndDragListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by yuyidong on 15/10/13.
 */
public class EditCategoryActivity extends BaseActivity implements SlideAndDragListView.OnDragListener,
        SlideAndDragListView.OnSlideListener, SlideAndDragListView.OnButtonClickListener, Handler.Callback {
    private SlideAndDragListView mListView;
    private CircleProgressBarLayout mProgressLayout;
    private List<Category> mCategoryList;
    private RandomColor mColor = RandomColor.MATERIAL;
    private Handler mHandler;
    private Menu mMenu;
    private int mCurrentPosition = -1;
    private CategoryAdapter mCategoryAdapter;

    private List<String> mDeleteCategoryLabelList;
    private Map<String, String> mRenameCategoryLabelMap;

    @Override
    public int setContentView() {
        return R.layout.activity_edit_category;
    }

    @Override
    public void initUiAndListener() {
        initToolBarUI();
        initCircleProgressBar();
        initData();
        initMenu();
        initListView();
        mHandler = new Handler(this);
    }

    private void initToolBarUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.edit_category));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_check_white_24dp);
        LollipopCompat.setElevation(toolbar, getResources().getDimension(R.dimen.ui_elevation));
    }

    private void initCircleProgressBar() {
        mProgressLayout = (CircleProgressBarLayout) findViewById(R.id.layout_progress);
    }

    private void initListView() {
        mListView = (SlideAndDragListView) findViewById(R.id.lv_edit_category);
        mListView.setMenu(mMenu);
        mCategoryAdapter = new CategoryAdapter();
        mListView.setAdapter(mCategoryAdapter);
        mListView.setOnDragListener(this, mCategoryList);
        mListView.setOnSlideListener(this);
        mListView.setOnButtonClickListener(this);
    }

    private void initData() {
        mCategoryList = CategoryDBModel.getInstance().findAll();
    }

    public void initMenu() {
        mMenu = new Menu((int) getResources().getDimension(R.dimen.slv_item_height), new ColorDrawable(Color.WHITE), true);
        mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_width) * 3 / 2)
                .setBackground(new ColorDrawable(getResources().getColor(R.color.red_colorPrimary)))
                .setText(getResources().getString(R.string.delete))
                .setTextColor(Color.WHITE)
                .setTextSize((int) getResources().getDimension(R.dimen.txt_small) / 2)
                .build());
        mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_width) * 3 / 2)
                .setBackground(new ColorDrawable(getResources().getColor(R.color.fab_blue)))
                .setText(getResources().getString(R.string.rename))
                .setDirection(MenuItem.DIRECTION_RIGHT)
                .setTextColor(Color.WHITE)
                .setTextSize((int) getResources().getDimension(R.dimen.txt_small) / 2)
                .build());
    }

    @Override
    public void onDragViewStart(int position) {
        mCurrentPosition = position;
    }

    @Override
    public void onDragViewMoving(int position) {
        mCurrentPosition = position;
    }

    @Override
    public void onDragViewDown(int position) {
        mCurrentPosition = -1;
    }

    @Override
    public void onSlideOpen(View view, View parentView, int position, int direction) {

    }

    @Override
    public void onSlideClose(View view, View parentView, int position, int direction) {

    }

    @Override
    public void onClick(View v, int itemPosition, int buttonPosition, int direction) {
        switch (direction) {
            case MenuItem.DIRECTION_LEFT:
                if (mDeleteCategoryLabelList == null) {
                    mDeleteCategoryLabelList = new ArrayList<>();
                }
                Category category = mCategoryList.remove(itemPosition);
                String label = category.getLabel();
                mCategoryAdapter.notifyDataSetChanged();
                mDeleteCategoryLabelList.add(label);
                break;
            case MenuItem.DIRECTION_RIGHT:
                renameDialog(itemPosition);
                break;
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        finish();
        mProgressLayout.hide();
        return true;
    }


    class CategoryAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mCategoryList.size();
        }

        @Override
        public Object getItem(int position) {
            return mCategoryList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(EditCategoryActivity.this).inflate(R.layout.item_setting_edit_category, null);
                holder.imgLogo = (ImageView) convertView.findViewById(R.id.img_item_edit_category);
                holder.txtName = (TextView) convertView.findViewById(R.id.txt_item_edit_categoty);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            String label = mCategoryList.get(position).getLabel();
            String firstWord = null;
            if (label.length() > 0) {
                firstWord = label.substring(0, 1);
            } else {
                firstWord = "N";
            }
            if (mCurrentPosition == position) {
                holder.imgLogo.setImageDrawable(TextDrawable.builder().buildRound(firstWord, getResources().getColor(R.color.red_colorPrimary)));
                holder.txtName.setTextColor(getResources().getColor(R.color.red_colorPrimary));
            } else {
                holder.imgLogo.setImageDrawable(TextDrawable.builder().buildRound(firstWord, mColor.getColor(firstWord)));
                holder.txtName.setTextColor(getResources().getColor(R.color.txt_gray));
            }
            holder.txtName.setText(mCategoryList.get(position).getLabel());
            return convertView;
        }

        class ViewHolder {
            public ImageView imgLogo;
            public TextView txtName;
        }
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mProgressLayout.show();
                NoteApplication.getInstance().getExecutorPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        renameCategories();
                        deleteCategories();
                        CategoryDBModel.getInstance().updateOrder(mCategoryList);
                        mHandler.sendEmptyMessage(1);
                    }
                });
                break;
        }
        return true;
    }

    private void deleteCategories() {
        if (mDeleteCategoryLabelList != null && mDeleteCategoryLabelList.size() > 0) {
            for (String label : mDeleteCategoryLabelList) {
                Category category = CategoryDBModel.getInstance().findByCategoryLabel(label);
                boolean isCheck = category.isCheck();
                CategoryDBModel.getInstance().delete(category);
                if (isCheck) {//如果是menu中当前选中的这个
                    if (mCategoryList.size() > 0) {
                        Category newCategory = mCategoryList.get(0);
                        newCategory.setCheck(true);
                    } else {
                        //todo 当所有的都没有了怎么办
                    }
                }
            }
        }
    }

    private void renameCategories() {
        if (mRenameCategoryLabelMap != null && mRenameCategoryLabelMap.size() > 0) {
            Iterator<Map.Entry<String, String>> iterator = mRenameCategoryLabelMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                String originalLabel = entry.getKey();
                String newLabel = entry.getValue();
                boolean bool = CategoryDBModel.getInstance().updateLabel(originalLabel, newLabel);
            }
        }
    }

    private void renameDialog(final int position) {
        if (mRenameCategoryLabelMap == null) {
            mRenameCategoryLabelMap = new HashMap<>();
        }
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_edittext, null);
        final EditText editText = (EditText) v.findViewById(R.id.edit_dialog);
        new AlertDialog.Builder(this, R.style.note_dialog)
                .setTitle(R.string.rename)
                .setCancelable(false)
                .setView(v)
                .setPositiveButton(R.string.dialog_btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = editText.getText().toString();
                        if (text.length() == 0) {
                            Toast.makeText(EditCategoryActivity.this, getResources().getString(R.string.toast_fail), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            return;
                        }
                        Category category = mCategoryList.get(position);
                        mRenameCategoryLabelMap.put(category.getLabel(), text);
                        category.setLabel(text);
                        dialog.dismiss();
                        mCategoryAdapter.notifyDataSetChanged();
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

}
