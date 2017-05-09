package com.miuhouse.community.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.miuhouse.community.R;
import com.miuhouse.community.bean.Image;

/**
 * Created by kings on 12/31/2015.
 */
public class TestDrag extends BaseActivity {

    private LinearLayout linear;
    private ImageView imageOne;
    private ImageView imageTwo;
    private ImageView imageThree;


    @Override
    public void initTitle() {

    }

    @Override
    public void initView() {
        setContentView(R.layout.drag);
        linear = (LinearLayout) findViewById(R.id.linear);
        imageOne = (ImageView) findViewById(R.id.image_one);
        imageTwo = (ImageView) findViewById(R.id.image_two);
        imageThree = (ImageView) findViewById(R.id.image_three);
//        setupDragSort(imageOne);
//        setupDragSort(imageTwo);
//        setupDragSort(imageThree);
        imageOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                share();
            }
        });
    }

    @Override
    public void initData() {

    }

    @Override
    public void initVariables() {

    }

    @Override
    public String getTag() {
        return null;
    }


}
