package com.yydcdut.note.controller.setting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.yydcdut.note.R;
import com.yydcdut.note.controller.BaseFragment;

/**
 * Created by yuyidong on 15/10/27.
 */
public class FloatingEditFragment extends BaseFragment implements View.OnClickListener {
    public static final String TYPE = "type";
    private int mType;

    public static FloatingEditFragment getInstance() {
        return new FloatingEditFragment();
    }

    @Override
    public void getBundle(Bundle bundle) {
        mType = bundle.getInt(TYPE);
    }

    @Override
    public View inflateView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.frag_floating_edit, null);
    }

    @Override
    public void initUI(View view) {
        if (mType == 0) {
            view.findViewById(R.id.layout_fab_album).setVisibility(View.VISIBLE);
            view.findViewById(R.id.layout_fab_edittext).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.layout_fab_edittext).setVisibility(View.VISIBLE);
            view.findViewById(R.id.layout_fab_album).setVisibility(View.GONE);
        }
    }

    @Override
    public void initListener(View view) {
        switch (mType) {
            case 0:
                view.findViewById(R.id.fab_camera).setOnClickListener(this);
                view.findViewById(R.id.fab_local).setOnClickListener(this);
                break;
            case 1:
                view.findViewById(R.id.fab_evernote_update).setOnClickListener(this);
                view.findViewById(R.id.fab_voice).setOnClickListener(this);
                break;
        }
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_camera:
                break;
            case R.id.fab_local:
                break;
            case R.id.fab_evernote_update:
                break;
            case R.id.fab_voice:
                break;
        }
    }
}
