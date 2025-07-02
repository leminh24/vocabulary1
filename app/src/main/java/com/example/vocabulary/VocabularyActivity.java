package com.example.vocabulary;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
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


        DatabaseHelper helper = new DatabaseHelper(this);
        db = helper.getReadableDatabase();

        vocabList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM Vocabulary WHERE topic_id = ?", new String[]{String.valueOf(topicId)});
        while (cursor.moveToNext()) {
            vocabList.add(new Vocabulary(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)));
        }
        cursor.close();

        adapter = new VocabularyAdapter(vocabList);
        recyclerView.setAdapter(adapter);
    }
}
