package com.yydcdut.note.markdown.grammar;

import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;

import java.util.regex.Pattern;

/**
 * Created by yuyidong on 16/5/3.
 */
class BoldGrammar implements IGrammar {
    public static final String KEY = "**";

    @Override
    public boolean isMatch(@Nullable String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        if (!text.contains(KEY)) {
            return false;
        }
        Pattern pattern = Pattern.compile(".*[\\*]{2}.*[\\*]{2}.*");
        return pattern.matcher(text).matches();
    }

    @Nullable
    @Override
    public SpannableStringBuilder format(@Nullable SpannableStringBuilder ssb) {
        if (ssb == null) {
            return new SpannableStringBuilder("");
        }
        String text = ssb.toString();
        if (TextUtils.isEmpty(text)) {
            return ssb;
        }
        if (!text.contains(KEY)) {
            return ssb;
        }
        if (!isMatch(text)) {
            return ssb;
        }
        return complex(text, ssb);
    }

    private SpannableStringBuilder complex(String text, SpannableStringBuilder ssb) {
        SpannableStringBuilder tmp = new SpannableStringBuilder();
        String tmpTotal = text;
        while (true) {
            int positionHeader = tmpTotal.indexOf(KEY);
            if (positionHeader == -1) {
                tmp.append(tmpTotal.substring(0, tmpTotal.length()));
                break;
            }
            tmp.append(tmpTotal.substring(0, positionHeader));
            int index = tmp.length();
            tmpTotal = tmpTotal.substring(positionHeader + KEY.length(), tmpTotal.length());
            int positionFooter = tmpTotal.indexOf(KEY);
            if (positionFooter != -1) {
                ssb.delete(tmp.length(), tmp.length() + KEY.length());
                tmp.append(tmpTotal.substring(0, positionFooter));
                ssb.setSpan(new StyleSpan(Typeface.BOLD), index, tmp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb.delete(tmp.length(), tmp.length() + KEY.length());
            } else {
                tmp.append(KEY);
                tmp.append(tmpTotal.substring(0, tmpTotal.length()));
                break;
            }
            tmpTotal = tmpTotal.substring(positionFooter + KEY.length(), tmpTotal.length());
        }
        return ssb;
    }

    @Override
    public String toString() {
        return "BoldGrammar{}";
    }
}
