package com.example.vincent.yamlparser;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.uni.utils.FrameProperty;
import com.uni.utils.Property;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        YAMLParser parser = new YAMLParser(getFilesDir(), "test.yml");

        Property p1 = new Property(1);
        Property p2 = new Property(2);
        
        parser.addFrame(0.1, 0.2);
        parser.addElement(p1);
        parser.addElement(p2);
        
        parser.addFrame(0.3, 0.4);
        parser.addElement(new Property(3));
        parser.addElement(new Property(4));

        //parser.addFrame(0.5f, 0.4f);
        try{
            parser.saveYAML();
            Log.i(TAG, "onCreate: saved");
            parser.loadYAML();
            Log.i(TAG, "onCreate: loaded");

            Log.i(TAG, "onCreate: "+parser.getMaxElementId());

            List<FrameProperty> frameProperties = new ArrayList<>();

            while (parser.hasNextFrame() != -1){

                frameProperties.add(parser.getFrame());
                Log.i(TAG, "onCreate: " + (parser.getFrame()).Id);

                while (parser.hasNextElement() != -1){

                    Property property = parser.getElement();
                    Log.i(TAG, "onCreate: "+property.Id + " " + property.x + " " + property.y);

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
