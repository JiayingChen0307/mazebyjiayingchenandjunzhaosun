package edu.wm.cs.cs301.JunzhaoSun.gui;

import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Path;

import androidx.annotation.Nullable;

public class MazePanel extends View {

    Canvas canvas;
    Bitmap bitmap;
    Paint paint;

    private BitmapShader skyShader;
    private BitmapShader wallShader;
    private BitmapShader groundShader;

    public MazePanel(Context context) {
        super(context);
        init(null,0);
    }

    public MazePanel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs,0);

        skyShader = new BitmapShader(BitmapFactory.decodeResource(context.getResources(), R.drawable.sky), Shader.TileMode.CLAMP,Shader.TileMode.REPEAT);
        wallShader = new BitmapShader(BitmapFactory.decodeResource(context.getResources(), R.drawable.wallcolor), Shader.TileMode.CLAMP,Shader.TileMode.CLAMP);
        groundShader = new BitmapShader(BitmapFactory.decodeResource(context.getResources(), R.drawable.road), Shader.TileMode.CLAMP,Shader.TileMode.REPEAT);
    }

    public MazePanel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs,defStyleAttr);
    }

    public void init(AttributeSet attrs, int defStyleAttr){
        paint = new Paint();
        paint.setColor(Color.GRAY);
        bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    }

    /**
     * Method to draw the buffer image on a graphics object that is
     * obtained from the superclass.
     * Warning: do not override getGraphics() or drawing might fail.
     */
    public void update() {
        invalidate();
    }

    /**
     * Obtains a graphics object that can be used for drawing.
     * This MazePanel object internally stores the graphics object
     * and will return the same graphics object over multiple method calls.
     * The graphics object acts like a notepad where all clients draw
     * on to store their contribution to the overall image that is to be
     * delivered later.
     * To make the drawing visible on screen, one needs to trigger
     * a call of the paint method, which happens
     * when calling the update method.
     * @return graphics object to draw on, null if impossible to obtain image
     */
    public Canvas getBufferGraphics() {
        if (null==canvas){
            if (null==bitmap){
                bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
            }
            this.canvas = new Canvas(bitmap);
        }
        return canvas;
    }

    public void setColor(int x, int y, int z) {
        paint.setColor(Color.rgb(x,y,z));
    }

    public void fillRect(int x, int y, int w, int h) {
        canvas.drawRect(x, y, x+w, y+ h, paint);
    }

    public void fillPolygon(int[] xps, int[] yps, int x) {
        Path poly = new Path();
        poly.moveTo(xps[0],yps[0]);
        for (int i = 1; i <x; i++){
            poly.lineTo(xps[i],yps[i]);
        }
        poly.lineTo(xps[0],yps[0]);
        poly.close();
        canvas.drawPath(poly,paint);
    }

    public void fillOval(int x, int y, int w, int h) {
        canvas.drawOval(x, y, x+w, y-h, paint);
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        canvas.drawLine(x1, y1, x2, y2, paint);
    }

    public Color getWallColor(int r, int g, int b) {
        Color col = Color.valueOf(Color.rgb(r,g,b));
        return col;
    }

    public Color getWallColor(int rgb) {
        Color col = Color.valueOf(rgb);
        return col;
    }

    /**
     * this method is for testing purpose in Challenge1 only
     */
    /*private void testDraw(){
        canvas.drawColor(Color.WHITE);
        setColor(255,0,0);//red
        fillOval(0,100,100,100);
        setColor(0,255,0);//green
        fillOval(100,100,100,100);
        setColor(255,255,0);//yellow
        fillRect(0,200,100,100);
        drawLine(300,300,400,400);
        setColor(0,0,255);//blue
        int[] xps = new int[]{200,210,220};
        int[] yps = new int[]{200,250,300};
        fillPolygon(xps,yps,3);
    }*/

    @Override
    public void onDraw(Canvas canvas){
        if (null == canvas) {
            System.out.println("MazePanel.paint: no graphics object, skipping drawImage operation");
        }else {
            super.onDraw(canvas);
            //testDraw();//this line is for testing purpose only
            Rect src = new Rect(0, 0, bitmap.getWidth() - 1, bitmap.getHeight() - 1);
            Rect dest = new Rect(0, 0, getWidth() - 1, getHeight() - 1);
            canvas.drawBitmap(bitmap, src, dest, paint);
        }
    }

    public void removeShader(){paint.setShader(null);}

    public void setSkyShader(){paint.setShader(skyShader);}
    public void setWallShader(){paint.setShader(wallShader);}
    public void setGroundShader(){paint.setShader(groundShader);}
}
