package br.edu.puccampinas.PI3_ES_2024_Time17;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    private static final int SPLASH_SCREEN_DURATION = 3000; // 3 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Exibir o layout com o logo
        setContentView(R.layout.splash_screen);

        // Agendar uma tarefa para iniciar a MainActivity após um intervalo
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Iniciar a MainActivity
                Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                startActivity(intent);

                // Encerrar a SplashScreenActivity
                finish();
            }
        }, SPLASH_SCREEN_DURATION);
    }
}
