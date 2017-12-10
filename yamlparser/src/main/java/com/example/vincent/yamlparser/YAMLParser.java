package com.example.vincent.yamlparser;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.renderscript.RenderScript;
import android.support.annotation.NonNull;
import android.util.Log;

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
    private String mUNIVersion = "";
    private String mAuthor = "";
    private String mCreateTime = "";
    private String mUpdateTime = "";

    private File mFilePath;
    private String mFileName;
    private File mFile;

    private ArrayList<FrameProperty> mFrames = new ArrayList<>();
    private int mFrameIndex;

    private List<ArrayList<Property>> mElements = new ArrayList<ArrayList<Property>>();
    private ArrayList<Property> curElementList;
    private int mCurElementIndex;

    private int mMaxElementId;

    /**
     * 初始化函数
     * @param filePath
     * @param fileName
     */
    public YAMLParser(File filePath, String fileName){
        mFilePath = filePath;
        mFileName = fileName;
    }

    public void addFrame (Double duration, Double interval){

        mFrames.add(new FrameProperty(mFrames.size()+1, duration, interval));

        ArrayList<Property> elementList = new ArrayList<Property>();
        mElements.add(elementList);
        curElementList = elementList;
    }


    /**
     * 默认在当前帧增加Element
     * @param property
     */
    public void addElement(Property property){
        if(property.ID > mMaxElementId) mMaxElementId =  property.ID;
        curElementList.add(property);
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
        return curElementList.get(mCurElementIndex);
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

   public void saveYAML() throws IOException{
        try{
            mFile = new File(mFilePath, mFileName);
            FileWriter fileWriter = new FileWriter(mFile, false);

            Map<String, Object>UNIFrame = new HashMap<>();

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
            for(List<Property> elementLists: mElements){
                Map<String, Object> list= new HashMap<>();

                int i = 0;

                for(Property element: elementLists){
                    Map<String, Object> elementMap = new HashMap<>();
                    elementMap.put("Id", element.ID);
                    elementMap.put("x", element.x);
                    elementMap.put("y",element.y);
                    elementMap.put("height",element.height);
                    elementMap.put("width", element.width);
                    elementMap.put("opacity", (Float)element.opacity);
                    elementMap.put("mode",element.mode);

                    list.put(String.valueOf(i++), elementMap);
                }

                ElementsSet.put(String.valueOf(j++), list);

            }

            UNIFrame.put("Elements", ElementsSet);

            Yaml yaml = new Yaml();

            yaml.dump(UNIFrame, fileWriter);
            fileWriter.close();

        }catch (Exception e){
            e.printStackTrace();
        }
   }

   public void loadYAML() throws IOException{
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


           Map<String, Object> Frames = (Map<String, Object>)UNIFrame.get("Frames");
           for(String key: Frames.keySet()){
               Map<String, Object> frame = (Map<String, Object>) Frames.get(key);
               int Id = (Integer) frame.get("Id");
               Double duration = (Double) frame.get("duration");
               Double interval =  (Double) frame.get("interval");
               mFrames.add(new FrameProperty(Id, duration, interval));
           }

           Map<String, Object> ElementSet = (Map<String, Object>)UNIFrame.get("Elements");
           for(String key: ElementSet.keySet()){
               Map<String, Object> elementListMap = (Map<String, Object>) ElementSet.get(key);
               ArrayList<Property> elementList = new ArrayList<Property>();

               for(String subKey:elementListMap.keySet()){
                   Map<String, Object> elementMap = (Map<String, Object>) elementListMap.get(subKey);
                   int Id = (Integer) elementMap.get("Id");
                   int x = (Integer) elementMap.get("x");
                   int y = (Integer) elementMap.get("y");
                   int height = (Integer) elementMap.get("height");
                   int width = (Integer) elementMap.get("width");
                   Double opacity = (Double) ( elementMap.get("opacity"));
                   Property.Mode mode = (Property.Mode) elementMap.get("mode");
                   elementList.add(new Property(Id, x, y, height, width, opacity.floatValue(), mode));
                   if (Id > mMaxElementId) mMaxElementId = Id;
               }

               mElements.add(elementList);
           }

           mFrameIndex = -1;
           mCurElementIndex = -1;

       }catch (Exception e){
           e.printStackTrace();
       }
   }

    public String getUNIVersion() {
        return mUNIVersion;
    }

    public void setUNIVersion(String mUNIVersion) {
        this.mUNIVersion = mUNIVersion;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String mAuthor) {
        this.mAuthor = mAuthor;
    }

    public String getCreateTime() {
        return mCreateTime;
    }

    public void setCreateTime(String mCreateTime) {
        this.mCreateTime = mCreateTime;
    }

    public String getUpdateTime() {
        return mUpdateTime;
    }

    public void setUpdateTime(String mUpdateTime) {
        this.mUpdateTime = mUpdateTime;
    }

    public int getMaxElementId() {
        return mMaxElementId;
    }
}
