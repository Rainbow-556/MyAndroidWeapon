package com.lx.myandroidweapon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.lx.lib.webviewcache.WebActivity;

/**
 * Created by glennli on 2019/6/26.<br/>
 */
public final class WebViewCacheTestActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_cache_test);
        etUrl = findViewById(R.id.et_url);
        findViewById(R.id.btn_open_url).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_open_url:
                openUrl();
                break;
            default:
                break;
        }
    }

    private void openUrl() {
        String url = etUrl.getText().toString();
        Intent intent = new Intent(this, WebActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }
}
