package com.dema.versatile.lib.alive;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.concurrent.TimeUnit;

import com.dema.versatile.lib.utils.AliveHelp;
import com.dema.versatile.lib.utils.UtilsLog;

public class AliveWorker extends Worker {
    public AliveWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        UtilsLog.aliveLog("worker", null);
        UtilsLog.send();
        AliveHelp.startPull("worker");
        return Result.success();
    }

    public static void startAliveWorker(Context context) {
        try {
            WorkManager.getInstance(context).cancelAllWork();
            WorkManager.getInstance(context).enqueue(new PeriodicWorkRequest.Builder(AliveWorker.class, 5, TimeUnit.MINUTES).build());
        } catch (Exception | Error e) {

        }
    }
}
