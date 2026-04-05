package com.suresurya.sureprompt.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.suresurya.sureprompt.R;
import com.suresurya.sureprompt.models.RunRequest;
import com.suresurya.sureprompt.network.ApiService;
import com.suresurya.sureprompt.network.RetrofitClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LiveTryBottomSheet extends BottomSheetDialogFragment {

    private String promptText;
    private TextView tvLiveResult, tvLiveStatus;
    private ApiService apiService;

    public static LiveTryBottomSheet newInstance(String prompt) {
        LiveTryBottomSheet fragment = new LiveTryBottomSheet();
        Bundle args = new Bundle();
        args.putString("prompt", prompt);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            promptText = getArguments().getString("prompt");
        }
        apiService = RetrofitClient.getClient().create(ApiService.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_bottom_sheet_live, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvLiveResult = view.findViewById(R.id.tvLiveResult);
        tvLiveStatus = view.findViewById(R.id.tvLiveStatus);

        view.findViewById(R.id.btnCloseSheet).setOnClickListener(v -> dismiss());

        runLive();
    }

    private void runLive() {
        tvLiveResult.setText("Connecting to AI Gateway...\nGenerating output using your API key.");
        
        apiService.runLive(new RunRequest(promptText)).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        tvLiveResult.setText(response.body().string());
                        tvLiveStatus.setText("Execution Complete • Gemini 1.5");
                    } catch (Exception e) {
                        tvLiveResult.setText("Error reading output: " + e.getMessage());
                    }
                } else {
                    if (response.code() == 400) {
                        tvLiveResult.setText("Failed: Missing API Key.\nPlease add your Gemini key in the app Settings.");
                    } else if (response.code() == 429) {
                        tvLiveResult.setText("Failed: Daily Limit Reached (429).\nUpgrade or wait until tomorrow.");
                    } else {
                        tvLiveResult.setText("AI Gateway Error Code: " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                tvLiveResult.setText("Network Failure: Make sure the server is running at http://10.0.2.2:8080");
            }
        });
    }
}
