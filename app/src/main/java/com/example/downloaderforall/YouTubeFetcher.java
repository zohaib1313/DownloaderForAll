package com.example.downloaderforall;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YouTubeUriExtractor;
import at.huber.youtubeExtractor.YtFile;

public  class YouTubeFetcher {



    public static String fetchUrl(String url, Context context)
    {
         String downloadUrlYoutube="";

        Log.d("ytfiles","in function");


        new YouTubeExtractor(context) {

            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {

                if (ytFiles == null) {
                    // Something went wrong we got no urls. Always check this.
                    Log.d("ytfiles","null ytfile");
                                     return;
                }
                // Iterate over itags
                for (int i = 0, itag; i < ytFiles.size(); i++) {
                    itag = ytFiles.keyAt(i);
                    // ytFile represents one file with its url and meta data
                    YtFile ytFile = ytFiles.get(itag);
                         Log.d( "ytfilee",ytFile.getUrl());
                    // Just add videos in a decent format => height -1 = audio
                    if (ytFile.getFormat().getHeight() == -1 || ytFile.getFormat().getHeight() >= 360) {

                    }
                }
            }
        }.extract("https://www.youtube.com/watch?v=TEZsU2AdAkI", true, true);



        return downloadUrlYoutube;
    }
}
