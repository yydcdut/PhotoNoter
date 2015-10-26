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
     * Text Style里面的时间显示
     *
     * @param time
     * @return
     */
    public static String decodeTimeInTextDetail(long time) {
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat sdfMonth = new SimpleDateFormat("MM");
        sb.append(getChineseMonth(Integer.parseInt(sdfMonth.format(time))));
        SimpleDateFormat sdfDayAndYear = new SimpleDateFormat(" dd. yyyy ");
        sb.append(sdfDayAndYear.format(time));
        sb.append("at");
        SimpleDateFormat sdfHour = new SimpleDateFormat(" hh:mm:ss a");
        sb.append(sdfHour.format(time));
        return sb.toString();
    }

    /**
     * 月份的转化
     *
     * @param month
     * @return
     */
    private static String getChineseMonth(int month) {
        switch (month) {
            case 1:
                return "一月";
            case 2:
                return "二月";
            case 3:
                return "三月";
            case 4:
                return "四月";
            case 5:
                return "五月";
            case 6:
                return "六月";
            case 7:
                return "七月";
            case 8:
                return "八月";
            case 9:
                return "九月";
            case 10:
                return "十月";
            case 11:
                return "十一月";
            case 12:
                return "十二月";
            default:
                return "bug!";
        }
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
