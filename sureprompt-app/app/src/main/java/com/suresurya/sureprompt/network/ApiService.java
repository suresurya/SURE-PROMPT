package com.suresurya.sureprompt.network;

import com.suresurya.sureprompt.models.FeedResponseDto;
import com.suresurya.sureprompt.models.AuthResponse;
import com.suresurya.sureprompt.models.LoginRequest;
import com.suresurya.sureprompt.models.RefreshTokenRequest;
import com.suresurya.sureprompt.models.PromptDetailDto;
import com.suresurya.sureprompt.models.RunRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Field;

public interface ApiService {

    @Headers("No-Authentication: true")
    @POST("api/auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @Headers("No-Authentication: true")
    @POST("api/auth/refresh")
    Call<AuthResponse> refresh(@Body RefreshTokenRequest request);

    @GET("api/v1/feed")
    Call<FeedResponseDto> getFeed(
            @Query("tab") String tab,
            @Query("page") int page
    );

    @GET("api/v1/prompts/{id}")
    Call<PromptDetailDto> getPrompt(@Path("id") Long id);

    @FormUrlEncoded
    @POST("api/v1/settings/api-key")
    Call<ResponseBody> saveKey(@Field("key") String key);

    @POST("api/ai/live-try")
    Call<ResponseBody> runLive(@Body RunRequest request);
}
