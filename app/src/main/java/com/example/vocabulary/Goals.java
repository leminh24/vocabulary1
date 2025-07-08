package com.example.vocabulary;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Goals extends AppCompatActivity {

    CheckBox checkBox1, checkBox2, checkBox3, checkBox4;
    EditText editText;

    private static final String PREF_NAME = "GoalPrefs";
    private static final String SELECTED_GOAL = "selected_goal";
    private static final String CUSTOM_WORDS = "custom_words";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);

        checkBox1 = findViewById(R.id.checkBox1);
        checkBox2 = findViewById(R.id.checkBox2);
        checkBox3 = findViewById(R.id.checkBox3);
        checkBox4 = findViewById(R.id.checkBox4);
        editText = findViewById(R.id.edtText1);

        // Khôi phục trạng thái
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String selected = prefs.getString(SELECTED_GOAL, "");
        String savedInput = prefs.getString(CUSTOM_WORDS, "");

        if (!savedInput.isEmpty()) {
            editText.setText(savedInput);
        }

        switch (selected) {
            case "goal1":
                checkBox1.setChecked(true);
                break;
            case "goal2":
                checkBox2.setChecked(true);
                break;
            case "goal3":
                checkBox3.setChecked(true);
                break;
            case "goal4":
                checkBox4.setChecked(true);
                break;
        }

        // CheckBox 1
        checkBox1.setOnCheckedChangeListener((btn, isChecked) -> {
            if (isChecked) {
                uncheckOthers(checkBox1);
                saveGoal("goal1", "");
                Toast.makeText(this, "Đã thiết lập mục tiêu học 5 từ mỗi ngày.", Toast.LENGTH_SHORT).show();
            }
        });

        // CheckBox 2
        checkBox2.setOnCheckedChangeListener((btn, isChecked) -> {
            if (isChecked) {
                uncheckOthers(checkBox2);
                saveGoal("goal2", "");
                Toast.makeText(this, "Đã thiết lập mục tiêu học 8 từ mỗi ngày.", Toast.LENGTH_SHORT).show();
            }
        });

        // CheckBox 3
        checkBox3.setOnCheckedChangeListener((btn, isChecked) -> {
            if (isChecked) {
                uncheckOthers(checkBox3);
                saveGoal("goal3", "");
                Toast.makeText(this, "Đã thiết lập mục tiêu học 10 từ mỗi ngày.", Toast.LENGTH_SHORT).show();
            }
        });

        // CheckBox 4
        checkBox4.setOnCheckedChangeListener((btn, isChecked) -> {
            if (isChecked) {
                uncheckOthers(checkBox4);
                String input = editText.getText().toString().trim();
                if (input.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập số từ.", Toast.LENGTH_SHORT).show();
                    checkBox4.setChecked(false); // Bỏ chọn
                } else {
                    saveGoal("goal4", input);
                    Toast.makeText(this, "Đã thiết lập mục tiêu học " + input + " từ mỗi ngày.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveGoal(String goalId, String customInput) {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SELECTED_GOAL, goalId);
        editor.putString(CUSTOM_WORDS, customInput);
        editor.apply();
    }

    private void uncheckOthers(CheckBox selected) {
        if (selected != checkBox1) checkBox1.setChecked(false);
        if (selected != checkBox2) checkBox2.setChecked(false);
        if (selected != checkBox3) checkBox3.setChecked(false);
        if (selected != checkBox4) checkBox4.setChecked(false);
    }
}
