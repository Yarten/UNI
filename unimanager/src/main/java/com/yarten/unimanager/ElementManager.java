package com.yarten.unimanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.vincent.yamlparser.YAMLParser;
import com.uni.uniplayer.UNIElement;
import com.uni.utils.Brief;
import com.uni.utils.FileUtils;
import com.uni.utils.FrameProperty;
import com.uni.utils.GraphicsTools;
import com.uni.utils.Property;
import com.yarten.unicache.KeyFrame;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 元素管理：
 * 1.
 * Created by yfic on 2017/12/11.
 */

class ElementManager
{
    private class Path
    {
        public File dir;
        public String name;
    }

    private Context context;

    private Map<String, UNIElement> loaded = new HashMap<>();

    private Map<String, Brief> cache = new HashMap<>();

    ElementManager(Context context)
    {
        this.context = context;
    }

    public List<Brief> getElements()
    {
        String[] items =
                {
                        "cloud", "dialog", "dialog2", "dialog3", "dialog4", "low", "high", "people", "sun"
                };
        for(int i = 0; i < items.length; i++)
            items[i] = "System/" + items[i];

        List<Brief> menu = new LinkedList<>();
        for(int i = 0; i < items.length; i++)
        {
            Brief brief = getBrief(items[i]);
            menu.add(brief);
        }

        return menu;
    }

    public UNIElement getElement(String url)
    {
        if(loaded.containsKey(url))
            return loaded.get(url);

        Path path = get(url);
        if(path == null) return null;

        YAMLParser yaml = new YAMLParser(path.dir, path.name + ".yaml");
        if(!yaml.loadYAML()) return null;

        UNIElement element = new UNIElement();
        element.init();
        element.setLoop(true);

        while(yaml.hasNextFrame() != -1)
        {
            FrameProperty frameProperty = yaml.getFrame();
            element.addFrame(frameProperty.interval, frameProperty.duration);

            while(yaml.hasNextElement() != -1)
            {
                Property property = yaml.getElement();
                path = get(yaml.getElementUrl());
                Bitmap image = GraphicsTools.loadFromLocal(path.dir, path.name);
                element.addElement(property.ID, image, property);
            }
        }

        loaded.put(url, element);

        return element;
    }

    public Brief getBrief(String url)
    {
        if(cache.containsKey(url))
            return cache.get(url);

        Path path = get(url);
        if(path == null) return null;

        YAMLParser yaml = new YAMLParser(path.dir, path.name + ".yaml");
        if(!yaml.loadYAML()) return null;

        Brief brief = yaml.getBrief();
        brief.thumb = GraphicsTools.loadFromLocal(path.dir, "thumb");
        if(brief.thumb == null)
            Log.e("Element", "NULL");
        else Log.i("Element", "GOOD");
        cache.put(url, brief);

        return brief;
    }

    private Path get(String url)
    {
        String[] words = url.split("/");
        if(words.length != 2) return null;

        Path path = new Path();
        String source = words[0];
        String name = words[1];

        if(source.equals("System"))
        {
            // TODO: 从 /Files/DB 搜索是否存在该UNI元素，存在则加载
            path.dir = FileUtils.instance.getFileDir(name);
            path.name = name;
            return path;
        }
        else if(source.equals("Image"))
        {
            // 加载UNI元素局部路径文件夹的原子图像。
            path.name = "Images/" + name;
            return path;
        }
        else
        {
            // TODO: 从用户 /User/DB 搜索是否存在该元素，存在则加载元素
            if(true)
            {
                path.dir = context.getDir(source, Context.MODE_PRIVATE);
                path.name = name;
                return path;
            }
        }

        // TODO: 如果以上路径均未搜索不到，则向服务器请求数据，进行阻塞更新。
        // TODO: 数据将存放在cache中。
        path.dir = context.getCacheDir();
        path.name = name;
        return path;
    }
}
