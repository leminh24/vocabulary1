package com.example.vocabulary;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class Reminder extends AppCompatActivity {

    private EditText editTextTime;
    private Switch switchAlarm;

    private int hourOfDay = -1;
    private int minute = -1;

    private static final String PREFS_NAME = "ReminderPrefs";
    private static final String PREF_HOUR = "hour";
    private static final String PREF_MINUTE = "minute";
    private static final String PREF_SWITCH = "switch_on";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        editTextTime = findViewById(R.id.editTextTime3);
        switchAlarm = findViewById(R.id.switch1);

        // Khi bấm vào EditText, mở TimePickerDialog
        editTextTime.setOnClickListener(v -> showTimePickerDialog());

        // Khi bật Switch thì cài báo thức, tắt thì hủy báo thức
        switchAlarm.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (hourOfDay == -1 || minute == -1) {
                    Toast.makeText(this, "Vui lòng chọn thời gian trước", Toast.LENGTH_SHORT).show();
                    switchAlarm.setChecked(false);
                } else {
                    setAlarm(hourOfDay, minute);
                }
            } else {
                cancelAlarm();
            }
        });

        // Khôi phục dữ liệu đã lưu
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        hourOfDay = prefs.getInt(PREF_HOUR, -1);
        minute = prefs.getInt(PREF_MINUTE, -1);
        boolean switchOn = prefs.getBoolean(PREF_SWITCH, false);

        if (hourOfDay != -1 && minute != -1) {
            String timeString = String.format("%02d:%02d", hourOfDay, minute);
            editTextTime.setText(timeString);
        }

        switchAlarm.setChecked(switchOn);
    }

    private void showTimePickerDialog() {
        Calendar c = Calendar.getInstance();
        int currentHour = c.get(Calendar.HOUR_OF_DAY);
        int currentMinute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hour, minute) -> {
            hourOfDay = hour;
            this.minute = minute;

            // Hiển thị giờ đã chọn
            String timeString = String.format("%02d:%02d", hour, minute);
            editTextTime.setText(timeString);

            // Lưu vào SharedPreferences
            SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
            editor.putInt(PREF_HOUR, hour);
            editor.putInt(PREF_MINUTE, minute);
            editor.apply();
        }, currentHour, currentMinute, true);

        timePickerDialog.show();
    }

    private void setAlarm(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Toast.makeText(this, "Báo thức đã được đặt vào " + String.format("%02d:%02d", hour, minute), Toast.LENGTH_SHORT).show();
        }

        // Lưu trạng thái switch là ON
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(PREF_SWITCH, true);
        editor.apply();
    }

    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            Toast.makeText(this, "Báo thức đã được hủy", Toast.LENGTH_SHORT).show();
        }

        // Lưu trạng thái switch là OFF
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(PREF_SWITCH, false);
        editor.apply();
    }
}
