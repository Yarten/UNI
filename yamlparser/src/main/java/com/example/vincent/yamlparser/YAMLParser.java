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
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static android.content.ContentValues.TAG;

/**
 * Created by D105-01 on 2017/12/9.
 */

public class YAMLParser {

    private static final String TAG = "YAMLParser";
    private String mUNIVersion;
    private String mAuthor;
    private String mCreateTime;
    private String mUpdateTime;

    private File mFilePath;
    private String mFileName;
    private File mFile;

    //private FileReader mYAMLReader;
    private BufferedReader mYAMLBufferedReader;

    private ArrayList<FrameProperty> mFrames = new ArrayList<>();
    private int mFrameIndex;

    private List<ArrayList<Property>> mElements = new ArrayList<ArrayList<Property>>();
    private ArrayList<Property> curElementList;
    private int mCurElementListIndex;

    public YAMLParser(File filePath, String fileName){
        mFilePath = filePath;
        mFileName = fileName;
    }

    public void addFrame (float duration, float interval){

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
        curElementList.add(property);
    }

    public FrameProperty getFrame(){
        return mFrames.get(mFrameIndex);
    }

    public Property getElement(){
        return curElementList.get(mCurElementListIndex);
    }

    public int hasNextFrame(){
        mFrameIndex++;
        if(mFrameIndex < mFrames.size()){
            return mFrameIndex;
        }
        else{
            return -1;
        }
    }

   public int hasNextElement(){
        mCurElementListIndex++;
        if(mCurElementListIndex < curElementList.size()){
            return mCurElementListIndex;
        }
        else {
            return -1;
        }
   }

   public void saveYAML() throws IOException{
       if(curElementList.size() > 0){
           ArrayList<Property> elementList = new ArrayList<Property>();
           mElements.add(elementList);
           curElementList = elementList;
       }

        try{
            mFile = new File(mFilePath, mFileName);
            FileWriter fileWriter = new FileWriter(mFile, false);
            Yaml yaml = new Yaml();
            //Property p1 = new Property(1);
            Person person = new Person();
            person.setName("JJJ");
            person.setPhone("13354");
            person.setAddress("23445");
            yaml.dump(person, fileWriter);
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
           String content = "";
           String lines = "";
           while ((lines = bufferedReader.readLine()) != null){
               Log.i(TAG, "onCreate: "+ lines);
           }
           String ss = "name: Joe\n" + "phone: 111-111-1111\n"
                   + "address: Park Dr, Charlie Hill";
           Yaml yaml = new Yaml();
           Object parser = yaml.loadAs(fileReader, Person.class);
           Log.i(TAG, "loadYAML: "+parser.getClass());


       }catch (Exception e){
           e.printStackTrace();
       }
   }

   public class Person{
       private String name;
       private String phone;
       private String address;

       public String getName() {
           return name;
       }

       public void setName(String name) {
           this.name = name;
       }

       public String getPhone() {
           return phone;
       }

       public void setPhone(String phone) {
           this.phone = phone;
       }

       public String getAddress() {
           return address;
       }

       public void setAddress(String address) {
           this.address = address;
       }
   }

}
