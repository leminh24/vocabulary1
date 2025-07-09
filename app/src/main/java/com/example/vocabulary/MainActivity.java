package com.example.vocabulary;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<Course> courseList;
    CourseAdapter adapter;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerCourses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setVisibility(View.GONE); // ẩn vì không cần ở MainActivity


        DatabaseHelper helper = new DatabaseHelper(this);
        db = helper.getReadableDatabase();

        courseList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM Course", null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            int wordCount = cursor.getInt(2);
            int progress = cursor.getInt(3);

            // Gán ảnh theo tên
            int imageResId = R.drawable.toefl;
            if (name.equalsIgnoreCase("Beginner")) {
                imageResId = R.drawable.toefl;
            } else if (name.equalsIgnoreCase("Intermediate")) {
                imageResId = R.drawable.ielts;
            }
            courseList.add(new Course(id, name, wordCount, progress, imageResId));
        }
        cursor.close();


        adapter = new CourseAdapter(courseList, courseId -> {
            Intent intent = new Intent(MainActivity.this, TopicActivity.class);
            intent.putExtra("course_id", courseId);
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        // Gán sự kiện cho 3 nút menu
        findViewById(R.id.menuStatistics).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.menuStudy).setOnClickListener(v -> {
            // Ví dụ: quay lại MainActivity hoặc trang danh sách khóa học
            recyclerView.scrollToPosition(0); // Hoặc load lại danh sách
        });

//        findViewById(R.id.menuSettings).setOnClickListener(v -> {
//            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
//            startActivity(intent);
//        });

    }
}