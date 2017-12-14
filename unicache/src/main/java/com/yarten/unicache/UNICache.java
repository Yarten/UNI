package com.yarten.unicache;

import android.graphics.Bitmap;
import android.util.SparseArray;
import com.uni.utils.CAN;
import com.uni.utils.FrameProperty;
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

    public UNICache()
    {
        CAN.login(this);
    }

    @Override
    protected void finalize() throws Throwable {
        CAN.logout(this);
    }

    public List<KeyFrame> getFrames(){return frames;}

    public void clear()
    {
        frames.clear();
    }

    public void updateFrame(int ID, FrameProperty property)
    {
        frames.get(ID).frameProperty = property.clone();
    }

    public void addFrame(int ID, FrameProperty property)
    {
        KeyFrame keyFrame;

        if(ID != 0)
            keyFrame = frames.get(ID-1).clone();
        else keyFrame = new KeyFrame();

        keyFrame.frameProperty = property;
        int size = frames.size();
        if(ID == size)
        {
            if(size != 0) frames.get(size-1).frameProperty.hasNext = true;
            keyFrame.frameProperty.hasNext = false;
        }

        frames.add(ID, keyFrame);
    }

    public void deleteFrame(int ID)
    {
        int size = frames.size();
        if(ID == size-1 && ID != 0)
        {
            frames.get(ID-1).frameProperty.hasNext = false;
        }

        frames.remove(ID);

        if(ID == 0)
            addFrame(0, new FrameProperty(0));
    }

    public void getFrame(int ID)
    {
        if(frames.size() == 0)
        {
            addFrame(ID, new FrameProperty(0));
        }

        KeyFrame keyFrame = frames.get(ID);

        int size = keyFrame.elements.size();
        SparseArray<Property> props = new SparseArray<>(size);
        SparseArray<Bitmap> images = new SparseArray<>(size);

        for(int i = 0; i < size; i++)
        {
            int id = keyFrame.elements.keyAt(i);
            KeyFrame.Element element = keyFrame.elements.valueAt(i);
            props.put(id, element.property);
            images.put(id, element.image);
        }

        CAN.DataBus.updateEditor(props, images, keyFrame.frameProperty);
    }

    public void addElement(int where, int ID, Property property, Bitmap image, String url)
    {
        for(int i = where, size = frames.size(); i < size; i++)
            frames.get(i).add(ID, property, image, url);
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

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void UpdateElement(CAN.Package.ElementRequest pkg)
    {
        switch (pkg.what)
        {
            case Update:
                updateElement(pkg.where, pkg.which, pkg.how);
                break;
            case Add:
                addElement(pkg.where, pkg.which, pkg.how, pkg.image, pkg.url);
                break;
            case Delete:
                deleteElement(pkg.where, pkg.which);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void UpdateFrame(CAN.Package.FrameRequest pkg)
    {
        switch (pkg.what)
        {
            case Update:
                updateFrame(pkg.which, pkg.how);
                break;
            case Add:
                addFrame(pkg.which, new FrameProperty(pkg.which));
                break;
            case Delete:
                deleteFrame(pkg.which);
                if(pkg.which == frames.size() && pkg.which != 0)
                    pkg.which--;
                break;
        }

        getFrame(pkg.which);
    }
}
