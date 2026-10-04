package com.deeptally.modetest;
import android.content.*;import android.graphics.*;import android.view.*;
public class MonthChartView extends View{
  public interface Listener{void week(int i);}
  private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);private long[] v=new long[0];private String[] labels=new String[0];private int sel;private Listener l;private float downX,downY;
  public MonthChartView(Context c){super(c);setClickable(true);}
  public void set(long[] x,String[] names,int s){v=x.clone();labels=names.clone();sel=Math.max(0,Math.min(v.length-1,s));invalidate();}
  public void listener(Listener x){l=x;}
  private float dp(float x){return x*getResources().getDisplayMetrics().density;}private float sp(float x){return x*getResources().getDisplayMetrics().scaledDensity;}
  protected void onMeasure(int w,int h){setMeasuredDimension(resolveSize((int)dp(340),w),resolveSize((int)dp(280),h));}
  protected void onDraw(Canvas c){
    if(v.length==0)return;float L=dp(12),R=getWidth()-dp(12),B=getHeight()-dp(72),slot=(R-L)/v.length,bw=Math.min(dp(30),slot*.48f),top=dp(20);long max=3600000;for(long x:v)max=Math.max(max,x);
    p.setColor(Color.rgb(222,227,229));c.drawLine(L,B+dp(2),R,B+dp(2),p);
    for(int i=0;i<v.length;i++){float cx=L+slot*i+slot/2f,h=v[i]==0?dp(3):Math.max(dp(8),(B-top)*(v[i]/(float)max));p.setColor(i==sel?Color.rgb(65,147,122):Color.rgb(191,208,203));p.setStyle(Paint.Style.FILL);c.drawRoundRect(new RectF(cx-bw/2,B-h,cx+bw/2,B),dp(8),dp(8),p);
      p.setTextAlign(Paint.Align.CENTER);p.setColor(Color.rgb(61,68,73));p.setTextSize(sp(10));c.drawText(labels[i],cx,B+dp(23),p);p.setTextSize(sp(11));p.setColor(Color.rgb(104,112,117));c.drawText(v[i]==0?"—":TimeUtils.compact(v[i]),cx,B+dp(45),p);}
  }
  public boolean onTouchEvent(MotionEvent e){if(e.getAction()==MotionEvent.ACTION_DOWN){downX=e.getX();downY=e.getY();return true;}if(e.getAction()==MotionEvent.ACTION_UP){if(v.length>0&&Math.abs(e.getX()-downX)<dp(12)&&Math.abs(e.getY()-downY)<dp(12)){float L=dp(12),slot=(getWidth()-dp(24))/v.length;int i=Math.max(0,Math.min(v.length-1,(int)((e.getX()-L)/slot)));sel=i;invalidate();if(l!=null)l.week(i);performClick();}return true;}return true;}
  public boolean performClick(){super.performClick();return true;}
}
