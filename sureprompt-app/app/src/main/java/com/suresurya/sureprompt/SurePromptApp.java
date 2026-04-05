package com.suresurya.sureprompt;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

public class SurePromptApp extends Application {

    private static final String TAG = "SurePromptApp";

    @Override
    public void onCreate() {
        super.onCreate();

        // Global unhandled exception catching
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
                Log.e(TAG, "FATAL CRASH on thread " + thread.getName(), throwable);
                
                // In a production environment, you might log this to Firebase Crashlytics
                // and show a graceful error dialog before closing.
                
                System.exit(2);
            }
        });
    }
}
