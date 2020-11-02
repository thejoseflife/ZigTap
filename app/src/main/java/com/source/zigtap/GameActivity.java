package com.source.zigtap;

import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class GameActivity extends AppCompatActivity {

    private GamePanel gamePanel;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gamePanel = new GamePanel(this);
        setContentView(gamePanel);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    protected void onDestroy() {
        super.onDestroy();
        gamePanel.gameplayScene.destroy();
    }

    protected void onStop() {
        super.onStop();

        gamePanel.gameplayScene.updateHighScore();

        stopMusic(1);
        stopMusic(2);
    }

    public void stopMusic(int track) {
        if(Constants.SOUNDTRACKS[track] != null) {
            Constants.SOUNDTRACKS[track].release();
            Constants.SOUNDTRACKS[track] = null;
        }
    }

}
