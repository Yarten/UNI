package com.example.vincent.yamlparser;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.uni.utils.FrameProperty;
import com.uni.utils.MenuButton.MenuButton;
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

        MenuButton menuButton = findViewById(R.id.menu_button);
        menuButton.setOnItemClickListener(new MenuButton.OnItemClickListener() {
            @Override
            public void onNextButtonClick() {
                Toast.makeText(MainActivity.this, "You've clicked  Next", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMenuItemClick(int position) {
                switch (position){
                    case 0:
                        Toast.makeText(MainActivity.this, "You've clicked  0", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(MainActivity.this, "You've clicked  1", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(MainActivity.this, "You've clicked  2", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onPrevButtonClick() {
                Toast.makeText(MainActivity.this, "You've clicked  Prev", Toast.LENGTH_SHORT).show();
            }
        });
//        YAMLParser parser = new YAMLParser(getFilesDir(), "test.yml");
//        parser.setBrief("vincent", "student", "12-11", "parser-url", "Test");
//        Property p1 = new Property(1);
//        Property p2 = new Property(2);
//
//        parser.addFrame(1000, 1000);
//        parser.addElement(p1, "p1_url");
//        parser.addElement(p2, "p2_url");
//
//        parser.addFrame(1000, 1000);
//        parser.addElement(new Property(3), "p3_url");
//        parser.addElement(new Property(4), "p4_url");
//
//        //parser.addFrame(0.5f, 0.4f);
//        try{
//            parser.saveYAML();
//            Log.i(TAG, "onCreate: saved");
//            parser.loadYAML();
//            Log.i(TAG, "onCreate: loaded");
//
//            Log.i(TAG, "onCreate: "+parser.getMaxElementId() + parser.getFrameUrl() + parser.getTitle());
//
//            List<FrameProperty> frameProperties = new ArrayList<>();
//
//            while (parser.hasNextFrame() != -1){
//
//                frameProperties.add(parser.getFrame());
//                Log.i(TAG, "onCreate: " + (parser.getFrame()).Id);
//
//                while (parser.hasNextElement() != -1){
//
//                    Property property = parser.getElement();
//                    String url = parser.getElementUrl();
//                    Log.i(TAG, "onCreate: "+property.ID + " " + property.x + " " + property.y + " " + url
//                    );
//
//                }
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }
}
