package com.uni.uniplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.uni.utils.Playable;

/**
 * Created by yfic on 2017/11/19.
 */

public class UNIView extends SurfaceView implements SurfaceHolder.Callback, Playable
{
    //region 构造器
    private SurfaceHolder sfh;
    private RenderThread renderThread;

    public UNIView(Context context)
    {
        this(context, null);
    }

    public UNIView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public UNIView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        this(context, attrs, defStyleAttr, 0);
    }

    public UNIView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);

        sfh = this.getHolder();
        sfh.addCallback(this);
        sfh.setFormat(PixelFormat.TRANSLUCENT);
        super.setZOrderOnTop(true);
        renderThread = new RenderThread();
    }

    private UNIFrame uniFrame;

    synchronized public void setUNIFrame(UNIFrame uniFrame)
    {
        this.uniFrame = uniFrame;
    }

    public UNIFrame getUNIFrame(){return uniFrame;}
    //endregion

    //region 渲染线程管理
    private boolean isRunning = false;
    private boolean isFinishRunning = true;
    private boolean isEnding = true;
    private boolean isPlay = false;
    private long currentTime = 0;
    private long lastTime = 0;
    private long rateTime = 40;

    class RenderThread extends Thread
    {
        @Override
        public void run() {
            long sleepTime = 0;
            currentTime = getTime();
            lastTime = currentTime;
            isFinishRunning = false;
            isEnding = false;

            while(true)
            {
                synchronized (this)
                {
                    if(!isRunning || isEnding) break;
                    long deltaT = currentTime - lastTime;
                    lastTime = getTime();
                    if(isPlay) draw(deltaT);
                    currentTime = getTime();
                    sleepTime = rateTime - currentTime + lastTime;
                }
                Log.i("UNIView", String.format("%d", sleepTime));
                try{Thread.sleep(sleepTime);}
                catch (Exception e){Log.e("UNIView", e.toString());}

                currentTime = getTime();
            }

            synchronized (this){isFinishRunning = true;}
        }
    }

    synchronized public void setRate(int hz)
    {
        rateTime = (long)(1000.0 / hz);
    }

    public void play()
    {
        stop();
        uniFrame.play();
        currentTime = 0;
        lastTime = 0;
        isPlay = true;
        isRunning = true;

        while(true)
        {
            synchronized (this)
            {
                if(isFinishRunning) break;
            }

            try
            {
                Thread.sleep(1);
            } catch (Exception e){}
        }

        renderThread.start();
    }

    public boolean isPlaying(){return isPlay;}

    synchronized public void pause()
    {
        isPlay = false;
    }

    synchronized public void resume()
    {
        isPlay = true;
        lastTime = getTime();
    }

    public void stop()
    {
        synchronized(this)
        {
            if(!isRunning) return;
            isRunning = false;
            isPlay = false;
            renderThread.interrupt();
        }

        while(true)
        {
            synchronized (this)
            {
                if(isFinishRunning) break;
            }

            try{ Thread.sleep(1);}
            catch (Exception e){Log.e("UNIView", e.toString());}
        }

        setThumb();
    }
    //endregion

    //region 渲染模块
    private long getTime()
    {
        return System.currentTimeMillis();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setThumb();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stop();
    }

    private void draw(long deltaT)
    {
        Canvas canvas = sfh.lockCanvas();
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        isEnding = !uniFrame.render(canvas, deltaT);

        sfh.unlockCanvasAndPost(canvas);
    }

    private void setThumb()
    {
        Canvas canvas = sfh.lockCanvas();
        if(canvas == null) return;

        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        Matrix matrix = new Matrix();
        Bitmap thumb = uniFrame.getBrief().thumb;
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        int thumbWidth = thumb.getWidth();
        int thumbHeight = thumb.getHeight();

        matrix.postScale(1.0f * canvasWidth / thumb.getWidth(), 1.0f * canvasHeight / thumb.getHeight());
        Bitmap resized = Bitmap.createBitmap(thumb, 0, 0, thumbWidth, thumbHeight, matrix, false);

        final Paint paint = new Paint();
        canvas.drawBitmap(resized, 0, 0, paint);

        sfh.unlockCanvasAndPost(canvas);
    }

    //endregion
}
