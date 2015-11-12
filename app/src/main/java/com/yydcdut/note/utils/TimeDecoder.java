package com.yydcdut.note.utils;

import java.text.SimpleDateFormat;

/**
 * Created by yyd on 15-3-21.
 */
public class TimeDecoder {
    /**
     * Image Style里面的时间显示
     *
     * @param time
     * @return
     */
    public static String decodeTimeInImageDetail(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
        return sdf.format(time);
    }

    /**
     * 通过时间戳给出固定格式的照片名字
     *
     * @param time
     * @return
     */
    public static String getTime4Photo(long time) {
        String s;
        java.text.DateFormat format1 = new java.text.SimpleDateFormat("yyyyMMddhhmmss");
        s = format1.format(time);
        return s;
    }


    public static String calculateDeltaTime(long now, long before) {
        long delta = (now - before) / 1000 / 60 / 60 / 24;
        return (delta + 1) + "";
    }

}
