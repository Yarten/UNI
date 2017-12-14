package com.example.vincent.yamlparser;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.RenderScript;
import android.support.annotation.NonNull;
import android.util.Log;

import com.uni.utils.Brief;
import com.uni.utils.FrameProperty;
import com.uni.utils.Property;
import org.yaml.snakeyaml.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.Handler;

import static android.content.ContentValues.TAG;

/**
 * Created by D105-01 on 2017/12/9.
 */

public class YAMLParser {

    private static final String TAG = "YAMLParser";

    private class MProperty{
        public MProperty(Property property, String url){
            this.property = property;
            this.url = url;
        }
        public Property property;
        public String url;
    }

    private File mFilePath;
    private String mFileName;
    private File mFile;

    private ArrayList<FrameProperty> mFrames = new ArrayList<>();
    private int mFrameIndex;

    private List<ArrayList<MProperty>> mElements = new ArrayList<ArrayList<MProperty>>();
    private ArrayList<MProperty> curElementList;
    private int mCurElementIndex;

    private int mMaxElementId;

    private Brief brief = new Brief();

    /**
     * 初始化函数
     * @param filePath
     * @param fileName
     */
    public YAMLParser(File filePath, String fileName){
        mFilePath = filePath;
        mFileName = fileName;
    }

    public void addFrame (long duration, long interval){

        mFrames.add(new FrameProperty(mFrames.size()+1, duration, interval));

        ArrayList<MProperty> elementList = new ArrayList<>();
        mElements.add(elementList);
        curElementList = elementList;
    }


    /**
     * 默认在当前帧增加Element
     * @param property
     */
    public void addElement(Property property, String url){
        if(property.ID > mMaxElementId) mMaxElementId =  property.ID;
        curElementList.add(new MProperty(property, url));
    }

    /**
     * 返回当前帧的属性
     * @return
     */
    public FrameProperty getFrame(){
        return mFrames.get(mFrameIndex);
    }

    /**
     * 返回当前帧的当前元素的属性
     * @return
     */
    public Property getElement(){
        return (curElementList.get(mCurElementIndex)).property;
    }

    /**
     * 若有下一帧，则返回下一帧的index
     * 否则返回-1
     * @return
     */
    public int hasNextFrame(){
        mFrameIndex++;
        if(mFrameIndex < mFrames.size()){
            mCurElementIndex = -1;
            curElementList = mElements.get(mFrameIndex);
            return mFrameIndex;
        }
        else{
            return -1;
        }
    }

    /**
     * 若有下一个元素，则返回下一个元素的index
     * 否则返回-1
     * @return
     */
   public int hasNextElement(){
        mCurElementIndex++;
        if(mCurElementIndex < curElementList.size()){
            return mCurElementIndex;
        }
        else {
            return -1;
        }
   }

   public boolean saveYAML(){
        try{
            mFile = new File(mFilePath, mFileName);
            FileWriter fileWriter = new FileWriter(mFile, false);

            Map<String, Object>UNIFrame = new HashMap<>();

            UNIFrame.put("author", this.brief.author);
            UNIFrame.put("description", this.brief.description);
            UNIFrame.put("data", this.brief.date);
            UNIFrame.put("url", this.brief.url);
            UNIFrame.put("title", this.brief.title);

            Map<String, Object>Frames = new HashMap<>();
            for(FrameProperty frame:mFrames){
                Map<String, Object> frameProperty = new HashMap<>();
                frameProperty.put("Id", frame.Id);
                frameProperty.put("duration", frame.duration);
                frameProperty.put("interval", frame.interval);
                Frames.put(String.valueOf(frame.Id), frameProperty);
            }

            UNIFrame.put("Frames", Frames);

            Map<String, Object>ElementsSet = new HashMap<>();

            int j = 0;
            for(List<MProperty> elementLists: mElements){
                Map<String, Object> list= new HashMap<>();

                int i = 0;

                for(MProperty element: elementLists){
                    Map<String, Object> elementMap = new HashMap<>();
                    elementMap.put("Id", element.property.ID);
                    elementMap.put("x", element.property.x);
                    elementMap.put("y",element.property.y);
                    elementMap.put("height",element.property.height);
                    elementMap.put("width", element.property.width);
                    elementMap.put("opacity", element.property.opacity);
                    elementMap.put("rotation",element.property.rotation);
                    elementMap.put("scale", element.property.scale);
                    elementMap.put("mode",element.property.mode);
                    elementMap.put("url", element.url);

                    list.put(String.valueOf(i++), elementMap);
                }

                ElementsSet.put(String.valueOf(j++), list);

            }

            UNIFrame.put("Elements", ElementsSet);

            Yaml yaml = new Yaml();

            yaml.dump(UNIFrame, fileWriter);

            fileWriter.close();

            return true;

        }catch (Exception e){
            e.printStackTrace();

            return false;
        }
   }

