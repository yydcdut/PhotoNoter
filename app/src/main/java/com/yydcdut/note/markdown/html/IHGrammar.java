package com.yydcdut.note.markdown.html;

import android.support.annotation.Nullable;

/**
 * Created by yuyidong on 16/5/4.
 */
public interface IHGrammar {
    @Nullable
    String format(@Nullable String text);

    boolean isMatch(@Nullable String text);
}
