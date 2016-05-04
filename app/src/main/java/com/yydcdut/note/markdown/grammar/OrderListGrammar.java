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
public class OrderListGrammar implements IGrammar {
    @Nullable
    @Override
    public SpannableStringBuilder format(@Nullable String text) {
        if (TextUtils.isEmpty(text)) {
            return new SpannableStringBuilder("");
        }
        if (!isMatch(text)) {
            return new SpannableStringBuilder(text);
        }
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(text);
        ssb.setSpan(new BulletSpan(10, Color.TRANSPARENT), 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ssb;
    }

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
}
