package com.deeptally.modetest;
import android.content.*;import android.graphics.*;import android.view.*;
public class WeekChartView extends View{
  public interface Listener{void day(int i);}private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);private final long[] v=new long[7];private int sel;private Listener l;
  public WeekChartView(Context c){super(c);setClickable(true);}public void set(long[] x,int s){for(int i=0;i<7;i++)v[i]=x[i];sel=s;invalidate();}public void listener(Listener x){l=x;}
  private float dp(float x){return x*getResources().getDisplayMetrics().density;}private float sp(float x){return x*getResources().getDisplayMetrics().scaledDensity;}
  protected void onMeasure(int w,int h){setMeasuredDimension(resolveSize((int)dp(340),w),resolveSize((int)dp(280),h));}
  protected void onDraw(Canvas c){float L=dp(18),R=getWidth()-dp(18),B=getHeight()-dp(70),slot=(R-L)/7f,bw=Math.min(dp(27),slot*.55f),top=dp(20);long max=3600000;for(long x:v)max=Math.max(max,x);p.setColor(Color.rgb(222,227,229));c.drawLine(L,B+dp(2),R,B+dp(2),p);String[] d={"Mon","Tue","Wed","Thu","Fri","Sat","Sun"};for(int i=0;i<7;i++){float cx=L+slot*i+slot/2f,h=v[i]==0?dp(3):Math.max(dp(8),(B-top)*(v[i]/(float)max));p.setColor(i==sel?Color.rgb(65,147,122):Color.rgb(191,208,203));p.setStyle(Paint.Style.FILL);c.drawRoundRect(new RectF(cx-bw/2,B-h,cx+bw/2,B),dp(8),dp(8),p);p.setTextAlign(Paint.Align.CENTER);p.setColor(Color.rgb(61,68,73));p.setTextSize(sp(12));c.drawText(d[i],cx,B+dp(24),p);p.setTextSize(sp(11));p.setColor(Color.rgb(104,112,117));c.drawText(v[i]==0?"—":TimeUtils.compact(v[i]),cx,B+dp(45),p);}}
  public boolean onTouchEvent(MotionEvent e){if(e.getAction()==MotionEvent.ACTION_UP){float L=dp(18),slot=(getWidth()-dp(36))/7f;int i=Math.max(0,Math.min(6,(int)((e.getX()-L)/slot)));sel=i;invalidate();if(l!=null)l.day(i);performClick();}return true;}public boolean performClick(){super.performClick();return true;}
}
