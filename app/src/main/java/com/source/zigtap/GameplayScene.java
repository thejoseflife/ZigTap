package com.source.zigtap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.view.MotionEvent;

public class GameplayScene {

    private Rect r = new Rect();
    private RectF homeButton;
    private boolean homeButtonSelected = false;
    private boolean restartGameSelected = false;

    private Player player;
    private ObstacleManager obstacleManager;

    private boolean gameOver = false;
    private long gameOverTime;
    private int score = 0;
    private int oldHighScore;

    private Bitmap[] playerImages = new Bitmap[2];
    private Bitmap obstacleTextureTop, obstacleTextureBottom;
    private Bitmap background;
    private Rect backgroundRect, backgroundDuplicateRect;
    private int backgroundWidth;

    private SoundPool soundPool;
    private int explosionSound;

    public GameplayScene() {
        playerImages[0] = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), R.drawable.spacerocketrotatedup);
        playerImages[1] = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), R.drawable.spacerocketrotateddown);

        player = new Player(new Rect(100, Constants.SCREEN_HEIGHT/2 - 100, 300, Constants.SCREEN_HEIGHT/2 + 100), playerImages);

        obstacleTextureTop = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), R.drawable.obstacletop);
        obstacleTextureBottom = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), R.drawable.obstaclebottom);

        background = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), R.drawable.background3);

        backgroundWidth = background.getWidth() / background.getHeight() * Constants.SCREEN_WIDTH;

        backgroundRect = new Rect(0, 0,  backgroundWidth, Constants.SCREEN_HEIGHT);
        backgroundDuplicateRect = new Rect(backgroundWidth, 0,  backgroundWidth * 2, Constants.SCREEN_HEIGHT);

        obstacleManager = new ObstacleManager((int)(player.getRectangle().height() * 2.5), obstacleTextureTop, obstacleTextureBottom, this);

        homeButton = new RectF(((float)Constants.SCREEN_WIDTH/2 - 150), (float)Constants.SCREEN_HEIGHT/2 + 100, (float)Constants.SCREEN_WIDTH/2 + 150, (float)Constants.SCREEN_HEIGHT/2 + 400);

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(2)
                .setAudioAttributes(audioAttributes)
                .build();

        explosionSound = soundPool.load(Constants.CURRENT_CONTEXT, R.raw.explosion, 1);

        oldHighScore = Constants.HIGH_SCORE;

    }

    public void reset() {
        player = new Player(new Rect(100, Constants.SCREEN_HEIGHT/2 - 100, 300, Constants.SCREEN_HEIGHT/2 + 100), playerImages);

        backgroundRect.left = 0;
        backgroundRect.right = backgroundWidth;

        backgroundDuplicateRect.left = backgroundWidth;
        backgroundDuplicateRect.right = backgroundWidth * 2;

        obstacleManager = new ObstacleManager((int)(player.getRectangle().height() * 2.5), obstacleTextureTop, obstacleTextureBottom, this);

        updateHighScore();

        if(Constants.WANTS_MUSIC) {
            playMusic(1);
        }

        score = 0;

    }

    public void updateHighScore() {
        if(score > Constants.HIGH_SCORE) {
            SharedPreferences prefs = Constants.CURRENT_CONTEXT.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("key", score);
            editor.commit();
            Constants.HIGH_SCORE = score;
        }
    }

    public void receiveTouch(MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(!gameOver) {
                    if(player.getRectangle().bottom <= Constants.SCREEN_HEIGHT && !obstacleManager.playerCollision(player)) {
                        player.setDirection(-1);
                    }
                    player.clearTrail();
                    if(Constants.WANTS_SOUND)
                        playMusic(2);
                }
                if(gameOver && System.currentTimeMillis() - gameOverTime >= 1000 && !homeButton.contains(event.getX(), event.getY())) {
                    restartGameSelected = true;
                } else if(gameOver && homeButton.contains(event.getX(), event.getY())) {
                    homeButtonSelected = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if(!gameOver) {
                    pauseMusic(2);
                    if(player.getRectangle().top > 0 && !obstacleManager.playerCollision(player)) {
                        player.setDirection(1);
                    }
                } else if(gameOver && homeButton.contains(event.getX(), event.getY()) && homeButtonSelected) {
                    Constants.CURRENT_CONTEXT.startActivity(new Intent(Constants.CURRENT_CONTEXT, MainActivity.class));
                } else if(gameOver && !homeButton.contains(event.getX(), event.getY()) && restartGameSelected && !homeButtonSelected) {
                    reset();
                    gameOver = false;
                }

                if(homeButtonSelected)
                    homeButtonSelected = false;
                if(restartGameSelected)
                    restartGameSelected = false;
                break;

        }
    }

    public void shiftBackground(float speed) {
        backgroundRect.left -= speed;
        backgroundRect.right -= speed;
        backgroundDuplicateRect.left -= speed;
        backgroundDuplicateRect.right -= speed;

        if(backgroundRect.left <= -backgroundWidth) {
            backgroundRect.left = backgroundWidth;
            backgroundRect.right = backgroundWidth * 2;
        }

        if(backgroundDuplicateRect.left <= -backgroundWidth) {
            backgroundDuplicateRect.left = backgroundWidth;
            backgroundDuplicateRect.right = backgroundWidth * 2;
        }

    }

    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        canvas.drawColor(Color.WHITE);

        int crashedSizeSP = 35; //"You crashed!" and score
        int scoreSizeSP = 35;   //score
        int textSize1SP = 30;   //"You scored...
        int textSize2SP = 25;   //You high score is...
        int textSize3SP = 30;   //New high score of...
        int textSize4SP = 25;   //Your old high score was...
        int restartSizeSP = 20; //Tap anywhere to retry.

        float crashedSize = crashedSizeSP * Constants.CURRENT_CONTEXT.getResources().getDisplayMetrics().scaledDensity;
        float scoreSize = scoreSizeSP * Constants.CURRENT_CONTEXT.getResources().getDisplayMetrics().scaledDensity;
        float textSize1 = textSize1SP * Constants.CURRENT_CONTEXT.getResources().getDisplayMetrics().scaledDensity;
        float textSize2 = textSize2SP * Constants.CURRENT_CONTEXT.getResources().getDisplayMetrics().scaledDensity;
        float textSize3 = textSize3SP * Constants.CURRENT_CONTEXT.getResources().getDisplayMetrics().scaledDensity;
        float textSize4 = textSize4SP * Constants.CURRENT_CONTEXT.getResources().getDisplayMetrics().scaledDensity;
        float restartSize = restartSizeSP * Constants.CURRENT_CONTEXT.getResources().getDisplayMetrics().scaledDensity;

        Typeface scoreFont = Typeface.createFromAsset(Constants.CURRENT_CONTEXT.getAssets(), "fonts/font1.ttf");
        Typeface gameOverFont = Typeface.createFromAsset(Constants.CURRENT_CONTEXT.getAssets(), "fonts/font2.ttf");
        paint.setTypeface(gameOverFont);

        canvas.drawBitmap(background, null, backgroundRect, paint);
        canvas.drawBitmap(background, null, backgroundDuplicateRect, paint);

        obstacleManager.draw(canvas, paint);

        player.draw(canvas, paint);

        if(gameOver) {
            paint.setColor(Color.BLACK);
            canvas.drawRoundRect(new RectF(Constants.SCREEN_WIDTH/22, Constants.SCREEN_HEIGHT/7, Constants.SCREEN_WIDTH - Constants.SCREEN_WIDTH/22, Constants.SCREEN_HEIGHT - Constants.SCREEN_HEIGHT/7), 30, 30, paint);
            paint.setColor(Color.rgb(255, 180, 0));
            canvas.drawRoundRect(new RectF(Constants.SCREEN_WIDTH/11, Constants.SCREEN_HEIGHT/6, Constants.SCREEN_WIDTH - Constants.SCREEN_WIDTH/11, Constants.SCREEN_HEIGHT - Constants.SCREEN_HEIGHT/6), 30, 30, paint);
            paint.setColor(Color.BLACK);
            paint.setTextSize(crashedSize);
            drawCenterText(canvas, paint, "You Crashed!", 0, -Constants.SCREEN_HEIGHT/4 - Constants.SCREEN_HEIGHT/52);

            paint.setTypeface(scoreFont);
            if(score <= Constants.HIGH_SCORE) {
                paint.setTextSize(textSize1);
                drawCenterText(canvas, paint, "You scored " + score + ".", 0, -Constants.SCREEN_HEIGHT/6);
                paint.setTextSize(textSize2);
                drawCenterText(canvas, paint, "Your high score is " + Constants.HIGH_SCORE + ".", 0, -Constants.SCREEN_HEIGHT/12);
            } else {
                paint.setTextSize(textSize3);
                drawCenterText(canvas, paint, "New high score of " + score + "!", 0, -Constants.SCREEN_HEIGHT/6);
                paint.setTextSize(textSize4);
                drawCenterText(canvas, paint, "Your old high score was " + oldHighScore + ".", 0, -Constants.SCREEN_HEIGHT/12);
            }
            paint.setColor(Color.BLACK);
            paint.setTextSize(restartSize);
            drawCenterText(canvas, paint, "Tap anywhere to retry.", 0, -Constants.SCREEN_HEIGHT / 120);

            paint.setColor(Color.BLUE);
            canvas.drawRoundRect(homeButton, 60, 60, paint);
        } else {
            paint.setTypeface(scoreFont);
            paint.setTextSize(scoreSize);
            paint.setColor(Color.CYAN);
            drawCenterText(canvas, paint, score + "");
        }
    }

    public void update() {
        if(!gameOver) {

            if(player.getRectangle().top <= 0 || player.getRectangle().bottom > Constants.SCREEN_HEIGHT || obstacleManager.playerCollision(player)) {
                player.stop();
                gameOver();

            }

            player.update();

            if(player.getDirection() != 0) {
                obstacleManager.update();
                shiftBackground(4);
            }

        }

    }

    public void incrementScore(int amount) {
        score += amount;
    }

    public void gameOver() {
        gameOver = true;
        if(Constants.WANTS_SOUND)
            playExplosionSound();
        pauseMusic(1);
        pauseMusic(2);
        gameOverTime = System.currentTimeMillis();
        oldHighScore = Constants.HIGH_SCORE;
    }

    private void drawCenterText(Canvas canvas, Paint paint, String text, int offsetX, int offSetY) {
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.getClipBounds(r);
        int cHeight = r.height();
        int cWidth = r.width();
        paint.getTextBounds(text, 0, text.length(), r);
        float x = cWidth / 2f - r.width() / 2f - r.left;
        float y = cHeight / 2f + r.height() / 2f - r.bottom;
        canvas.drawText(text, x + offsetX, y + offSetY, paint);
    }

    private void drawCenterText(Canvas canvas, Paint paint, String text) {
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.getClipBounds(r);
        int cWidth = r.width();
        paint.getTextBounds(text, 0, text.length(), r);
        float x = cWidth / 2f - r.width() / 2f - r.left;
        float y = (float)Constants.SCREEN_HEIGHT/18;
        canvas.drawText(text, x, y, paint);
    }

    public void destroy() {
        soundPool.release();
        soundPool = null;
    }

    public void playExplosionSound() { //0 is rocket, 1 is explosion
        soundPool.play(explosionSound, 1, 1, 0, 0, 1);
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
        if(Constants.SOUNDTRACKS[track] != null && Constants.SOUNDTRACKS[track].isPlaying())
            Constants.SOUNDTRACKS[track].pause();
    }

    public void stopMusic(int track) {
        if(Constants.SOUNDTRACKS[track] != null) {
            Constants.SOUNDTRACKS[track].release();
            Constants.SOUNDTRACKS[track] = null;
        }
    }

}
