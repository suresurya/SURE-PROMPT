package com.suresurya.sureprompt.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
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
    private TextView tvLiveResult;
    private TextView tvLiveStatus;
    private LinearProgressIndicator liveProgress;
    private MaterialButton btnCopyOutput;
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
        liveProgress = view.findViewById(R.id.liveProgress);
        btnCopyOutput = view.findViewById(R.id.btnCopyOutput);

        view.findViewById(R.id.btnCloseSheet).setOnClickListener(v -> dismiss());
        btnCopyOutput.setOnClickListener(v -> copyOutput());

        runLive();
    }

    private void runLive() {
        setLoading(true);
        tvLiveResult.setText(getString(R.string.live_sheet_connecting));

        apiService.runLive(new RunRequest(promptText)).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        tvLiveResult.setText(response.body().string());
                        tvLiveStatus.setText(getString(R.string.live_sheet_complete));
                    } catch (Exception exception) {
                        tvLiveResult.setText(getString(R.string.live_sheet_read_error, exception.getMessage()));
                    }
                    return;
                }

                if (response.code() == 400) {
                    tvLiveResult.setText(getString(R.string.live_sheet_missing_key));
                } else if (response.code() == 429) {
                    tvLiveResult.setText(getString(R.string.live_sheet_rate_limited));
                } else {
                    tvLiveResult.setText(getString(R.string.live_sheet_error_code, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                setLoading(false);
                tvLiveResult.setText(getString(R.string.live_sheet_network_error));
            }
        });
    }

    private void setLoading(boolean loading) {
        liveProgress.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnCopyOutput.setEnabled(!loading);
    }

    private void copyOutput() {
        ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard == null) {
            return;
        }
        ClipData clip = ClipData.newPlainText("ai-output", tvLiveResult.getText());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(requireContext(), getString(R.string.live_sheet_copy_success), Toast.LENGTH_SHORT).show();
    }
}
