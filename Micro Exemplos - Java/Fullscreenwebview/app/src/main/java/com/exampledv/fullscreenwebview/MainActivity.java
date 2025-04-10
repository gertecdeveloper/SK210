package com.exampledv.fullscreenwebview;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        webView = findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = webView.getSettings();
        // Define o nível de zoom inicial (100%)
        webView.setInitialScale(100);
        // Ajusta o nível de zoom da webview para o tamanho da tela
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        webSettings.setJavaScriptEnabled(true); // Ativar JavaScript se o conteúdo for confiável
        webSettings.setAllowFileAccess(true); // Permitir acesso a arquivos
        webSettings.setAllowContentAccess(true); // Permitir acesso a conteúdos
        webSettings.setDomStorageEnabled(true); // Ativar armazenamento DOM, se necessário
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW); // Permitir conteúdo misto


        // Carregar a URL
        webView.loadUrl("https://loja.gertec.com.br/");

        // Ativar o modo de tela cheia
        enterFullScreenMode();

    }

    //Função que deixa a tela cheia, sem aparacer a barra de tarefas
    private void enterFullScreenMode() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }



    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
