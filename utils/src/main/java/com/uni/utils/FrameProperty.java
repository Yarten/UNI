package com.uni.utils;

import android.os.Parcel;

import java.util.List;

/**
 * Created by D105-01 on 2017/12/9.
 */

public class FrameProperty {

    public int Id;

    public long duration;

    public long interval;

    public boolean hasNext = true;

    public FrameProperty(int id)
    {
        this(id, 1000, 0);
    }

    public FrameProperty(int id, long duration, long interval)
    {
        this(id, duration, interval, true);
    }

    public FrameProperty(int id, long duration, long interval, boolean hasNext)
    {
        Id = id;
        this.duration = duration;
        this.interval = interval;
        this.hasNext = hasNext;
    }

    public FrameProperty clone()
    {
        return new FrameProperty(Id, duration, interval, hasNext);
    }
}
