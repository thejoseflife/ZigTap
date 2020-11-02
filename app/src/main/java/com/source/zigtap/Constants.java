package com.source.zigtap;

import android.content.Context;
import android.media.MediaPlayer;
import android.widget.Toast;

import java.util.ArrayList;

public class Constants {

    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;

    public static Context CURRENT_CONTEXT;
    public static MediaPlayer[] SOUNDTRACKS = new MediaPlayer[3];
    /*
    0 = menu music
    1 = game music
    2 = rocket sound
     */

    public static long INIT_TIME;

    public static boolean WANTS_MUSIC = true;
    public static boolean WANTS_SOUND = true;

    public static float VOLUME = 0.2f;

    public static int HIGH_SCORE;

}
