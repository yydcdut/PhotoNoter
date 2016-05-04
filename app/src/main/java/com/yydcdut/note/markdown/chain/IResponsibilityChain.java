package com.yydcdut.note.markdown.chain;

import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;

/**
 * Created by yuyidong on 16/5/4.
 */
public interface IResponsibilityChain {
    @Nullable
    boolean handleGrammar(@Nullable SpannableStringBuilder ssb);

    boolean addNextHandleGrammar(@Nullable IResponsibilityChain nextHandleGrammar);

    boolean setNextHandleGrammar(@Nullable IResponsibilityChain nextHandleGrammar);

}
