package com.miuhouse.community.activity;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.miuhouse.community.R;
import com.miuhouse.community.widget.StatusCompat;

public class AboutActivity extends BaseActivity {
    private TextView tv_title;


    @Override
    public void initTitle() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.titlebar);
        mToolbar.setNavigationIcon(R.mipmap.back_black);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        StatusCompat.compat(this, StatusCompat.COLOR_DEFAULT);
        TextView title = (TextView) findViewById(R.id.title);
        title.setText("关于瞄房社区");
        title.setTextColor(Color.parseColor("#1E2129"));
        title.setClickable(true);
    }
//    ActivityOptionsCompat activityOptionsCompat =ActivityOptionsCompat.makeCustomAnimation(this,new Pair<View,String>(view.));

//            CoordinatorLayout

    @Override
    public void initView() {
        setContentView(R.layout.activity_about);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
