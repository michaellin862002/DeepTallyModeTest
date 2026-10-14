package com.deeptally.modetest;

import android.content.*;
import android.graphics.*;
import android.view.*;

public class TimerClusterGlowView extends View {
  private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
  private boolean running=false;
  private long elapsed=0;

  public TimerClusterGlowView(Context c){
    super(c);
    setClickable(false);
    setFocusable(false);
    setLayerType(View.LAYER_TYPE_SOFTWARE,null);
  }

  private float dp(float v){return v*getResources().getDisplayMetrics().density;}

  public void setState(boolean r,long e){
    running=r;elapsed=Math.max(0,e);invalidate();
  }

  protected void onDraw(Canvas c){
    super.onDraw(c);
    if(!running)return;

    float cx=getWidth()/2f,cy=getHeight()/2f;
    float bigCy=cy+dp(16);
    float bigR=dp(132);
    float smallCy=cy-dp(113);
    float smallR=dp(61.5f);

    Path small=new Path();
    small.addCircle(cx,smallCy,smallR,Path.Direction.CW);
    Path exclusion=new Path();
    exclusion.addCircle(cx,bigCy,dp(140),Path.Direction.CW);
    small.op(exclusion,Path.Op.DIFFERENCE);

    int base=elapsed>=TimeUtils.THRESHOLD_MS?Color.rgb(85,122,110):Color.rgb(88,112,109);
    int r=Color.red(base),g=Color.green(base),b=Color.blue(base);

    p.setStyle(Paint.Style.STROKE);
    p.setStrokeCap(Paint.Cap.ROUND);
    p.setStrokeJoin(Paint.Join.ROUND);
    p.setStrokeWidth(dp(2.2f));
    p.setColor(Color.argb(72,r,g,b));
    p.setShadowLayer(dp(13),0,0,Color.argb(92,r,g,b));

    c.drawCircle(cx,bigCy,bigR+dp(1),p);
    c.drawPath(small,p);

    p.clearShadowLayer();
    p.setStrokeWidth(dp(5));
    p.setColor(Color.argb(18,r,g,b));
    c.drawCircle(cx,bigCy,bigR+dp(3),p);
    c.drawPath(small,p);
  }
}
