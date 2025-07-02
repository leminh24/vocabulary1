package com.example.vocabulary;

public class Vocabulary {
    public int id;
    public int topicId;
    public String word;
    public String definition;
    public String example;

    public Vocabulary(int id, int topicId, String word, String definition, String example) {
        this.id = id;
        this.topicId = topicId;
        this.word = word;
        this.definition = definition;
        this.example = example;
    }
}