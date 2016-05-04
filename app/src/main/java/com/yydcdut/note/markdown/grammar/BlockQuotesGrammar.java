package com.yydcdut.note.markdown.grammar;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.QuoteSpan;

import com.yydcdut.note.utils.Utils;

/**
 * Created by yuyidong on 16/5/4.
 */
public class BlockQuotesGrammar implements IGrammar {
    public static final String KEY = "> ";

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
        ssb.append(text.substring(KEY.length(), text.length()));
        ssb.setSpan(new QuoteSpan(Color.GRAY), 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        Utils.marginSSBLeft(ssb, 20);
        return ssb;
    }

    @Override
    public boolean isMatch(@Nullable String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        if (!text.startsWith(KEY)) {
            return false;
        }
        return true;
    }
}
