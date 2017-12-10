package com.uni.uniplayer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import com.uni.utils.*;
import java.util.ArrayList;

/**
 * Created by yfic on 2017/11/19.
 */

public class UNIFrame
{
    private ArrayList<UNIElement> elements = new ArrayList<>();

    private ArrayList<Bitmap> res = new ArrayList<>();

    private TimeTable timeTable = new TimeTable();

    private Brief brief;

    public boolean play()
    {
        for(UNIElement UNIElement : elements)
            UNIElement.play();
        return timeTable.play();
    }

    public boolean render(Canvas canvas, long deltaT) {
        Paint paint = new Paint();

        boolean isEnd = timeTable.nextDuration(deltaT);

        while(timeTable.hasNextElement())
        {
            Log.i("UNIFrame", String.format("%d", deltaT));
            int elementId = timeTable.nextElement();
            UNIElement UNIElement = elements.get(elementId);
            Bitmap bitmap = res.get(elementId);

            Canvas subcanvas = new Canvas(bitmap);
            UNIElement.render(subcanvas, deltaT);

            Property state = timeTable.render(elementId);
            state.setPaint(paint);
            canvas.drawBitmap(bitmap, state.x, state.y, paint);
        }

        return isEnd;
    }

    public void init()
    {
        elements.clear();
        res.clear();
        timeTable.clear();
    }

    public void setBrief(Brief brief)
    {
        this.brief = brief;
    }

    public void addFrame(long interval, long duration)
    {
        timeTable.addFrame(interval, duration);
    }

    public void addElement(int id, UNIElement UNIElement, Property state)
    {
        if(id >= elements.size())
        {
            elements.add(UNIElement);
            Bitmap bitmap = Bitmap.createBitmap(UNIElement.width, UNIElement.height, Bitmap.Config.ARGB_8888);
            res.add(bitmap);
        }

        timeTable.addElement(id, state);
    }

    public void setLoop(boolean isLoop)
    {
        timeTable.isLoop = isLoop;
    }

    public String toString()
    {
        return brief.title;
    }
}
