package com.miuhouse.community.activity;

import android.graphics.Color;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.miuhouse.community.R;
import com.miuhouse.community.application.MyApplication;
import com.miuhouse.community.bean.BaseBean;
import com.miuhouse.community.http.FinalData;
import com.miuhouse.community.http.GsonRequest;
import com.miuhouse.community.http.VolleySingleton;
import com.miuhouse.community.utils.StringUtils;
import com.miuhouse.community.utils.ToastUtils;
import com.miuhouse.community.widget.StatusCompat;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 意见反馈
 * Created by kings on 1/22/2016.
 */
public class FeedbackActivity extends BaseActivity {

    private static final String TAG="FeedbackActivity";
    private EditText etMessage;
    private EditText etContactInformation;
    private Button btnSubmit;

    @Override
    public void initTitle() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.titlebar);
        mToolbar.setTitle("");
        mToolbar.setNavigationIcon(R.mipmap.back_black);
        setSupportActionBar(mToolbar);
        StatusCompat.compat(this, StatusCompat.COLOR_DEFAULT);
        TextView tvTiltle = (TextView) findViewById(R.id.title);
        tvTiltle.setText("意见反馈");
        tvTiltle.setTextColor(Color.parseColor("#1E2129"));
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_feedback);
        etContactInformation = (EditText) findViewById(R.id.et_contact_information);
        etMessage = (EditText) findViewById(R.id.et_message);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRqeust();
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
        return TAG;
    }


    public void sendRqeust() {
        String urlPath = FinalData.URL_VALUE + "saveBack";
        String content = etMessage.getText().toString();
        String contactInformation = etContactInformation.getText().toString();
        if (StringUtils.isEmpty(content)) {
            showToast("请输入你的建议和感想");
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("id", MyApplication.getInstance().getUserBean().getId());
        map.put("content", contactInformation);
        if (!StringUtils.isEmpty(contactInformation))
            map.put("link", contactInformation);
        GsonRequest<BaseBean> request = new GsonRequest<>(Request.Method.POST, urlPath, BaseBean.class, map, new Response.Listener<BaseBean>() {
            @Override
            public void onResponse(BaseBean baseBean) {
             showToast(baseBean.getMsg());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            showToast("提交失败");
            }
        });
        request.setTag(TAG);
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
