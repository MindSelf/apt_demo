package com.watayouxiang.aptdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.watayouxiang.apt_annotation.BindView;
import com.watayouxiang.apt_annotation.MyButterKnife;

/**
 * 使用自定义的 APT
 */
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv_text1)
    TextView textView1;
    @BindView(R.id.tv_text2)
    TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyButterKnife.bind(this);
        Log.i("zlx", "onCreate");
    }
}