package com.source.zigtap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.
                FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Constants.SCREEN_WIDTH = dm.widthPixels;
        Constants.SCREEN_HEIGHT = dm.heightPixels;

        SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.
                MODE_PRIVATE);
        Constants.HIGH_SCORE = prefs.getInt("key", 0);

        setContentView(R.layout.menu_scene);

        Constants.CURRENT_CONTEXT = this;

        if(Constants.WANTS_MUSIC)
            playMusic(0);

        configureButtons();
    }

    private void configureButtons() {
        Button startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, GameActivity.class));
                if(Constants.WANTS_MUSIC) {
                    stopMusic(0);
                    playMusic(1);
                }
            }
        });


        Button settingsButton = findViewById(R.id.settings_button);

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                if (SettingsActivity.soundButton != null && SettingsActivity.musicButton != null) {
                    if (Constants.WANTS_SOUND)
                        SettingsActivity.soundButton.setBackground(Constants.CURRENT_CONTEXT.getResources().getDrawable(R.drawable.round_rectangle_button_active));
                    else
                        SettingsActivity.soundButton.setBackground(Constants.CURRENT_CONTEXT.getResources().getDrawable(R.drawable.round_rectangle_button));

                    if (Constants.WANTS_MUSIC)
                        SettingsActivity.musicButton.setBackground(Constants.CURRENT_CONTEXT.getResources().getDrawable(R.drawable.round_rectangle_button_active));
                    else
                        SettingsActivity.musicButton.setBackground(Constants.CURRENT_CONTEXT.getResources().getDrawable(R.drawable.round_rectangle_button));
                }
            }
        });
    }

    protected void onStop() {
        super.onStop();

    }

    public void playMusic(int track) {
        if(track == 0) {
            if(Constants.SOUNDTRACKS[track] == null) {
                Constants.SOUNDTRACKS[track] = MediaPlayer.create(Constants.CURRENT_CONTEXT, R.raw.menubackgroundmusic);
                Constants.SOUNDTRACKS[track].setLooping(true);
                Constants.SOUNDTRACKS[track].setVolume(2 * Constants.VOLUME, 2 *Constants.VOLUME);
                Constants.SOUNDTRACKS[track].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        stopMusic(0);
                    }
                });
            }
        } else if(track == 1) {
            if(Constants.SOUNDTRACKS[track] == null) {
                Constants.SOUNDTRACKS[track] = MediaPlayer.create(Constants.CURRENT_CONTEXT, R.raw.backgroundmusic);
                Constants.SOUNDTRACKS[track].setLooping(true);
                Constants.SOUNDTRACKS[track].setVolume(Constants.VOLUME, Constants.VOLUME);
                Constants.SOUNDTRACKS[track].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        stopMusic(1);
                    }
                });
            }
        } else if(track == 2) {
            if(Constants.SOUNDTRACKS[track] == null) {
                Constants.SOUNDTRACKS[track] = MediaPlayer.create(Constants.CURRENT_CONTEXT, R.raw.rocketsoundeffect);
                Constants.SOUNDTRACKS[track].setLooping(true);
                Constants.SOUNDTRACKS[track].setVolume(1, 1);
                Constants.SOUNDTRACKS[track].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        stopMusic(2);
                    }
                });
            }
        }
        if(!Constants.SOUNDTRACKS[track].isPlaying())
            Constants.SOUNDTRACKS[track].start();
    }

    public void stopMusic(int track) {
        if(Constants.SOUNDTRACKS[track] != null) {
            Constants.SOUNDTRACKS[track].release();
            Constants.SOUNDTRACKS[track] = null;
        }
    }

}
