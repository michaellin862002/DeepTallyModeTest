package com.deeptally.modetest;
import android.content.*;import android.graphics.*;import android.view.*;
import java.time.*;import java.time.format.*;import java.util.*;

public class HistoryTimelineView extends View{
  public interface Listener{void week(LocalDate weekStart);}
  private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);private LocalDate[] weeks=new LocalDate[0];private long[] totals=new long[0];private long goal;private long[] monthTotals=new long[12];private Listener l;private float downX,downY;
  private static final DateTimeFormatter MON=DateTimeFormatter.ofPattern("MMM",Locale.ENGLISH);
  public HistoryTimelineView(Context c){super(c);setClickable(true);setLayerType(View.LAYER_TYPE_SOFTWARE,null);}
  public void set(LocalDate[] w,long[] t,long g,long[] mt){weeks=w.clone();totals=t.clone();goal=g;monthTotals=mt.clone();requestLayout();invalidate();}
  public void listener(Listener x){l=x;}
  private float dp(float x){return x*getResources().getDisplayMetrics().density;}private float sp(float x){return x*getResources().getDisplayMetrics().scaledDensity;}
  protected void onMeasure(int w,int h){int wanted=(int)(dp(44)+weeks.length*dp(48)+dp(14));setMeasuredDimension(resolveSize((int)dp(340),w),resolveSize(wanted,h));}
  protected void onDraw(Canvas c){
    if(weeks.length==0)return;
    float L=dp(1),top=dp(42),rowH=dp(48),timeRight=L+dp(44),dateX=L+dp(52),barStart=L+dp(114),monthCol=dp(86),barEnd=getWidth()-monthCol-dp(1);
    long max=Math.max(goal,3600000L);for(long x:totals)max=Math.max(max,x);max=(long)(max*1.12f);
    float goalX=barStart+(barEnd-barStart)*(goal/(float)Math.max(1,max));

    p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(dp(1));p.setPathEffect(new DashPathEffect(new float[]{dp(5),dp(5)},0));p.setColor(Color.rgb(135,145,149));c.drawLine(goalX,dp(26),goalX,top+weeks.length*rowH,p);p.setPathEffect(null);p.setStyle(Paint.Style.FILL);
    p.setTextAlign(Paint.Align.CENTER);p.setTypeface(Typeface.DEFAULT_BOLD);p.setTextSize(sp(18));p.setColor(Color.rgb(61,106,92));c.drawText("★",goalX,dp(17),p);
    p.setTextSize(sp(9));p.setColor(Color.GRAY);c.drawText("Goal",goalX,dp(31),p);

    int groupStart=0;
    while(groupStart<weeks.length){
      int month=weeks[groupStart].getMonthValue(),groupEnd=groupStart;
      while(groupEnd+1<weeks.length&&weeks[groupEnd+1].getMonthValue()==month&&weeks[groupEnd+1].getYear()==weeks[groupStart].getYear())groupEnd++;
      float center=top+((groupStart+groupEnd+1)/2f)*rowH;
      YearMonth ym=YearMonth.of(weeks[groupStart].getYear(),month);long mt=monthTotals[month-1];long mins=mt/60000,hours=mins/60,rem=mins%60;
      float mx=barEnd+dp(6);p.setTextAlign(Paint.Align.LEFT);p.setTypeface(Typeface.DEFAULT_BOLD);p.setTextSize(sp(15));p.setColor(Color.rgb(55,62,66));
      c.drawText(ym.format(MON),mx,center-dp(4),p);
      p.setTypeface(Typeface.DEFAULT);p.setTextSize(sp(10.5f));p.setColor(Color.rgb(90,98,103));
      c.drawText(hours+" hrs "+rem+" mins",mx,center+dp(13),p);
      if(groupEnd+1<weeks.length){float y=top+(groupEnd+1)*rowH;p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(dp(1));p.setPathEffect(new DashPathEffect(new float[]{dp(6),dp(5)},0));p.setColor(Color.rgb(190,195,198));c.drawLine(L,y,getWidth()-dp(4),y,p);p.setPathEffect(null);p.setStyle(Paint.Style.FILL);}
      groupStart=groupEnd+1;
    }

    for(int i=0;i<weeks.length;i++){
      float cy=top+i*rowH+rowH/2f;LocalDate w=weeks[i],e=w.plusDays(6);
      p.setTypeface(Typeface.MONOSPACE);p.setTextAlign(Paint.Align.RIGHT);p.setTextSize(sp(13));p.setColor(Color.rgb(49,56,60));c.drawText(TimeUtils.compact(totals[i]),timeRight,cy+dp(5),p);
      p.setTypeface(Typeface.DEFAULT);p.setTextAlign(Paint.Align.LEFT);p.setTextSize(sp(11));p.setColor(Color.rgb(70,77,82));String range=w.getMonthValue()+"/"+w.getDayOfMonth()+"–"+e.getMonthValue()+"/"+e.getDayOfMonth();c.drawText(range,dateX,cy+dp(5),p);
      if(totals[i]>0){float bw=(barEnd-barStart)*(totals[i]/(float)Math.max(1,max));p.setColor(Color.rgb(158,190,180));c.drawRoundRect(new RectF(barStart,cy-dp(7),barStart+Math.max(dp(4),bw),cy+dp(7)),dp(7),dp(7),p);}
    }
  }
  public boolean onTouchEvent(MotionEvent e){
    if(e.getAction()==MotionEvent.ACTION_DOWN){downX=e.getX();downY=e.getY();return true;}
    if(e.getAction()==MotionEvent.ACTION_UP){if(Math.abs(e.getX()-downX)<dp(12)&&Math.abs(e.getY()-downY)<dp(12)){int i=(int)((e.getY()-dp(42))/dp(48));if(i>=0&&i<weeks.length&&e.getX()<getWidth()-dp(86)){if(l!=null)l.week(weeks[i]);performClick();}}return true;}return true;
  }
  public boolean performClick(){super.performClick();return true;}
}
