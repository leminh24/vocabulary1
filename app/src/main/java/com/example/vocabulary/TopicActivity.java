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

public class TopicActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<Topic> topicList;
    TopicAdapter adapter;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        int courseId = getIntent().getIntExtra("course_id", -1);

        recyclerView = findViewById(R.id.recyclerTopics);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(v -> finish());

        DatabaseHelper helper = new DatabaseHelper(this);
        db = helper.getReadableDatabase();

        topicList = new ArrayList<>();
        Cursor cursor = db.rawQuery(
                "SELECT Topic.id, Topic.course_id, Topic.name, Topic.word_count, Course.name " +
                        "FROM Topic JOIN Course ON Topic.course_id = Course.id WHERE Topic.course_id = ?",
                new String[]{String.valueOf(courseId)}
        );

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            int courseID = cursor.getInt(1);
            String topicName = cursor.getString(2);
            int wordCount = cursor.getInt(3);
            String courseName = cursor.getString(4);

            topicList.add(new Topic(id, courseID, topicName, courseName, wordCount));
        }
        cursor.close();

        adapter = new TopicAdapter(topicList, topicId -> {
            Intent intent = new Intent(TopicActivity.this, VocabularyActivity.class);
            intent.putExtra("topic_id", topicId);
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
    }
}

