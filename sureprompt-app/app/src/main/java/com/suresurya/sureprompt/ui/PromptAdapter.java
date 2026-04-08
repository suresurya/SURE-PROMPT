package com.suresurya.sureprompt.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.suresurya.sureprompt.R;
import com.suresurya.sureprompt.models.PromptDetailDto;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class PromptAdapter extends RecyclerView.Adapter<PromptAdapter.ViewHolder> {

    private List<PromptDetailDto> prompts = new ArrayList<>();
    private final Context context;

    public PromptAdapter(Context context) {
        this.context = context;
    }

    public void addPrompts(List<PromptDetailDto> newPrompts) {
        int startPosition = this.prompts.size();
        this.prompts.addAll(newPrompts);
        notifyItemRangeInserted(startPosition, newPrompts.size());
    }

    public void clear() {
        this.prompts.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prompt, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PromptDetailDto prompt = prompts.get(position);
        holder.tvTitle.setText(prompt.getTitle());
        holder.tvAuthor.setText("by " + (prompt.getAuthorUsername() != null ? prompt.getAuthorUsername() : "User"));
        
        if (prompt.getAiScore() != null) {
            holder.cardScore.setVisibility(View.VISIBLE);
            double scoreValue = prompt.getAiScore() / 4.0; // Normalized 1-10 like on web
            holder.tvScore.setText(String.format("%.1f", scoreValue));
            
            if (scoreValue >= 8.5) {
                holder.cardScore.setCardBackgroundColor(ContextCompat.getColor(context, R.color.status_success_bg));
                holder.tvScore.setTextColor(ContextCompat.getColor(context, R.color.status_success_text));
            } else if (scoreValue >= 6.0) {
                holder.cardScore.setCardBackgroundColor(ContextCompat.getColor(context, R.color.status_warning_bg));
                holder.tvScore.setTextColor(ContextCompat.getColor(context, R.color.status_warning_text));
            } else {
                holder.cardScore.setCardBackgroundColor(ContextCompat.getColor(context, R.color.status_error_bg));
                holder.tvScore.setTextColor(ContextCompat.getColor(context, R.color.status_error_text));
            }
        } else {
            holder.cardScore.setVisibility(View.GONE);
        }

        // Handle Tags
        holder.chipGroup.removeAllViews();
        if (prompt.getTags() != null) {
            for (String tag : prompt.getTags()) {
                Chip chip = new Chip(context);
                chip.setText(tag);
                chip.setChipBackgroundColorResource(R.color.tag_chip_bg);
                chip.setTextColor(ContextCompat.getColor(context, R.color.tag_chip_text));
                chip.setChipMinHeight(30f);
                chip.setTextSize(12f);
                chip.setClickable(false);
                chip.setCheckable(false);
                chip.setEnsureMinTouchTargetSize(false);
                holder.chipGroup.addView(chip);
            }
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PromptDetailActivity.class);
            intent.putExtra("prompt_id", prompt.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return prompts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAuthor, tvScore;
        MaterialCardView cardScore;
        ChipGroup chipGroup;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvScore = itemView.findViewById(R.id.tvScore);
            cardScore = itemView.findViewById(R.id.cardScore);
            chipGroup = itemView.findViewById(R.id.chipGroup);
        }
    }
}
