package com.uni.uniplayer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
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

    public UNIFrame clone()
    {
        UNIFrame copy = new UNIFrame();
        copy.elements = elements;
        copy.res = res;
        copy.timeTable = timeTable;
        copy.brief = brief;
        return copy;
    }

    public boolean play()
    {
        for(UNIElement UNIElement : elements)
            UNIElement.play();
        return timeTable.play();
    }

    public boolean render(Canvas canvas, long deltaT) {
        boolean isPlaying = timeTable.nextDuration(deltaT);
        float localScale = Property.FrameWidth * GraphicsTools.dipScale() / canvas.getWidth();

        while(timeTable.hasNextElement())
        {
            Log.i("UNIFrame", String.format("%d", deltaT));
            int elementId = timeTable.nextElement();
            UNIElement uniElement = elements.get(elementId);
            Bitmap bitmap = res.get(elementId);

            Canvas subcanvas = new Canvas(bitmap);
            uniElement.render(subcanvas, deltaT);
            Log.i("UNIFrame", canvas.getHeight() + " " + canvas.getWidth());
            Property state = timeTable.render(elementId);
            state.draw(canvas, bitmap);
        }

        return isPlaying;
    }

    public void init()
    {
        elements = new ArrayList<>();
        res = new ArrayList<>();
        timeTable = new TimeTable();
        brief = new Brief();
    }

    public void setBrief(Brief brief)
    {
        this.brief = brief;
    }

    public Brief getBrief(){return brief;}

    public void addFrame(long interval, long duration)
    {
        timeTable.addFrame(interval, duration);
    }

    public void addElement(int id, UNIElement UNIElement, Property state)
    {
        if(id >= elements.size())
        {
            elements.add(UNIElement);
         //   Bitmap bitmap = Bitmap.createBitmap(UNIElement.width, UNIElement.height, Bitmap.Config.ARGB_8888);
            Bitmap bitmap = Bitmap.createBitmap(Property.ElementLength * GraphicsTools.dipScale(), Property.ElementLength * GraphicsTools.dipScale(), Bitmap.Config.ARGB_8888);
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
