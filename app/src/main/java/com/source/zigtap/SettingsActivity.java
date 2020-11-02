package com.source.zigtap;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

public class SettingsActivity extends AppCompatActivity {

    private ImageButton homeButton;
    public static ImageButton musicButton;
    public static ImageButton soundButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Constants.CURRENT_CONTEXT = this;

        setContentView(R.layout.settings_scene);

        configureButtons();
    }

    private void configureButtons() {

        homeButton = (ImageButton) findViewById(R.id.home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, MainActivity.class));
            }
        });

        musicButton = (ImageButton) findViewById(R.id.music_button);
        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.WANTS_MUSIC = !Constants.WANTS_MUSIC;
                if(Constants.WANTS_MUSIC) {
                    playMusic(0);
                    musicButton.setBackground(Constants.CURRENT_CONTEXT.getResources().getDrawable(R.drawable.round_rectangle_button_active));
                } else {
                    pauseMusic(0);
                    musicButton.setBackground(Constants.CURRENT_CONTEXT.getResources().getDrawable(R.drawable.round_rectangle_button));
                }
            }
        });

        soundButton = (ImageButton) findViewById(R.id.sound_button);
        soundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.WANTS_SOUND = !Constants.WANTS_SOUND;
                if(Constants.WANTS_SOUND) {
                    soundButton.setBackground(Constants.CURRENT_CONTEXT.getResources().getDrawable(R.drawable.round_rectangle_button_active));
                } else
                    soundButton.setBackground(Constants.CURRENT_CONTEXT.getResources().getDrawable(R.drawable.round_rectangle_button));
            }
        });

        if (soundButton != null && musicButton != null) {
            if (Constants.WANTS_SOUND)
                soundButton.setBackground(Constants.CURRENT_CONTEXT.getResources().getDrawable(R.drawable.round_rectangle_button_active));
            else
                soundButton.setBackground(Constants.CURRENT_CONTEXT.getResources().getDrawable(R.drawable.round_rectangle_button));

            if (Constants.WANTS_MUSIC)
                musicButton.setBackground(Constants.CURRENT_CONTEXT.getResources().getDrawable(R.drawable.round_rectangle_button_active));
            else
                musicButton.setBackground(Constants.CURRENT_CONTEXT.getResources().getDrawable(R.drawable.round_rectangle_button));
        }

    }

    protected void onStop() {
        super.onStop();
    }

    public void playMusic(int track) {
        if(track == 0) {
            if(Constants.SOUNDTRACKS[track] == null) {
                Constants.SOUNDTRACKS[track] = MediaPlayer.create(Constants.CURRENT_CONTEXT, R.raw.menubackgroundmusic);
                Constants.SOUNDTRACKS[track].setLooping(true);
                Constants.SOUNDTRACKS[track].setVolume(2 * Constants.VOLUME, 2 * Constants.VOLUME);
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

    public void pauseMusic(int track) {
        if(Constants.SOUNDTRACKS[track] != null)
            Constants.SOUNDTRACKS[track].pause();
    }

    public void stopMusic(int track) {
        if(Constants.SOUNDTRACKS[track] != null) {
            Constants.SOUNDTRACKS[track].release();
            Constants.SOUNDTRACKS[track] = null;
        }
    }
}
