package com.yydcdut.note.markdown;

import android.support.annotation.Nullable;
import android.text.style.AlignmentSpan;

import com.yydcdut.rxmarkdown.RxMDEditText;
import com.yydcdut.rxmarkdown.span.MDOrderListSpan;
import com.yydcdut.rxmarkdown.span.MDUnOrderListSpan;

/**
 * Created by yuyidong on 16/8/17.
 */
public class Utils {
    /**
     * find '\n' from "start" position
     *
     * @param s     text
     * @param start start position
     * @return the '\n' position
     */
    public static int findNextNewLineChar(CharSequence s, int start) {
        for (int i = start; i < s.length(); i++) {
            if (s.charAt(i) == '\n') {
                return i;
            }
        }
        return -1;
    }

    /**
     * find '\n' before "start" position
     *
     * @param s     text
     * @param start start position
     * @return the '\n' position
     */
    public static int findBeforeNewLineChar(CharSequence s, int start) {
        for (int i = start - 1; i > 0; i--) {
            if (s.charAt(i) == '\n') {
                return i;
            }
        }
        return -1;
    }

    @Nullable
    public static <T> T getSpans(RxMDEditText rxMDEditText, int start, int end, Class<T> clazz) {
        T[] ts = rxMDEditText.getText().getSpans(start, end, clazz);
        if (ts != null && ts.length > 0) {
            return ts[0];
        }
        return null;
    }

    public static boolean hasCenterSpan(RxMDEditText rxMDEditText, int start, int end) {
        AlignmentSpan.Standard centerSpan = Utils.getSpans(rxMDEditText, start, end, AlignmentSpan.Standard.class);
        if (centerSpan == null) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean hasOrderListSpan(RxMDEditText rxMDEditText, int start, int end) {
        MDOrderListSpan orderListSpan = Utils.getSpans(rxMDEditText, start, end, MDOrderListSpan.class);
        if (orderListSpan == null) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean hasUnOrderListSpan(RxMDEditText rxMDEditText, int start, int end) {
        MDUnOrderListSpan unOrderListSpan = Utils.getSpans(rxMDEditText, start, end, MDUnOrderListSpan.class);
        if (unOrderListSpan == null) {
            return false;
        } else {
            return true;
        }
    }


    public static boolean hasTodoDone(RxMDEditText rxMDEditText, int start) {
        CharSequence charSequence = rxMDEditText.getText().subSequence(start, start + "- [x] ".length());
        if (charSequence.toString().equalsIgnoreCase("- [x] ")) {
            return true;
        } else {
            return false;
        }
    }

}
