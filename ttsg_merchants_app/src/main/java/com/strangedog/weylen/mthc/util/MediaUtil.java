package com.strangedog.weylen.mthc.util;

import android.content.Context;
import android.media.MediaPlayer;

import com.strangedog.weylen.mtch.R;

/**
 * Created by weylen on 2016-09-12.
 */
public class MediaUtil {

    private static MediaPlayer player;
    public static void paly(Context context){
        reset();
        player = MediaPlayer.create(context, R.raw.alarm);
        if (player != null){
            player.start();
            player.setOnCompletionListener(mp -> mp.start());
        }
    }

    private static void reset(){
        if (player != null){
            player.stop();
            player.reset();
            player.release();
            player = null;
        }
    }

    public static void stop(){
        reset();
    }
}
