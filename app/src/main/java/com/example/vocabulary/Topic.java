package com.example.vocabulary;

public class Topic {
    public int id;
    public int courseId;
    public String name;
    public int wordCount;
    public String courseName;


    public Topic(int id, int courseId, String name, String courseName , int wordCount) {
        this.id = id;
        this.courseId = courseId;
        this.name = name;
        this.courseName = name;
        this.wordCount = wordCount;
    }

}