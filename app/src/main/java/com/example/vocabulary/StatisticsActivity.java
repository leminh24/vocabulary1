package com.example.vocabulary;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class StatisticsActivity extends AppCompatActivity {

    private ProgressCircleView weekProgress, monthProgress;
    private TextView txtTotalLearned, txtWeekCount, txtMonthCount;

    private static final int WEEK_TARGET = 56;
    private static final int MONTH_TARGET = 248;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        weekProgress = findViewById(R.id.weekProgress);
        monthProgress = findViewById(R.id.monthProgress);
        txtTotalLearned = findViewById(R.id.txtTotalLearned);
        txtWeekCount = findViewById(R.id.txtWeekCount);
        txtMonthCount = findViewById(R.id.txtMonthCount);

        // Đọc dữ liệu từ DB
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Tổng từ đã thuộc (status = 1)
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM Vocabulary WHERE status = 1", null);
        int totalLearned = 0;
        if (cursor.moveToFirst()) {
            totalLearned = cursor.getInt(0);
        }
        cursor.close();

        // TODO: Có thể thêm điều kiện WHERE theo thời gian (7 ngày, 30 ngày) nếu có cột date_learned
        int weekLearned = totalLearned;  // Giả định học trong tuần bằng tổng
        int monthLearned = totalLearned;

        // Cập nhật giao diện
        txtTotalLearned.setText(totalLearned + " từ đã thuộc");

        txtWeekCount.setText(weekLearned + "/" + WEEK_TARGET + " từ vựng trong tuần này");
        weekProgress.setProgress((float) weekLearned / WEEK_TARGET);

        txtMonthCount.setText(monthLearned + "/" + MONTH_TARGET + " từ vựng trong tháng này");
        monthProgress.setProgress((float) monthLearned / MONTH_TARGET);
        // Gán sự kiện cho 3 nút menu
        findViewById(R.id.menuStatistics).setOnClickListener(v -> {
            Intent intent = new Intent(StatisticsActivity.this, StatisticsActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.menuStudy).setOnClickListener(v -> {
            Intent intent = new Intent(StatisticsActivity.this, MainActivity.class);
            startActivity(intent);
        });

//        findViewById(R.id.menuSettings).setOnClickListener(v -> {
//            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
//            startActivity(intent);
//        });

    }
}
