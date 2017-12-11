package com.yarten.unicache;

import android.graphics.Bitmap;
import android.util.SparseArray;

import com.uni.utils.FrameProperty;
import com.uni.utils.Property;

import java.net.Inet4Address;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yfic on 2017/12/9.
 */

public class KeyFrame
{
    public static class Element
    {
        Element()
        {
            this(null, null, null);
        }

        Element(Property property, Bitmap image, String url)
        {
            this.image = image;
            this.property = property;
            this.url = url;
        }

        public Element clone()
        {
            Element r = new Element(property.clone(), image, url);
            return r;
        }


        public Bitmap image;
        public Property property;
        public String url;
    }

    SparseArray<Element> elements = new SparseArray<>();

    FrameProperty frameProperty = null;

    public void add(int ID, Property property, Bitmap bitmap, String url)
    {
        Element element = new Element(property, bitmap, url);
        elements.put(ID, element);
    }

    public void delete(int ID)
    {
        elements.delete(ID);
    }

    public boolean contains(int ID)
    {
        return elements.indexOfKey(ID) >= 0;
    }

    public void update(int ID, Property property)
    {
        elements.get(ID).property = property.clone();
    }

    public KeyFrame clone()
    {
        KeyFrame r = new KeyFrame();

        for(int i = 0, size = elements.size(); i < size; i++)
        {
            int key = elements.keyAt(i);
            Element element = elements.valueAt(i).clone();
            r.elements.put(key, element);
        }

        r.frameProperty = frameProperty.clone();

        return r;
    }

    public SparseArray<Element> getElements(){return elements;}

    public FrameProperty getFrameProperty(){return frameProperty;}
}
