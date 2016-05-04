package com.yydcdut.note.markdown.html;

import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Created by yuyidong on 16/5/4.
 */
public class HCenterAlignGrammar implements IHGrammar {
    public static final String KEY0 = "[";
    public static final String KEY1 = "]";

    public static final String HTML_BEGIN = "<center>";
    public static final String HTML_END = "</center>";

    @Nullable
    @Override
    public String format(@Nullable String text) {
        if (TextUtils.isEmpty(text)) {
            return "";
        }
        if (!text.contains(KEY0) && !text.contains(KEY1)) {
            return text;
        }
        if (!isMatch(text)) {
            return text;
        }
        String tmp = text.trim();
        StringBuilder sb = new StringBuilder();
        sb.append(HTML_BEGIN).append(tmp.substring(1, tmp.length() - 1)).append(HTML_END);
        return sb.toString();
    }

    @Override
    public boolean isMatch(@Nullable String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        if (!text.contains(KEY0) && !text.contains(KEY1)) {
            return false;
        }
        if (text.trim().startsWith(KEY0) && text.trim().endsWith(KEY1)) {
            return true;
        }
        return false;
    }
}
