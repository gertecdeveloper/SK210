package com.exampledv.myscanfebran;

import static androidx.core.view.WindowCompat.getInsetsController;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class FullScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configuração para o full screen
        ativarFullScreen();
    }

    private void ativarFullScreen() {
        // Usando o WindowInsetsControllerCompat para configurar o modo de tela cheia
        WindowInsetsControllerCompat windowInsetsControllerCompat =
                getInsetsController(getWindow(), getWindow().getDecorView());

        // Definindo o comportamento das barras de sistema
        windowInsetsControllerCompat.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        // Ocultando as barras do sistema (barra de status e barra de navegação)
        windowInsetsControllerCompat.hide(WindowInsetsCompat.Type.systemBars());
    }
}
