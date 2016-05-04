package com.yydcdut.note.markdown.html;

import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Created by yuyidong on 16/5/4.
 */
public class HHeadLine3Grammar implements IHGrammar {
    public static final String KEY = "### ";

    public static final String HTML_BEGIN = "<h3>";
    public static final String HTML_END = "</h3>";

    @Nullable
    @Override
    public String format(@Nullable String text) {
        if (TextUtils.isEmpty(text)) {
            return "";
        }
        if (!text.startsWith(KEY)) {
            return text;
        }
        if (!isMatch(text)) {
            return text;
        }
        StringBuilder sb = new StringBuilder(HTML_BEGIN);
        sb.append(text.substring(KEY.length(), text.length()));
        sb.append(HTML_END);
        return sb.toString();
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
