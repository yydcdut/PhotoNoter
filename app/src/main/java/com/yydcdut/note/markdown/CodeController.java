package com.yydcdut.note.markdown;

import android.text.Editable;
import android.widget.Toast;

import com.yydcdut.rxmarkdown.RxMDConfiguration;
import com.yydcdut.rxmarkdown.RxMDEditText;

/**
 * Created by yuyidong on 16/8/17.
 */
public class CodeController {
    private RxMDEditText mRxMDEditText;
    private RxMDConfiguration mRxMDConfiguration;

    public CodeController(RxMDEditText rxMDEditText, RxMDConfiguration rxMDConfiguration) {
        mRxMDEditText = rxMDEditText;
        mRxMDConfiguration = rxMDConfiguration;
    }

    public void doInlineCode() {
        int start = mRxMDEditText.getSelectionStart();
        int end = mRxMDEditText.getSelectionEnd();
        if (start == end) {
            mRxMDEditText.getText().insert(start, "``");
            mRxMDEditText.setSelection(start + 1, end + 1);
        } else if (end - start > 2) {//选中了4个以上
            int position0 = Utils.findBeforeNewLineChar(mRxMDEditText.getText(), start) + 1;
            int position00 = Utils.findBeforeNewLineChar(mRxMDEditText.getText(), end) + 1;
            if (position0 != position00) {
                Toast.makeText(mRxMDEditText.getContext(), "无法操作多行", Toast.LENGTH_SHORT).show();
                return;
            }
            Editable editable = mRxMDEditText.getText();
            if ("`".equals(editable.subSequence(start, start + "`".length()).toString()) &&
                    "`".equals(editable.subSequence(end - "`".length(), end).toString())) {
                mRxMDEditText.getText().delete(end - "`".length(), end);
                mRxMDEditText.getText().delete(start, start + "`".length());
                mRxMDEditText.setSelection(start, end - "`".length() * 2);
            } else {
                mRxMDEditText.getText().insert(end, "`");
                mRxMDEditText.getText().insert(start, "`");
                mRxMDEditText.setSelection(start, end + "`".length() * 2);
            }
        } else {
            mRxMDEditText.getText().insert(end, "`");
            mRxMDEditText.getText().insert(start, "`");
            mRxMDEditText.setSelection(start, end + "`".length() * 2);
        }
    }

    public void doCode() {
        int start = mRxMDEditText.getSelectionStart();
        int end = mRxMDEditText.getSelectionEnd();
        if (start == end) {
            int position0 = Utils.findBeforeNewLineChar(mRxMDEditText.getText(), start) + 1;
            int position1 = Utils.findNextNewLineChar(mRxMDEditText.getText(), end);
            if (position1 == -1) {
                position1 = mRxMDEditText.length();
            }
            Editable editable = mRxMDEditText.getText();
            if (position0 >= 4 && position1 < mRxMDEditText.length() - 4) {
                boolean begin = "```".equals(editable.subSequence(position0 - 1 - "```".length(), position0 - 1).toString());
                if (begin && "```\n".equals(editable.subSequence(position1 + 1, position1 + 1 + "```\n".length()).toString())) {
                    mRxMDEditText.getText().delete(position1 + 1, position1 + 1 + "```\n".length());
                    mRxMDEditText.getText().delete(position0 - "\n```".length(), position0);
                    return;
                }
            }

            int selectedStart = mRxMDEditText.getSelectionStart();
            char c = mRxMDEditText.getText().charAt(position1 >= mRxMDEditText.length() ? mRxMDEditText.length() - 1 : position1);
            if (c == '\n') {
                mRxMDEditText.getText().insert(position1, "\n```");
            } else {
                mRxMDEditText.getText().insert(position1, "\n```\n");
            }
            mRxMDEditText.getText().insert(position0, "```\n");
            mRxMDEditText.setSelection(selectedStart + "```\n".length(), selectedStart + "```\n".length());
        } else if (end - start > 6) {
            Editable editable = mRxMDEditText.getText();
            if ("```".equals(editable.subSequence(start, start + "```".length()).toString()) &&
                    "```".equals(editable.subSequence(end - "```".length(), end).toString())) {
                int selectedStart = mRxMDEditText.getSelectionStart();
                int selectedEnd = mRxMDEditText.getSelectionEnd();
                mRxMDEditText.getText().delete(end - "\n```".length(), end);
                mRxMDEditText.getText().delete(start, start + "```\n".length());
                mRxMDEditText.setSelection(selectedStart, selectedEnd - 8);
                return;
            }

            code(start, end);
        } else {
            code(start, end);
        }
    }

    private void code(int start, int end) {
        int selectedStart = mRxMDEditText.getSelectionStart();
        int selectedEnd = mRxMDEditText.getSelectionEnd();
        int endAdd = 0;
        char c = mRxMDEditText.getText().charAt(end >= mRxMDEditText.length() ? mRxMDEditText.length() - 1 : end);
        if (c == '\n') {
            mRxMDEditText.getText().insert(end, "\n```");
            endAdd += 4;
        } else {
            mRxMDEditText.getText().insert(end, "\n```\n");
            endAdd += 5;
            selectedStart = selectedStart + 1;
        }
        char c1 = mRxMDEditText.getText().charAt(start - 1 < 0 ? 0 : start - 1);
        if (c1 == '\n' || start - 1 < 0) {
            mRxMDEditText.getText().insert(start, "```\n");
            endAdd += 4;
        } else {
            mRxMDEditText.getText().insert(start, "\n```\n");
            endAdd += 4;
        }
        mRxMDEditText.setSelection(selectedStart, selectedEnd + endAdd);
    }
}
