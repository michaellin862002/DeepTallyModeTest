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
    // New channel ID so silent/vibration settings take effect even if an older
    // notification channel already exists on the device.
    private static final String CHANNEL_ID = "deep_tally_routine_trigger_silent_v2";
    private static final int START_ID = 1001;
    private static final int STOP_ID = 1002;
    private static final long TRIGGER_TIMEOUT_MS = 3000L;

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
        title.setText("Deep Tally Routine Test v4");
        title.setTextSize(28);
        title.setTextColor(Color.BLACK);
        title.setGravity(Gravity.CENTER);
        root.addView(title, params(0, dp(20)));

        TextView intro = new TextView(this);
        intro.setText(
            "這版測試「靜音 + 自動消失」的 Routine 觸發通知。\n\n" +
            "START_DEEP_WORK → 開啟深度工作模式\n" +
            "STOP_DEEP_WORK → Ask Bixby 關閉深度工作模式\n\n" +
            "通知本身設定為無聲、無震動，約 3 秒後自動移除。"
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
            if (ensureNotificationPermission()) {
                sendTrigger(START_ID, "START_DEEP_WORK", "Deep Tally start trigger");
            }
        });
        root.addView(start, params(0, dp(14)));

        Button stop = new Button(this);
        stop.setText("STOP TEST");
        stop.setTextSize(18);
        stop.setMinHeight(dp(60));
        stop.setOnClickListener(v -> {
            if (ensureNotificationPermission()) {
                sendTrigger(STOP_ID, "STOP_DEEP_WORK", "Deep Tally stop trigger");
            }
        });
        root.addView(stop, params(0, dp(22)));

        TextView note = new TextView(this);
        note.setText(
            "測試重點：\n" +
            "1. START 是否仍能觸發深度工作 Mode。\n" +
            "2. START 時是否不再震動。\n" +
            "3. 通知是否約 3 秒後自動消失。\n" +
            "4. STOP 是否仍能觸發 Bixby 關閉 Mode。"
        );
        note.setTextSize(16);
        note.setTextColor(Color.DKGRAY);
        note.setLineSpacing(0,1.2f);
        root.addView(note, params(0,0));

        setContentView(root);
    }

    private boolean ensureNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33 &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.POST_NOTIFICATIONS},
                7
            );
            return false;
        }
        return true;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Deep Tally routine triggers (silent)",
                NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Silent trigger notifications for Samsung Modes & Routines");
            channel.setSound(null, null);
            channel.enableVibration(false);
            channel.enableLights(false);
            channel.setShowBadge(false);

            NotificationManager nm =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nm.createNotificationChannel(channel);
        }
    }

    private void sendTrigger(int id, String title, String text) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setSilent(true)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setTimeoutAfter(TRIGGER_TIMEOUT_MS)
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
