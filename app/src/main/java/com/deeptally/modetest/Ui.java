package com.deeptally.modetest;
import android.app.*;import android.content.*;import android.graphics.*;import android.graphics.drawable.*;import android.view.*;import android.view.inputmethod.InputMethodManager;import android.widget.*;
public final class Ui{
  private Ui(){}
  public static int dp(Context c,float v){return Math.round(v*c.getResources().getDisplayMetrics().density);}
  public static TextView text(Context c,String s,float sp,int color,boolean bold){TextView t=new TextView(c);t.setText(s);t.setTextSize(sp);t.setTextColor(color);t.setTypeface(bold?Typeface.create(Typeface.DEFAULT,Typeface.BOLD):Typeface.DEFAULT);return t;}
  public static GradientDrawable bg(Context c,int color,float r){GradientDrawable g=new GradientDrawable();g.setColor(color);g.setCornerRadius(dp(c,r));return g;}
  public static ScrollView scroll(Context c){ScrollView s=new ScrollView(c);s.setFillViewport(true);s.setClipToPadding(false);s.setBackgroundColor(Color.rgb(247,248,249));return s;}
  public static LinearLayout page(Context c){LinearLayout p=new LinearLayout(c);p.setOrientation(LinearLayout.VERTICAL);p.setPadding(dp(c,22),dp(c,24),dp(c,22),dp(c,34));p.setBackgroundColor(Color.rgb(247,248,249));return p;}
  public static void space(LinearLayout p,Context c,int h){Space s=new Space(c);p.addView(s,new LinearLayout.LayoutParams(1,dp(c,h)));}
  public static void hideKeyboard(Activity a){View f=a.getCurrentFocus();if(f!=null){InputMethodManager i=(InputMethodManager)a.getSystemService(Context.INPUT_METHOD_SERVICE);i.hideSoftInputFromWindow(f.getWindowToken(),0);}}
}
