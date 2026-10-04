package com.deeptally.modetest;
import android.content.*;import android.view.*;import android.widget.*;
public class PeriodScrollView extends ScrollView{
  private float downX,downY;private int startY;private Runnable previous,next,current;
  public PeriodScrollView(Context c){super(c);setOverScrollMode(OVER_SCROLL_ALWAYS);}
  public void setPeriodGestures(Runnable p,Runnable n,Runnable c){previous=p;next=n;current=c;}
  private float dp(float x){return x*getResources().getDisplayMetrics().density;}
  public boolean dispatchTouchEvent(MotionEvent e){
    if(e.getAction()==MotionEvent.ACTION_DOWN){downX=e.getX();downY=e.getY();startY=getScrollY();}
    boolean handled=super.dispatchTouchEvent(e);
    if(e.getAction()==MotionEvent.ACTION_UP){
      float dx=e.getX()-downX,dy=e.getY()-downY,ax=Math.abs(dx),ay=Math.abs(dy);
      if(ax>dp(90)&&ax>ay*1.25f){Runnable r=dx>0?previous:next;if(r!=null)post(r);}
      else if(dy>dp(110)&&ay>ax*1.25f&&startY<=dp(2)){if(current!=null)post(current);}
    }
    return handled;
  }
}
