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
public class BoldGrammar implements IGrammar {
    public static final String KEY = "**";

    @Nullable
    @Override
    public SpannableStringBuilder format(@Nullable String text) {
        if (TextUtils.isEmpty(text)) {
            return new SpannableStringBuilder("");
        }
        if (!text.contains(KEY)) {
            return new SpannableStringBuilder(text);
        }
        if (!isMatch(text)) {
            return new SpannableStringBuilder(text);
        }
        Pattern pattern = Pattern.compile("[**]*[**]");
        String[] strings = pattern.split(text);
        if (strings.length == 0) {//情况：****
            return simple0();
        } else if (strings.length == 2) {//情况：**text**或者text0**text1**
            return simple2(strings);
        } else if (strings.length >= 3) {//情况：有两个以上的**
            return complex(text);
        }
        return new SpannableStringBuilder(text);
    }

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
        return ssb;
    }

    private SpannableStringBuilder simple0() {
        SpannableStringBuilder ssb = new SpannableStringBuilder("");
        ssb.setSpan(new StyleSpan(Typeface.BOLD), 0, 0, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ssb;
    }

    private SpannableStringBuilder simple2(String[] texts) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(texts[0]);
        int index = ssb.length();
        ssb.append(texts[1]);
        ssb.setSpan(new StyleSpan(Typeface.BOLD), index, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ssb;
    }

    private SpannableStringBuilder complex(String text) {
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        String tmpTotal = text;
        while (true) {
            int positionHeader = tmpTotal.indexOf(KEY);
            if (positionHeader == -1) {
                ssb.append(tmpTotal.substring(0, tmpTotal.length()));
                break;
            }
            ssb.append(tmpTotal.substring(0, positionHeader));
            int index = ssb.length();
            tmpTotal = tmpTotal.substring(positionHeader + KEY.length(), tmpTotal.length());
            int positionFooter = tmpTotal.indexOf(KEY);
            if (positionFooter != -1) {
                ssb.append(tmpTotal.substring(0, positionFooter));
                ssb.setSpan(new StyleSpan(Typeface.BOLD), index, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                ssb.append(KEY);
                ssb.append(tmpTotal.substring(0, tmpTotal.length()));
                break;
            }
            tmpTotal = tmpTotal.substring(positionFooter + KEY.length(), tmpTotal.length());
        }
        return ssb;
    }


}
