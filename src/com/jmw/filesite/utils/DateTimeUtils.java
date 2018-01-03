package com.jmw.filesite.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtils
{
  public static Timestamp getCurrentDateTime()
  {
    Date nowTime = new Date();
    SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String time = matter.format(nowTime);
    return Timestamp.valueOf(time);
  }

  public static String formatDateTime(Timestamp time, String format)
  {
    SimpleDateFormat matter = new SimpleDateFormat(format);
    return matter.format(time);
  }

  public static String getRelativeDay(String date, int n)
  {
    Date temDate = new Date(Timestamp.valueOf(date).getTime());
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(temDate);
    calendar.add(5, n);
    Date newDate = calendar.getTime();
    SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd");
    return matter.format(newDate);
  }
  public static String getTimeOrder() {
    Date date = new Date();
    SimpleDateFormat matter = new SimpleDateFormat("yyyyMMddHHmmss");
    String time = matter.format(date);
    return time;
  }
}