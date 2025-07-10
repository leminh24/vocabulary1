package com.example.vocabulary;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
    private List<Course> courseList;
    private OnCourseClickListener listener;

    public interface OnCourseClickListener {
        void onCourseClick(int courseId);
    }

    public CourseAdapter(List<Course> courseList, OnCourseClickListener listener) {
        this.courseList = courseList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.courseName.setText(course.name);
        holder.wordCount.setText("Words: " + course.wordCount);
        holder.imageView.setImageResource(course.imageResId); // Load ảnh
        holder.itemView.setOnClickListener(v -> listener.onCourseClick(course.id));
        ImageView imageView = holder.imageView;
        String name = course.name;

        switch (name) {
            case "TOEIC":
                imageView.setImageResource(R.drawable.toeic);
                break;
            case "TOEFL":
                imageView.setImageResource(R.drawable.toefl);
                break;
            case "SAT":
                imageView.setImageResource(R.drawable.sat);
                break;
            case "IELTS":
                imageView.setImageResource(R.drawable.ielts);
                break;
            case "Business English":
                imageView.setImageResource(R.drawable.business_english);
                break;
            case "Basic English":
                imageView.setImageResource(R.drawable.basic);
                break;
            default:
                imageView.setImageResource(R.drawable.default_image); // fallback
                break;
        }

    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView courseName, wordCount;
        ImageView imageView;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseName = itemView.findViewById(R.id.courseName);
            wordCount = itemView.findViewById(R.id.wordCount);
            imageView = itemView.findViewById(R.id.imageView); // ánh xạ ảnh
        }
    }
}
