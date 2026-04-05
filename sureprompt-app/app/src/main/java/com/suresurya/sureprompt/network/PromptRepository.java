package com.suresurya.sureprompt.network;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.suresurya.sureprompt.db.AppDatabase;
import com.suresurya.sureprompt.db.PromptDao;
import com.suresurya.sureprompt.db.PromptEntity;
import com.suresurya.sureprompt.models.FeedResponseDto;
import com.suresurya.sureprompt.models.PromptDetailDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PromptRepository {

    private final PromptDao promptDao;
    private final ApiService apiService;

    public PromptRepository(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        promptDao = db.promptDao();
        apiService = RetrofitClient.getClient(context).create(ApiService.class);
    }

    public LiveData<List<PromptEntity>> getLocalPrompts() {
        return promptDao.getAllPrompts();
    }

    public void refreshFeed(String tab, int page, final RefreshCallback callback) {
        apiService.getFeed(tab, page).enqueue(new Callback<FeedResponseDto>() {
            @Override
            public void onResponse(Call<FeedResponseDto> call, Response<FeedResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PromptDetailDto> dtos = response.body().getPrompts();
                    if (dtos != null && !dtos.isEmpty()) {
                        // Save to DB on background thread
                        AppDatabase.databaseWriteExecutor.execute(() -> {
                            List<PromptEntity> entities = dtos.stream().map(dto -> new PromptEntity(
                                    dto.getId(),
                                    dto.getTitle(),
                                    dto.getPromptBody(),
                                    dto.getAuthorUsername(),
                                    dto.getAuthorAvatar(),
                                    dto.getAiScore(),
                                    dto.getAiStatus(),
                                    dto.getLikeCount(),
                                    dto.isLiked(),
                                    dto.getTags()
                            )).collect(Collectors.toList());
                            
                            if (page == 0) {
                                promptDao.deleteAll();
                            }
                            promptDao.insertPrompts(entities);
                        });
                    }
                    callback.onSuccess(dtos);
                } else {
                    callback.onError("Failed to fetch data");
                }
            }

            @Override
            public void onFailure(Call<FeedResponseDto> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public interface RefreshCallback {
        void onSuccess(List<PromptDetailDto> prompts);
        void onError(String error);
    }
}
