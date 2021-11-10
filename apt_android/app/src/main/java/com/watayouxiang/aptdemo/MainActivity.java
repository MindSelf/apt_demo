package com.watayouxiang.aptdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.watayouxiang.apt_annotation.BindView;
import com.watayouxiang.apt_annotation.MyButterKnife;

/**
 * 使用自定义的 APT
 */
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv_text)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyButterKnife.bind(this);

        textView.setText("APT 实现 ButterKnife");
    }
}