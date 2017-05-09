package com.miuhouse.community.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.miuhouse.community.R;
import com.miuhouse.community.activity.BaseActivity;
import com.miuhouse.community.http.FinalData;
import com.miuhouse.community.utils.Constants;
import com.miuhouse.community.utils.MyUtils;
import com.miuhouse.community.utils.SPUtils;
import com.miuhouse.community.utils.ToastUtils;
import com.miuhouse.community.widget.StatusCompat;

import org.w3c.dom.Text;

/**
 * 功能库
 * Created by kings on 1/22/2016.
 */
public class FunctionActivity extends BaseActivity implements View.OnClickListener {
    private final static long PROPERTYID_DEFAULT = 4;

    private long cityId;
    private String propertName;
    private String companyMobile;

    @Override
    public void initTitle() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.titlebar);
        mToolbar.setNavigationIcon(R.mipmap.back_black);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        TextView tvTitle = (TextView) findViewById(R.id.title);
        StatusCompat.compat(this, StatusCompat.COLOR_DEFAULT);
        tvTitle.setText("功能库");
        tvTitle.setTextColor(Color.parseColor("#1E2129"));
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_function);

        findViewById(R.id.linear_tousu).setOnClickListener(this);

        findViewById(R.id.linear_baoxiu).setOnClickListener(this);

        findViewById(R.id.linear_passport).setOnClickListener(this);

        findViewById(R.id.linear_express).setOnClickListener(this);

        findViewById(R.id.linear_buy).setOnClickListener(this);

        findViewById(R.id.linear_rent).setOnClickListener(this);

        findViewById(R.id.linear_praise).setOnClickListener(this);

        findViewById(R.id.linear_property_placard).setOnClickListener(this);

        findViewById(R.id.linear_community_service).setOnClickListener(this);

        findViewById(R.id.newHouse).setOnClickListener(this);

        findViewById(R.id.linear_snzx).setOnClickListener(this);

        findViewById(R.id.linear_glcs).setOnClickListener(this);

        findViewById(R.id.linear_jiaofei).setOnClickListener(this);

        findViewById(R.id.linear_community_news).setOnClickListener(this);
    }

    @Override
    public void initData() {
        cityId = getIntent().getLongExtra("cityId", 0);
        propertName = getIntent().getStringExtra("propertName");
        companyMobile = getIntent().getStringExtra("companyMobile");
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.linear_tousu:
                if (!MyUtils.isLoggedIn()) {
                    startActivity(LoginActivity.class);
                } else {
                    startActivity(TousuActivity.class);
                }
                break;
            case R.id.linear_baoxiu:
                if (!MyUtils.isLoggedIn()) {
                    startActivity(LoginActivity.class);
                } else {
                    startActivity(BaoxiuActivity.class);
                }
                break;
            case R.id.linear_passport:
                if (!MyUtils.isLoggedIn()) {
                    startActivity(LoginActivity.class);
                } else {
                    startActivity(PassportActivity.class);
                }
                break;
            case R.id.linear_express:
                if (!MyUtils.isLoggedIn()) {
                    startActivity(LoginActivity.class);
                } else {
                    startActivity(ExpressActivity.class);
                }
                break;
            case R.id.linear_buy:

                Bundle bundle = new Bundle();
                bundle.putInt(HouseListActivity.TAG_PURPOSE, Constants.SELL);
                startActivity(HouseListActivity.class, bundle);
                break;
            case R.id.linear_rent:
                Bundle bundleRent = new Bundle();
                bundleRent.putInt(HouseListActivity.TAG_PURPOSE, Constants.LEASE);
                startActivity(HouseListActivity.class, bundleRent);
                break;
            case R.id.linear_praise:
                if (!MyUtils.isLoggedIn()) {
                    startActivity(LoginActivity.class);
                } else {
                    startActivity(PraiseActivity.class);
                }
                break;
            case R.id.linear_property_placard:
                activity.startActivity(
                    new Intent(activity, NewsActivity.class).putExtra("cityId", cityId));
                break;
            case R.id.linear_community_service:
                activity.startActivity(new Intent(activity, BrowseActivity.class)
                    .putExtra(BrowseActivity.BROWSER_KEY,
                        FinalData.URL_HEAD + "/mobile/xqwy/"
                            + SPUtils.getSPData(Constants.PROPERTYID, PROPERTYID_DEFAULT))
                    .putExtra("title", "小区无忧")
                    .putExtra("shareContent", propertName)
                );
                break;
            case R.id.newHouse:
                activity.startActivity(new Intent(activity, NewHouseListActivity.class)
                    .putExtra("propertyId",
                        SPUtils.getSPData(Constants.PROPERTYID, PROPERTYID_DEFAULT))
                    .putExtra("locX", 0d).putExtra("locY", 0d));
                break;
            case R.id.linear_snzx:
                activity.startActivity(
                    new Intent(activity, InformationActivity.class).putExtra("type",
                        Constants.TYPE_SNZX));
                break;
            case R.id.linear_glcs:
                activity.startActivity(
                    new Intent(activity, InformationActivity.class).putExtra("type",
                        Constants.TYPE_GLCS));
                break;
            case R.id.linear_community_news:
                //activity.startActivity(new Intent(activity, NewsListActivity.class)
                //    .putExtra(NewsListActivity.TITLE, "社区动态")
                //    .putExtra(NewsListActivity.PROPERTYID,
                //        SPUtils.getSPData(Constants.PROPERTYID, PROPERTYID_DEFAULT))
                //    .putExtra(NewsListActivity.TYPE, 6));
                //break;
            case R.id.linear_jiaofei:
                ToastUtils.showToast(this, "暂未开通");

                break;
        }
    }
}
