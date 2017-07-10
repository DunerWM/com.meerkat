package com.meerkat.base.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by wm on 16/9/19.
 */

public class DateUtil {
    public static final String FORMAT_ONE = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_TWO = "yyyy-MM-dd HH:mm";
    public static final String FORMAT_HH = "yyyy-MM-dd HH";
    public static final String FORMAT_THREE = "yyyyMMdd-HHmmss";
    public static final String LONG_DATE_FORMAT = "yyyy-MM-dd";
    public static final String SHORT_DATE_FORMAT = "MM-dd";
    public static final String MINI_DATE_FORMAT = "MM/dd";
    public static final String LONG_TIME_FORMAT = "HH:mm:ss";
    public static final String LONG_DATE_COMPACT_FORMAT = "yyyyMMddHHmmss";
    public static final String LONG_DATE_T_COMPACT_FORMAT = "yyyyMMdd\'T\'HHmmss";
    public static final int SUB_YEAR = 1;
    public static final int SUB_MONTH = 2;
    public static final int SUB_DAY = 5;
    public static final int SUB_HOUR = 10;
    public static final int SUB_MINUTE = 12;
    public static final int SUB_SECOND = 13;
    public static final int SECONDS_PER_DAY = 86400;
    private static final String HOUR_IN_DAY = "HH";
    protected static Log log = LogFactory.getLog(DateUtil.class);
    private int weeks = 0;
    private int MaxDate;
    private int MaxYear;

    public DateUtil() {
    }

    public static Date StringtoDate(String dateStr, String format) {
        Date d = null;
        SimpleDateFormat formater = new SimpleDateFormat(format);

        try {
            formater.setLenient(false);
            d = formater.parse(dateStr);
        } catch (Exception var5) {
            d = null;
        }

        return d;
    }

    public static Date StringtoDate(String dateStr, String format, ParsePosition pos) {
        Date d = null;
        SimpleDateFormat formater = new SimpleDateFormat(format);

        try {
            formater.setLenient(false);
            d = formater.parse(dateStr, pos);
        } catch (Exception var6) {
            d = null;
        }

        return d;
    }

    public static String DateToString(Date date) {
        return DateToString(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static String DateToString(Date date, String format) {
        String result = "";
        SimpleDateFormat formater = new SimpleDateFormat(format);

        try {
            result = formater.format(date);
        } catch (Exception var5) {
            ;
        }

        return result;
    }

    public static String getCurrDate(String format) {
        return DateToString(new Date(), format);
    }

    public static String DateSub(int dateKind, String dateStr, int amount) {
        Date date = StringtoDate(dateStr, "yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(dateKind, amount);
        return DateToString(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
    }

    public static long TimeSub(String firstTime, String secTime) {
        long first = StringtoDate(firstTime, "yyyy-MM-dd HH:mm:ss").getTime();
        long second = StringtoDate(secTime, "yyyy-MM-dd HH:mm:ss").getTime();
        return (second - first) / 1000L;
    }

    public static int getDaysOfMonth(String year, String month) {
        boolean days = false;
        byte days1;
        if (!month.equals("1") && !month.equals("3") && !month.equals("5") && !month.equals("7") && !month.equals("8") && !month.equals("10") && !month.equals("12")) {
            if (!month.equals("4") && !month.equals("6") && !month.equals("9") && !month.equals("11")) {
                if ((Integer.parseInt(year) % 4 != 0 || Integer.parseInt(year) % 100 == 0) && Integer.parseInt(year) % 400 != 0) {
                    days1 = 28;
                } else {
                    days1 = 29;
                }
            } else {
                days1 = 30;
            }
        } else {
            days1 = 31;
        }

        return days1;
    }

    public static int getDaysOfMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);
        return calendar.getActualMaximum(5);
    }

    public static int getToday() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(5);
    }

    public static int getToMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(2) + 1;
    }

