package com.surya.optics.mobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.graphics.Color;
import android.view.Gravity;
import android.text.InputType;
import android.content.SharedPreferences;

/** Android client for the existing Surya Optics Spring Boot web application. */
public class MainActivity extends Activity {
    private static final int FILE_CHOOSER_REQUEST = 2417;
    private static final String PREFS = "surya_optics_mobile";
    private static final String SERVER_URL_KEY = "server_url";
    private WebView webView;
    private ProgressBar progressBar;
    private TextView statusView;
    private ValueCallback<Uri[]> fileChooserCallback;
    private SharedPreferences preferences;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences(PREFS, MODE_PRIVATE);
        buildLayout();
        configureWebView();
        String savedUrl = preferences.getString(SERVER_URL_KEY, "");
        if (savedUrl == null || savedUrl.trim().isEmpty()) showServerDialog();
        else loadServer(savedUrl);
    }

    private void buildLayout() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE);
        LinearLayout toolbar = new LinearLayout(this);
        toolbar.setGravity(Gravity.CENTER_VERTICAL);
        toolbar.setPadding(dp(14), dp(8), dp(10), dp(8));
        toolbar.setBackgroundColor(Color.rgb(18, 59, 114));
        TextView title = new TextView(this);
        title.setText("Surya Optics Billing");
        title.setTextColor(Color.WHITE);
        title.setTextSize(18);
        title.setTypeface(null, 1);
        toolbar.addView(title, new LinearLayout.LayoutParams(0, dp(44), 1));
        Button settings = new Button(this);
        settings.setText("Server");
        settings.setOnClickListener(v -> showServerDialog());
        toolbar.addView(settings, new LinearLayout.LayoutParams(-2, dp(44)));
        root.addView(toolbar);
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(100);
        progressBar.setVisibility(View.GONE);
        root.addView(progressBar, new LinearLayout.LayoutParams(-1, dp(3)));
        statusView = new TextView(this);
        statusView.setTextColor(Color.DKGRAY);
        statusView.setTextSize(14);
        statusView.setGravity(Gravity.CENTER);
        statusView.setPadding(dp(18), dp(16), dp(18), dp(16));
        root.addView(statusView, new LinearLayout.LayoutParams(-1, -2));
        webView = new WebView(this);
        root.addView(webView, new LinearLayout.LayoutParams(-1, 0, 1));
        setContentView(root);
    }

    private void configureWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowFileAccess(false);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) webView.getSettings().setSafeBrowsingEnabled(true);
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, false);
        webView.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                String scheme = uri.getScheme();
                if ("https".equalsIgnoreCase(scheme) || "http".equalsIgnoreCase(scheme)) return false;
                try { startActivity(new Intent(Intent.ACTION_VIEW, uri)); }
                catch (ActivityNotFoundException ignored) { }
                return true;
            }
            @Override public void onPageFinished(WebView view, String url) { statusView.setVisibility(View.GONE); }
            @Override public void onReceivedError(WebView view, WebResourceRequest request, android.webkit.WebResourceError error) {
                if (request.isForMainFrame()) {
                    statusView.setVisibility(View.VISIBLE);
                    statusView.setText("Can't reach the billing server. Check your internet connection or update the server URL using the Server button.");
                }
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(progress);
                progressBar.setVisibility(progress >= 100 ? View.GONE : View.VISIBLE);
            }
            @Override public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> callback, FileChooserParams params) {
                if (fileChooserCallback != null) fileChooserCallback.onReceiveValue(null);
                fileChooserCallback = callback;
                Intent intent = params.createIntent();
                try { startActivityForResult(intent, FILE_CHOOSER_REQUEST); }
                catch (ActivityNotFoundException ex) { fileChooserCallback = null; return false; }
                return true;
            }
        });
        webView.setDownloadListener((url, userAgent, contentDisposition, mimeType, contentLength) -> {
            try {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                String cookie = CookieManager.getInstance().getCookie(url);
                if (cookie != null && !cookie.isEmpty()) request.addRequestHeader("Cookie", cookie);
                request.setMimeType(mimeType);
                String fileName = URLUtil.guessFileName(url, contentDisposition, mimeType);
                request.setTitle(fileName);
                request.setDescription("Downloading billing document");
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                ((DownloadManager) getSystemService(DOWNLOAD_SERVICE)).enqueue(request);
            } catch (Exception ex) {
                statusView.setVisibility(View.VISIBLE);
                statusView.setText("Download could not be started. Please try again.");
            }
        });
    }

    private void showServerDialog() {
        EditText input = new EditText(this);
        input.setSingleLine(true);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
        input.setHint("https://your-billing-domain.example");
        input.setText(preferences.getString(SERVER_URL_KEY, ""));
        new AlertDialog.Builder(this)
            .setTitle("Connect billing server")
            .setMessage("Enter the HTTPS address where your existing Surya Optics billing application is deployed. The APK is a client; it does not run the database/server itself.")
            .setView(input)
            .setCancelable(false)
            .setPositiveButton("Connect", (dialog, which) -> {
                String url = input.getText().toString().trim();
                if (!url.startsWith("https://")) {
                    new AlertDialog.Builder(this).setTitle("HTTPS required")
                        .setMessage("For customer and prescription data safety, use an HTTPS server URL.")
                        .setPositiveButton("OK", (d, w) -> showServerDialog()).show();
                    return;
                }
                preferences.edit().putString(SERVER_URL_KEY, url.replaceAll("/+$", "")).apply();
                loadServer(url);
            })
            .setNegativeButton("Cancel", (dialog, which) -> {
                statusView.setVisibility(View.VISIBLE);
                statusView.setText("Set the server URL to start using billing, customers, prescriptions and reports.");
            }).show();
    }

    private void loadServer(String url) {
        String normalized = url.trim().replaceAll("/+$", "");
        if (!normalized.startsWith("https://")) { showServerDialog(); return; }
        statusView.setVisibility(View.VISIBLE);
        statusView.setText("Connecting to Surya Optics…");
        webView.loadUrl(normalized);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER_REQUEST && fileChooserCallback != null) {
            Uri[] results = WebChromeClient.FileChooserParams.parseResult(resultCode, data);
            fileChooserCallback.onReceiveValue(results);
            fileChooserCallback = null;
        }
    }

    @Override public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }

    private int dp(int value) { return (int) (value * getResources().getDisplayMetrics().density + 0.5f); }

    @Override protected void onDestroy() {
        if (fileChooserCallback != null) fileChooserCallback.onReceiveValue(null);
        if (webView != null) { webView.stopLoading(); webView.destroy(); }
        super.onDestroy();
    }
}
