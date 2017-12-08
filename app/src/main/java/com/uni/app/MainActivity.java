package com.uni.app;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.stone.vega.library.VegaLayoutManager;
import com.uni.utils.Graphicstools.statusBar;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import pub.tanzby.unieditor.UNIElementView;
import pub.tanzby.unieditor.UniEditActivity;

public class MainActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    UNIFrameAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        evenbing();
    }

    private void init()
    {

        statusBar.immerseStatusBar(this);

        List<String> l = new ArrayList<>();
        l.add("uni frame 1");
        l.add("uni frame 2");
        l.add("uni frame 3");
        l.add("uni frame 4");
        l.add("uni frame 5");
        l.add("uni frame 6");

        mAdapter = new UNIFrameAdapter<String>(getApplicationContext(),l);

        mRecyclerView = findViewById(R.id.rv_mainactivity_framelist);

        mRecyclerView.setLayoutManager(new VegaLayoutManager());

        mRecyclerView.setAdapter(mAdapter);

    }

    private void evenbing()
    {
        mAdapter.setOnItemClickLitener(new UNIFrameAdapter.OnItemClickLitener() {
            boolean toggle = false;
            @Override
            public void onFrameClick(View view, int position) {
                Toast.makeText(getApplicationContext(),"click",Toast.LENGTH_SHORT).show();
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

    }
}
