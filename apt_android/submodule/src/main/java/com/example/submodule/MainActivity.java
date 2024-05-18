package com.example.submodule;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.watayouxiang.apt_annotation.BindView;
import com.watayouxiang.apt_annotation.MyButterKnife;

public class MainActivity extends AppCompatActivity {

//    @BindView(112345)   //编译时demo，不能够运行
    TextView textView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        MyButterKnife.bind(this);
        Log.i("zlx", "submodule onCreate");
    }
}