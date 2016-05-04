package com.yydcdut.note.markdown.grammar;

import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;

import com.yydcdut.note.utils.Utils;

/**
 * Created by yuyidong on 16/5/3.
 */
public class HeadLine2Grammar implements IGrammar {
    public static final String KEY = "## ";

    @Nullable
    @Override
    public SpannableStringBuilder format(@Nullable String text) {
        if (TextUtils.isEmpty(text)) {
            return new SpannableStringBuilder("");
        }
        if (!text.startsWith(KEY)) {
            return new SpannableStringBuilder(text);
        }
        if (!isMatch(text)) {
            return new SpannableStringBuilder(text);
        }
        SpannableStringBuilder ssb = new SpannableStringBuilder(text.substring(KEY.length(), text.length()));
        ssb.setSpan(new RelativeSizeSpan(1.3f), 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        Utils.marginSSBLeft(ssb, 10);
        return ssb;
    }

    @Override
    public boolean isMatch(@Nullable String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        return text.startsWith(KEY);
    }
}
