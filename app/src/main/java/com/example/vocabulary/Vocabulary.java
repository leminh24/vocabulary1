package com.example.vocabulary;

public class Vocabulary {
    public int id, topicId, status;
    public String word, definition, example,meaning_vi,sound;

    public Vocabulary(int id, int topicId, String word, String definition, String example,String meaning_vi,String sound, int status) {
        this.id = id;
        this.topicId = topicId;
        this.word = word;
        this.definition = definition;
        this.example = example;
        this.status = status;
        this.meaning_vi = meaning_vi;
        this.sound = sound;
    }
}

