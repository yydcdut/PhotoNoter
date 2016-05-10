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
    private static final String KEY0 = "* ";
    private static final String KEY1 = "+ ";
    private static final String KEY2 = "- ";

    private static final int START_POSITION = 2;

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
        ssb.delete(0, START_POSITION);
        ssb.setSpan(new BulletSpan(10, Color.BLACK), 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ssb;
    }

    @Override
    public String toString() {
        return "UnOrderListGrammar{}";
    }
}
