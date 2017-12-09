package com.yarten.unicache;

import android.graphics.Bitmap;

import com.uni.utils.CAN;
import com.uni.utils.Property;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * UNI创建过程中的缓存
 * Created by yfic on 2017/12/9.
 */

public class UNICache
{
    private List<KeyFrame> frames = new ArrayList<>();

    public void addFrame(int ID)
    {
        KeyFrame keyFrame;

        if(ID != 0)
            keyFrame = frames.get(ID-1).clone();
        else keyFrame = new KeyFrame();

        frames.add(ID, keyFrame);
    }

    public void deleteFrame(int ID)
    {
        frames.remove(ID);
    }

    public void addElement(int where, int ID, Property property, Bitmap image)
    {
        for(int i = where, size = frames.size(); i < size; i++)
            frames.get(i).add(ID, property, image);
    }

    public void deleteElement(int where, int ID)
    {
        for(int i = where, size = frames.size(); i < size; i++)
        {
            KeyFrame keyFrame = frames.get(i);
            if(keyFrame.contains(ID))
                keyFrame.delete(ID);
            else break;
        }
    }

    public void updateElement(int where, int ID, Property property)
    {
        KeyFrame keyFrame = frames.get(where);
        keyFrame.update(ID, property);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void UpdateElement(CAN.Package.ElementRequire pkg)
    {
        switch (pkg.what)
        {
            case Update:
                updateElement(pkg.where, pkg.which, pkg.how);
                break;
            case Add:
                addElement(pkg.where, pkg.which, pkg.how, pkg.image);
                break;
            case Delete:
                deleteElement(pkg.where, pkg.which);
                break;
        }
    }
}
