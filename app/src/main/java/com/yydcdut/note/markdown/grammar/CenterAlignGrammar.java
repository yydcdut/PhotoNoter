package com.yydcdut.note.markdown.grammar;

import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AlignmentSpan;

/**
 * Created by yuyidong on 16/5/4.
 */
class CenterAlignGrammar implements IGrammar {
    public static final String KEY0 = "[";
    public static final String KEY1 = "]";

    @Nullable
    @Override
    public SpannableStringBuilder format(@Nullable String text) {
        if (TextUtils.isEmpty(text)) {
            return new SpannableStringBuilder("");
        }
        if (!text.contains(KEY0) && !text.contains(KEY1)) {
            return new SpannableStringBuilder(text);
        }
        if (!isMatch(text)) {
            return new SpannableStringBuilder(text);
        }
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(text.substring(1, text.length() - 1));
        ssb.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ssb;
    }

    @Override
    public boolean isMatch(@Nullable String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        return text.startsWith(KEY0) && text.endsWith(KEY1);
    }

    @Nullable
    @Override
    public SpannableStringBuilder format(@Nullable SpannableStringBuilder ssb) {
        return ssb;
    }
}
