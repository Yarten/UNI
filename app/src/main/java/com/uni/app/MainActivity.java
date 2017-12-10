package com.uni.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.stone.vega.library.VegaLayoutManager;
import com.uni.uniplayer.UNIFrame;
import com.uni.uniplayer.UNIView;
import com.uni.utils.GraphicsTools.statusBar;

import java.util.ArrayList;
import java.util.List;

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

        List<UNIFrame> ls = new ArrayList<>();



        mAdapter = new UNIFrameAdapter<>(getApplicationContext(),ls);

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

                UNIView uniView = (UNIView)view;
                toggle = !toggle;
                if(toggle)
                {
                    uniView.play();
                }
                else uniView.stop();
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
