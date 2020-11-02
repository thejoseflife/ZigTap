package com.source.zigtap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;

public class ObstacleManager {
    private ArrayList<Obstacle> obstacles;
    private int playerGap;
    private Bitmap textureTop, textureBottom;

    private float speed = 25;
    private float scalarWidth;
    private float scalarHeight;
    private int obstacleGap = 100;

    private GameplayScene gameplayScene;

    public ObstacleManager(int playerGap, Bitmap textureTop, Bitmap textureBottom, GameplayScene gameplayScene) {
        this.playerGap = playerGap;

        obstacles = new ArrayList<Obstacle>();

        this.textureTop = textureTop;
        this.textureBottom = textureBottom;
        this.gameplayScene = gameplayScene;

        populateObstacles();

    }

    public boolean playerCollision(Player player) {
        for(Obstacle o: obstacles) {
            if(o.playerCollision(player))
                return true;
        }
        return false;
    }

    private void populateObstacles() {
        obstacles.add(new Obstacle(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT/2 - playerGap/2, playerGap, textureTop, textureBottom, scalarWidth, scalarHeight));
        scalarWidth = obstacles.get(0).getRectangle().width();
        scalarHeight = obstacles.get(0).getRectangle().height();
    }

    public void draw(Canvas canvas, Paint paint) {
        for(Obstacle o: obstacles) {
            o.draw(canvas, paint);
        }
    }

    public void update() {

        for(Obstacle o: obstacles) {
            o.incrementX(speed);
        }


        if(obstacles.get(obstacles.size() - 1).getRectangle().right <= -obstacleGap) {
            int randomLength = (int)(Math.random() * (Constants.SCREEN_HEIGHT - playerGap));
            obstacles.add(0, new Obstacle(Constants.SCREEN_WIDTH + 50, randomLength, playerGap, textureTop, textureBottom, scalarWidth, scalarHeight));
            obstacles.remove(obstacles.size() - 1);
            gameplayScene.incrementScore(1);

            if(playerGap > 400)
                playerGap--;

            if(obstacleGap < 200)
                obstacleGap++;

            if(speed < 40)
                speed++;
        }

    }

}