    public static String getToMonthToEnglish() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(2) + 1;
        String monthToEnglish;
        switch (month) {
            case 1:
                monthToEnglish = "JAN";
                break;
            case 2:
                monthToEnglish = "FEB";
                break;
            case 3:
                monthToEnglish = "MAR";
                break;
            case 4:
                monthToEnglish = "APR";
                break;
            case 5:
                monthToEnglish = "MAY";
                break;
            case 6:
                monthToEnglish = "JUN";
                break;
            case 7:
                monthToEnglish = "JUL";
                break;
            case 8:
                monthToEnglish = "AUG";
                break;
            case 9:
                monthToEnglish = "SEP";
                break;
            case 10:
                monthToEnglish = "OCT";
                break;
            case 11:
                monthToEnglish = "NOV";
                break;
            default:
                monthToEnglish = "DEC";
        }

        return monthToEnglish;
    }

    public static int getToYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(1);
    }

    public static int getDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(5);
    }

    public static int getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(1);
    }

    public static int getMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(2) + 1;
    }

    public static long dayDiff(Date date1, Date date2) {
        return (date2.getTime() - date1.getTime()) / 86400000L;
    }

    public static int yearDiff(String before, String after) {
        Date beforeDay = StringtoDate(before, "yyyy-MM-dd");
        Date afterDay = StringtoDate(after, "yyyy-MM-dd");
        return getYear(afterDay) - getYear(beforeDay);
    }

    public static int yearDiffCurr(String after) {
        Date beforeDay = new Date();
        Date afterDay = StringtoDate(after, "yyyy-MM-dd");
        return getYear(beforeDay) - getYear(afterDay);
    }

    public static int getFirstWeekdayOfMonth(int year, int month) {
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(7);
        c.set(year, month - 1, 1);
        return c.get(7);
    }

    public static int getLastWeekdayOfMonth(int year, int month) {
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(7);
        c.set(year, month - 1, getDaysOfMonth(year, month));
        return c.get(7);
    }

    public static String getCurrent() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int year = cal.get(1);
        int month = cal.get(2) + 1;
        int day = cal.get(5);
        int hour = cal.get(11);
        int minute = cal.get(12);
        int second = cal.get(13);
        StringBuffer sb = new StringBuffer();
        sb.append(year).append("_").append(StringUtil.addzero(month, 2)).append("_").append(StringUtil.addzero(day, 2)).append("_").append(StringUtil.addzero(hour, 2)).append("_").append(StringUtil.addzero(minute, 2)).append("_").append(StringUtil.addzero(second, 2));
        return sb.toString();
    }

    public static String getNow() {
        Calendar today = Calendar.getInstance();
        return DateToString(today.getTime(), "yyyy-MM-dd HH:mm:ss");
    }

    public static String getAstro(String birth) {
        if (!isDate(birth)) {
            birth = "2000" + birth;
        }

        if (!isDate(birth)) {
            return "";
        } else {
            int month = Integer.parseInt(birth.substring(birth.indexOf("-") + 1, birth.lastIndexOf("-")));
            int day = Integer.parseInt(birth.substring(birth.lastIndexOf("-") + 1));
            log.debug(month + "-" + day);
            String s = "魔羯水瓶双鱼牡羊金牛双子巨蟹狮子处女天秤天蝎射手魔羯";
            int[] arr = new int[]{20, 19, 21, 21, 21, 22, 23, 23, 23, 23, 22, 22};
            int start = month * 2 - (day < arr[month - 1] ? 2 : 0);
            return s.substring(start, start + 2) + "座";
        }
    }

    public static boolean isDate(String date) {
        StringBuffer reg = new StringBuffer("^((\\d{2}(([02468][048])|([13579][26]))-?((((0?");
        reg.append("[13578])|(1[02]))-?((0?[1-9])|([1-2][0-9])|(3[01])))");
        reg.append("|(((0?[469])|(11))-?((0?[1-9])|([1-2][0-9])|(30)))|");
        reg.append("(0?2-?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][12");
        reg.append("35679])|([13579][01345789]))-?((((0?[13578])|(1[02]))");
        reg.append("-?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))");
        reg.append("-?((0?[1-9])|([1-2][0-9])|(30)))|(0?2-?((0?[");
        reg.append("1-9])|(1[0-9])|(2[0-8]))))))");
        Pattern p = Pattern.compile(reg.toString());
        return p.matcher(date).matches();
    }

    public static String getNextMonthFirst(int month) {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar lastDate = Calendar.getInstance();
        lastDate.add(2, month);
        lastDate.set(5, 1);
        str = sdf.format(lastDate.getTime());
        return str;
    }

    public static String getNextMonthSecond(int month) {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar lastDate = Calendar.getInstance();
        lastDate.add(2, month);
        lastDate.set(5, 2);
        str = sdf.format(lastDate.getTime());
        return str;
    }

    public static String getNextMonthEnd(int month) {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar lastDate = Calendar.getInstance();
        lastDate.add(2, month);
        lastDate.set(5, 1);
        lastDate.roll(5, -1);
        str = sdf.format(lastDate.getTime());
        return str;
    }

    public static String getNextSaturday(int weeks) {
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(5, mondayPlus + 7 * weeks + 5);
        Date saturday = currentDate.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String preSaturday = df.format(saturday);
        return preSaturday;
    }

    public static String getNextSunday(int weeks) {
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(5, mondayPlus + 7 * weeks - 1);
        Date sunday = currentDate.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String preSunday = df.format(sunday);
        return preSunday;
    }

    public static String getNextMonday(int weeks) {
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(5, mondayPlus + 7 * weeks);
        Date sunday = currentDate.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String preSunday = df.format(sunday);
        return preSunday;
    }

    public static Date nextMonth(Date date, int months) {
        Calendar cal = Calendar.getInstance();
        if (date != null) {
            cal.setTime(date);
        }

        cal.add(2, months);
        return cal.getTime();
    }

    public static Date nextDay(Date date, int day) {
        Calendar cal = Calendar.getInstance();
        if (date != null) {
            cal.setTime(date);
        }

        cal.add(6, day);
        return cal.getTime();
    }

    public static Date nextWeek(Date date, int week) {
        Calendar cal = Calendar.getInstance();
        if (date != null) {
            cal.setTime(date);
        }

        cal.add(4, week);
        return cal.getTime();
    }

    public static String currDay() {
        return DateToString(new Date(), "yyyy-MM-dd");
    }

    public static String befoDay() {
        return DateToString(nextDay(new Date(), -1), "yyyy-MM-dd");
    }

    public static String afterDay() {
        return DateToString(nextDay(new Date(), 1), "yyyy-MM-dd");
    }

    public static int getDayNum() {
        boolean daynum = false;
        GregorianCalendar gd = new GregorianCalendar();
        Date dt = gd.getTime();
        GregorianCalendar gd1 = new GregorianCalendar(1900, 1, 1);
        Date dt1 = gd1.getTime();
        int daynum1 = (int) ((dt.getTime() - dt1.getTime()) / 86400000L);
        return daynum1;
    }

    public static Date getDateByNum(int day) {
        GregorianCalendar gd = new GregorianCalendar(1900, 1, 1);
        Date date = gd.getTime();
        date = nextDay(date, day);
        return date;
    }

    public static String getTwoDay(String sj1, String sj2) {
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
        long day = 0L;

        try {
            Date e = myFormatter.parse(sj1);
            Date mydate = myFormatter.parse(sj2);
            day = (e.getTime() - mydate.getTime()) / 86400000L;
        } catch (Exception var7) {
            return "";
        }

        return day + "";
    }

    public static String getWeek(String sdate) {
        Date date = strToDate(sdate);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return (new SimpleDateFormat("EEEE")).format(c.getTime());
    }

    public static Boolean isWorkDay(Date date) {
        Boolean isWorkDay = Boolean.valueOf(false);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int hour = c.get(7);
        if (hour != 1 && hour != 7) {
            isWorkDay = Boolean.valueOf(true);
        }

        return isWorkDay;
    }

    public static Date strToDate(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    public static Date strToDate(String strDate, SimpleDateFormat sdf) {
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = sdf.parse(strDate, pos);
        return strtodate;
    }

    public static long getDays(String date1, String date2) {
        if (date1 != null && !date1.equals("")) {
            if (date2 != null && !date2.equals("")) {
                SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
                Date date = null;
                Date mydate = null;

                try {
                    date = myFormatter.parse(date1);
                    mydate = myFormatter.parse(date2);
                } catch (Exception var7) {
                    ;
                }

                long day = (date.getTime() - mydate.getTime()) / 86400000L;
                return day;
            } else {
                return 0L;
            }
        } else {
            return 0L;
        }
    }

    public static String getNowTime(String dateformat) {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateformat);
        String hehe = dateFormat.format(now);
        return hehe;
    }

    private static int getMondayPlus() {
        Calendar cd = Calendar.getInstance();
        int dayOfWeek = cd.get(7) - 1;
        return dayOfWeek == 1 ? 0 : 1 - dayOfWeek;
    }

    public static Date getTomorrow() {
        Calendar current = Calendar.getInstance();
        current.add(5, 1);
        Date tomorrow = current.getTime();
        return tomorrow;
    }

    public static Date getBeforOrAfterDay(int dateNum) {
        Calendar current = Calendar.getInstance();
        current.add(5, dateNum);
        return current.getTime();
    }

    public static String getChineseDesc(Date date) {
        long currentDate = (new DateTime()).getMillis();
        long createDate = (new DateTime(date)).getMillis();
        long sub = currentDate - createDate;
        String val = "刚刚";
        if (sub > 0L) {
            sub /= 1000L;
            if (sub < 60L) {
                val = sub + "秒前";
            } else if (sub >= 60L && sub < 3600L) {
                val = sub / 60L + "分钟前";
            } else if (sub >= 3600L && sub < 86400L) {
                val = sub / 3600L + "小时前";
            } else if (sub > 86400L && sub < 31536000L) {
                val = sub / 86400L + "天前";
            } else {
                val = "1年前";
            }
        }

        return val;
    }

    public static void main(String[] args) {
        for (int i = -12; i <= 18; ++i) {
            System.out.println("获取" + i + "天的日期:" + getWeek(DateToString(nextDay(new Date(), i), "yyyy-MM-dd")));
        }

        System.out.println(86400);
    }

    public static String[][] getPreTwelveList(int year, int month) {
        String[][] list = new String[12][3];
        String start = "";
        String end = "";

        for (int i = 0; i < 12; ++i) {
            int days = getDaysOfMonth(year, month);
            if (month >= 1) {
                start = year + "-" + month + "-01 00:00:00";
                end = year + "-" + month + "-" + days + " 00:00:00";
            } else {
                month = 12;
                start = year - 1 + "-" + month + "-01 00:00:00";
                end = year - 1 + "-" + month + "-" + days + " 00:00:00";
                --year;
            }

            list[12 - i - 1][0] = start;
            list[12 - i - 1][1] = end;
            list[12 - i - 1][2] = year + "/" + month;
            --month;
        }

        return list;
    }

    public static String[][] getPreMonthList(int month, int year) {
        String[][] list = new String[12][3];
        String start = "";
        String end = "";

        for (int i = 0; i < 12; ++i) {
            if (month >= 1) {
                int days = getDaysOfMonth(year, month);
                start = year + "-" + month + "-01 00:00:00";
                end = year + "-" + month + "-" + days + " 00:00:00";
                list[12 - i - 1][0] = start;
                list[12 - i - 1][1] = end;
                list[12 - i - 1][2] = year + "/" + month;
                --month;
            }
        }

        return list;
    }

    public static List<Integer> getPreYear(int beginYear) {
        int preYear = getToYear() - 1;
        int noOfYear = preYear - beginYear + 1;
        ArrayList yearList = new ArrayList(noOfYear);

        for (int year = preYear; year >= beginYear; --year) {
            yearList.add(Integer.valueOf(year));
        }

        return yearList;
    }

    public static List<Integer> getBeginYearToPreYear(int beginYear) {
        int curyear = getToYear();
        int noOfYear = curyear - beginYear + 1;
        ArrayList yearList = new ArrayList(noOfYear);

        for (int year = curyear; year >= beginYear; --year) {
            yearList.add(Integer.valueOf(year));
        }

        return yearList;
    }

    public static Date getBeginOfDay() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Calendar calendar = Calendar.getInstance();

        try {
            calendar.setTime(format.parse(format.format(calendar.getTime())));
        } catch (ParseException var3) {
            log.error(var3.getMessage(), var3);
        }

        return calendar.getTime();
    }

    public static long getCurDayTime(String timeStr) throws IllegalArgumentException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd " + timeStr);
        String dateStr = format.format(new Date());
        Date date = null;

        try {
            date = new Date(dateStr);
        } catch (IllegalArgumentException var5) {
            throw var5;
        }

        return date.getTime();
    }

    public static long StringToTimestamp(String dateString, String format) {
        Date date1;
        try {
            date1 = (new SimpleDateFormat(format)).parse(dateString);
        } catch (ParseException var5) {
            log.error("DateUtil--StringToTimestamp wrong. dateString:" + dateString + "; format:" + format);
            date1 = getBeginOfDay();
        }

        long temp = date1.getTime();
        return temp;
    }

    public static int getAreaTime(Date date) {
        SimpleDateFormat f = new SimpleDateFormat("HH");
        String time = f.format(date);
        int hour = Integer.parseInt(time);
        byte area = 0;
        if (hour <= 6 || hour >= 12) {
            if (hour >= 12 && hour <= 18) {
                area = 1;
            } else {
                area = 2;
            }
        }

        return area;
    }

    public String getDefaultDay() {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar lastDate = Calendar.getInstance();
        lastDate.set(5, 1);
        lastDate.add(2, 1);
        lastDate.add(5, -1);
        str = sdf.format(lastDate.getTime());
        return str;
    }

    public String getPreviousMonthFirst() {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar lastDate = Calendar.getInstance();
        lastDate.set(5, 1);
        lastDate.add(2, -1);
        str = sdf.format(lastDate.getTime());
        return str;
    }

    public String getFirstDayOfMonth() {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar lastDate = Calendar.getInstance();
        lastDate.set(5, 1);
        str = sdf.format(lastDate.getTime());
        return str;
    }

    public String getCurrentWeekday() {
        this.weeks = 0;
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(5, mondayPlus + 6);
        Date monday = currentDate.getTime();
        DateFormat df = DateFormat.getDateInstance();
        String preMonday = df.format(monday);
        return preMonday;
    }

    public String getMondayOFWeek() {
        this.weeks = 0;
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(5, mondayPlus);
        Date monday = currentDate.getTime();
        DateFormat df = DateFormat.getDateInstance();
        String preMonday = df.format(monday);
        return preMonday;
    }

    public String getSaturday() {
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(5, mondayPlus + 7 * this.weeks + 6);
        Date monday = currentDate.getTime();
        DateFormat df = DateFormat.getDateInstance();
        String preMonday = df.format(monday);
        return preMonday;
    }

    public String getPreviousWeekSunday() {
        this.weeks = 0;
        --this.weeks;
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(5, mondayPlus + this.weeks);
        Date monday = currentDate.getTime();
        DateFormat df = DateFormat.getDateInstance();
        String preMonday = df.format(monday);
        return preMonday;
    }

    public String getPreviousWeekday() {
        --this.weeks;
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(5, mondayPlus + 7 * this.weeks);
        Date monday = currentDate.getTime();
        DateFormat df = DateFormat.getDateInstance();
        String preMonday = df.format(monday);
        return preMonday;
    }

    public String getNextMonday() {
        ++this.weeks;
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(5, mondayPlus + 7);
        Date monday = currentDate.getTime();
        DateFormat df = DateFormat.getDateInstance();
        String preMonday = df.format(monday);
        return preMonday;
    }

    public String getNextSunday() {
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(5, mondayPlus + 7 + 6);
        Date monday = currentDate.getTime();
        DateFormat df = DateFormat.getDateInstance();
        String preMonday = df.format(monday);
        return preMonday;
    }

    private int getMonthPlus() {
        Calendar cd = Calendar.getInstance();
        int monthOfNumber = cd.get(5);
        cd.set(5, 1);
        cd.roll(5, -1);
        this.MaxDate = cd.get(5);
        return monthOfNumber == 1 ? -this.MaxDate : 1 - monthOfNumber;
    }

    public String getPreviousMonthEnd() {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar lastDate = Calendar.getInstance();
        lastDate.add(2, -1);
        lastDate.set(5, 1);
        lastDate.roll(5, -1);
        str = sdf.format(lastDate.getTime());
        return str;
    }

    public static String getNextMonthFirst() {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar lastDate = Calendar.getInstance();
        lastDate.add(2, 1);
        lastDate.set(5, 1);
        str = sdf.format(lastDate.getTime());
        return str;
    }

    public String getNextMonthEnd() {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar lastDate = Calendar.getInstance();
        lastDate.add(2, 1);
        lastDate.set(5, 1);
        lastDate.roll(5, -1);
        str = sdf.format(lastDate.getTime());
        return str;
    }

    public String getNextYearEnd() {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar lastDate = Calendar.getInstance();
        lastDate.add(1, 1);
        lastDate.set(6, 1);
        lastDate.roll(6, -1);
        str = sdf.format(lastDate.getTime());
        return str;
    }

    public String getNextYearFirst() {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar lastDate = Calendar.getInstance();
        lastDate.add(1, 1);
        lastDate.set(6, 1);
        str = sdf.format(lastDate.getTime());
        return str;
    }

    private int getMaxYear() {
        Calendar cd = Calendar.getInstance();
        cd.set(6, 1);
        cd.roll(6, -1);
        int MaxYear = cd.get(6);
        return MaxYear;
    }

    private int getYearPlus() {
        Calendar cd = Calendar.getInstance();
        int yearOfNumber = cd.get(6);
        cd.set(6, 1);
        cd.roll(6, -1);
        int MaxYear = cd.get(6);
        return yearOfNumber == 1 ? -MaxYear : 1 - yearOfNumber;
    }

    public String getCurrentYearFirst() {
        int yearPlus = this.getYearPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(5, yearPlus);
        Date yearDay = currentDate.getTime();
        DateFormat df = DateFormat.getDateInstance();
        String preYearDay = df.format(yearDay);
        return preYearDay;
    }

    public String getCurrentYearEnd() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        String years = dateFormat.format(date);
        return years + "-12-31";
    }

    public String getPreviousYearFirst() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        String years = dateFormat.format(date);
        int years_value = Integer.parseInt(years);
        --years_value;
        return years_value + "-1-1";
    }

    public String getPreviousYearEnd() {
        --this.weeks;
        int yearPlus = this.getYearPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(5, yearPlus + this.MaxYear * this.weeks + (this.MaxYear - 1));
        Date yearDay = currentDate.getTime();
        DateFormat df = DateFormat.getDateInstance();
        String preYearDay = df.format(yearDay);
        this.getThisSeasonTime(11);
        return preYearDay;
    }

    public String getThisSeasonFirstDay(int month) {
        int[][] array = new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}, {10, 11, 12}};
        byte season = 1;
        if (month >= 1 && month <= 3) {
            season = 1;
        }

        if (month >= 4 && month <= 6) {
            season = 2;
        }

        if (month >= 7 && month <= 9) {
            season = 3;
        }

        if (month >= 10 && month <= 12) {
            season = 4;
        }

        int start_month = array[season - 1][0];
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        String years = dateFormat.format(date);
        int years_value = Integer.parseInt(years);
        byte start_days = 1;
        return years_value + "-" + start_month + "-" + start_days;
    }

    public String getThisSeasonTime(int month) {
        int[][] array = new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}, {10, 11, 12}};
        byte season = 1;
        if (month >= 1 && month <= 3) {
            season = 1;
        }

        if (month >= 4 && month <= 6) {
            season = 2;
        }

        if (month >= 7 && month <= 9) {
            season = 3;
        }

        if (month >= 10 && month <= 12) {
            season = 4;
        }

        int start_month = array[season - 1][0];
        int end_month = array[season - 1][2];
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        String years = dateFormat.format(date);
        int years_value = Integer.parseInt(years);
        byte start_days = 1;
        int end_days = this.getLastDayOfMonth(years_value, end_month);
        String seasonDate = years_value + "-" + start_month + "-" + start_days + ";" + years_value + "-" + end_month + "-" + end_days;
        return seasonDate;
    }

    private int getLastDayOfMonth(int year, int month) {
        return month != 1 && month != 3 && month != 5 && month != 7 && month != 8 && month != 10 && month != 12 ? (month != 4 && month != 6 && month != 9 && month != 11 ? (month == 2 ? (this.isLeapYear(year) ? 29 : 28) : 0) : 30) : 31;
    }

    public boolean isLeapYear(int year) {
        return year % 4 == 0 && year % 100 != 0 || year % 400 == 0;
    }

    public static Date getForeverDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(1, 100);
        return calendar.getTime();
    }
}

