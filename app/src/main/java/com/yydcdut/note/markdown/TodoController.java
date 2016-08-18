package com.yydcdut.note.markdown;

import android.widget.Toast;

import com.yydcdut.rxmarkdown.RxMDConfiguration;
import com.yydcdut.rxmarkdown.RxMDEditText;

/**
 * Created by yuyidong on 16/8/17.
 */
public class TodoController {
    private RxMDEditText mRxMDEditText;
    private RxMDConfiguration mRxMDConfiguration;

    public TodoController(RxMDEditText rxMDEditText, RxMDConfiguration rxMDConfiguration) {
        mRxMDEditText = rxMDEditText;
        mRxMDConfiguration = rxMDConfiguration;
    }

    public void doTodo() {
        int start = mRxMDEditText.getSelectionStart();
        int end = mRxMDEditText.getSelectionEnd();
        int position0 = Utils.findBeforeNewLineChar(mRxMDEditText.getText(), start) + 1;
        int position00 = Utils.findBeforeNewLineChar(mRxMDEditText.getText(), end) + 1;
        if (position0 != position00) {
            Toast.makeText(mRxMDEditText.getContext(), "无法操作多行", Toast.LENGTH_SHORT).show();
            return;
        }
        if ("- [ ] ".equals(mRxMDEditText.getText().subSequence(position0, position0 + "- [ ] ".length()).toString())) {
            mRxMDEditText.getText().delete(position0, position0 + "- [ ] ".length());
        } else if ("- [x] ".equalsIgnoreCase(mRxMDEditText.getText().subSequence(position0, position0 + "- [ ] ".length()).toString())) {
            mRxMDEditText.getText().delete(position0, position0 + "- [x] ".length());
            mRxMDEditText.getText().insert(position0, "- [ ] ");
        } else {
            mRxMDEditText.getText().insert(position0, "- [ ] ");
        }
    }

    public void doTodoDone() {
        int start = mRxMDEditText.getSelectionStart();
        int end = mRxMDEditText.getSelectionEnd();
        int position0 = Utils.findBeforeNewLineChar(mRxMDEditText.getText(), start) + 1;
        int position00 = Utils.findBeforeNewLineChar(mRxMDEditText.getText(), end) + 1;
        if (position0 != position00) {
            Toast.makeText(mRxMDEditText.getContext(), "无法操作多行", Toast.LENGTH_SHORT).show();
            return;
        }
        if ("- [x] ".equals(mRxMDEditText.getText().subSequence(position0, position0 + "- [x] ".length()).toString())) {
            mRxMDEditText.getText().delete(position0, position0 + "- [x] ".length());
        } else if ("- [ ] ".equalsIgnoreCase(mRxMDEditText.getText().subSequence(position0, position0 + "- [ ] ".length()).toString())) {
            mRxMDEditText.getText().delete(position0, position0 + "- [ ] ".length());
            mRxMDEditText.getText().insert(position0, "- [x] ");
        } else {
            mRxMDEditText.getText().insert(position0, "- [x] ");
        }
    }
}
