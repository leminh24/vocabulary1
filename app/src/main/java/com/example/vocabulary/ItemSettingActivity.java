package com.example.vocabulary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class ItemSettingActivity extends AppCompatActivity {

    private AudioManager audioManager;
    private TextToSpeech tts;

    private static final String PREFS_NAME = "settings_prefs";
    private static final String KEY_TTS_SLOW = "tts_slow";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_setting);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // Khởi tạo TextToSpeech
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);

                // Khôi phục tốc độ nói khi TTS đã sẵn sàng
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                boolean isSlow = prefs.getBoolean(KEY_TTS_SLOW, false);
                tts.setSpeechRate(isSlow ? 0.5f : 1.0f);
            }
        });

        // Mua gói
        Button btnPurchase = findViewById(R.id.btnPurchase);
        btnPurchase.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(ItemSettingActivity.this, Packages.class);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Màn hình Packages chưa được tạo", Toast.LENGTH_SHORT).show();
            }
        });

        // Phục hồi giao diện
        Button btnRestore = findViewById(R.id.btnRestore);
        btnRestore.setOnClickListener(v -> {
            Intent intent = new Intent(ItemSettingActivity.this, Packages.class);
            intent.putExtra("restore", true);
            startActivity(intent);
        });

        // Switch điều khiển tốc độ nói
        Switch switchVolume = findViewById(R.id.switch1);
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isSlow = prefs.getBoolean(KEY_TTS_SLOW, false);
        switchVolume.setChecked(isSlow);

        switchVolume.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
            editor.putBoolean(KEY_TTS_SLOW, isChecked);
            editor.apply();

            if (tts != null) {
                if (isChecked) {
                    tts.setSpeechRate(0.5f);
                    Toast.makeText(this, "Đã bật chế độ phát âm chậm", Toast.LENGTH_SHORT).show();
                } else {
                    tts.setSpeechRate(1.0f);
                    Toast.makeText(this, "Đã tắt chế độ phát âm chậm", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Nhắc nhở
        Button btnReminder = findViewById(R.id.btnReminder);
        btnReminder.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(ItemSettingActivity.this, Reminder.class);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Màn hình Reminder chưa được tạo", Toast.LENGTH_SHORT).show();
            }
        });

        // Mục tiêu
        Button btnGoals = findViewById(R.id.btnGoals);
        btnGoals.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(ItemSettingActivity.this, Goals.class);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Màn hình Goals chưa được tạo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
