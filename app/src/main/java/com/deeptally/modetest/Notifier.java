package com.deeptally.modetest;
import android.*;import android.app.*;import android.content.*;import android.content.pm.PackageManager;import android.os.*;import androidx.core.app.*;
public final class Notifier{
  public static final String CH="deep_tally_routine_trigger_final_v1";private Notifier(){}
  public static void init(Context c){if(Build.VERSION.SDK_INT>=26){NotificationChannel ch=new NotificationChannel(CH,"Deep Tally routine triggers",NotificationManager.IMPORTANCE_LOW);ch.setSound(null,null);ch.enableVibration(false);ch.enableLights(false);ch.setShowBadge(false);((NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(ch);}}
  public static boolean allowed(Activity a){return Build.VERSION.SDK_INT<33||ActivityCompat.checkSelfPermission(a,Manifest.permission.POST_NOTIFICATIONS)==PackageManager.PERMISSION_GRANTED;}
  public static void request(Activity a){if(!allowed(a)&&Build.VERSION.SDK_INT>=33)ActivityCompat.requestPermissions(a,new String[]{Manifest.permission.POST_NOTIFICATIONS},71);}
  public static void trigger(Activity a,int id,String title){if(!allowed(a))return;NotificationCompat.Builder b=new NotificationCompat.Builder(a,CH).setSmallIcon(android.R.drawable.ic_lock_idle_alarm).setContentTitle(title).setContentText("Deep Tally trigger").setPriority(NotificationCompat.PRIORITY_LOW).setSilent(true).setOnlyAlertOnce(true).setAutoCancel(true).setTimeoutAfter(3000);NotificationManagerCompat.from(a).notify(id,b.build());}
}
