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
  private final boolean leftEar;
  private boolean active=false,pressedInside=false;

  public AuxTimerButtonView(Context c,String first,String second,boolean isLeftEar){
    super(c);line1=first;line2=second;leftEar=isLeftEar;setClickable(true);
    setContentDescription(first+" "+second.replace("…",""));
  }

  private float dp(float v){return v*getResources().getDisplayMetrics().density;}
  private float sp(float v){return v*getResources().getDisplayMetrics().scaledDensity;}

  public void setActive(boolean value){
    active=value;setEnabled(value);setClickable(value);invalidate();
  }

  public boolean isActive(){return active;}

  protected void onSizeChanged(int w,int h,int oldw,int oldh){
    super.onSizeChanged(w,h,oldw,oldh);
    float cx=w/2f,cy=h/2f;
    float visualRadius=dp(42),touchRadius=dp(52);

    float bigCx=cx+(leftEar?dp(94):-dp(94));
    float bigCy=cy+dp(133);

    Path ear=new Path();ear.addCircle(cx,cy,visualRadius,Path.Direction.CW);
    Path visualExclusion=new Path();
    visualExclusion.addCircle(bigCx,bigCy,dp(142),Path.Direction.CW);
    shape.set(ear);shape.op(visualExclusion,Path.Op.DIFFERENCE);

    Path touch=new Path();touch.addCircle(cx,cy,touchRadius,Path.Direction.CW);
    Path touchExclusion=new Path();
    touchExclusion.addCircle(bigCx,bigCy,dp(142),Path.Direction.CW);
    touch.op(touchExclusion,Path.Op.DIFFERENCE);
    Region clip=new Region(0,0,w,h);hit.setPath(touch,clip);
  }

  protected void onDraw(Canvas c){
    super.onDraw(c);
    p.setStyle(Paint.Style.FILL);
    p.setColor(active?Color.rgb(85,122,110):Color.rgb(241,243,243));
    c.drawPath(shape,p);

    p.setTextAlign(Paint.Align.CENTER);
    p.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
    p.setTextSize(sp(11.5f));
    p.setColor(active?Color.WHITE:Color.rgb(165,171,173));
    float x=getWidth()/2f+(leftEar?-dp(3):dp(3));
    float mid=getHeight()/2f-dp(3);
    c.drawText(line1,x,mid-sp(2),p);
    c.drawText(line2,x,mid+sp(12),p);
  }

  public boolean onTouchEvent(MotionEvent e){
    if(!active)return false;
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
