package com.source.zigtap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Obstacle implements GameObject {

    private Rect rectangle;
    private Rect rectangle2;
    private int width = 100;
    private Bitmap textureTop, textureBottom;

    public Obstacle(int startX, int rectLength, int playerGap, Bitmap textureTop, Bitmap textureBottom, float scalarWidth, float scalarHeight) {
        rectangle = new Rect(startX, 0, startX , rectLength);
        rectangle2 = new Rect(startX, rectLength + playerGap, startX, Constants.SCREEN_HEIGHT);
        this.textureTop = textureTop;
        this.textureBottom = textureBottom;

        int scalar1 = (int)(scalarWidth * rectangle.height() / scalarHeight);
        int scalar2 = (int)(scalarWidth * rectangle2.height() / scalarHeight);
        int scalar;
        if(scalar1 > scalar2) {
            scalar = scalar1;
            rectangle2.bottom = rectangle2.top + rectangle.height();
        } else {
            scalar = scalar2;
            rectangle.top = rectangle.bottom - rectangle2.height();
        }
        rectangle.left = startX - scalar/2;
        rectangle.right = startX + width + scalar/2;
        rectangle2.left = startX - scalar/2;
        rectangle2.right = startX + width + scalar/2;

    }

    public Rect getRectangle() {
        return rectangle;
    }

    public Rect getRectangle2() {
        return rectangle2;
    }

    public void incrementX(float x) {
        rectangle.left -= x;
        rectangle.right -= x;
        rectangle2.left -= x;
        rectangle2.right -= x;
    }

    public boolean playerCollision(Player player) {
        for(Pixel p: player.getPixels()) {
            if(rectangle2.contains(player.getRectangle().left + p.getX(), player.getRectangle().top + p.getY())
                    || rectangle.contains(player.getRectangle().left + p.getX(), player.getRectangle().top + p.getY()))
                return true;
        }
        return false;
    }


    public void draw(Canvas canvas, Paint paint) {

        if(textureTop != null && textureBottom != null) {
            //canvas.drawBitmap(textureTop, null, rectangle, paint);
            //canvas.drawBitmap(textureBottom, null, rectangle2, paint);

            paint.setColor(Color.RED);
            canvas.drawRect(rectangle, paint);
            canvas.drawRect(rectangle2, paint);
        }
    }

    public void update() {

    }

}