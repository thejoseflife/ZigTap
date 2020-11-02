package com.source.zigtap;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Particle {

    private int size = 18;
    private int offSet = 10;
    private int x, y;
    private int color;

    public Particle(int x, int y, int random) {
        x = (int) (x - offSet + Math.random() * (offSet * 2));
        y = (int) (y - offSet + Math.random() * (offSet * 2));
        this.x = x;
        this.y = y;

        if (random == 2)
            color = Color.RED;
        else if (random == 1)
            color = Color.rgb(255, 255, 0);
        else
            color = Color.rgb(255, 180, 0);
    }

    public int getX() {
        return x;
    }

    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(color);

        canvas.drawCircle(x, y, size, paint);
    }

    public void update(int speed) {
        x -= speed;
    }
}
