package com.uni.uniplayer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.Log;
import com.uni.utils.*;
import java.util.ArrayList;

/**
 * Created by yfic on 2017/11/18.
 */

public class UNIElement
{
    private ArrayList<Bitmap> res = new ArrayList<>();

    private TimeTable timeTable = new TimeTable();

    public UNIElement clone()
    {
        UNIElement copy = new UNIElement();

        copy.res = res;
        copy.timeTable = timeTable.clone();
        copy.width = width;
        copy.height = height;

        return copy;
    }

    public boolean play()
    {
        return timeTable.play();
    }

    public void render(Canvas canvas, long deltaT)
    {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        timeTable.nextDuration(deltaT);

        while(timeTable.hasNextElement())
        {
            int elementId = timeTable.nextElement();
            Property state = timeTable.render(elementId);
            state.draw(canvas, res.get(elementId));
        }
    }

    //region 属性
    public int width = 0;

    public int height = 0;
    //endregion

    public void init()
    {
        res.clear();
        timeTable.clear();
    }

    public void addFrame(long interval, long duration)
    {
        timeTable.addFrame(interval, duration);
    }

    public void addElement(int id, Bitmap image, Property state)
    {
        if(id >= res.size())  res.add(image);

        timeTable.addElement(id, state);
    }

    public void setLoop(boolean isLoop)
    {
        timeTable.isLoop = isLoop;
    }
}
