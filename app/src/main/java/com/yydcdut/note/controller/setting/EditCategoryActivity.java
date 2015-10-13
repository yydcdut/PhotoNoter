package com.yydcdut.note.controller.setting;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.yydcdut.note.utils.RandomColor;
import com.yydcdut.note.view.CircleProgressBarLayout;
import com.yydcdut.note.view.SlideAndDragListView;
import com.yydcdut.note.view.TextDrawable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by yuyidong on 15/7/31.
 */
public class EditCategoryActivity extends BaseActivity implements SlideAndDragListView.OnItemLongClickListener, SlideAndDragListView.OnDragListener,
        Handler.Callback, View.OnClickListener, SlideAndDragListView.OnScrollListener, AdapterView.OnItemClickListener {
    private SlideAndDragListView mListView;
    private CircleProgressBarLayout mProgressLayout;
    private Toolbar mToolbar;
    private List<Category> mCategoryList;
    private RandomColor mColor = RandomColor.MATERIAL;
    private Handler mHandler;

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
        mListView = (SlideAndDragListView) findViewById(R.id.lv_edit_category);
        mCategoryList = CategoryDBModel.getInstance().findAll();

        mListView.setAdapter(mBaseAdapter);
        mListView.setData(mCategoryList);
        mListView.setOnItemLongClickListener(this);
        mListView.setOnDragListener(this);
        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(this);

        mHandler = new Handler(this);
    }

    private void initCircleProgressBar() {
        mProgressLayout = (CircleProgressBarLayout) findViewById(R.id.layout_progress);
    }


    private void initToolBarUI() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(getResources().getString(R.string.edit_category));
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_check_white_24dp);
    }

    private int mCurrentPosition = -1;

    @Override
    public void onItemLongClick(View view, int position) {
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

    private BaseAdapter mBaseAdapter = new BaseAdapter() {
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
                holder.layoutScroll = convertView.findViewById(R.id.layout_item_edit_category);
                holder.btnDelete = (TextView) convertView.findViewById(R.id.txt_item_edit_category_delete);
                holder.btnRename = (TextView) convertView.findViewById(R.id.txt_item_edit_category_rename);
                holder.layoutBG = convertView.findViewById(R.id.layout_item_edit_category_txt);
                holder.imgBG = convertView.findViewById(R.id.img_item_edit_category_bg);
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
            holder.layoutScroll.scrollTo(0, 0);
            holder.btnDelete.setOnClickListener(EditCategoryActivity.this);
            holder.btnRename.setOnClickListener(EditCategoryActivity.this);
            holder.imgBG.setVisibility(View.VISIBLE);
            holder.layoutBG.setVisibility(View.VISIBLE);
            return convertView;
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_item_edit_category_delete:
                if (mBtnPosition != -1) {
                    if (mDeleteCategoryLabelList == null) {
                        mDeleteCategoryLabelList = new ArrayList<>();
                    }
                    Category category = mCategoryList.remove(mBtnPosition);
                    String label = category.getLabel();
                    mBaseAdapter.notifyDataSetChanged();
                    mDeleteCategoryLabelList.add(label);
                }
                break;
            case R.id.txt_item_edit_category_rename:
                if (mBtnPosition != -1) {
                    renameDialog();
                }
                break;
        }
    }

    private int mBtnPosition = -1;

    @Override
    public void onScrollOpen(View view, int position) {
        mBtnPosition = position;
        mListView.setOnItemClickListener(null);
    }

    @Override
    public void onScrollClose(View view, int position) {
        mBtnPosition = -1;
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }


    class ViewHolder {
        public View layoutScroll;
        public ImageView imgLogo;
        public TextView txtName;
        public TextView btnDelete;
        public TextView btnRename;
        public View layoutBG;
        public View imgBG;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mProgressLayout.show();
                NoteApplication.getInstance().getExecutorPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        renameCategories();
                        deleteCategories();
                        CategoryDBModel.getInstance().updateOrder(mCategoryList);
                        sendDataUpdateBroadcast(false, null, true, false, false);
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
                        sendDataUpdateBroadcast(true, null, false, false, false);
                    } else {
                        //todo 当所有的都没有了怎么办
                    }
                } else {
                    sendDataUpdateBroadcast(true, null, false, false, false);
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
            sendDataUpdateBroadcast(true, null, false, false, false);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        finish();
        mProgressLayout.hide();
        return true;
    }

    private void renameDialog() {
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
                        Category category = (Category) mBaseAdapter.getItem(mBtnPosition);
                        mRenameCategoryLabelMap.put(category.getLabel(), text);
                        category.setLabel(text);
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


}
