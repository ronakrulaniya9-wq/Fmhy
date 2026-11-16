package com.fmhy.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.*;
import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends Activity {
    private static final String HOME = "https://fmhy.net";
    private static final String[] ALLOWED = {"fmhy.net", "fmhy.pages.dev"};
    private static final String HIDE_ADS_JS =
        "(function(){var s=document.createElement('style');" +
        "s.innerHTML='ins.adsbygoogle,[id^=\"google_ads\"],[class*=\"adsbox\"],[class*=\"ad-banner\"]," +
        "[id*=\"ad-container\"],iframe[src*=\"doubleclick\"],iframe[src*=\"ads\"]{display:none!important}';" +
        "document.head.appendChild(s);})();";

    private WebView web;
    private final Set<String> blocked = new HashSet<>();

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        loadBlocklist();
        web = new WebView(this);
        setContentView(web);

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setSupportMultipleWindows(false);
        s.setJavaScriptCanOpenWindowsAutomatically(false);
        s.setMediaPlaybackRequiresUserGesture(true);

        web.setWebChromeClient(new WebChromeClient());
        web.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView v, WebResourceRequest r) {
                if (isBlocked(r.getUrl().getHost())) {
                    return new WebResourceResponse("text/plain", "utf-8",
                        new ByteArrayInputStream(new byte[0]));
                }
                return null;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r) {
                String host = r.getUrl().getHost();
                if (isBlocked(host)) return true;
                if (isAllowed(host)) return false;
                try { startActivity(new Intent(Intent.ACTION_VIEW, r.getUrl())); } catch (Exception ignored) {}
                return true;
            }

            @Override
            public void onPageFinished(WebView v, String url) {
                v.evaluateJavascript(HIDE_ADS_JS, null);
            }
        });

        if (b == null) web.loadUrl(HOME); else web.restoreState(b);
    }

    private void loadBlocklist() {
        try (BufferedReader r = new BufferedReader(new InputStreamReader(getAssets().open("blocklist.txt")))) {
            String line;
            while ((line = r.readLine()) != null) {
                line = line.trim().toLowerCase();
                if (!line.isEmpty() && !line.startsWith("#")) blocked.add(line);
            }
        } catch (IOException ignored) {}
    }

    private boolean isBlocked(String host) {
        if (host == null) return false;
        host = host.toLowerCase();
        while (host.contains(".")) {
            if (blocked.contains(host)) return true;
            host = host.substring(host.indexOf('.') + 1);
        }
        return false;
    }

    private boolean isAllowed(String host) {
        if (host == null) return false;
        for (String a : ALLOWED) if (host.equals(a) || host.endsWith("." + a)) return true;
        return false;
    }

    @Override
    public void onBackPressed() {
        if (web.canGoBack()) web.goBack(); else super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        web.saveState(out);
    }
}
