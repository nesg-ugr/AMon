package es.ugr.mdsm.amon;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class UpdateWorker extends Worker {
    private static String TAG = "Amon.UpdateWorker";

    public UpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @Override
    public Result doWork(){
        Log.d(TAG, "Starting update check");
        Update.checkUpdate(getApplicationContext());
        return Result.success();
    }
}
