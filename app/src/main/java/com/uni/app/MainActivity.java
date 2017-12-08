package com.uni.app;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.uni.utils.Graphicstools.statusBar;;import pub.tanzby.unieditor.UniEditActivity;

public class MainActivity extends AppCompatActivity {

    Button bnt;

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

        bnt = findViewById(R.id.bnt_intent_editor);
    }

    private void evenbing()
    {
        bnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent view = new Intent(MainActivity.this, UniEditActivity.class);
                startActivity(view);
            }
        });
    }
}
