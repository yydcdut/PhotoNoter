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
    private static final String KEY0 = "[";
    private static final String KEY1 = "]";

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
        if (ssb == null) {
            return new SpannableStringBuilder("");
        }
        String text = ssb.toString();
        if (TextUtils.isEmpty(text)) {
            return ssb;
        }
        if (!text.contains(KEY0) && !text.contains(KEY1)) {
            return ssb;
        }
        if (!isMatch(text)) {
            return ssb;
        }
        ssb.delete(0, 1).delete(ssb.length() - 1, ssb.length());
        ssb.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ssb;
    }

    @Override
    public String toString() {
        return "CenterAlignGrammar{}";
    }
}
