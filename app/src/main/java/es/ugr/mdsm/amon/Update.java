package es.ugr.mdsm.amon;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class Update {
    private static String TAG = "Amon.Update";
    private static final String UPDATE_URL = "https://nesg.ugr.es/mdsm/downloads/last-version";
    private static int NOTIFY_UPDATE = 101;
    private static UpdateControl updateControl;

    public static void checkUpdate(final Context context){
        if(updateControl==null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(UpdateControl.ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
            updateControl = retrofit.create(UpdateControl.class);
        }

        updateControl.getLastVersion()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<Version>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<Version> versionResponse) {
                        if(versionResponse.isSuccessful()){
                            if(versionResponse.body().getLastVersion() > BuildConfig.VERSION_CODE){
                                displayUpdateNotification(context);
                            }
                        }else{
                            Log.e(TAG, "Unable to retrieve last version");
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Can't connect to the update server");
                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }

    private static void displayUpdateNotification(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(UPDATE_URL));
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "update")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.update_msg))
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        builder.setCategory(NotificationCompat.CATEGORY_STATUS)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFY_UPDATE, builder.build());
    }

    public static void createNotificationChannel(Context context){
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_update);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("update", name, importance);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }

}
