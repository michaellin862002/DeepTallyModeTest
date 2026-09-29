package com.deeptally.modetest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
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
        root.addView(title, params(dp(0), dp(24)));

        TextView intro = new TextView(this);
        intro.setText(
            "這是極簡測試版。\n\n" +
            "目的只測 Samsung「深度工作」模式：\n" +
            "1. 打開本 App 時是否啟動。\n" +
            "2. 離開本 App 後是否維持。\n\n" +
            "請先把本 App 加到「深度工作 → 自動開啟 → 已開啟應用程式」。"
        );
        intro.setTextSize(18);
        intro.setTextColor(Color.DKGRAY);
        intro.setLineSpacing(0, 1.25f);
        root.addView(intro, params(dp(0), dp(32)));

        Button home = new Button(this);
        home.setText("GO HOME — LEAVE APP");
        home.setTextSize(16);
        home.setMinHeight(dp(56));
        home.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
        root.addView(home, params(dp(0), dp(16)));

        Button settings = new Button(this);
        settings.setText("OPEN SETTINGS — LEAVE APP");
        settings.setTextSize(16);
        settings.setMinHeight(dp(56));
        settings.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_SETTINGS)));
        root.addView(settings, params(dp(0), dp(24)));

        TextView note = new TextView(this);
        note.setText(
            "離開後不要立刻回來。\n" +
            "直接看狀態列或快速設定，確認「深度工作」模式是否仍然開啟。"
        );
        note.setTextSize(16);
        note.setTextColor(Color.DKGRAY);
        note.setLineSpacing(0, 1.2f);
        root.addView(note, params(dp(0), dp(0)));

        setContentView(root);
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
