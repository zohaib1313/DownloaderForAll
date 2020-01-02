package com.example.downloaderforall;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.commit451.youtubeextractor.YouTubeExtractionResult;
import com.commit451.youtubeextractor.YouTubeExtractor;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Priority;
import com.downloader.Progress;
import com.downloader.Status;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.CacheControl;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    ProgressBar progressBar;
    private static String dirPath;
    private Button btnDownload, btnCancelDownload, btnPauseDownloading;
    private EditText etUrl;
    private int downloadIdOne;
    private TextView textViewProgressOne;


    private static final String YOUTUBE_ID = "TEZsU2AdAkI";
String hello="asasdfasdfasd";


    private Callback<YouTubeExtractionResult> mExtractionCallback = new Callback<YouTubeExtractionResult>() {
        @Override
        public void onResponse(Call<YouTubeExtractionResult> call, Response<YouTubeExtractionResult> response) {
            //      bindVideoResult(response.body());
            if(response.isSuccessful()) {
                if(response.body()!=null){
                Log.d("taggg", "responce yes:::::"+response.body().getBestAvailableQualityVideoUri().toString());
            }
            }
            else {
               //response.code();
              // response.message();
                Log.d("tagg","no responce "+response.message()+ response.body() +"\n"+response.code());
            }

        }

        @Override
        public void onFailure(Call<YouTubeExtractionResult> call, Throwable t) {
            //    onError(t);

            Log.d("tagg","failure ::::: "+t.getMessage());
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        dirPath = Utils.getRootDirPath(getApplicationContext());

//      YouTubeExtractor mExtractor = YouTubeExtractor.create();
//
//         mExtractor.extract(YOUTUBE_ID).enqueue(mExtractionCallback);


        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String downloadUrl = etUrl.getText().toString().trim();


                if (downloadUrl.isEmpty()) {
                    Toast.makeText(MainActivity.this, "paste url first", Toast.LENGTH_SHORT).show();
                } else {
                    //startDownloading
                    etUrl.setEnabled(false);
                    //if already downloading

//                    if (Status.RUNNING == PRDownloader.getStatus(downloadIdOne)) {
//                        PRDownloader.pause(downloadIdOne);
//                        btnDownload.setText("start");
//                        return;
//                    }
//                    btnDownload.setText("pause");
//                    btnDownload.setEnabled(false);
//                    progressBar.setIndeterminate(true);
//                    progressBar.getIndeterminateDrawable().setColorFilter(
//                            Color.BLUE, android.graphics.PorterDuff.Mode.SRC_IN);
//
//                    if (Status.PAUSED == PRDownloader.getStatus(downloadIdOne)) {
//                        PRDownloader.resume(downloadIdOne);
//                        btnDownload.setText("pause");
//                        return;
//                    }

                    progressBar.setIndeterminate(true);
                    progressBar.getIndeterminateDrawable().setColorFilter(
                            Color.BLUE, android.graphics.PorterDuff.Mode.SRC_IN);
                       YouTubeFetcher.fetchUrl(downloadUrl,MainActivity.this.getApplicationContext());

//                    if(downloadUrl.contains("youtube.com"))
//                    {
//                        //it is a youtube link
//                    }


                    String fileName = checkLinkExtension(downloadUrl);


                    Log.d("urlname", dirPath);
                    downloadIdOne = PRDownloader.download(downloadUrl, dirPath, System.currentTimeMillis() + fileName)
                            .setConnectTimeout(300)
                            .setPriority(Priority.HIGH)
                            .build()
                            .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                                @Override
                                public void onStartOrResume() {
                                    progressBar.setIndeterminate(false);
                                    Toast.makeText(MainActivity.this, "started", Toast.LENGTH_SHORT).show();

                                }
                            })
                            .setOnPauseListener(new OnPauseListener() {
                                @Override
                                public void onPause() {

                                    Toast.makeText(MainActivity.this, "pause", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setOnCancelListener(new OnCancelListener() {
                                @Override
                                public void onCancel() {
                                    progressBar.setProgress(0);
                                    progressBar.setIndeterminate(false);
                                    Toast.makeText(MainActivity.this, "cancelled", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setOnProgressListener(new OnProgressListener() {
                                @Override
                                public void onProgress(Progress progress) {
                                    long progressPercent = progress.currentBytes * 100 / progress.totalBytes;
                                    progressBar.setProgress((int) progressPercent);
                                    textViewProgressOne.setText(Utils.getProgressDisplayLine(progress.currentBytes, progress.totalBytes));
                                    progressBar.setIndeterminate(false);
                                }
                            })
                            .start(new OnDownloadListener() {
                                @Override
                                public void onDownloadComplete() {
                                    btnDownload.setEnabled(true);
                                    Toast.makeText(MainActivity.this, "completed", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onError(Error error) {

                                    String errorr;

                                    if (error.isConnectionError()) {
                                        errorr = error.getConnectionException().getMessage();
                                    }
                                    if (error.isServerError()) {

                                        errorr = error.getServerErrorMessage();
                                    }
                                    errorr = "unknow error occured";
                                    Toast.makeText(MainActivity.this, getString(R.string.some_error_occurred) + " " + errorr, Toast.LENGTH_SHORT).show();

                                }
                            });


                }
            }
        });

        btnCancelDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PRDownloader.cancel(downloadIdOne);
                btnDownload.setEnabled(true);
                etUrl.setEnabled(true);
                progressBar.setProgress(0);
                progressBar.setIndeterminate(false);
                PRDownloader.cleanUp(1);

            }
        });
        btnPauseDownloading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Status.RUNNING == PRDownloader.getStatus(downloadIdOne)) {
                    PRDownloader.pause(downloadIdOne);
                    btnPauseDownloading.setText("resume");
                    return;
                }
                progressBar.setIndeterminate(true);

                if (Status.PAUSED == PRDownloader.getStatus(downloadIdOne)) {
                    PRDownloader.resume(downloadIdOne);
                    btnPauseDownloading.setText("pause");
                    return;
                }
            }
        });

    }

    private String checkLinkExtension(String downloadUrl) {
        String fileName = downloadUrl.substring(downloadUrl.lastIndexOf('/') + 1, downloadUrl.length());
//         if (downloadUrl.contains(".jpg")) {
//            name = System.currentTimeMillis() + ".jpg";
//        }
//        if(downloadUrl.contains(".jpeg"))
//        {
//            name=System.currentTimeMillis() + ".jpeg";
//        }

        Log.d("urlname", System.currentTimeMillis() + fileName);
        return fileName;

    }

    private void initViews() {
        btnPauseDownloading = findViewById(R.id.btnPauseDownloading);
        progressBar = findViewById(R.id.progressBar);
        btnDownload = findViewById(R.id.btnStartDownloading);
        btnCancelDownload = findViewById(R.id.btnCancelDownloading);
        etUrl = findViewById(R.id.etUrl);
        textViewProgressOne = findViewById(R.id.textViewProgressOne);

    }
}
