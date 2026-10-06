package com.deeptally.modetest;
import android.content.*;import android.graphics.*;import android.view.*;
import java.time.*;import java.time.format.*;import java.util.*;

public class WeekChartView extends View{
  public interface Listener{void day(int i);}
  private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);private final long[] v=new long[7];private final LocalDate[] dates=new LocalDate[7];
  private int sel=0,today=-1;private Listener l;private float downX,downY;
  private static final DateTimeFormatter WD=DateTimeFormatter.ofPattern("EEE",Locale.ENGLISH);
  public WeekChartView(Context c){super(c);setClickable(true);}
  public void set(long[] x,LocalDate weekStart,int selected,int todayIndex){for(int i=0;i<7;i++){v[i]=x[i];dates[i]=weekStart.plusDays(i);}sel=Math.max(0,Math.min(6,selected));today=todayIndex;invalidate();}
  public void listener(Listener x){l=x;}
  private float dp(float x){return x*getResources().getDisplayMetrics().density;}private float sp(float x){return x*getResources().getDisplayMetrics().scaledDensity;}
  protected void onMeasure(int w,int h){setMeasuredDimension(resolveSize((int)dp(340),w),resolveSize((int)dp(344),h));}
  protected void onDraw(Canvas c){
    float L=dp(4),R=getWidth()-dp(4),rowH=dp(48),timeRight=L+dp(54),weekdayX=L+dp(66),dateX=L+dp(101),barStart=L+dp(148),barEnd=R-dp(4);
    long max=3600000L;for(long x:v)max=Math.max(max,x);
    for(int i=0;i<7;i++){
      float top=i*rowH+dp(2),cy=top+rowH/2f;
      if(i==sel){p.setColor(Color.rgb(232,242,238));c.drawRoundRect(new RectF(L,top,R,top+rowH-dp(4)),dp(12),dp(12),p);}
      else if(i==today){p.setColor(Color.rgb(243,247,245));c.drawRoundRect(new RectF(L,top,R,top+rowH-dp(4)),dp(12),dp(12),p);}
      p.setTypeface(Typeface.MONOSPACE);p.setTextSize(sp(14));p.setTextAlign(Paint.Align.RIGHT);p.setColor(Color.rgb(49,56,60));c.drawText(TimeUtils.compact(v[i]),timeRight,cy+dp(5),p);
      p.setTypeface(Typeface.DEFAULT_BOLD);p.setTextAlign(Paint.Align.LEFT);p.setTextSize(sp(14));c.drawText(dates[i].format(WD),weekdayX,cy+dp(5),p);
      p.setTypeface(Typeface.DEFAULT);p.setTextAlign(Paint.Align.LEFT);p.setTextSize(sp(14));c.drawText(dates[i].getMonthValue()+"/"+dates[i].getDayOfMonth(),dateX,cy+dp(5),p);
      if(v[i]>0){float w=(barEnd-barStart)*(v[i]/(float)max);p.setColor(i==sel?Color.rgb(65,147,122):Color.rgb(158,190,180));c.drawRoundRect(new RectF(barStart,cy-dp(7),barStart+Math.max(dp(4),w),cy+dp(7)),dp(7),dp(7),p);}
      if(i<6){p.setColor(Color.rgb(232,235,236));p.setStrokeWidth(dp(.7f));c.drawLine(L,top+rowH-dp(2),R,top+rowH-dp(2),p);}
    }
  }
  public boolean onTouchEvent(MotionEvent e){
    if(e.getAction()==MotionEvent.ACTION_DOWN){downX=e.getX();downY=e.getY();return true;}
    if(e.getAction()==MotionEvent.ACTION_UP){if(Math.abs(e.getX()-downX)<dp(12)&&Math.abs(e.getY()-downY)<dp(12)){int i=Math.max(0,Math.min(6,(int)(e.getY()/dp(48))));sel=i;invalidate();if(l!=null)l.day(i);performClick();}return true;}return true;
  }
  public boolean performClick(){super.performClick();return true;}
}
