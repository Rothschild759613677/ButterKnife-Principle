package com.limitless.butter_knife_demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.limitless.butter_knife.ButterKnife;
import com.limitless.butterknife_annotations.BindView;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_tv)
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Log.d("Nick", "onCreate: "+tv.getText());
    }
}
