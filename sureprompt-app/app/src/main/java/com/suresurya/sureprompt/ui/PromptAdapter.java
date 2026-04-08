package com.suresurya.sureprompt.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.suresurya.sureprompt.R;
import com.suresurya.sureprompt.models.PromptDetailDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PromptAdapter extends RecyclerView.Adapter<PromptAdapter.ViewHolder> {

    private final List<PromptDetailDto> prompts = new ArrayList<>();
    private final Context context;

    public PromptAdapter(Context context) {
        this.context = context;
    }

    public void addPrompts(List<PromptDetailDto> newPrompts) {
        int startPosition = prompts.size();
        prompts.addAll(newPrompts);
        notifyItemRangeInserted(startPosition, newPrompts.size());
    }

    public void clear() {
        prompts.clear();
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
        String author = prompt.getAuthorUsername() != null
                ? prompt.getAuthorUsername()
                : context.getString(R.string.prompt_author_fallback);
        holder.tvAuthor.setText(context.getString(R.string.item_author_format, author));

        holder.ivVerified.setVisibility(prompt.isAiVerified() ? View.VISIBLE : View.GONE);
        bindScore(holder, prompt.getAiScore());
        bindStatusChip(holder, prompt.getAiStatus(), prompt.isAiVerified());
        bindTags(holder, prompt.getTags());

        int likeCount = prompt.getLikeCount() != null ? prompt.getLikeCount() : 0;
        holder.tvLikeMeta.setText(context.getString(R.string.item_likes_format, likeCount));

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, PromptDetailActivity.class);
            intent.putExtra("prompt_id", prompt.getId());
            context.startActivity(intent);
        });
    }

    private void bindScore(ViewHolder holder, Double aiScore) {
        if (aiScore == null) {
            holder.cardScore.setVisibility(View.GONE);
            holder.promptCard.setStrokeColor(ContextCompat.getColor(context, R.color.md_theme_surface_variant));
            return;
        }

        holder.cardScore.setVisibility(View.VISIBLE);
        double scoreValue = aiScore / 4.0;
        holder.tvScore.setText(String.format(Locale.US, "%.1f", scoreValue));

        int bgColor;
        int textColor;
        int cardStroke;
        if (scoreValue >= 8.5) {
            bgColor = ContextCompat.getColor(context, R.color.status_success_bg);
            textColor = ContextCompat.getColor(context, R.color.status_success_text);
            cardStroke = ContextCompat.getColor(context, R.color.status_success_text);
        } else if (scoreValue >= 6.0) {
            bgColor = ContextCompat.getColor(context, R.color.status_warning_bg);
            textColor = ContextCompat.getColor(context, R.color.status_warning_text);
            cardStroke = ContextCompat.getColor(context, R.color.status_warning_text);
        } else {
            bgColor = ContextCompat.getColor(context, R.color.status_error_bg);
            textColor = ContextCompat.getColor(context, R.color.status_error_text);
            cardStroke = ContextCompat.getColor(context, R.color.status_error_text);
        }

        holder.cardScore.setCardBackgroundColor(bgColor);
        holder.tvScore.setTextColor(textColor);
        holder.promptCard.setStrokeColor(cardStroke);
    }

    private void bindStatusChip(ViewHolder holder, String aiStatus, boolean verified) {
        if (aiStatus == null && !verified) {
            holder.chipStatus.setVisibility(View.GONE);
            return;
        }

        holder.chipStatus.setVisibility(View.VISIBLE);

        String status = aiStatus != null ? aiStatus : "COMPLETED";
        int bgColor;
        int textColor;
        String label;

        switch (status) {
            case "PENDING":
                label = context.getString(R.string.item_status_processing);
                bgColor = ContextCompat.getColor(context, R.color.status_warning_bg);
                textColor = ContextCompat.getColor(context, R.color.status_warning_text);
                break;
            case "COMPLETED":
                label = context.getString(R.string.item_status_verified);
                bgColor = ContextCompat.getColor(context, R.color.status_success_bg);
                textColor = ContextCompat.getColor(context, R.color.status_success_text);
                break;
            case "FAILED":
                label = context.getString(R.string.item_status_review);
                bgColor = ContextCompat.getColor(context, R.color.status_error_bg);
                textColor = ContextCompat.getColor(context, R.color.status_error_text);
                break;
            default:
                label = context.getString(R.string.item_status_unknown);
                bgColor = ContextCompat.getColor(context, R.color.md_theme_surface_variant);
                textColor = ContextCompat.getColor(context, R.color.md_theme_on_surface_variant);
                break;
        }

        holder.chipStatus.setText(label);
        holder.chipStatus.setChipBackgroundColorResource(R.color.tag_chip_bg);
        holder.chipStatus.setChipBackgroundColor(android.content.res.ColorStateList.valueOf(bgColor));
        holder.chipStatus.setTextColor(textColor);
    }

    private void bindTags(ViewHolder holder, List<String> tags) {
        holder.chipGroup.removeAllViews();
        if (tags == null || tags.isEmpty()) {
            return;
        }

        for (String tag : tags) {
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

    @Override
    public int getItemCount() {
        return prompts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvAuthor;
        TextView tvScore;
        TextView tvLikeMeta;
        MaterialCardView promptCard;
        MaterialCardView cardScore;
        ImageView ivVerified;
        Chip chipStatus;
        ChipGroup chipGroup;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvScore = itemView.findViewById(R.id.tvScore);
            tvLikeMeta = itemView.findViewById(R.id.tvLikeMeta);
            promptCard = itemView.findViewById(R.id.promptCard);
            cardScore = itemView.findViewById(R.id.cardScore);
            ivVerified = itemView.findViewById(R.id.ivVerified);
            chipStatus = itemView.findViewById(R.id.chipStatus);
            chipGroup = itemView.findViewById(R.id.chipGroup);
        }
    }
}
