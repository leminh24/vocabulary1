package com.example.vocabulary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ItemSettingActivity extends AppCompatActivity {

    AudioManager audioManager;
    int originalVolume = -1; // Lưu âm lượng trước khi mute

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_setting);

        // Khởi tạo AudioManager
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

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
            intent.putExtra("restore", true); // Truyền cờ để báo là yêu cầu restore
            startActivity(intent);
        });
        // Switch điều khiển âm lượng
        Switch switchVolume = findViewById(R.id.switch1);
        switchVolume.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Hiện thanh điều chỉnh âm lượng
                audioManager.adjustVolume(AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI);
            } else {
                // Lưu âm lượng hiện tại nếu chưa lưu
                if (originalVolume == -1) {
                    originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
                }
                // Mute bằng cách đặt âm lượng = 0
                audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
                Toast.makeText(this, "Đã tắt âm thanh chuông", Toast.LENGTH_SHORT).show();
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
}
