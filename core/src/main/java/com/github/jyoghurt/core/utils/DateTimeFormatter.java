package com.github.jyoghurt.core.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

// date time utils
public class DateTimeFormatter {
    public final String SHORT_DATE = "MM-dd";
    public final String SHORT_TIME = "HH:mm";
    public final String SHORT_DATE_TIME = "yyyy-MM-dd HH:mm";
    public final String FULL_DATE = "yyyy-MM-dd";
    public final String FULL_TIME = "HH:mm:ss";
    public final String FULL_DATE_TIME = "yyyy-MM-dd HH:mm:ss";

    public final SimpleDateFormat SD = new SimpleDateFormat(SHORT_DATE);
    public final SimpleDateFormat ST = new SimpleDateFormat(SHORT_TIME);
    public final SimpleDateFormat SDT = new SimpleDateFormat(SHORT_DATE_TIME);
    public final SimpleDateFormat FD = new SimpleDateFormat(FULL_DATE);
    public final SimpleDateFormat FT = new SimpleDateFormat(FULL_TIME);
    public final SimpleDateFormat FDT = new SimpleDateFormat(FULL_DATE_TIME);

    public String sd() {
        return SD.format(new Date());
    }

    public String st() {
        return ST.format(new Date());
    }

    public String sdt() {
        return SDT.format(new Date());
    }

    public String fd() {
        return FD.format(new Date());
    }

    public String ft() {
        return FT.format(new Date());
    }

    public String fdt() {
        return FDT.format(new Date());
    }

    public String sd(Date date) {
        return SD.format(date);
    }

    public String st(Date date) {
        return ST.format(date);
    }

    public String sdt(Date date) {
        return SDT.format(date);
    }

    public String fd(Date date) {
        return FD.format(date);
    }

    public String ft(Date date) {
        return FT.format(date);
    }

    public String fdt(Date date) {
        return FDT.format(date);
    }

    public Date sd(String source) throws ParseException {
        return SD.parse(source);
    }

    public Date st(String source) throws ParseException {
        return ST.parse(source);
    }

    public Date sdt(String source) throws ParseException {
        return SDT.parse(source);
    }

    public Date fd(String source) throws ParseException {
        return FD.parse(source);
    }

    public Date ft(String source) throws ParseException {
        return FT.parse(source);
    }

    public Date fdt(String source) throws ParseException {
        return FDT.parse(source);
    }

    /**
     * 判断某个时间类型是否是对应的格式
     *
     * @param dateStr 时间串，例如'2016-02-17'
     * @param format  格式，例如'yyyy-MM-dd'
     * @return boolean
     */
    public static boolean isValidFormatDate(String dateStr, String format) {
        Boolean flag = true;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            sdf.setLenient(false);
            sdf.parse(dateStr);
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     * 判断某个时间类型是否是对应的格式
     *
     * @param dateStr 时间串，例如'2016-02-17'
     * @return boolean
     */
    public static boolean isYYYYMMddFormatDate(String dateStr) {
        return isValidFormatDate(dateStr, "yyyy-MM-dd");
    }
}
