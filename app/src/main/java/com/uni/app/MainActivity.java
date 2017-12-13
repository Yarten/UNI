package com.uni.app;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.vincent.yamlparser.YAMLParser;
import com.stone.vega.library.VegaLayoutManager;
import com.uni.uniplayer.UNIFrame;
import com.uni.uniplayer.UNIView;
import com.uni.utils.Brief;
import com.uni.utils.FileUtils;
import com.uni.utils.GraphicsTools;
import com.uni.utils.GraphicsTools.statusBar;
import com.uni.utils.Permission;
import com.uni.utils.Property;
import com.yarten.unimanager.UNIManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pub.tanzby.unieditor.UniEditActivity;

public class MainActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    UNIFrameAdapter mAdapter;

    ImageButton bnt_add_new;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Permission.verifyExternalStoragePermissions(this);
        init();
        evenBinding();
        makeSomeNoise();
    }


    private void init()
    {
        UNIManager.instance.init(this);

        statusBar.immerseStatusBar(this);

        List<UNIFrame> ls = new ArrayList<>();

        List<String> l = new ArrayList<>();
        l.add("XXXX");
        l.add("XXXX");
        l.add("XXXX");
        l.add("XXXX");
        l.add("XXXX");
        l.add("XXXX");
        l.add("XXXX");
        l.add("XXXX");
        l.add("XXXX");
        l.add("XXXX");
        l.add("XXXX");
        l.add("XXXX");

        mAdapter = new UNIFrameAdapter<String>(this,l);

        mRecyclerView = findViewById(R.id.rv_mainactivity_framelist);

        mRecyclerView.setLayoutManager(new VegaLayoutManager());

        mRecyclerView.setAdapter(mAdapter);


        bnt_add_new = findViewById(R.id.bnt_add_new_uni_frame);

    }

    private void evenBinding()
    {
        mAdapter.setOnItemClickLitener(new UNIFrameAdapter.OnItemClickLitener() {

            @Override
            public void onFrameClick(View view, int position) {
                Toast.makeText(getApplicationContext(),"click",Toast.LENGTH_SHORT).show();

                UNIView uniView = (UNIView)view;
                if(uniView.isPlaying())
                {
                    uniView.stop();
                }
                else uniView.play();
            }

            @Override
            public void onFrameLongClick(View view, int position) {
                Toast.makeText(getApplicationContext(),"long click",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFavourButtomClick(View view, int position,Boolean isfavour) {
                Toast.makeText(getApplicationContext(),""+isfavour,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onEditButtomClick(View view, int position) {
                Intent nwIntend = new Intent(MainActivity.this,UniEditActivity.class);
                startActivity(nwIntend);
            }
        });

        bnt_add_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nwIntend = new Intent(MainActivity.this,UniEditActivity.class);
                startActivity(nwIntend);
            }
        });
    }

    class Element
    {
        Element(String name, int sourceID)
        {
            this.name = name;
            this.sourceID = sourceID;
        }

        String name;
        int sourceID;
    }

    Element[] elements = {
            new Element("cloud", R.drawable.cloud),
            new Element("dialog", R.drawable.dialog),
            new Element("dialog2", R.drawable.dialog2),
            new Element("dialog3", R.drawable.dialog3),
            new Element("dialog4", R.drawable.dialog4),
            new Element("low", R.drawable.low),
            new Element("high", R.drawable.high),
            new Element("people", R.drawable.people),
            new Element("sun", R.drawable.sun)
    };

    private void makeSomeNoise()
    {
        Resources resources = getResources();

        for(int i = 0; i < elements.length; i++)
        {
            File root = FileUtils.instance.getFileDir(elements[i].name);
            File imageDir = FileUtils.instance.getFileDir(elements[i].name + "/Images");

            Bitmap image = BitmapFactory.decodeResource(resources, elements[i].sourceID);
            GraphicsTools.saveToLocal(imageDir, elements[i].name, image);
            GraphicsTools.saveToLocal(root, "thumb", image);

            YAMLParser parser = new YAMLParser(root, elements[i].name + ".yaml");
            parser.setBrief("AA", "BB", "12-11", "System/" + elements[i].name, "Demo");
            parser.addFrame(0, 0);
            Property property = new Property();
            parser.addElement(property, "System/" + elements[i].name);
            parser.saveYAML();
        }
    }
}
