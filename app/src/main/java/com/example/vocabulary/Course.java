package com.example.vocabulary;

public class Course {
    public int id;
    public String name;
    public int wordCount;
    public int progress;
    public int imageResId;

    public Course(int id, String name, int wordCount, int progress, int imageResId) {
        this.id = id;
        this.name = name;
        this.wordCount = wordCount;
        this.progress = progress;
        this.imageResId = imageResId;
    }
}