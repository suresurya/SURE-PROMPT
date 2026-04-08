package com.suresurya.sureprompt;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.suresurya.sureprompt.models.PromptDetailDto;
import com.suresurya.sureprompt.network.PromptRepository;
import com.suresurya.sureprompt.network.TokenManager;
import com.suresurya.sureprompt.ui.LoginActivity;
import com.suresurya.sureprompt.ui.PromptAdapter;

import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PromptAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private TextView tvEmptyState;
    private TextView tvFeedMeta;
    private View emptyStateContainer;
    private MaterialToolbar toolbar;
    private MaterialButton btnRetryFeed;
    private MaterialButtonToggleGroup toggleFeed;
    private LinearProgressIndicator loadingIndicator;
    private PromptRepository repository;

    private int currentPage = 0;
    private boolean isLastPage = false;
    private boolean isLoading = false;
    private String activeTab = "trending";

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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recyclerView);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        tvFeedMeta = findViewById(R.id.tvFeedMeta);
        emptyStateContainer = findViewById(R.id.emptyStateContainer);
        btnRetryFeed = findViewById(R.id.btnRetryFeed);
        toggleFeed = findViewById(R.id.toggleFeed);
        loadingIndicator = findViewById(R.id.loadingIndicator);

        swipeRefresh.setColorSchemeResources(
                R.color.md_theme_primary,
                R.color.md_theme_secondary,
                R.color.md_theme_tertiary
        );

        adapter = new PromptAdapter(this);
        recyclerView.setAdapter(adapter);

        repository = new PromptRepository(this);

        setupFeedControls();
        setupSwipeRefresh();
        setupInfiniteScroll();

        repository.getLocalPrompts().observe(this, entities -> {
            if (entities != null && !entities.isEmpty() && currentPage == 0 && "trending".equals(activeTab)) {
                List<PromptDetailDto> cachedDtos = entities.stream().map(entity -> {
                    PromptDetailDto dto = new PromptDetailDto();
                    dto.setId(entity.getId());
                    dto.setTitle(entity.getTitle());
                    dto.setPromptBody(entity.getPromptBody());
                    dto.setAuthorUsername(entity.getAuthorUsername());
                    dto.setAuthorAvatar(entity.getAuthorAvatar());
                    dto.setAiScore(entity.getAiScore());
                    dto.setAiStatus(entity.getAiStatus());
                    dto.setLikeCount(entity.getLikeCount());
                    dto.setLiked(entity.isLiked());
                    dto.setTags(entity.getTags());
                    return dto;
                }).collect(Collectors.toList());

                adapter.clear();
                adapter.addPrompts(cachedDtos);
                updateFeedMeta(cachedDtos.size());
                updateEmptyState();
            }
        });

        updateToolbarSubtitle();
        updateFeedMeta(0);
        resetAndFetch();
    }

    private void setupFeedControls() {
        toggleFeed.check(R.id.btnFeedTrending);
        toggleFeed.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) {
                return;
            }

            String nextTab = resolveTab(checkedId);
            if (nextTab.equals(activeTab) && currentPage == 0) {
                return;
            }

            activeTab = nextTab;
            updateToolbarSubtitle();
            resetAndFetch();
        });

        btnRetryFeed.setOnClickListener(view -> resetAndFetch());
    }

    private void setupSwipeRefresh() {
        swipeRefresh.setOnRefreshListener(this::resetAndFetch);
    }

    private void setupInfiniteScroll() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager == null || isLoading || isLastPage) {
                    return;
                }

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                    fetchFeed(currentPage + 1);
                }
            }
        });
    }

    private void resetAndFetch() {
        currentPage = 0;
        isLastPage = false;
        adapter.clear();
        updateFeedMeta(0);
        fetchFeed(0);
    }

    private void fetchFeed(int page) {
        setLoading(true);
        repository.refreshFeed(activeTab, page, new PromptRepository.RefreshCallback() {
            @Override
            public void onSuccess(List<PromptDetailDto> prompts, int totalPages, long totalElements) {
                setLoading(false);
                currentPage = page;

                if (page == 0) {
                    adapter.clear();
                }

                if (prompts != null && !prompts.isEmpty()) {
                    adapter.addPrompts(prompts);
                }

                if (totalPages <= 0) {
                    isLastPage = prompts == null || prompts.isEmpty();
                } else {
                    isLastPage = page >= (totalPages - 1);
                }

                int count = (int) Math.max(totalElements, adapter.getItemCount());
                updateFeedMeta(count);
                updateEmptyState();
            }

            @Override
            public void onError(String error) {
                setLoading(false);
                updateEmptyState();
                Toast.makeText(MainActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        isLoading = loading;
        swipeRefresh.setRefreshing(loading);
        loadingIndicator.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private String resolveTab(int checkedId) {
        if (checkedId == R.id.btnFeedAll) {
            return "all";
        }
        if (checkedId == R.id.btnFeedFollowing) {
            return "following";
        }
        return "trending";
    }

    private void updateToolbarSubtitle() {
        if (toolbar == null) {
            return;
        }
        int subtitleRes;
        switch (activeTab) {
            case "all":
                subtitleRes = R.string.feed_subtitle_all;
                break;
            case "following":
                subtitleRes = R.string.feed_subtitle_following;
                break;
            default:
                subtitleRes = R.string.feed_subtitle_trending;
                break;
        }
        toolbar.setSubtitle(subtitleRes);
    }

    private void updateFeedMeta(int count) {
        if (tvFeedMeta != null) {
            tvFeedMeta.setText(getString(R.string.feed_meta_template, count));
        }
    }

    private int getEmptyMessageRes() {
        switch (activeTab) {
            case "all":
                return R.string.feed_empty_all;
            case "following":
                return R.string.feed_empty_following;
            default:
                return R.string.feed_empty_trending;
        }
    }

    private void updateEmptyState() {
        boolean showEmpty = adapter.getItemCount() == 0 && !isLoading;
        tvEmptyState.setText(getString(getEmptyMessageRes()));
        tvEmptyState.setVisibility(showEmpty ? View.VISIBLE : View.GONE);
        emptyStateContainer.setVisibility(showEmpty ? View.VISIBLE : View.GONE);
        btnRetryFeed.setVisibility(showEmpty ? View.VISIBLE : View.GONE);
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
        }
        if (item.getItemId() == R.id.action_logout) {
            new TokenManager(this).clearToken();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
