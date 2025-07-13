package com.example.vocabulary;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder> {
    private List<Topic> topicList;
    private OnTopicClickListener listener;

    public interface OnTopicClickListener {
        void onTopicClick(int topicId);
    }

    public TopicAdapter(List<Topic> topicList, OnTopicClickListener listener) {
        this.topicList = topicList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_topic, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        Topic topic = topicList.get(position);
        holder.topicName.setText("Chủ đề: " + topic.name);
        holder.courseName.setText("Khóa học: " + topic.courseName);
        holder.wordCount.setText("Số từ: " + topic.wordCount);
        holder.itemView.setOnClickListener(v -> listener.onTopicClick(topic.id));
    }

    @Override
    public int getItemCount() {
        return topicList.size();
    }

    static class TopicViewHolder extends RecyclerView.ViewHolder {
        TextView topicName, courseName, wordCount;

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            topicName = itemView.findViewById(R.id.topicName);
            courseName = itemView.findViewById(R.id.courseName);
            wordCount = itemView.findViewById(R.id.wordCount);
        }
    }
}
