package com.example.vocabulary;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.vocabulary.VocabularyAdapter;
import com.example.vocabulary.DatabaseHelper;
import com.example.vocabulary.Vocabulary;
import java.util.ArrayList;

public class VocabularyActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<Vocabulary> vocabList;
    VocabularyAdapter adapter;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary);

        int topicId = getIntent().getIntExtra("topic_id", -1);

        recyclerView = findViewById(R.id.recyclerVocab);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(v -> finish());

        Button buttonHoc = findViewById(R.id.button);
        buttonHoc.setOnClickListener(v -> {
            Intent intent = new Intent(VocabularyActivity.this, PracticeActivity.class);
            intent.putExtra("topic_id", topicId); // topicId bạn đã truyền từ trước
            startActivity(intent);
        });


        DatabaseHelper helper = new DatabaseHelper(this);
        db = helper.getReadableDatabase();

        vocabList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM Vocabulary WHERE topic_id = ?", new String[]{String.valueOf(topicId)});
        while (cursor.moveToNext()) {
            vocabList.add(new Vocabulary(
                    cursor.getInt(0),    // id
                    cursor.getInt(1),    // topicId
                    cursor.getString(2), // word
                    cursor.getString(3), // definition
                    cursor.getString(4), // example
                    cursor.getString(5), // meaning_vi
                    cursor.getString(6), // sound
                    cursor.getInt(7)     // status
            ));

        }

        cursor.close();

        adapter = new VocabularyAdapter(vocabList);
        recyclerView.setAdapter(adapter);
    }
}
