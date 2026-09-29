package com.deeptally.modetest;
import android.content.*;import android.graphics.*;import android.view.*;
public class CircleTimerView extends View{
  private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);private boolean running;private long elapsed;
  public CircleTimerView(Context c){super(c);setClickable(true);}
  public void setState(boolean r,long e){running=r;elapsed=Math.max(0,e);invalidate();}
  private float dp(float v){return v*getResources().getDisplayMetrics().density;}private float sp(float v){return v*getResources().getDisplayMetrics().scaledDensity;}
  private void t(Canvas c,String s,float y,float size,int color,boolean bold){p.reset();p.setAntiAlias(true);p.setTextAlign(Paint.Align.CENTER);p.setTextSize(sp(size));p.setColor(color);p.setTypeface(bold?Typeface.create(Typeface.DEFAULT,Typeface.BOLD):Typeface.DEFAULT);c.drawText(s,getWidth()/2f,y,p);}
  protected void onMeasure(int w,int h){int d=(int)dp(276);int x=Math.min(resolveSize(d,w),resolveSize(d,h));setMeasuredDimension(x,x);}
  protected void onDraw(Canvas c){float x=getWidth()/2f,y=getHeight()/2f,r=Math.min(getWidth(),getHeight())/2f-dp(6);int fill=!running?Color.rgb(24,39,45):(elapsed>=TimeUtils.THRESHOLD_MS?Color.rgb(29,89,75):Color.rgb(35,54,66));p.setStyle(Paint.Style.FILL);p.setColor(fill);c.drawCircle(x,y,r,p);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(dp(2));p.setColor(Color.argb(45,255,255,255));c.drawCircle(x,y,r-dp(1),p);if(!running){t(c,"START",y-sp(3),25,Color.WHITE,true);t(c,"DEEP WORK",y+sp(25),14,Color.rgb(215,225,228),false);}else{t(c,TimeUtils.timer(elapsed),y-sp(22),31,Color.WHITE,true);t(c,elapsed>=TimeUtils.THRESHOLD_MS?"COUNTED ✓":"Counts after 30:00",y+sp(10),14,Color.rgb(215,225,228),false);t(c,"STOP",y+sp(43),17,Color.WHITE,true);}}
}
