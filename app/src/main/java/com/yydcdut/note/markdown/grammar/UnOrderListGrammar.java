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
class UnOrderListGrammar implements IGrammar {
    public static final String KEY0 = "* ";
    public static final String KEY1 = "+ ";
    public static final String KEY2 = "- ";

    private static final int START_POSITION = 2;

    @Nullable
    @Override
    public SpannableStringBuilder format(@Nullable String text) {
        if (TextUtils.isEmpty(text)) {
            return new SpannableStringBuilder("");
        }
        if (!isMatch(text)) {
            return new SpannableStringBuilder(text);
        }
        SpannableStringBuilder ssb = new SpannableStringBuilder(text.substring(START_POSITION, text.length()));
        ssb.setSpan(new BulletSpan(10, Color.BLACK), 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ssb;
    }

    @Override
    public boolean isMatch(@Nullable String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        return text.startsWith(KEY0) ||
                text.startsWith(KEY1) ||
                text.startsWith(KEY2);
    }

    @Nullable
    @Override
    public SpannableStringBuilder format(@Nullable SpannableStringBuilder ssb) {
        return ssb;
    }
}
