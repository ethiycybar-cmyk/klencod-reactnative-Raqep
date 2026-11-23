package com.raqeeb.app;

import androidx.annotation.NonNull;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class AppLoggerModule extends ReactContextBaseJavaModule {
    private final ReactApplicationContext reactContext;

    public AppLoggerModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @NonNull
    @Override
    public String getName() {
        return "AppLogger";
    }

    @ReactMethod
    public void log(String level, String message) {
        CrashLogger.logMessage(reactContext, level, message);
    }

    @ReactMethod
    public void getCrashLogs(Promise promise) {
        try {
            File logFile = new File(reactContext.getFilesDir(), "crash_log.txt");
            if (!logFile.exists()) {
                promise.resolve("");
                return;
            }
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(logFile));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            promise.resolve(sb.toString());
        } catch (Exception e) {
            promise.reject("LOG_READ_ERROR", e);
        }
    }

    @ReactMethod
    public void clearCrashLogs(Promise promise) {
        try {
            File logFile = new File(reactContext.getFilesDir(), "crash_log.txt");
            if (logFile.exists()) {
                logFile.delete();
            }
            promise.resolve(true);
        } catch (Exception e) {
            promise.reject("LOG_CLEAR_ERROR", e);
        }
    }
}
