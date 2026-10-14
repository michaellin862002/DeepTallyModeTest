package com.deeptally.modetest;

import android.content.*;
import android.graphics.*;
import android.view.*;

public class TimerClusterGlowView extends View {
  private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
  private boolean running=false;

  public TimerClusterGlowView(Context c){
    super(c);
    setClickable(false);
    setFocusable(false);
    setLayerType(View.LAYER_TYPE_SOFTWARE,null);
  }

  private float dp(float v){return v*getResources().getDisplayMetrics().density;}

  public void setState(boolean r,long e){
    running=r;invalidate();
  }

  protected void onDraw(Canvas c){
    super.onDraw(c);
    if(!running)return;

    float cx=getWidth()/2f,cy=getHeight()/2f;
    float bigCy=cy+dp(16);
    float bigR=dp(132);
    float smallCy=cy-dp(113);
    float smallR=dp(61.5f);

    // One soft cluster silhouette, deliberately drawn with FILL only.
    // There is no stroke/outline, so the glow has no visible line edge.
    Path cluster=new Path();
    cluster.addCircle(cx,bigCy,bigR,Path.Direction.CW);
    cluster.addCircle(cx,smallCy,smallR,Path.Direction.CW);

    // A tiny low-alpha bridge makes both controls read as one ambient
    // focus field without painting a hard contour through the 8 dp gap.
    RectF bridge=new RectF(cx-dp(28),smallCy+dp(42),cx+dp(28),bigCy-bigR+dp(18));
    if(bridge.bottom>bridge.top)cluster.addRoundRect(bridge,dp(22),dp(22),Path.Direction.CW);

    int r=88,g=112,b=109;

    // Broad ambient bloom.
    p.setStyle(Paint.Style.FILL);
    p.setColor(Color.argb(5,r,g,b));
    p.setShadowLayer(dp(28),0,0,Color.argb(38,r,g,b));
    c.drawPath(cluster,p);

    // Slightly denser inner bloom, still with no outline.
    p.setColor(Color.argb(3,r,g,b));
    p.setShadowLayer(dp(16),0,0,Color.argb(34,r,g,b));
    c.drawPath(cluster,p);

    p.clearShadowLayer();
  }
}
