package com.raqeeb.app;

import android.content.Context;
import android.util.Log;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CrashLogger implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "CrashLogger";
    private final Context context;
    private final Thread.UncaughtExceptionHandler defaultHandler;

    private CrashLogger(Context context) {
        this.context = context.getApplicationContext();
        this.defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    public static void install(Context context) {
        Thread.UncaughtExceptionHandler current = Thread.getDefaultUncaughtExceptionHandler();
        if (!(current instanceof CrashLogger)) {
            Thread.setDefaultUncaughtExceptionHandler(new CrashLogger(context));
        }
    }

    public static String getCrashLogPath(Context context) {
        File logFile = new File(context.getFilesDir(), "crash_log.txt");
        return logFile.getAbsolutePath();
    }

    public static void logMessage(Context context, String level, String message) {
        try {
            File logFile = new File(context.getFilesDir(), "app_log.txt");
            FileWriter writer = new FileWriter(logFile, true);
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date());
            writer.append("[").append(timestamp).append("] [").append(level).append("] ").append(message).append("\n");
            writer.flush();
            writer.close();
        } catch (Exception e) {
            Log.e(TAG, "Failed to write app log", e);
        }
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        try {
            File logFile = new File(context.getFilesDir(), "crash_log.txt");
            FileWriter writer = new FileWriter(logFile, true);
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date());
            StringWriter sw = new StringWriter();
            throwable.printStackTrace(new PrintWriter(sw));
            writer.append("=== CRASH AT ").append(timestamp).append(" (Thread: ").append(thread.getName()).append(") ===\n");
            writer.append(sw.toString()).append("\n");
            writer.flush();
            writer.close();
        } catch (Exception e) {
            Log.e(TAG, "Failed to save crash log", e);
        }
        if (defaultHandler != null) {
            defaultHandler.uncaughtException(thread, throwable);
        }
    }
}
