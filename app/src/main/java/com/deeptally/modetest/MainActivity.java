package com.deeptally.modetest;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MainActivity extends Activity {
    private static final String CHANNEL_ID = "deep_tally_mode_test";
    private static final int START_ID = 1001;
    private static final int STOP_ID = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNotificationChannel();

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(dp(24), dp(48), dp(24), dp(24));
        root.setBackgroundColor(Color.rgb(247,247,247));

        TextView title = new TextView(this);
        title.setText("Deep Tally Routine Test");
        title.setTextSize(28);
        title.setTextColor(Color.BLACK);
        title.setGravity(Gravity.CENTER);
        root.addView(title, params(0, dp(20)));

        TextView intro = new TextView(this);
        intro.setText(
            "這版用通知觸發 Samsung 日常行程。\n\n" +
            "先在『收到的通知』條件中選本 App，並用關鍵字：\n" +
            "START_DEEP_WORK\n\n" +
            "按 START TEST 後，App 會送出通知。"
        );
        intro.setTextSize(17);
        intro.setTextColor(Color.DKGRAY);
        intro.setLineSpacing(0,1.2f);
        root.addView(intro, params(0, dp(28)));

        Button start = new Button(this);
        start.setText("START TEST");
        start.setTextSize(18);
        start.setMinHeight(dp(60));
        start.setOnClickListener(v -> {
            requestNotifPermissionIfNeeded();
            sendNotification(START_ID, "START_DEEP_WORK", "Deep Tally start trigger");
        });
        root.addView(start, params(0, dp(14)));

        Button clear = new Button(this);
        clear.setText("CLEAR START NOTIFICATION");
        clear.setTextSize(16);
        clear.setMinHeight(dp(56));
        clear.setOnClickListener(v -> NotificationManagerCompat.from(this).cancel(START_ID));
        root.addView(clear, params(0, dp(14)));

        Button stop = new Button(this);
        stop.setText("SEND STOP TEST");
        stop.setTextSize(16);
        stop.setMinHeight(dp(56));
        stop.setOnClickListener(v -> {
            requestNotifPermissionIfNeeded();
            sendNotification(STOP_ID, "STOP_DEEP_WORK", "Deep Tally stop trigger");
        });
        root.addView(stop, params(0, dp(22)));

        TextView note = new TextView(this);
        note.setText(
            "建議先只測 START：\n" +
            "1. Routine 的 If 設為本 App + 關鍵字 START_DEEP_WORK。\n" +
            "2. Then 設為開啟你的『深度工作』Mode。\n" +
            "3. 按 START TEST，看 Mode 是否開啟。\n\n" +
            "如果成功，再測 STOP_DEEP_WORK 是否能做另一條結束用 Routine。"
        );
        note.setTextSize(16);
        note.setTextColor(Color.DKGRAY);
        note.setLineSpacing(0,1.2f);
        root.addView(note, params(0,0));

        setContentView(root);
    }

    private void requestNotifPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= 33 &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.POST_NOTIFICATIONS}, 7);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Deep Tally test triggers",
                NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Test notifications for Samsung Modes & Routines");
            NotificationManager nm =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nm.createNotificationChannel(channel);
        }
    }

    private void sendNotification(int id, String title, String text) {
        if (Build.VERSION.SDK_INT >= 33 &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(false)
            .setOngoing(false);

        NotificationManagerCompat.from(this).notify(id, builder.build());
    }

    private LinearLayout.LayoutParams params(int top, int bottom) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        p.topMargin = top;
        p.bottomMargin = bottom;
        return p;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
