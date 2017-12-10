package com.uni.utils;

import android.os.Parcel;

import java.util.List;

/**
 * Created by D105-01 on 2017/12/9.
 */

public class FrameProperty {

    public int Id;

    public Double duration;

    public Double interval;

    public FrameProperty(int id, Double duration, Double interval) {
        Id = id;
        this.duration = duration;
        this.interval = interval;
    }
}
