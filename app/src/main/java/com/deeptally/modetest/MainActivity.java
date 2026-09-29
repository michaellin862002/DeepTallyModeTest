package com.deeptally.modetest;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {
    private TextView statusView;
    private TextView timeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(dp(24), dp(48), dp(24), dp(24));
        root.setBackgroundColor(Color.rgb(247, 247, 247));

        TextView title = new TextView(this);
        title.setText("Deep Tally Mode Test");
        title.setTextSize(28);
        title.setTextColor(Color.BLACK);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        root.addView(title, matchWrap(dp(0), dp(18)));

        TextView intro = new TextView(this);
        intro.setText(
                "用途：測試 Samsung『深度工作』模式是否能以「已開啟應用程式」自動啟動，" +
                "以及離開本 App 後是否仍維持。\n\n" +
                "先到：設定 → 模式與日常行程 → 深度工作 → 自動開啟 → 已開啟應用程式，" +
                "選擇 Deep Tally Mode Test。然後重新開啟本 App。"
        );
        intro.setTextSize(17);
        intro.setTextColor(Color.DKGRAY);
        intro.setLineSpacing(0, 1.25f);
        root.addView(intro, matchWrap(dp(0), dp(28)));

        statusView = new TextView(this);
        statusView.setTextSize(24);
        statusView.setTextColor(Color.BLACK);
        statusView.setGravity(Gravity.CENTER);
        statusView.setTypeface(null, android.graphics.Typeface.BOLD);
        root.addView(statusView, matchWrap(dp(0), dp(8)));

        timeView = new TextView(this);
        timeView.setTextSize(14);
        timeView.setTextColor(Color.GRAY);
        timeView.setGravity(Gravity.CENTER);
        root.addView(timeView, matchWrap(dp(0), dp(26)));

        Button refresh = button("CHECK DND NOW");
        refresh.setOnClickListener(v -> refreshStatus());
        root.addView(refresh, matchWrap(dp(0), dp(12)));

        Button openSettings = button("OPEN SETTINGS — LEAVE APP");
        openSettings.setOnClickListener(v -> {
            Intent i = new Intent(Settings.ACTION_SETTINGS);
            startActivity(i);
        });
        root.addView(openSettings, matchWrap(dp(0), dp(12)));

        Button home = button("GO HOME — LEAVE APP");
        home.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_HOME);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
        root.addView(home, matchWrap(dp(0), dp(20)));

        TextView steps = new TextView(this);
        steps.setText(
                "判讀方式\n" +
                "1. 開啟本 App：Samsung 深度工作模式應啟動，若該模式含勿擾，DND 應顯示 ON。\n" +
                "2. 按 OPEN SETTINGS 或 GO HOME 離開本 App。\n" +
                "3. 不要立刻回本 App；直接觀察狀態列／快速設定，確認深度工作模式或勿擾是否仍維持。\n" +
                "4. 若一離開就關閉，代表「已開啟應用程式」只適合前景觸發，正式 Deep Tally 不能只靠這個方法。"
        );
        steps.setTextSize(16);
        steps.setTextColor(Color.DKGRAY);
        steps.setLineSpacing(0, 1.2f);
        root.addView(steps, matchWrap(dp(0), dp(0)));

        setContentView(root);
        refreshStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshStatus();
    }

    private void refreshStatus() {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String status;
        try {
            int filter = nm.getCurrentInterruptionFilter();
            switch (filter) {
                case NotificationManager.INTERRUPTION_FILTER_ALL:
                    status = "DND: OFF";
                    statusView.setTextColor(Color.rgb(160, 45, 45));
                    break;
                case NotificationManager.INTERRUPTION_FILTER_PRIORITY:
                    status = "DND: ON — PRIORITY";
                    statusView.setTextColor(Color.rgb(46, 125, 50));
                    break;
                case NotificationManager.INTERRUPTION_FILTER_NONE:
                    status = "DND: ON — NONE";
                    statusView.setTextColor(Color.rgb(46, 125, 50));
                    break;
                case NotificationManager.INTERRUPTION_FILTER_ALARMS:
                    status = "DND: ON — ALARMS";
                    statusView.setTextColor(Color.rgb(46, 125, 50));
                    break;
                default:
                    status = "DND: UNKNOWN";
                    statusView.setTextColor(Color.DKGRAY);
            }
        } catch (Throwable t) {
            status = "DND: UNAVAILABLE";
            statusView.setTextColor(Color.DKGRAY);
        }
        statusView.setText(status);
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        timeView.setText("Checked: " + now);
    }

    private Button button(String text) {
        Button b = new Button(this);
        b.setText(text);
        b.setTextSize(16);
        b.setAllCaps(false);
        b.setMinHeight(dp(52));
        return b;
    }

    private LinearLayout.LayoutParams matchWrap(int top, int bottom) {
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
