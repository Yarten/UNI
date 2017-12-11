package com.uni.utils;

import android.content.Context;

import java.io.File;

/**
 * Created by yfic on 2017/12/11.
 */

public class FileUtils
{
    private Context context;

    public static FileUtils instance = null;

    public static FileUtils init(Context context)
    {
        if(instance == null)
        {
            instance = new FileUtils(context);
        }
        return instance;
    }


    public FileUtils(Context context)
    {
        this.context = context;
    }

    public File getUserDir(String dir)
    {
        File file = new File(context.getDir("User", Context.MODE_PRIVATE) + "/" + dir);
        return get(file);
    }

    public File getFileDir(String dir)
    {
        File file = new File(context.getFilesDir().getAbsolutePath() + "/" + dir);
        return get(file);
    }

    public File getCacheDir(String dir)
    {
        File file = new File(context.getCacheDir().getAbsolutePath() + "/" + dir);
        return get(file);
    }

    public File getDir(File root, String dir)
    {
        File file = new File(root.getAbsolutePath() + "/" + dir);
        return get(file);
    }

    public File get(File file)
    {
        if(!file.exists())
            if(!file.mkdirs())
                return null;
        return file;
    }
}
