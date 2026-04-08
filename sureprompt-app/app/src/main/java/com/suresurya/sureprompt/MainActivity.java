package com.suresurya.sureprompt;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.annotation.NonNull;
import com.google.android.material.appbar.MaterialToolbar;
import com.suresurya.sureprompt.network.TokenManager;
import com.suresurya.sureprompt.ui.LoginActivity;
import com.suresurya.sureprompt.ui.PromptAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.widget.Toast;
import java.util.List;
import com.suresurya.sureprompt.models.PromptDetailDto;
import com.suresurya.sureprompt.network.PromptRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PromptAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private TextView tvEmptyState;
    private MaterialToolbar toolbar;
    private PromptRepository repository;

    private int currentPage = 0;
    private boolean isLastPage = false;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TokenManager tokenManager = new TokenManager(this);
        if (tokenManager.getToken() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recyclerView);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        swipeRefresh.setColorSchemeResources(
            R.color.md_theme_primary,
            R.color.md_theme_secondary,
            R.color.md_theme_tertiary
        );

        adapter = new PromptAdapter(this);
        recyclerView.setAdapter(adapter);

        repository = new PromptRepository(this);

        // Offline-first: Observe local DB for immediate UI updates
        repository.getLocalPrompts().observe(this, entities -> {
            if (entities != null && !entities.isEmpty() && currentPage == 0) {
                // Mapping entities back to DTOs for the adapter
                List<PromptDetailDto> cachedDtos = entities.stream().map(e -> {
                    PromptDetailDto dto = new PromptDetailDto();
                    dto.setId(e.getId());
                    dto.setTitle(e.getTitle());
                    dto.setPromptBody(e.getPromptBody());
                    dto.setAuthorUsername(e.getAuthorUsername());
                    dto.setAuthorAvatar(e.getAuthorAvatar());
                    dto.setAiScore(e.getAiScore());
                    dto.setAiStatus(e.getAiStatus());
                    dto.setLikeCount(e.getLikeCount());
                    dto.setLiked(e.isLiked());
                    dto.setTags(e.getTags());
                    return dto;
                }).collect(java.util.stream.Collectors.toList());
                adapter.clear();
                adapter.addPrompts(cachedDtos);
                updateEmptyState();
            }
        });

        swipeRefresh.setOnRefreshListener(() -> {
            currentPage = 0;
            isLastPage = false;
            adapter.clear();
            fetchFeed(0);
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && !isLoading && !isLastPage) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                        currentPage++;
                        fetchFeed(currentPage);
                    }
                }
            }
        });
        
        updateEmptyState();
        fetchFeed(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, com.suresurya.sureprompt.ui.SettingsActivity.class));
            return true;
        } else if (item.getItemId() == R.id.action_logout) {
            new TokenManager(this).clearToken();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchFeed(int page) {
        isLoading = true;
        swipeRefresh.setRefreshing(true);
        updateEmptyState();
        repository.refreshFeed("trending", page, new PromptRepository.RefreshCallback() {
            @Override
            public void onSuccess(List<PromptDetailDto> prompts) {
                isLoading = false;
                swipeRefresh.setRefreshing(false);
                if (prompts == null || prompts.isEmpty()) {
                    isLastPage = true;
                } else {
                    if (page == 0) adapter.clear();
                    adapter.addPrompts(prompts);
                }
                updateEmptyState();
            }

            @Override
            public void onError(String error) {
                isLoading = false;
                swipeRefresh.setRefreshing(false);
                updateEmptyState();
                Toast.makeText(MainActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateEmptyState() {
        if (tvEmptyState == null) {
            return;
        }
        boolean showEmpty = adapter.getItemCount() == 0 && !isLoading;
        tvEmptyState.setVisibility(showEmpty ? View.VISIBLE : View.GONE);
    }
}