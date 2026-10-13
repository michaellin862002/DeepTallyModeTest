package com.deeptally.modetest;

import android.content.*;
import android.graphics.*;
import android.graphics.Region;
import android.view.*;

public class AuxTimerButtonView extends View {
  private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
  private final Path shape=new Path();
  private final Region hit=new Region();
  private final String label;
  private boolean pressedInside=false;

  public AuxTimerButtonView(Context c,String first,String second){
    super(c);
    label=first+" "+second;
    setClickable(true);
    setContentDescription(first+" "+second.replace("…",""));
  }

  private float dp(float v){return v*getResources().getDisplayMetrics().density;}
  private float sp(float v){return v*getResources().getDisplayMetrics().scaledDensity;}

  protected void onSizeChanged(int w,int h,int oldw,int oldh){
    super.onSizeChanged(w,h,oldw,oldh);
    float cx=w/2f,cy=h/2f;
    float visualRadius=dp(60),touchRadius=dp(68);

    // The main timer center is 130 dp below this button center.
    // A 140 dp exclusion radius stays 8 dp outside the main timer's
    // 132 dp painted radius, while cutting much deeper into this larger
    // secondary circle to create a slimmer horizontal cap.
    float bigCx=cx,bigCy=cy+dp(130);

    Path button=new Path();button.addCircle(cx,cy,visualRadius,Path.Direction.CW);
    Path visualExclusion=new Path();
    visualExclusion.addCircle(bigCx,bigCy,dp(140),Path.Direction.CW);
    shape.set(button);shape.op(visualExclusion,Path.Op.DIFFERENCE);

    // Keep a forgiving touch target on the visible cap, but clip it slightly
    // farther away from the main timer than the painted edge.
    Path touch=new Path();touch.addCircle(cx,cy,touchRadius,Path.Direction.CW);
    Path touchExclusion=new Path();
    touchExclusion.addCircle(bigCx,bigCy,dp(142),Path.Direction.CW);
    touch.op(touchExclusion,Path.Op.DIFFERENCE);
    Region clip=new Region(0,0,w,h);hit.setPath(touch,clip);
  }

  protected void onDraw(Canvas c){
    super.onDraw(c);
    p.setStyle(Paint.Style.FILL);
    p.setColor(Color.rgb(107,140,130));
    c.drawPath(shape,p);

    p.setTextAlign(Paint.Align.CENTER);
    p.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
    p.setTextSize(sp(13.5f));
    p.setColor(Color.WHITE);
    Paint.FontMetrics fm=p.getFontMetrics();
    float x=getWidth()/2f;
    float y=getHeight()/2f-dp(31)-(fm.ascent+fm.descent)/2f;
    c.drawText(label,x,y,p);
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
