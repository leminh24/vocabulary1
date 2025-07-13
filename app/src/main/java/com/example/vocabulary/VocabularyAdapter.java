package com.example.vocabulary;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VocabularyAdapter extends RecyclerView.Adapter<VocabularyAdapter.VocabViewHolder> {
    private List<Vocabulary> vocabList;

    public VocabularyAdapter(List<Vocabulary> vocabList) {
        this.vocabList = vocabList;
    }

    @NonNull
    @Override
    public VocabViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vocabulary, parent, false);
        return new VocabViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VocabViewHolder holder, int position) {
        Vocabulary vocab = vocabList.get(position);
        holder.word.setText(vocab.word);
        holder.definition.setText(vocab.meaning_vi);
        holder.example.setText(vocab.example);

        // Đặt trạng thái ✓ hoặc ✗
        if (vocab.status == 1) {
            holder.status.setText("✓");
            holder.status.setTextColor(0xFF2E7D32); // xanh lá
        } else {
            holder.status.setText("✗");
            holder.status.setTextColor(0xFFD32F2F); // đỏ
        }
    }


    @Override
    public int getItemCount() {
        return vocabList.size();
    }

    static class VocabViewHolder extends RecyclerView.ViewHolder {
        TextView word, definition, example, status;

        public VocabViewHolder(@NonNull View itemView) {
            super(itemView);
            word = itemView.findViewById(R.id.txtWord);
            definition = itemView.findViewById(R.id.txtDefinition);
            example = itemView.findViewById(R.id.txtExample);
            status = itemView.findViewById(R.id.txtStatus);
        }
    }
}
