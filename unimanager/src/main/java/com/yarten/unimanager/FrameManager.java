package com.yarten.unimanager;

import android.content.Context;

import com.example.vincent.yamlparser.YAMLParser;
import com.uni.utils.Brief;
import com.uni.utils.GraphicsTools;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by yfic on 2017/12/11.
 */

class FrameManager
{
    private Context context;

    FrameManager(Context context)
    {
        this.context = context;
    }

    public List<Brief> getFrames()
    {
        File root = context.getFilesDir();
        File[] dirs = root.listFiles();
        List<Brief> items = new LinkedList<>();

        for(File dir : dirs)
        {
            // UNI
            if(dir.isDirectory())
            {
                String name = dir.getName();
                Brief brief = getBrief(dir, name);
                items.add(brief);
            }
        }

        return items;
    }

    public Brief getBrief(File dir, String name)
    {
        YAMLParser yaml = new YAMLParser(dir, name + ".yaml");
        if(!yaml.loadYAML()) return null;

        Brief brief = yaml.getBrief();
        brief.thumb = GraphicsTools.loadFromLocal(dir, "thumb.png");

        return brief;
    }
}
