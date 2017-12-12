package com.yarten.unimanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;

import com.example.vincent.yamlparser.YAMLParser;
import com.uni.uniplayer.UNIElement;
import com.uni.uniplayer.UNIFrame;
import com.uni.utils.Brief;
import com.uni.utils.CAN;
import com.uni.utils.FileUtils;
import com.uni.utils.FrameProperty;
import com.uni.utils.GraphicsTools;
import com.uni.utils.Property;
import com.yarten.unicache.KeyFrame;
import com.yarten.unicache.UNICache;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.security.Policy;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by yfic on 2017/12/9.
 */

public class UNIManager
{
    public static UNIManager instance = new UNIManager();

    private UNIManager(){}

    private ElementManager elementManager = null;

    private FrameManager frameManager = null;

    private UNICache cache = new UNICache();

    private UNIFrame uniFrame = new UNIFrame();

    private FileUtils fileUtils = null;

    private Context context;

    private boolean hasInited = false;

    public void init(Context context)
    {
        if(hasInited) return;

        this.context = context;
        elementManager = new ElementManager(context);
        frameManager = new FrameManager(context);
        fileUtils = FileUtils.init(context);
        hasInited = true;

        CAN.login(this);
    }

    @Override
    protected void finalize() throws Throwable {
        CAN.logout(this);
        super.finalize();
    }

    public UNIFrame getUNIFrame(File dir, String name)
    {
        if(!loadYaml(dir, name + ".yaml", false))
            return null;
        return uniFrame;
    }


    public boolean loadYaml(File dir, String yaml, boolean editMode)
    {
        YAMLParser parser = new YAMLParser(dir, yaml);
        if(!parser.loadYAML()) return false;
        Brief yamlBrief = parser.getBrief();

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
            uniFrame.setBrief(yamlBrief);

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

    public void saveUNIFrame(File root, Brief brief)
    {
        File dir = fileUtils.getDir(root, brief.title);
        YAMLParser yaml = new YAMLParser(dir, brief.title + ".yaml");
        yaml.setBrief(brief.author, brief.description, brief.date, brief.url, brief.title);

        List<KeyFrame> keyFrames = cache.getFrames();
        for(KeyFrame keyFrame : keyFrames)
        {
            FrameProperty frameProperty = keyFrame.getFrameProperty();
            yaml.addFrame(frameProperty.duration, frameProperty.interval);

            SparseArray<KeyFrame.Element> elements = keyFrame.getElements();

            for(int i = 0, size = elements.size(); i < size; i++)
            {
                KeyFrame.Element element = elements.valueAt(i);
                yaml.addElement(element.property, element.url);
            }
        }

        yaml.saveYAML();
        GraphicsTools.saveToLocal(dir, "thumb", brief.thumb);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void updateMenu(CAN.Package.Menu.Request pkg)
    {
        switch (pkg.what)
        {
            case EditorMenu:
                updateElementMenu(pkg.what);
                break;
            case MainMenu:
                updateMainMenu();
                break;
        }

    }

    public void updateMainMenu()
    {
        List<Brief> items = frameManager.getFrames();
        CAN.Control.updateMainMenu(items);
    }

    public void updateElementMenu(CAN.Package.Menu.Type from)
    {
        List<Brief> menu = elementManager.getElements();

        switch (from)
        {
            case EditorMenu:
                CAN.DataBus.updateMenu(menu);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void commit(CAN.Package.EditorCommit pkg)
    {
        // TODO: 草稿箱
        switch (pkg.state)
        {
            case Drop: return;
            case Save:
                // TODO: 根据用户名字存放，并最后上交给服务器，现在全放在cache
                saveUNIFrame(context.getCacheDir(), pkg.brief);
                // TODO: 临时的处理：刷新主界面
                updateMainMenu();
                break;
        }
    }
}
