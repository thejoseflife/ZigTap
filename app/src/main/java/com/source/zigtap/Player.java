package com.source.zigtap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;

public class Player implements GameObject {

    private Rect rectangle;
    private int direction = 0;
    private int speed = 50;
    private int trailSpeed = 20;
    private Bitmap[] images;
    private ArrayList<Particle> trail = new ArrayList<>();
    private Pixel[] coloredPixelsUp = new Pixel[7];
    private Pixel[] coloredPixelsDown = new Pixel[7];

    public Rect getRectangle() {
        return rectangle;
    }

    public Player(Rect rectangle, Bitmap[] images) { //images[1] = facingDown, images[0] = facingUp
        this.rectangle = rectangle;
        this.images = images;

        coloredPixelsUp[0] = new Pixel(198, 1);
        coloredPixelsUp[1] = new Pixel(155, 14);
        coloredPixelsUp[2] = new Pixel(119, 46);
        coloredPixelsUp[3] = new Pixel(86, 79);
        coloredPixelsUp[4] = new Pixel(120, 119);
        coloredPixelsUp[5] = new Pixel(154, 80);
        coloredPixelsUp[6] = new Pixel(185, 44);

        coloredPixelsDown[0] = new Pixel(199, 198);
        coloredPixelsDown[1] = new Pixel(185, 155);
        coloredPixelsDown[2] = new Pixel(155, 185);
        coloredPixelsDown[3] = new Pixel(152, 119);
        coloredPixelsDown[4] = new Pixel(117, 151);
        coloredPixelsDown[5] = new Pixel(120, 88);
        coloredPixelsDown[6] = new Pixel(86, 121);

    }

    public Pixel[] getPixels() {
        if(direction > 0)
            return coloredPixelsDown;
        else
            return coloredPixelsUp;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getDirection() {
        return direction;
    }

    public Bitmap getImage() {
        if(direction == 1)
            return images[1];
        else
            return images[0];
    }

    public void clearTrail() {
        trail.clear();
    }

    public void stop() {
        speed = 0;
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        if(direction == 1)
            canvas.drawBitmap(images[1], null, rectangle, paint);
        else
            canvas.drawBitmap(images[0], null, rectangle, paint);


        for(int i = 0; i < trail.size(); i++) {
            if(direction < 0)
                trail.get(i).draw(canvas);
        }

        /*paint.setColor(Color.YELLOW);
        if(direction > 0) {
            for(Pixel p: coloredPixelsDown) {
                canvas.drawRect(new Rect(rectangle.left + p.getX() - 5, rectangle.top + p.getY() - 5, rectangle.left + p.getX(), rectangle.top + p.getY()), paint);
            }
        } else {
            for(Pixel p: coloredPixelsUp) {
                canvas.drawRect(new Rect(rectangle.left + p.getX() - 5, rectangle.top + p.getY() - 5, rectangle.left + p.getX(), rectangle.top + p.getY()), paint);
            }
        }*/

    }

    public void addParticles() {
        trail.add(new Particle(rectangle.left, rectangle.bottom, (int)(Math.random() * 3)));
    }

    public void update() {
        if(direction < 0) {
            addParticles();
        }


        for(int i = 0; i < trail.size(); i++) {
            trail.get(i).update(trailSpeed);
            if(trail.get(i).getX() < -20)
                trail.remove(i);
        }

        rectangle.top += speed * direction;
        rectangle.bottom += speed * direction;

    }
}