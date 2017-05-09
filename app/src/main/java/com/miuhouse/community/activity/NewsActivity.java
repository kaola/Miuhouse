package com.miuhouse.community.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.miuhouse.community.R;
import com.miuhouse.community.fragment.BuyHouseFragment;
import com.miuhouse.community.fragment.NewsFragment;
import com.miuhouse.community.fragment.NotificationFragment;
import com.miuhouse.community.fragment.RentingHouseFrxagment;
import com.miuhouse.community.widget.StatusCompat;

import org.w3c.dom.Text;

/**
 * 新闻
 * Created by kings on 1/26/2016.
 */
public class NewsActivity extends BaseActivity {

    private TextView tvNews;
    private TextView tvPropertyNews;
    private NewsFragment newFragment;
    private NotificationFragment propertyFragment;
    private int newType;
    private long cityId;

    @Override public void initTitle() {

        Toolbar mToolbar = (Toolbar) findViewById(R.id.titlebar);
        mToolbar.setNavigationIcon(R.mipmap.back_black);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        StatusCompat.compat(this, StatusCompat.COLOR_DEFAULT);
        tvNews = (TextView) findViewById(R.id.tv_option_left);
        tvPropertyNews = (TextView) findViewById(R.id.tv_option_right);
        if (newType == 0) {
            tvPropertyNews.setSelected(true);
            tvNews.setSelected(false);
            tvNews.setTextColor(getResources().getColor(R.color.text_red));
            tvPropertyNews.setTextColor(getResources().getColor(R.color.white));

        } else {
            tvPropertyNews.setSelected(false);
            tvNews.setSelected(true);
            tvNews.setTextColor(getResources().getColor(R.color.white));
            tvPropertyNews.setTextColor(getResources().getColor(R.color.text_red));
        }

        tvPropertyNews.setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View v) {

                tvPropertyNews.setSelected(true);
                tvNews.setSelected(false);
                tvNews.setTextColor(getResources().getColor(R.color.text_red));
                tvPropertyNews.setTextColor(getResources().getColor(R.color.white));
                FragmentManager fm = getSupportFragmentManager();
                // 开启Fragment事务
                FragmentTransaction transaction = fm.beginTransaction();
                if (newFragment == null) {
                    newFragment = new NewsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("tag", 0);
                    bundle.putLong("cityId", cityId);
                    newFragment.setArguments(bundle);
                }

                transaction.replace(R.id.fl_make_money_container, newFragment);
                transaction.commit();

            }
        });
        tvNews.setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View v) {

                tvPropertyNews.setSelected(false);
                tvNews.setSelected(true);
                tvNews.setTextColor(getResources().getColor(R.color.white));
                tvPropertyNews.setTextColor(getResources().getColor(R.color.text_red));
                FragmentManager fm = getSupportFragmentManager();
                // 开启Fragment事务
                FragmentTransaction transaction = fm.beginTransaction();
                //
                if (propertyFragment == null) {
                    propertyFragment = new NotificationFragment();
                    //                    Bundle bundle = new Bundle();
                    //                    bundle.putInt("tag", 1);
                    //                    propertyFragment.setArguments(bundle);
                }

                transaction.replace(R.id.fl_make_money_container, propertyFragment);
                transaction.commit();
            }
        });
    }

    @Override public void initView() {

        setContentView(R.layout.activity_news);
    }

    @Override public void initData() {

        newType = getIntent().getIntExtra("newType", 0);
        cityId = getIntent().getLongExtra("cityId", 0);
        Log.i("TAG", "cityId=" + cityId);
        if (newType == 0) {
            newFragment = new NewsFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("tag", 0);
            bundle.putLong("cityId", cityId);
            newFragment.setArguments(bundle);
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.fl_make_money_container, newFragment).commit();
        } else {
            propertyFragment = new NotificationFragment();

            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.fl_make_money_container, propertyFragment).commit();
        }

    }

    @Override public void initVariables() {

    }

    @Override public String getTag() {

        return null;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
