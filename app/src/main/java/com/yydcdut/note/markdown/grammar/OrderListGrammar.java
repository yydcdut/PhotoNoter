package com.yydcdut.note.markdown.grammar;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BulletSpan;

/**
 * Created by yuyidong on 16/5/4.
 */
class OrderListGrammar implements IGrammar {

    @Override
    public boolean isMatch(@Nullable String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        if (text.length() < 3) {
            return false;
        }
        if (TextUtils.isDigitsOnly(text.charAt(0) + "")) {
            int dotPosition = 1;
            for (int i = 1; i < text.length(); i++) {
                char c = text.charAt(i);
                if (TextUtils.isDigitsOnly(c + "")) {
                    continue;
                } else {
                    dotPosition = i;
                    break;
                }
            }
            char dot = text.charAt(dotPosition);
            if (dot == '.') {
                if (text.charAt(dotPosition + 1) == ' ') {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
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
        if (!isMatch(text)) {
            return ssb;
        }
        ssb.setSpan(new BulletSpan(10, Color.TRANSPARENT), 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ssb;
    }

    @Override
    public String toString() {
        return "OrderListGrammar{}";
    }
}
