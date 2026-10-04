package com.deeptally.modetest;
import java.time.*;import java.time.format.*;import java.time.temporal.*;import java.util.*;
public final class TimeUtils {
  public static final long THRESHOLD_MS=30L*60L*1000L;
  private static final DateTimeFormatter TF=DateTimeFormatter.ofPattern("HH:mm");
  private static final DateTimeFormatter DF=DateTimeFormatter.ofPattern("MMM d, yyyy",Locale.ENGLISH);
  private static final DateTimeFormatter DLF=DateTimeFormatter.ofPattern("EEEE, MMM d",Locale.ENGLISH);
  private static final DateTimeFormatter MD=DateTimeFormatter.ofPattern("MMM d",Locale.ENGLISH);
  private static final DateTimeFormatter MDY=DateTimeFormatter.ofPattern("MMM d, yyyy",Locale.ENGLISH);
  private static final DateTimeFormatter DAY_SHORT=DateTimeFormatter.ofPattern("EEE M/d",Locale.ENGLISH);
  private static final DateTimeFormatter MONTH_YEAR=DateTimeFormatter.ofPattern("MMMM yyyy",Locale.ENGLISH);
  private static final DateTimeFormatter MONTH_NAME=DateTimeFormatter.ofPattern("MMMM",Locale.ENGLISH);
  private static final DateTimeFormatter MONTH_SHORT=DateTimeFormatter.ofPattern("MMM",Locale.ENGLISH);
  public static LocalDate date(long ms){return Instant.ofEpochMilli(ms).atZone(ZoneId.systemDefault()).toLocalDate();}
  public static long start(LocalDate d){return d.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();}
  public static LocalDate monday(LocalDate d){return d.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));}
  public static String time(long ms){return Instant.ofEpochMilli(ms).atZone(ZoneId.systemDefault()).format(TF);}
  public static String range(Session s){String a=time(s.startMs),b=time(s.endMs); long days=ChronoUnit.DAYS.between(date(s.startMs),date(s.endMs));return days>0?a+" – "+b+" (+"+days+")":a+" – "+b;}
  public static String compact(long ms){long m=Math.max(0,ms)/60000,h=m/60;return String.format(Locale.US,"%02d:%02d",h,m%60);}
  public static String natural(long ms){long m=Math.max(0,ms)/60000,h=m/60,r=m%60;if(h==0)return m+(m==1?" min":" mins");if(r==0)return h+(h==1?" hour":" hours");return h+(h==1?" hour ":" hours ")+r+(r==1?" min":" mins");}
  public static String timer(long ms){long s=Math.max(0,ms)/1000;return String.format(Locale.US,"%02d:%02d:%02d",s/3600,(s%3600)/60,s%60);}
  public static long total(List<Session> xs){long t=0;for(Session s:xs)if(s.valid())t+=s.durationMs();return t;}
  public static String hist(LocalDate d){return d.format(DF);}
  public static String day(LocalDate d){return d.format(DLF);}
  public static String week(LocalDate m){LocalDate s=m.plusDays(6);return m.getMonth()==s.getMonth()?m.getMonth().toString().substring(0,3)+" "+m.getDayOfMonth()+" – "+s.getDayOfMonth():m.getMonth().toString().substring(0,3)+" "+m.getDayOfMonth()+" – "+s.getMonth().toString().substring(0,3)+" "+s.getDayOfMonth();}
  public static String weekWithYear(LocalDate m){LocalDate s=m.plusDays(6);return m.getYear()==s.getYear()?m.format(MD)+" – "+s.format(MDY):m.format(MDY)+" – "+s.format(MDY);}
  public static String shortWeek(LocalDate m){LocalDate s=m.plusDays(6);return m.getMonthValue()+"/"+m.getDayOfMonth()+"–"+s.getMonthValue()+"/"+s.getDayOfMonth();}
  public static String dayShort(LocalDate d){return d.format(DAY_SHORT);}
  public static String monthYear(YearMonth m){return m.format(MONTH_YEAR);}
  public static String monthName(YearMonth m){return m.format(MONTH_NAME);}
  public static String monthShort(YearMonth m){return m.format(MONTH_SHORT);}
  public static int isoWeek(LocalDate d){return d.get(WeekFields.ISO.weekOfWeekBasedYear());}
  public static int isoWeekYear(LocalDate d){return d.get(WeekFields.ISO.weekBasedYear());}
}
