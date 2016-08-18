package com.yydcdut.note.markdown;

import android.widget.Toast;

import com.yydcdut.rxmarkdown.RxMDConfiguration;
import com.yydcdut.rxmarkdown.RxMDEditText;
import com.yydcdut.rxmarkdown.span.MDOrderListSpan;
import com.yydcdut.rxmarkdown.span.MDUnOrderListSpan;

/**
 * Created by yuyidong on 16/8/17.
 */
public class ListController {
    private RxMDEditText mRxMDEditText;
    private RxMDConfiguration mRxMDConfiguration;

    public ListController(RxMDEditText rxMDEditText, RxMDConfiguration rxMDConfiguration) {
        mRxMDEditText = rxMDEditText;
        mRxMDConfiguration = rxMDConfiguration;
    }

    public void doUnOrderList() {
        int start = mRxMDEditText.getSelectionStart();
        int end = mRxMDEditText.getSelectionEnd();
        if (start == end) {
            MDUnOrderListSpan mdUnOrderListSpan = Utils.getSpans(mRxMDEditText, start, end, MDUnOrderListSpan.class);
            int position = Utils.findBeforeNewLineChar(mRxMDEditText.getText(), start) + 1;
            if (mdUnOrderListSpan != null) {
                if (mdUnOrderListSpan.getNested() == 0) {
                    mRxMDEditText.getText().delete(position, position + "* ".length());
                    return;
                }
                mRxMDEditText.getText().delete(position, position + 1);
                return;
            }
            mRxMDEditText.getText().insert(position, "* ");
        } else {
            int position0 = Utils.findBeforeNewLineChar(mRxMDEditText.getText(), start) + 1;
            int position00 = Utils.findBeforeNewLineChar(mRxMDEditText.getText(), end) + 1;
            if (position0 != position00) {
                Toast.makeText(mRxMDEditText.getContext(), "无法操作多行", Toast.LENGTH_SHORT).show();
                return;
            }
//            int selectedStart = mRxMDEditText.getSelectionStart();
//            int selectedEnd = mRxMDEditText.getSelectionEnd();
            MDUnOrderListSpan mdUnOrderListSpan = Utils.getSpans(mRxMDEditText, start, end, MDUnOrderListSpan.class);
            if (mdUnOrderListSpan != null) {
                if (mdUnOrderListSpan.getNested() == 0) {
                    mRxMDEditText.getText().delete(position0, position0 + "* ".length());
//                    mRxMDEditText.setSelection(selectedStart - "* ".length(), selectedEnd - "* ".length());
                    return;
                }
                mRxMDEditText.getText().delete(position0, position0 + 1);
//                mRxMDEditText.setSelection(selectedStart - 1, selectedEnd - 1);
                return;
            }
            mRxMDEditText.getText().insert(position0, "* ");
//            mRxMDEditText.setSelection(selectedStart + "* ".length(), selectedEnd + "* ".length());
        }
    }

    public void doOrderList() {
        int start = mRxMDEditText.getSelectionStart();
        int end = mRxMDEditText.getSelectionEnd();
        if (start == end) {
            MDOrderListSpan mdOrderListSpan = Utils.getSpans(mRxMDEditText, start, end, MDOrderListSpan.class);
            int position = Utils.findBeforeNewLineChar(mRxMDEditText.getText(), start) + 1;
            if (mdOrderListSpan != null) {
                mRxMDEditText.getText().delete(position, position + mdOrderListSpan.getNested() + (mdOrderListSpan.getNumber() / 10 + 1) + ". ".length());
                return;
            }
            if (position == 0) {
                mRxMDEditText.getText().insert(position, "1. ");
            } else {
                MDOrderListSpan mdBeforeLineOrderListSpan = Utils.getSpans(mRxMDEditText, position - 1, position - 1, MDOrderListSpan.class);
                if (mdBeforeLineOrderListSpan != null) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < mdBeforeLineOrderListSpan.getNested(); i++) {
                        sb.append(" ");
                    }
                    sb.append((mdBeforeLineOrderListSpan.getNumber() + 1)).append(". ");
                    mRxMDEditText.getText().insert(position, sb.toString());
                } else {
                    mRxMDEditText.getText().insert(position, "1. ");
                }
            }
        } else {
            int position0 = Utils.findBeforeNewLineChar(mRxMDEditText.getText(), start) + 1;
            int position00 = Utils.findBeforeNewLineChar(mRxMDEditText.getText(), end) + 1;
            if (position0 != position00) {
                Toast.makeText(mRxMDEditText.getContext(), "无法操作多行", Toast.LENGTH_SHORT).show();
                return;
            }
//            int selectedStart = mRxMDEditText.getSelectionStart();
//            int selectedEnd = mRxMDEditText.getSelectionEnd();
            MDOrderListSpan mdOrderListSpan = Utils.getSpans(mRxMDEditText, start, end, MDOrderListSpan.class);
            if (mdOrderListSpan != null) {
                if (mdOrderListSpan.getNested() == 0) {
                    int deleteLength = position0 + mdOrderListSpan.getNested() + (mdOrderListSpan.getNumber() / 10 + 1) + ". ".length();
                    mRxMDEditText.getText().delete(position0, deleteLength);
//                    mRxMDEditText.setSelection(selectedStart - deleteLength, selectedEnd - deleteLength);
                    return;
                }
                mRxMDEditText.getText().delete(position0, position0 + 1);
//                mRxMDEditText.setSelection(selectedStart - 1, selectedEnd - 1);
                return;
            }
            if (position0 == 0) {
                mRxMDEditText.getText().insert(position0, "1. ");
            } else {
                MDOrderListSpan mdBeforeLineOrderListSpan = Utils.getSpans(mRxMDEditText, position0 - 1, position0 - 1, MDOrderListSpan.class);
                if (mdBeforeLineOrderListSpan != null) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < mdBeforeLineOrderListSpan.getNested(); i++) {
                        sb.append(" ");
                    }
                    sb.append((mdBeforeLineOrderListSpan.getNumber() + 1)).append(". ");
                    mRxMDEditText.getText().insert(position0, sb.toString());
//                    mRxMDEditText.setSelection(selectedStart + sb.length(), selectedEnd + sb.length());
                } else {
                    mRxMDEditText.getText().insert(position0, "1. ");
//                    mRxMDEditText.setSelection(selectedStart + "1. ".length(), selectedEnd + "1. ".length());
                }
            }
        }
    }
}
