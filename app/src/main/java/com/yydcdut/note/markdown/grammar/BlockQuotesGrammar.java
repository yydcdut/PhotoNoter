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
class BlockQuotesGrammar implements IGrammar {
    private static final String KEY = "> ";

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

    @Nullable
    @Override
    public SpannableStringBuilder format(@Nullable SpannableStringBuilder ssb) {
        if (ssb == null) {
            return new SpannableStringBuilder("");
        }
        String text = ssb.toString();
        if (TextUtils.isEmpty(text)) {
            return new SpannableStringBuilder("");
        }
        if (!isMatch(text)) {
            return ssb;
        }
        ssb.delete(0, KEY.length() - 1);
        ssb.setSpan(new QuoteSpan(Color.GRAY), 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        Utils.marginSSBLeft(ssb, 20);
        return ssb;
    }

    @Override
    public String toString() {
        return "BlockQuotesGrammar{}";
    }
}
