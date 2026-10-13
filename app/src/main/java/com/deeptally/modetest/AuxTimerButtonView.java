package com.deeptally.modetest;

import android.content.*;
import android.graphics.*;
import android.graphics.Region;
import android.view.*;

public class AuxTimerButtonView extends View {
  private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
  private final Path shape=new Path();
  private final Region hit=new Region();
  private final String line1,line2;
  private boolean pressedInside=false;

  public AuxTimerButtonView(Context c,String first,String second){
    super(c);line1=first;line2=second;setClickable(true);
    setContentDescription(first+" "+second.replace("…",""));
  }

  private float dp(float v){return v*getResources().getDisplayMetrics().density;}
  private float sp(float v){return v*getResources().getDisplayMetrics().scaledDensity;}

  protected void onSizeChanged(int w,int h,int oldw,int oldh){
    super.onSizeChanged(w,h,oldw,oldh);
    float cx=w/2f,cy=h/2f;
    float visualRadius=dp(52),touchRadius=dp(60);

    // The main timer center sits 160 dp below this button center.
    // Excluding a 140 dp radius around it leaves an 8 dp visual gap
    // from the main timer's 132 dp painted radius.
    float bigCx=cx,bigCy=cy+dp(160);

    Path button=new Path();button.addCircle(cx,cy,visualRadius,Path.Direction.CW);
    Path visualExclusion=new Path();
    visualExclusion.addCircle(bigCx,bigCy,dp(140),Path.Direction.CW);
    shape.set(button);shape.op(visualExclusion,Path.Op.DIFFERENCE);

    // Give the secondary button a forgiving touch target, but never let
    // that invisible target spill into the 8 dp gap or main timer.
    Path touch=new Path();touch.addCircle(cx,cy,touchRadius,Path.Direction.CW);
    Path touchExclusion=new Path();
    touchExclusion.addCircle(bigCx,bigCy,dp(140),Path.Direction.CW);
    touch.op(touchExclusion,Path.Op.DIFFERENCE);
    Region clip=new Region(0,0,w,h);hit.setPath(touch,clip);
  }

  protected void onDraw(Canvas c){
    super.onDraw(c);
    p.setStyle(Paint.Style.FILL);
    p.setColor(Color.rgb(85,122,110));
    c.drawPath(shape,p);

    p.setTextAlign(Paint.Align.CENTER);
    p.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
    p.setTextSize(sp(13f));
    p.setColor(Color.WHITE);
    float x=getWidth()/2f;
    float mid=getHeight()/2f-dp(8);
    c.drawText(line1,x,mid-sp(2),p);
    c.drawText(line2,x,mid+sp(13),p);
  }

  public boolean onTouchEvent(MotionEvent e){
    int x=Math.round(e.getX()),y=Math.round(e.getY());
    if(e.getAction()==MotionEvent.ACTION_DOWN){
      pressedInside=hit.contains(x,y);
      if(pressedInside){setPressed(true);return true;}
      return false;
    }
    if(e.getAction()==MotionEvent.ACTION_MOVE){
      if(pressedInside)setPressed(hit.contains(x,y));
      return pressedInside;
    }
    if(e.getAction()==MotionEvent.ACTION_UP){
      boolean fire=pressedInside&&hit.contains(x,y);setPressed(false);pressedInside=false;
      if(fire){performClick();return true;}
      return false;
    }
    if(e.getAction()==MotionEvent.ACTION_CANCEL){setPressed(false);pressedInside=false;return false;}
    return pressedInside;
  }

  public boolean performClick(){super.performClick();return true;}
}
