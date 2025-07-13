package com.example.vocabulary;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Random;

public class PracticeActivity extends AppCompatActivity {

    private TextView questionText;
    private EditText answerInput;
    private RadioGroup choicesGroup;
    private Button listenButton, speakButton, okButton;

    private LinearLayout fillInLayout, multipleChoiceLayout, pronunciationLayout;

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    private ArrayList<VocabularyItem> vocabList;
    private int currentIndex = 0;

    private VocabularyItem currentWord;
    private int questionType = 0; // 0: điền từ, 1: trắc nghiệm, 2: phát âm

    private TextToSpeech tts;
    private int topicId = -1;

    private static final String PREFS_NAME = "settings_prefs";
    private static final String KEY_TTS_SLOW = "tts_slow";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);

        // Ánh xạ giao diện
        questionText = findViewById(R.id.questionText);
        answerInput = findViewById(R.id.answerInput);
        choicesGroup = findViewById(R.id.choicesGroup);
        listenButton = findViewById(R.id.listenButton);
        speakButton = findViewById(R.id.speakButton);
        okButton = findViewById(R.id.okButton);

        fillInLayout = findViewById(R.id.fillInLayout);
        multipleChoiceLayout = findViewById(R.id.multipleChoiceLayout);
        pronunciationLayout = findViewById(R.id.pronunciationLayout);

        // Nhận topic_id từ Intent
        topicId = getIntent().getIntExtra("topic_id", -1);

        // Khởi tạo TextToSpeech
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);

                // Lấy trạng thái switch từ SharedPreferences
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                boolean isSlow = prefs.getBoolean(KEY_TTS_SLOW, false);
                tts.setSpeechRate(isSlow ? 0.5f : 1.0f);
            }
        });

        // Lấy dữ liệu
        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();
        loadVocabulary();

        showNextQuestion();

        okButton.setOnClickListener(v -> checkAnswer());

        listenButton.setOnClickListener(v -> {
            if (currentWord != null) {
                // Kiểm tra lại trạng thái switch mỗi lần nói
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                boolean isSlow = prefs.getBoolean(KEY_TTS_SLOW, false);
                tts.setSpeechRate(isSlow ? 0.5f : 1.0f);

                tts.speak(currentWord.word, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

        speakButton.setOnClickListener(v -> {
            Toast.makeText(this, "Tính năng ghi âm chưa hỗ trợ", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadVocabulary() {
        vocabList = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM Vocabulary WHERE topic_id = ?", new String[]{String.valueOf(topicId)});
        while (c.moveToNext()) {
            VocabularyItem item = new VocabularyItem();
            item.id = c.getInt(c.getColumnIndexOrThrow("id"));
            item.word = c.getString(c.getColumnIndexOrThrow("word"));
            item.meaning = c.getString(c.getColumnIndexOrThrow("meaning_vi"));
            vocabList.add(item);
        }
        c.close();
        Collections.shuffle(vocabList);
    }

    private void showNextQuestion() {
        if (currentIndex >= vocabList.size()) {
            Toast.makeText(this, "Hoàn thành tất cả câu hỏi!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentWord = vocabList.get(currentIndex++);
        questionType = new Random().nextInt(3); // 0,1,2

        // Ẩn hết
        fillInLayout.setVisibility(View.GONE);
        multipleChoiceLayout.setVisibility(View.GONE);
        pronunciationLayout.setVisibility(View.GONE);

        switch (questionType) {
            case 0:
                fillInLayout.setVisibility(View.VISIBLE);
                questionText.setText("Nghĩa tiếng Việt: " + currentWord.meaning);
                answerInput.setText("");
                break;
            case 1:
                multipleChoiceLayout.setVisibility(View.VISIBLE);
                questionText.setText("Chọn nghĩa đúng của từ: " + currentWord.word);
                setupChoices();
                break;
            case 2:
                pronunciationLayout.setVisibility(View.VISIBLE);
                questionText.setText("Phát âm từ: " + currentWord.word);
                break;
        }
    }

    private void setupChoices() {
        ArrayList<String> options = new ArrayList<>();
        options.add(currentWord.meaning);

        for (VocabularyItem item : vocabList) {
            if (!item.word.equals(currentWord.word) && options.size() < 4) {
                options.add(item.meaning);
            }
        }

        Collections.shuffle(options);

        for (int i = 0; i < 4; i++) {
            RadioButton rb = (RadioButton) choicesGroup.getChildAt(i);
            rb.setText(i < options.size() ? options.get(i) : "");
        }

        choicesGroup.clearCheck();
    }

    private void checkAnswer() {
        boolean correct = false;

        switch (questionType) {
            case 0:
                String userInput = answerInput.getText().toString().trim();
                correct = userInput.equalsIgnoreCase(currentWord.word);
                break;
            case 1:
                int selectedId = choicesGroup.getCheckedRadioButtonId();
                if (selectedId != -1) {
                    RadioButton selected = findViewById(selectedId);
                    correct = selected.getText().toString().equals(currentWord.meaning);
                }
                break;
            case 2:
                correct = true;
                break;
        }

        if (correct) {
            ContentValues values = new ContentValues();
            values.put("status", 1);
            db.update("Vocabulary", values, "id = ?", new String[]{String.valueOf(currentWord.id)});
        }

        Toast.makeText(this, correct ? "Đúng!" : "Sai!", Toast.LENGTH_SHORT).show();
        showNextQuestion();
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    static class VocabularyItem {
        int id;
        String word;
        String meaning;
    }
}
