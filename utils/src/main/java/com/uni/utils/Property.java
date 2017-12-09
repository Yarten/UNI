package com.uni.utils;

import android.graphics.Paint;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

/**
 * Created by Yarten on 2017/11/18.
 * 属性的集合，都是公开的成员
 */
public class Property
{
    /**
     * 插值模式
     */
    public enum Mode
    {
        Linear, FadeInOut, FadeIn, FadeOut, Bounce
    }
    static final int TEMP_ID = -1;
    public final int Id;
    public int x;
    public int y;
    public int height;
    public int width;
    public float opacity;
    public Mode mode;

    public Property(int id) {
        this(id, 0, 0, 0, 0,0, Mode.Linear);
    }

    public Property(int id, int x, int y, int height, int width, int opacity, Mode mode) {
        Id = id;
        this.x = x;
        this.y = y;
        this.opacity = opacity;
        this.mode = mode;
    }

    /**
     * 根据相关属性，为传入的画笔设置样式
     * @param paint
     */
    public void setPaint(Paint paint)
    {
        paint.setAlpha((int)(opacity * 255));
    }

    /**
     * 注意，属性的赋值多使用深拷贝
     */
    public Property clone()
    {
        Property r = new Property(this.Id);
        r.x = x;
        r.y = y;
        r.opacity = opacity;
        r.mode = mode;
        return r;
    }

    /**
     * 根据前后两个属性，以及当前时间和总时间，返回插值。
     * 注意到，当播放时间小于0时，直接返回前一个属性；
     * 当播放时间大于总时间时，直接返回后一个属性。
     * @param last 上一个属性
     * @param next 下一个属性
     * @param t 当前播放的时间
     * @param duration 总的播放时间
     * @return 插值的属性
     */
    public static Property interpolation(Property last, Property next, long t, long duration)
    {
        if(t < 0) return last;
        if(t > duration) return next;

        Interpolator interpolator = pickInterpolator(next.mode);
        float alpha = interpolator.getInterpolation(t * 1.0f / duration);

        Property mid = new Property(TEMP_ID);
        mid.x = (int)(last.x + (next.x-last.x) * alpha);
        mid.y = (int)(last.y + (next.y-last.y) * alpha);
        mid.opacity = last.opacity + (next.opacity-last.opacity) * alpha;

        return mid;
    }

    private static Interpolator pickInterpolator(Mode mode)
    {
        switch (mode)
        {
            case Linear: return linear;
            case FadeIn: return fadeIn;
            case FadeOut: return fadeOut;
            case FadeInOut: return fadeInOut;
            case Bounce: return bounce;
            default: return linear;
        }
    }

    private static Interpolator linear = new LinearInterpolator();

    private static Interpolator fadeInOut = new AccelerateDecelerateInterpolator();

    private static Interpolator fadeIn = new AccelerateInterpolator();

    private static Interpolator fadeOut = new DecelerateInterpolator();

    private static Interpolator bounce = new BounceInterpolator();
}