   public boolean loadYAML(){
       try{
           mFile = new File(mFilePath, mFileName);
           FileReader fileReader = new FileReader(mFile);
           BufferedReader bufferedReader = new BufferedReader(fileReader);

//            String lines = "";
//            String content = "";
//            while ((lines = bufferedReader.readLine()) != null){
//                Log.i(TAG, "loadYAML: "+lines);
//            }

           Yaml yaml = new Yaml();
           Map<String, Object> UNIFrame = new HashMap<>();
           UNIFrame = yaml.loadAs(fileReader, UNIFrame.getClass());

           mFrames.clear();
           mElements.clear();
           mMaxElementId = -1;

           String author = (String) UNIFrame.get("author");
           String description = (String) UNIFrame.get("description");
           String date = (String) UNIFrame.get("date");
           String url = (String) UNIFrame.get("url");
           String title = (String) UNIFrame.get("title");

           this.setBrief(author, description, date, url, title);

           Map<String, Object> Frames = (Map<String, Object>)UNIFrame.get("Frames");
           for(String key: Frames.keySet()){
               Map<String, Object> frame = (Map<String, Object>) Frames.get(key);
               int Id = (Integer) frame.get("Id");
               long duration = (Integer) frame.get("duration");
               long interval =  (Integer) frame.get("interval");
               mFrames.add(new FrameProperty(Id, duration, interval));
           }

           Map<String, Object> ElementSet = (Map<String, Object>)UNIFrame.get("Elements");
           for(String key: ElementSet.keySet()){
               Map<String, Object> elementListMap = (Map<String, Object>) ElementSet.get(key);
               ArrayList<MProperty> elementList = new ArrayList<MProperty>();

               for(String subKey:elementListMap.keySet()){
                   Map<String, Object> elementMap = (Map<String, Object>) elementListMap.get(subKey);
                   int Id = (Integer) elementMap.get("Id");
                   int x = (Integer) elementMap.get("x");
                   int y = (Integer) elementMap.get("y");
                   int height = (Integer) elementMap.get("height");
                   int width = (Integer) elementMap.get("width");
                   Double opacity = (Double) ( elementMap.get("opacity"));
                   Double rotation = (Double) (elementMap.get("rotation"));
                   Double scale = (Double)(elementMap.get("scale"));
                   Property.Mode mode = (Property.Mode) elementMap.get("mode");
                   url = (String) elementMap.get("url");
                   Property p = new Property(Id, width, height, x, y,scale.floatValue(), opacity.floatValue(), rotation.floatValue(), mode);
                   elementList.add(new MProperty(p, url));
                   if (Id > mMaxElementId) mMaxElementId = Id;
               }

               mElements.add(elementList);
           }

           mFrameIndex = -1;
           mCurElementIndex = -1;

           return true;

       }catch (Exception e){

           e.printStackTrace();

           return  false;
       }
   }

    public int getMaxElementId() {
        return mMaxElementId;
    }

    public void setBrief(String author, String description, String date, String url, String title){
       this.brief.author = author;
       this.brief.description = description;
       this.brief.date = date;
       this.brief.url = url;
       this.brief.title = title;
    }

    public String getFrameUrl(){
        return this.brief.url;
    }

    public String getTitle(){
        return this.brief.title;
    }

    public String getElementUrl(){
        return (curElementList.get(mCurElementIndex)).url;
    }

    public Brief getBrief(){
        return this.brief;
    }

}
