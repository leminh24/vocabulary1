package com.example.vocabulary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "vocabulary";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Course (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, word_count INTEGER, progress INTEGER)");
        db.execSQL("CREATE TABLE Topic (id INTEGER PRIMARY KEY AUTOINCREMENT, course_id INTEGER, name TEXT, word_count INTEGER)");
        db.execSQL("CREATE TABLE Vocabulary (id INTEGER PRIMARY KEY AUTOINCREMENT, topic_id INTEGER, word TEXT, definition TEXT, example TEXT)");

        // Insert Course
        db.execSQL("INSERT INTO Course (name, word_count, progress) VALUES ('Beginner', 0, 30)");
        db.execSQL("INSERT INTO Course (name, word_count, progress) VALUES ('Intermediate', 0, 60)");

        // Insert Topic (word_count sẽ được cập nhật sau)
        db.execSQL("INSERT INTO Topic (course_id, name, word_count) VALUES (1, 'Colors', 0)");
        db.execSQL("INSERT INTO Topic (course_id, name, word_count) VALUES (1, 'Animals', 0)");
        db.execSQL("INSERT INTO Topic (course_id, name, word_count) VALUES (2, 'Weather', 0)");

        // Insert Vocabulary
        db.execSQL("INSERT INTO Vocabulary (topic_id, word, definition, example) VALUES (1, 'Red', 'The color of blood', 'The apple is red.')");
        db.execSQL("INSERT INTO Vocabulary (topic_id, word, definition, example) VALUES (1, 'Blue', 'The color of the sky', 'The sky is blue.')");
        db.execSQL("INSERT INTO Vocabulary (topic_id, word, definition, example) VALUES (2, 'Cat', 'A small domesticated carnivorous mammal', 'The cat sleeps on the sofa.')");
        db.execSQL("INSERT INTO Vocabulary (topic_id, word, definition, example) VALUES (3, 'Rain', 'Water falling from clouds', 'It is going to rain today.')");

        // Cập nhật word_count cho từng topic
        db.execSQL("UPDATE Topic SET word_count = (SELECT COUNT(*) FROM Vocabulary WHERE Vocabulary.topic_id = Topic.id)");

        // Cập nhật word_count cho từng course
        db.execSQL("UPDATE Course SET word_count = (SELECT SUM(word_count) FROM Topic WHERE Topic.course_id = Course.id)");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Course");
        db.execSQL("DROP TABLE IF EXISTS Topic");
        db.execSQL("DROP TABLE IF EXISTS Vocabulary");
        onCreate(db);
    }
}

