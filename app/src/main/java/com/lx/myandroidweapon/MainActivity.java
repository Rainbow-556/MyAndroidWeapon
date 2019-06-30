package com.lx.myandroidweapon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_web_view_cache).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_web_view_cache:
                openWebViewCacheTestActivity();
                break;
            default:
                break;
        }
    }

    private void openWebViewCacheTestActivity() {
        Intent intent = new Intent(this, WebViewCacheTestActivity.class);
        startActivity(intent);
    }
}
