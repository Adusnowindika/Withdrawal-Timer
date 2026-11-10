package com.example.readytostartwithdrawal;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;

public class MainActivity extends Activity {
    // Sri Lanka local time: 12 November 2026, 6:00 PM.
    private static final long TARGET_MILLIS = ZonedDateTime.of(
            2026, 11, 12, 18, 0, 0, 0, ZoneId.of("Asia/Colombo")
    ).toInstant().toEpochMilli();

    private final Handler handler = new Handler(Looper.getMainLooper());
    private TextView phaseTitle;
    private TextView timerText;
    private TextView hintText;
    private Button stopButton;
    private boolean stopped;
    private long stoppedAt;
    private final Runnable ticker = new Runnable() {
        @Override public void run() {
            render(System.currentTimeMillis());
            if (!stopped) handler.postDelayed(this, 100);
        }
    };

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        stopped = getPreferences(MODE_PRIVATE).getBoolean("stopped", false);
        stoppedAt = getPreferences(MODE_PRIVATE).getLong("stopped_at", 0L);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(dp(24), dp(28), dp(24), dp(28));
        root.setBackgroundColor(Color.rgb(10, 10, 12));

        TextView appName = new TextView(this);
        appName.setText("WITHDRAWAL COUNTDOWN");
        appName.setTextColor(Color.rgb(190, 165, 105));
        appName.setTextSize(13);
        appName.setLetterSpacing(0.15f);
        appName.setGravity(Gravity.CENTER);
        root.addView(appName, matchWrap());

        phaseTitle = new TextView(this);
        phaseTitle.setTextColor(Color.WHITE);
        phaseTitle.setTextSize(25);
        phaseTitle.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        phaseTitle.setGravity(Gravity.CENTER);
        phaseTitle.setPadding(0, dp(42), 0, dp(20));
        root.addView(phaseTitle, matchWrap());

        timerText = new TextView(this);
        timerText.setTextColor(Color.rgb(225, 190, 115));
        timerText.setTextSize(31);
        timerText.setTypeface(Typeface.create("monospace", Typeface.BOLD));
        timerText.setGravity(Gravity.CENTER);
        timerText.setIncludeFontPadding(true);
        root.addView(timerText, matchWrap());

        hintText = new TextView(this);
        hintText.setTextColor(Color.LTGRAY);
        hintText.setTextSize(13);
        hintText.setGravity(Gravity.CENTER);
        hintText.setPadding(0, dp(20), 0, dp(42));
        root.addView(hintText, matchWrap());

        stopButton = new Button(this);
        stopButton.setText("STOP TIMER");
        stopButton.setTextColor(Color.BLACK);
        stopButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.rgb(225, 190, 115)));
        stopButton.setAllCaps(false);
        stopButton.setTextSize(16);
        stopButton.setOnClickListener(v -> confirmStop());
        root.addView(stopButton, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(54)));

        TextView footer = new TextView(this);
        footer.setText("Sri Lanka time  •  Asia/Colombo");
        footer.setTextColor(Color.GRAY);
        footer.setTextSize(12);
        footer.setGravity(Gravity.CENTER);
        footer.setPadding(0, dp(28), 0, 0);
        root.addView(footer, matchWrap());

        setContentView(root);
        render(System.currentTimeMillis());
    }

    @Override protected void onResume() {
        super.onResume();
        if (!stopped) {
            handler.removeCallbacks(ticker);
            handler.post(ticker);
        } else {
            render(stoppedAt);
        }
    }

    @Override protected void onPause() {
        super.onPause();
        handler.removeCallbacks(ticker);
    }

    private void render(long now) {
        if (stopped) {
            now = stoppedAt;
            stopButton.setEnabled(false);
            stopButton.setText("TIMER STOPPED");
            hintText.setText("The display is frozen. It will remain stopped when you reopen the app.");
        } else {
            stopButton.setEnabled(true);
            stopButton.setText("STOP TIMER");
        }

        if (now < TARGET_MILLIS) {
            phaseTitle.setText("Ready to Start Withdrawal");
            long remaining = TARGET_MILLIS - now;
            timerText.setText(formatDuration(remaining, false));
            if (!stopped) hintText.setText("Countdown to 12 November 2026 • 6:00 PM");
        } else {
            phaseTitle.setText("Congratulations you did it!");
            long elapsed = now - TARGET_MILLIS;
            timerText.setText(formatDuration(elapsed, true));
            if (!stopped) hintText.setText("Time elapsed since your target time");
        }
    }

    private String formatDuration(long millis, boolean tenths) {
        long totalSeconds = Math.max(0, millis) / 1000;
        long days = totalSeconds / 86400;
        long hours = (totalSeconds % 86400) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        if (tenths) {
            long tenth = (Math.max(0, millis) % 1000) / 100;
            return String.format(Locale.US, "%d:%02d:%02d:%02d.%d", days, hours, minutes, seconds, tenth);
        }
        return String.format(Locale.US, "%d:%02d:%02d:%02d", days, hours, minutes, seconds);
    }

    private void confirmStop() {
        new AlertDialog.Builder(this)
                .setTitle("Stop timer?")
                .setMessage("This will permanently freeze the timer display at its current value.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Stop", (dialog, which) -> {
                    stoppedAt = System.currentTimeMillis();
                    stopped = true;
                    getPreferences(MODE_PRIVATE).edit()
                            .putBoolean("stopped", true)
                            .putLong("stopped_at", stoppedAt)
                            .apply();
                    handler.removeCallbacks(ticker);
                    render(stoppedAt);
                })
                .show();
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
    }
}
