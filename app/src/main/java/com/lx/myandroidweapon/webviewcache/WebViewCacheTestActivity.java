package com.lx.myandroidweapon.webviewcache;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lx.lib.common.util.ToastUtil;
import com.lx.lib.webviewcache.WebViewCacheManager;
import com.lx.myandroidweapon.R;

/**
 * Created by glennli on 2019/6/26.<br/>
 */
public final class WebViewCacheTestActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etUrl;
    private Button btnCacheStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_cache_test);
        etUrl = findViewById(R.id.et_url);
        btnCacheStatus = findViewById(R.id.btn_cache_status);
//        etUrl.setText("https://m.leka.club/childrens_day_activity/index.html");
//        etUrl.setText("https://m.leka.club/follow_wechat/index.html");
//        etUrl.setText("https://m.leka.club/download/app.html");
        etUrl.setText("https://m.leka.club/help/index.html");
//        etUrl.setText("https://vip.m.fenqile.com/vip_mall/index.html");
        findViewById(R.id.btn_open_url).setOnClickListener(this);
        findViewById(R.id.btn_clear_cache).setOnClickListener(this);
        btnCacheStatus.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_open_url:
                openUrl();
                break;
            case R.id.btn_cache_status:
                switchCacheStatus();
                break;
            case R.id.btn_clear_cache:
                clearCache();
                break;
            default:
                break;
        }
    }

    private void switchCacheStatus() {
        Object tag = btnCacheStatus.getTag();
        boolean enable = tag instanceof Boolean ? (boolean) tag : true;
        btnCacheStatus.setTag(!enable);
        WebViewCacheManager.get().setEnable(!enable);
        btnCacheStatus.setText(!enable ? "缓存：已开" : "缓存：已关");
    }

    private void openUrl() {
        String url = etUrl.getText().toString();
        Intent intent = new Intent(this, WebActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    private void clearCache() {
        WebViewCacheManager.get().clear();
        ToastUtil.show(this, "缓存已清除", true);
    }
}
