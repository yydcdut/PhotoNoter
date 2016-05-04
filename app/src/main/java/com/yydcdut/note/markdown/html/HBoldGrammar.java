package com.yydcdut.note.markdown.html;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.regex.Pattern;

/**
 * Created by yuyidong on 16/5/4.
 */
public class HBoldGrammar implements IHGrammar {
    public static final String KEY = "**";

    public static final String HTML_BEGIN = "<b>";
    public static final String HTML_END = "</b>";

    @Nullable
    @Override
    public String format(@Nullable String text) {
        if (TextUtils.isEmpty(text)) {
            return "";
        }
        if (!text.contains(KEY)) {
            return text;
        }
        if (!isMatch(text)) {
            return text;
        }
        Pattern pattern = Pattern.compile("[**]*[**]");
        String[] strings = pattern.split(text);
        if (strings.length == 0) {//情况：**
            return simple0();
        } else if (strings.length == 2) {//情况：*text*或者text0*text1*
            return simple2(strings);
        } else if (strings.length >= 3) {//情况：有两个以上的*
            return complex(text);
        }
        return text;
    }

    @Override
    public boolean isMatch(@Nullable String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        if (!text.contains(KEY)) {
            return false;
        }
        Pattern pattern = Pattern.compile(".*[\\*]{1}.*[\\*]{1}.*");
        return pattern.matcher(text).matches();
    }

    private String simple0() {
        return HTML_BEGIN + HTML_END;
    }

    private String simple2(String[] texts) {
        StringBuilder sb = new StringBuilder(texts[0]);
        sb.append(HTML_BEGIN).append(texts[1]).append(HTML_END);
        return sb.toString();
    }

    private String complex(String text) {
        StringBuilder sb = new StringBuilder();
        String tmpTotal = text;
        while (true) {
            int positionHeader = tmpTotal.indexOf(KEY);
            if (positionHeader == -1) {
                sb.append(tmpTotal.substring(0, tmpTotal.length()));
                break;
            }
            sb.append(tmpTotal.substring(0, positionHeader));
            tmpTotal = tmpTotal.substring(positionHeader + KEY.length(), tmpTotal.length());
            sb.append(HTML_BEGIN);
            int positionFooter = tmpTotal.indexOf(KEY);
            if (positionFooter != -1) {
                sb.append(tmpTotal.substring(0, positionFooter));
                sb.append(HTML_END);
            } else {
                sb.delete(sb.length() - HTML_BEGIN.length(), sb.length());
                sb.append(KEY);
                sb.append(tmpTotal.substring(0, tmpTotal.length()));
                break;
            }
            tmpTotal = tmpTotal.substring(positionFooter + KEY.length(), tmpTotal.length());
        }
        return sb.toString();
    }
}
