package com.uni.uniplayer;

import java.util.ArrayList;
import com.uni.utils.*;

/**
 * Created by yarten on 2017/11/18.
 * 时间表
 */

public class TimeTable
{
    //region 数据维护

    // 记录了所有帧（解析过程中，帧ID只会递增）
    private ArrayList<Frame> frames = new ArrayList<>();
    // 记录了涉及到的所有元素（解析过程中，元素ID只会递增）
    private ArrayList<Element> elements = new ArrayList<>();

    // 维护了每一帧中出现了哪些元素
    private class Frame
    {
        // 从上一帧变换到当前帧的时间
        long interval = 0;
        // 该帧维持本帧状态的时间
        long duration = 0;
        // 该帧存在的所有元素集合
        ArrayList<Integer> elements = new ArrayList<>();

        public Frame clone()
        {
            Frame r = new Frame();
            r.interval = interval;
            r.duration = duration;
            r.elements.ensureCapacity(elements.size());
            for(int i = 0, size = elements.size(); i < size; i++)
                r.elements.add(elements.get(i));
            return r;
        }
    }

    // 维护了元素在哪些帧中出现过，且状态如何
    private class Element
    {
        // 该元素出现的第一帧位置
        int start = 0;
        // 元素存活的每一帧的状态记录
        ArrayList<Property> states = new ArrayList<>();
        // 指向某一帧的属性的游标
        int cursor = 0;

        public Element clone()
        {
            Element r = new Element();
            r.start = start;
            r.cursor = cursor;
            r.states.ensureCapacity(states.size());
            for(int i = 0, size = states.size(); i < size; i++)
                r.states.add(states.get(i).clone());
            return r;
        }
    }

    public void clear()
    {
        frames.clear();
        elements.clear();
        reset();
    }

    public TimeTable clone()
    {
        TimeTable copy = new TimeTable();

        int numElements = elements.size();
        copy.elements.ensureCapacity(numElements);
        for(int i = 0; i < numElements; i++)
        {
            copy.elements.add(elements.get(i).clone());
        }

        int numFrames = frames.size();
        copy.frames.ensureCapacity(numFrames);
        for(int i = 0; i < numFrames; i++)
        {
            copy.frames.add(frames.get(i).clone());
        }
        copy.isLoop = isLoop;

        return copy;
    }

    //endregion

    //region 游标控制
    private int frameIndex = 0;
    private int elementIndex = 0;
    private Frame currentFrame = null;
    private Frame lastFrame = null;

    public void reset()
    {
        frameIndex = 0;
        elementIndex = 0;
        currentFrame = null;
    }

    void nextFrame()
    {
        lastFrame = currentFrame;
        currentFrame = frames.get(frameIndex);
        elementIndex = 0;

        for(int i = 0, size = currentFrame.elements.size(); i < size; i++)
        {
            int elementId = currentFrame.elements.get(i);
            Element element = elements.get(elementId);
            if(element.start == frameIndex)
                element.cursor = 0;
            else element.cursor++;
        }

        frameIndex++;
    }

    boolean hasNextFrame()
    {
        return frameIndex < frames.size();
    }

    public int nextElement()
    {
        return currentFrame.elements.get(elementIndex++);
    }

    public boolean hasNextElement()
    {
        if(currentFrame != null)
        {
            return elementIndex < currentFrame.elements.size();
        }
        else return false;
    }

    /**
     * addFrame: 添加帧，调用它时，帧游标自动下移，
     * 后续调用addElement时，元素添加到新的帧中。
     */
    public void addFrame(long interval, long duration)
    {
        frames.add(new Frame());
        currentFrame = frames.get(frameIndex++);
        currentFrame.interval = interval;
        currentFrame.duration = duration;
    }

    /**
     * addElement: 添加元素到当前游标所指的帧，注意，
     * 该函数需要调用addFrame后才可以用。
     *
     * @param id 元素对象的编号（不等同于元素种类编号）
     * @param state 该元素在该帧的属性
     */
    public void addElement(int id, Property state)
    {
        currentFrame.elements.add(id);
        if(id < elements.size())
        {
            elements.get(id).states.add(state.clone());
        }
        else
        {
            Element newElement = new Element();
            newElement.start = frameIndex-1;
            newElement.states.add(state.clone());
            elements.add(newElement);
        }
    }
    //endregion

    //region 时间轴控制

    // 时间轴上的游标
    private long playtime = 0;
    // 当前帧与上一个关键帧的时间间隔
    private long deltatime = 0;
    // 当前帧间时间差
    private long segtime = 0;
    // 是否无限循环播放
    public boolean isLoop = false;

    /**
     * play: 启动时间轴
     * 首先会reset，然后连续读两帧，将第一帧设置为初始帧，
     * 并往第二帧变换。如果第一帧都没有，则无法播放。
     *
     * @return 返回是否播放成功
     */
    public boolean play()
    {
        reset();

        if(hasNextFrame())
        {
            nextFrame();
        }
        else return false;

        if(hasNextFrame())
        {
            nextFrame();
        }
        else lastFrame = currentFrame;

        playtime = 0;
        deltatime = 0;
        segtime = lastFrame.duration + currentFrame.interval;
        return true;
    }

    /**
     * nextDuration: 向前推动时间轴
     * 每两帧间，都会累计播放时间deltatime，如果该值超过两帧的间隔，
     * 则会自动移到下一帧。
     * 若已经没有下一帧，则根据是否有循环播放，选择是否调用play，即重播。
     * 该函数需要在每一个渲染周期中，在调用render前之前调用。
     *
     * @param deltaT 向前推动的时间间隔，为两帧间的间隔
     * @return 返回是否正在播放
     */
    public boolean nextDuration(long deltaT)
    {
        playtime += deltaT;
        deltatime += deltaT;
        elementIndex = 0;

        // 使用while循环是因为考虑到有可能过分卡顿，
        // 使间隔一下子跳过了若干帧。
        while (deltatime >= segtime)
        {
            deltatime -= segtime;

            if(hasNextFrame())
            {
                nextFrame();
                segtime = lastFrame.duration + currentFrame.interval;
                continue;
            }
            else
            {
                if(isLoop)
                {
                    play();
                    return true;
                }
                else
                {
                    deltatime = segtime;
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * render: 返回指定元素在当前时间戳下的属性
     *
     * @param elementId
     * @return
     */
    public Property render(int elementId)
    {
        Element element = elements.get(elementId);
        int cursor = element.cursor;
        Property next = element.states.get(cursor);
        Property last = element.states.get(cursor == 0 ? 0 : cursor-1);
        return Property.interpolation(last, next, deltatime-lastFrame.duration, segtime);
    }
    //endregion
}
