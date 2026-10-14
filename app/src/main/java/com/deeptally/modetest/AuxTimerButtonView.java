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
    float visualRadius=dp(61.5f),touchRadius=dp(69.5f);

    // The underlying circle grows by 3 dp in diameter while the center
    // distance is reduced by about 1 dp. This keeps the beta27 embedded
    // proportion visually the same instead of exposing extra lower area.
    // The 140 dp exclusion radius still preserves the 8 dp visual gap
    // from the main timer's 132 dp painted radius.
    float centerDistance=dp(129);
    float bigCx=cx,bigCy=cy+centerDistance;

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

    // Re-center the single-line label inside the actual visible cap.
    // The lower visible boundary is the intersection chord between the
    // secondary circle and the 8 dp-offset main-circle exclusion.
    float visualRadius=dp(61.5f),exclusionRadius=dp(140),centerDistance=dp(129);
    float chordY=(visualRadius*visualRadius-exclusionRadius*exclusionRadius+
      centerDistance*centerDistance)/(2f*centerDistance);
    float capCenterY=getHeight()/2f+(chordY-visualRadius)/2f;
    float y=capCenterY-(fm.ascent+fm.descent)/2f;
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
