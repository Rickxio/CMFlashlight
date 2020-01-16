package com.dema.versatile.lib.alive;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;

import com.dema.versatile.lib.utils.AliveHelp;
import com.dema.versatile.lib.utils.UtilsEnv;
import com.dema.versatile.lib.utils.UtilsLog;
import com.dema.versatile.lib.utils.UtilsTime;

public class AliveJobService extends JobService {
    private static int VALUE_INT_JOB_ID = 0x90000;

    @Override
    public boolean onStartJob(JobParameters params) {
        UtilsLog.aliveLog("job", null);
        UtilsLog.send();
        AliveHelp.startPull("job");
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    public static void jobSchedule(Context context) {
        if (null == context)
            return;

        try {
            if (UtilsEnv.getAndroidVersion() >= 21) {
                JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                JobInfo jobInfo = new JobInfo.Builder(VALUE_INT_JOB_ID, new ComponentName(context, AliveJobService.class))
                        .setPeriodic(UtilsTime.VALUE_LONG_TIME_ONE_MINUTE * 5)
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .setPersisted(true)
                        .build();
                jobScheduler.cancelAll();
                jobScheduler.schedule(jobInfo);
            }
        } catch (Exception | Error e) {

        }
    }
}
