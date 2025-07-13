package com.example.vocabulary;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Random;

public class PracticeActivity extends AppCompatActivity {

    private TextView questionText, learnWord, learnMeaning,speakText;
    private EditText answerInput;
    private RadioGroup choicesGroup;
    private Button listenButton, speakButton, okButton, learnedButton;

    private LinearLayout fillInLayout, multipleChoiceLayout, pronunciationLayout, learnLayout;

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    private ArrayList<VocabularyItem> vocabList;
    private ArrayList<QuestionItem> questionList;
    private int questionIndex = 0;

    private QuestionItem currentQuestion;
    private VocabularyItem currentWord;
    private int questionType = 0;

    private TextToSpeech tts;
    private int topicId = -1;
    private boolean hasLearned = false;
    private SpeechRecognizer speechRecognizer;
    private Intent speechIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);

        // Ánh xạ giao diện
        questionText = findViewById(R.id.questionText);
        learnWord = findViewById(R.id.learnWord);
        learnMeaning = findViewById(R.id.learnMeaning);
        answerInput = findViewById(R.id.answerInput);
        choicesGroup = findViewById(R.id.choicesGroup);
        listenButton = findViewById(R.id.listenButton);
        speakButton = findViewById(R.id.speakButton);
        okButton = findViewById(R.id.okButton);
        speakText = findViewById(R.id.speakText);


        Button learnListenButton = findViewById(R.id.learnListenButton);
        Button learnOkButton = findViewById(R.id.learnOkButton);

        fillInLayout = findViewById(R.id.fillInLayout);
        multipleChoiceLayout = findViewById(R.id.multipleChoiceLayout);
        pronunciationLayout = findViewById(R.id.pronunciationLayout);
        learnLayout = findViewById(R.id.learnLayout);

        topicId = getIntent().getIntExtra("topic_id", -1);

        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
            }
        });

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();
        loadVocabulary();

        showNextQuestion();

        okButton.setOnClickListener(v -> checkAnswer());

        learnOkButton.setOnClickListener(v -> {
            hasLearned = true;
            showQuestionLayout();
        });

        learnListenButton.setOnClickListener(v -> {
            if (currentWord != null && tts != null) {
                tts.speak(currentWord.word, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

        listenButton.setOnClickListener(v -> {
            if (currentWord != null) tts.speak(currentWord.word, TextToSpeech.QUEUE_FLUSH, null, null);
        });

        speakButton.setOnClickListener(v -> {
            Toast.makeText(this, "Tính năng ghi âm chưa hỗ trợ", Toast.LENGTH_SHORT).show();
        });

        // Khởi tạo SpeechRecognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US);

// Xử lý sự kiện khi nhận được giọng nói
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override public void onReadyForSpeech(Bundle params) {}
            @Override public void onBeginningOfSpeech() {}
            @Override public void onRmsChanged(float rmsdB) {}
            @Override public void onBufferReceived(byte[] buffer) {}
            @Override public void onEndOfSpeech() {}
            @Override public void onError(int error) {
                Toast.makeText(PracticeActivity.this, "Lỗi nhận giọng nói", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String spokenText = matches.get(0).trim().toLowerCase();
                    String targetWord = currentWord.word.trim().toLowerCase();

                    // 👇 Thêm dòng này để hiển thị kết quả nói
                    speakText.setText(spokenText);

                    if (spokenText.equals(targetWord)) {
                        Toast.makeText(PracticeActivity.this, "Phát âm đúng!", Toast.LENGTH_SHORT).show();
                        ContentValues values = new ContentValues();
                        values.put("status", 1);
                        db.update("Vocabulary", values, "id = ?", new String[]{String.valueOf(currentWord.id)});
                        showNextQuestion();
                    } else {
                        Toast.makeText(PracticeActivity.this, "Chưa đúng, bạn nói: " + spokenText, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override public void onPartialResults(Bundle partialResults) {}
            @Override public void onEvent(int eventType, Bundle params) {}
        });


        speakButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{android.Manifest.permission.RECORD_AUDIO}, 1);
                        return true;
                    }
                    Toast.makeText(this, "Bắt đầu nói...", Toast.LENGTH_SHORT).show();
                    speechRecognizer.startListening(speechIntent);
                    return true;

                case MotionEvent.ACTION_UP:
                    Toast.makeText(this, "Dừng ghi âm", Toast.LENGTH_SHORT).show();
                    speechRecognizer.stopListening();
                    return true;
            }
            return false;
        });
    }


    private void loadVocabulary() {
        vocabList = new ArrayList<>();
        questionList = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM Vocabulary WHERE topic_id = ?", new String[]{String.valueOf(topicId)});
        while (c.moveToNext()) {
            VocabularyItem item = new VocabularyItem();
            item.id = c.getInt(c.getColumnIndexOrThrow("id"));
            item.word = c.getString(c.getColumnIndexOrThrow("word"));
            item.meaning = c.getString(c.getColumnIndexOrThrow("meaning_vi"));
            vocabList.add(item);

            questionList.add(new QuestionItem(item, 0));
            questionList.add(new QuestionItem(item, 1));
            questionList.add(new QuestionItem(item, 2));
        }
        c.close();
        Collections.shuffle(questionList);
    }

    private void showNextQuestion() {
        if (questionIndex >= questionList.size()) {
            Toast.makeText(this, "Hoàn thành tất cả câu hỏi!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentQuestion = questionList.get(questionIndex++);
        currentWord = currentQuestion.vocab;
        questionType = currentQuestion.type;
        hasLearned = false;

        // Ẩn các layout làm bài
        fillInLayout.setVisibility(View.GONE);
        multipleChoiceLayout.setVisibility(View.GONE);
        pronunciationLayout.setVisibility(View.GONE);
        questionText.setVisibility(View.GONE);
        okButton.setVisibility(View.GONE);

        // Hiện layout học từ
        learnLayout.setVisibility(View.VISIBLE);
        learnWord.setText(currentWord.word);
        learnMeaning.setText(currentWord.meaning);
    }

    private void showQuestionLayout() {
        learnLayout.setVisibility(View.GONE);
        questionText.setVisibility(View.VISIBLE);
        okButton.setVisibility(View.VISIBLE);

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

        Toast.makeText(this, correct ? "Đúng!" : "Sai!", Toast.LENGTH_SHORT).show();

        // Cập nhật nếu cần (ví dụ đánh dấu hoàn thành sau 3 câu hỏi)
        showNextQuestion();
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        super.onDestroy();
    }

    static class VocabularyItem {
        int id;
        String word;
        String meaning;
    }

    static class QuestionItem {
        VocabularyItem vocab;
        int type;

        public QuestionItem(VocabularyItem vocab, int type) {
            this.vocab = vocab;
            this.type = type;
        }
    }
}
