package com.yydcdut.note.markdown;

import android.text.Editable;
import android.widget.Toast;

import com.yydcdut.rxmarkdown.RxMDConfiguration;
import com.yydcdut.rxmarkdown.RxMDEditText;
import com.yydcdut.rxmarkdown.span.MDHorizontalRulesSpan;

/**
 * Created by yuyidong on 16/8/17.
 */
public class HorizontalRulesController {
    private RxMDEditText mRxMDEditText;
    private RxMDConfiguration mRxMDConfiguration;

    public HorizontalRulesController(RxMDEditText rxMDEditText, RxMDConfiguration rxMDConfiguration) {
        mRxMDEditText = rxMDEditText;
        mRxMDConfiguration = rxMDConfiguration;
    }

    public void doHorizontalRules() {
        int start = mRxMDEditText.getSelectionStart();
        int end = mRxMDEditText.getSelectionEnd();
        int position0 = Utils.findBeforeNewLineChar(mRxMDEditText.getText(), start) + 1;
        int position00 = Utils.findBeforeNewLineChar(mRxMDEditText.getText(), end) + 1;
        if (position0 != position00) {
            Toast.makeText(mRxMDEditText.getContext(), "无法操作多行", Toast.LENGTH_SHORT).show();
            return;
        }
        MDHorizontalRulesSpan mdHorizontalRulesSpan = Utils.getSpans(mRxMDEditText, start, end, MDHorizontalRulesSpan.class);
        if (mdHorizontalRulesSpan != null) {
            Editable editable = mRxMDEditText.getText();
            int spanStart = editable.getSpanStart(mdHorizontalRulesSpan);
            int spanEnd = editable.getSpanEnd(mdHorizontalRulesSpan);
            mRxMDEditText.getText().removeSpan(mdHorizontalRulesSpan);
            mRxMDEditText.getText().delete(spanStart, spanEnd);
        } else {
            char c0 = mRxMDEditText.getText().charAt(start <= 0 ? 0 : start - 1);
            char c1 = mRxMDEditText.getText().charAt(end >= mRxMDEditText.length() - 1 ? mRxMDEditText.length() - 1 : end + 1);
            StringBuilder sb = new StringBuilder();
            if (c0 != '\n' && start != 0) {
                sb.append("\n");
            }
            sb.append("---");
            if (c1 != '\n' || end >= mRxMDEditText.length()) {
                sb.append("\n");
            }
            mRxMDEditText.getText().insert(start, sb.toString());
        }
    }
}
