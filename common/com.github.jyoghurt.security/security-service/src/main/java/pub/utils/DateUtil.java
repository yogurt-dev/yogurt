package pub.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Project: 驴鱼社区-车险帮
 * @Package: pub.utils
 * @Description:
 * @author: baoxiaobing@lvyushequ.com
 * @date: 2016-01-21 10:20
 */
public class DateUtil {
    static Calendar calendar = Calendar.getInstance();
    /**
     * 获得当前日期
     * @return
     */
    public static Date getCurrDate(){
        return new Date();
    }

    public static Date getNextYear(){

        Date now = new Date();
        now.setTime(System.currentTimeMillis());
        calendar.setTime(now);
        calendar.add(Calendar.YEAR,1);
        return calendar.getTime();
    }

    /**
     * 获取几天后的日期
     * @param date 当前日期
     * @param count 几天以后
     * @return
     */
    public static Date getNextDay(Date date,int count){
        calendar.setTime(date);
        calendar.add(Calendar.DATE,count);
        return calendar.getTime();
    }
    /**
     * 获取几天前的日期
     * @param date 当前日期
     * @param count 几天以后
     * @return
     */
    public static Date getBeforeDay(Date date,int count){
        calendar.setTime(date);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - count);
        return calendar.getTime();
    }


    public static void main(String args[]){
//        Date date = DateUtil.getNextYear();
//        System.out.println(date.getYear());
//        System.out.println(getCurrDate().getYear());

        System.out.println(converDateToString(getCurrDate(),"YYYY-MM-dd"));

        System.out.println(converDateToString(getNextDay(getCurrDate(),5),"YYYY-MM-dd"));

    }

    /**
     * 日期转换String
     * @param date
     * @param format
     * @return
     */
    public static String converDateToString(Date date,String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }
}
