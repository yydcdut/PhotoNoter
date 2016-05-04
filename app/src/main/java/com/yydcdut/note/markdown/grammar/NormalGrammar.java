package com.yydcdut.note.markdown.grammar;

import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;

/**
 * Created by yuyidong on 16/5/4.
 */
public class NormalGrammar implements IGrammar {

    @Override
    public boolean isMatch(@Nullable String text) {
        return false;
    }

    @Nullable
    @Override
    public SpannableStringBuilder format(@Nullable SpannableStringBuilder ssb) {
        return ssb;
    }

    @Override
    public String toString() {
        return "NormalGrammar{}";
    }
}
