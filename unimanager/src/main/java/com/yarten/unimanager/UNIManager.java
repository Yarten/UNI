package com.yarten.unimanager;

import android.content.Context;
import android.graphics.Bitmap;

import com.example.vincent.yamlparser.YAMLParser;
import com.uni.uniplayer.UNIFrame;
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

    private UNICache cache = new UNICache();

    private UNIFrame uniFrame = new UNIFrame();

    private Context context;

    private File cacheDir;

    public void init(Context context)
    {
        this.context = context;
        cacheDir = context.getCacheDir();
    }

    @Deprecated
    public boolean loadFromCache(String yaml, boolean editMode)
    {
        return loadYaml(cacheDir, yaml, editMode);
    }

    @Deprecated
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
                cache.addFrame(frameProperty.Id);

                while (parser.hasNextElement() != -1)
                {
                    Property property = parser.getElement();
                    // TODO: 获得URL，根据URL获得元素的缩略图
                }
            }
        }
        else
        {
            // TODO: 播放模式初始化
        }

        return true;
    }

    void demo(File dir, boolean editMode)
    {
        if(editMode)
        {
            cache.clear();
            cache.addFrame(0);


        }
        else
        {

        }
    }
}
