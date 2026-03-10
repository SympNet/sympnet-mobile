package com.sympnet.app.ui.body;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
import com.sympnet.app.R;
import com.sympnet.app.ui.BaseActivity;

public class BodyMapActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_map);

        // ✅ Setup drawer
        setupDrawer(R.id.toolbar, R.id.drawerLayout, R.id.navigationView);

        WebView webView = findViewById(R.id.webViewBody);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/body_map.html");
    }
}