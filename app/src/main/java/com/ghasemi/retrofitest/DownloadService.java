package com.ghasemi.retrofitest;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;
 

 
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
 
import okhttp3.ResponseBody;
 
import retrofit2.Call;
import retrofit2.Retrofit;
 
public class DownloadService extends IntentService {
 
    public DownloadService() {
        super("Download Service");
    }
 
    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private int totalFileSize;


//    Here we create DownloadService class which extends IntentService.
//            If we download file using AsyncTask within Activity life cycle,
//            the download will be interrupted when the device is rotated.
//    In order to avoid that we perform download using IntentService and pass the result
//    back to Activity using a Broadcast. Also we need not worry about threads while using
//    Intent Service, it automatically creates a new thread to do work and destroys as soon as the work is finished.
//
//    We also use Notification progress to display how much file downloaded.
//            The initDownload() method initializes the download and gets the ResponseBody object.
//    Previously we used Retrofit to make Asynchronous requests. Since we are using IntentService
//    we can make Synchronous request. It is done by using calling execute() method on Call object.
//    For Asynchronous we used enqueue() method.
//
//    The ResponseBody object is passed to downloadFile() method which starts the download.
//    The downloaded file is stored in Downloads directory. The total file size is obtained by
//    calling contentLength() method on ResponseBody object which returns result in bytes.
//    We download the file by passing the InputStream to BufferedInputStream. While downloading the
//    file the notification is sent every 1 second so that it does not affect the main thread.
//
//    When the download is completed the onDownloadComplete() method is called. The sendIntent()
//    method sends the broadcast using LocalBroadcastManager which we will handle in MainActivity.
//    The sendNotification() method updates the notification progress.The onTaskRemoved method will
//    be called when the app is destroyed completel
 
    @Override
    protected void onHandleIntent(Intent intent) {
 
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
 
        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_file_download)
                .setContentTitle("Download")
                .setContentText("Downloading File")
                .setAutoCancel(true);
        notificationManager.notify(0, notificationBuilder.build());
 
        initDownload();
 
    }
 
    private void initDownload(){
 
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://download.learn2crack.com/")
                .build();
 
        RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);
 
        Call<ResponseBody> request = retrofitInterface.downloadFile();
        try {
 
            downloadFile(request.execute().body());
 
        } catch (IOException e) {
 
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
 
        }
    }
 
    private void downloadFile(ResponseBody body) throws IOException {
 
        int count;
        byte data[] = new byte[1024 * 4];
        long fileSize = body.contentLength();
        InputStream bis = new BufferedInputStream(body.byteStream(), 1024 * 8);
        File outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "file.zip");
        OutputStream output = new FileOutputStream(outputFile);
        long total = 0;
        long startTime = System.currentTimeMillis();
        int timeCount = 1;
        while ((count = bis.read(data)) != -1) {
 
            total += count;
            totalFileSize = (int) (fileSize / (Math.pow(1024, 2)));
            double current = Math.round(total / (Math.pow(1024, 2)));
 
            int progress = (int) ((total * 100) / fileSize);
 
            long currentTime = System.currentTimeMillis() - startTime;
 
            Download download = new Download();
            download.setTotalFileSize(totalFileSize);
 
            if (currentTime > 1000 * timeCount) {
 
                download.setCurrentFileSize((int) current);
                download.setProgress(progress);
                sendNotification(download);
                timeCount++;
            }
 
            output.write(data, 0, count);
        }
        onDownloadComplete();
        output.flush();
        output.close();
        bis.close();
 
    }
 
    private void sendNotification(Download download){
 
        sendIntent(download);
        notificationBuilder.setProgress(100,download.getProgress(),false);
        notificationBuilder.setContentText("Downloading file "+ download.getCurrentFileSize() +"/"+totalFileSize +" MB");
        notificationManager.notify(0, notificationBuilder.build());
    }
 
    private void sendIntent(Download download){
 
        Intent intent = new Intent(MainActivity.MESSAGE_PROGRESS);
        intent.putExtra("download",download);
        LocalBroadcastManager.getInstance(DownloadService.this).sendBroadcast(intent);
    }
 
    private void onDownloadComplete(){
 
        Download download = new Download();
        download.setProgress(100);
        sendIntent(download);
 
        notificationManager.cancel(0);
        notificationBuilder.setProgress(0,0,false);
        notificationBuilder.setContentText("File Downloaded");
        notificationManager.notify(0, notificationBuilder.build());
 
    }
 
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        notificationManager.cancel(0);
    }
 
}