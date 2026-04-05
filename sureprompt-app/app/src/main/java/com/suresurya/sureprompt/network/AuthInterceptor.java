package com.suresurya.sureprompt.network;

import androidx.annotation.NonNull;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import com.suresurya.sureprompt.models.AuthResponse;
import com.suresurya.sureprompt.models.RefreshTokenRequest;

public class AuthInterceptor implements Interceptor {

    private final TokenManager tokenManager;

    public AuthInterceptor(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder requestBuilder = request.newBuilder();

        if (request.header("No-Authentication") == null) {
            String token = tokenManager.getToken();
            if (token != null) {
                requestBuilder.addHeader("Authorization", "Bearer " + token);
            }
        }

        Response response = chain.proceed(requestBuilder.build());

        if (response.code() == 401) {
            synchronized (this) {
                String currentToken = tokenManager.getToken();
                
                // If token has changed from another thread's refresh while we were waiting
                if (currentToken != null && !currentToken.equals(request.header("Authorization"))) {
                    return chain.proceed(request.newBuilder()
                            .header("Authorization", "Bearer " + currentToken)
                            .build());
                }

                String refreshToken = tokenManager.getRefreshToken();
                if (refreshToken != null) {
                    ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
                    retrofit2.Response<AuthResponse> refreshResponse = apiService.refresh(new RefreshTokenRequest(refreshToken)).execute();

                    if (refreshResponse.isSuccessful() && refreshResponse.body() != null) {
                        String newAccessToken = refreshResponse.body().getToken();
                        tokenManager.saveToken(newAccessToken);
                        
                        response.close(); // Close the previous 401 response
                        
                        return chain.proceed(request.newBuilder()
                                .removeHeader("Authorization")
                                .addHeader("Authorization", "Bearer " + newAccessToken)
                                .build());
                    } else {
                        tokenManager.clearToken();
                    }
                } else {
                    tokenManager.clearToken();
                }
            }
        }

        return response;
    }
}
