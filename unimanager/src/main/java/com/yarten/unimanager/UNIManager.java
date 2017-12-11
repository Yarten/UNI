package com.yarten.unimanager;

import android.content.Context;
import android.graphics.Bitmap;

import com.example.vincent.yamlparser.YAMLParser;
import com.uni.uniplayer.UNIElement;
import com.uni.uniplayer.UNIFrame;
import com.uni.utils.Brief;
import com.uni.utils.FrameProperty;
import com.uni.utils.GraphicsTools;
import com.uni.utils.Property;
import com.yarten.unicache.UNICache;

import java.io.File;
import java.security.Policy;

/**
 * Created by yfic on 2017/12/9.
 */

public class UNIManager
{
    public static UNIManager instance = new UNIManager();

    private UNIManager(){}

    private ElementManager elementManager = null;

    private UNICache cache = new UNICache();

    private UNIFrame uniFrame = new UNIFrame();

    private Context context;

    public void init(Context context)
    {
        this.context = context;
        elementManager = new ElementManager(context);
    }

    public boolean loadFromCache(String yaml, boolean editMode)
    {
        return loadYaml(context.getCacheDir(), yaml, editMode);
    }

    public boolean loadYaml(File dir, String yaml, boolean editMode)
    {
        YAMLParser parser = new YAMLParser(dir, yaml);
        if(!parser.loadYAML()) return false;

        if(editMode)
        {
            Property.setIDCursor(parser.getMaxElementId()+1);
            cache.clear();

            while(parser.hasNextFrame() != -1)
            {
                FrameProperty frameProperty = parser.getFrame();
                cache.addFrame(frameProperty.Id, frameProperty);

                while (parser.hasNextElement() != -1)
                {
                    Property property = parser.getElement();
                    String url = parser.getElementUrl();
                    Brief brief = elementManager.getBrief(url);
                    cache.addElement(frameProperty.Id, property.ID, property, brief.thumb, brief.url);
                }
            }
        }
        else
        {
            uniFrame.init();
            uniFrame.setLoop(false);

            while(parser.hasNextFrame() != -1)
            {
                FrameProperty frameProperty = parser.getFrame();
                uniFrame.addFrame(frameProperty.interval, frameProperty.duration);

                while(parser.hasNextElement() != -1)
                {
                    Property property = parser.getElement();
                    String url = parser.getElementUrl();
                    UNIElement element = elementManager.getElement(url);
                    uniFrame.addElement(property.ID, element, property);
                }
            }
        }

        return true;
    }
}
