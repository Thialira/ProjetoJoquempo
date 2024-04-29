package com.example.projoquempo;



import static com.example.projoquempo.R.id.imageViewPerfilModoDeJogo;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ModoDeJogo extends AppCompatActivity {

    private ImageView imageViewPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modo_de_jogo);
        imageViewPerfil = findViewById(imageViewPerfilModoDeJogo);

        // Obter a URI da imagem do perfil da intent
        Uri uriDaImagemPerfil = getIntent().getParcelableExtra("URI_IMAGEM_PERFIL");

        if (uriDaImagemPerfil != null) {
            // Definir a imagem do perfil na imageViewPerfil
            imageViewPerfil.setImageURI(uriDaImagemPerfil);
        }
    }
}
