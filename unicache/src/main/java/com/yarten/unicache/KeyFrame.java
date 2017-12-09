package com.yarten.unicache;

import android.graphics.Bitmap;
import android.util.SparseArray;

import com.uni.utils.Property;

import java.net.Inet4Address;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yfic on 2017/12/9.
 */

class KeyFrame
{
    class Element
    {
        Element()
        {
            this(null, null);
        }

        Element(Property property, Bitmap image)
        {
            this.image = image;
            this.property = property;
        }

        public Element clone()
        {
            Element r = new Element(property.clone(), image);
            return r;
        }


        Bitmap image;
        Property property;
    }

    private SparseArray<Element> elements = new SparseArray<>();

    public void add(int ID, Property property, Bitmap bitmap)
    {
        Element element = new Element(property, bitmap);
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

        return r;
    }
}
