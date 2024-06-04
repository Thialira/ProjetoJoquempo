package com.example.finalprojoquempo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ModoDeJogo2 extends AppCompatActivity {

    private ImageView imageViewPerfil;
    private TextView textViewNomeJogador;

    private static final int PROFILE_UPDATE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modo_de_jogo);

        imageViewPerfil = findViewById(R.id.imageViewPerfilModoDeJogo);
        textViewNomeJogador = findViewById(R.id.textViewNomeJogador);

        updateProfileData();

        imageViewPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfileMenu();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateProfileData();
    }

    private void updateProfileData() {
        Intent intent = getIntent();
        String imagePath = intent.getStringExtra("imageUri");
        String nomeJogador = intent.getStringExtra("nomeJogador");

        if (imagePath != null) {
            Uri imageUri = Uri.parse(imagePath);
            imageViewPerfil.setImageURI(imageUri);
        } else {
            imageViewPerfil.setImageResource(R.drawable._58275); // Imagem padrão
        }

        if (nomeJogador != null) {
            textViewNomeJogador.setText(nomeJogador);
        }
    }

    private void openProfileMenu() {
        Intent intent = new Intent(this, ProfileMenuActivity2.class);

        String imagePath = getIntent().getStringExtra("imageUri");
        String nomeJogador = getIntent().getStringExtra("nomeJogador");
        String emailJogador = getIntent().getStringExtra("emailJogador");

        intent.putExtra("imageUri", imagePath);
        intent.putExtra("nomeJogador", nomeJogador);
        intent.putExtra("emailJogador", emailJogador);

        startActivityForResult(intent, PROFILE_UPDATE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PROFILE_UPDATE_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                String updatedName = data.getStringExtra("updatedName");
                String updatedEmail = data.getStringExtra("updatedEmail");
                String updatedImageUri = data.getStringExtra("updatedImageUri");

                if (updatedName != null) {
                    textViewNomeJogador.setText(updatedName);
                }
                if (updatedImageUri != null) {
                    Uri imageUri = Uri.parse(updatedImageUri);
                    imageViewPerfil.setImageURI(imageUri);
                }

                getIntent().putExtra("nomeJogador", updatedName);
                getIntent().putExtra("emailJogador", updatedEmail);
                getIntent().putExtra("imageUri", updatedImageUri);
            }
        }
    }

    public void startGameActivity(View view) {
        Intent intent;

        if (view.getId() == R.id.tvVsIa) {
            intent = new Intent(this, Jogo.class);
            intent.putExtra("nomeJogador", getIntent().getStringExtra("nomeJogador")); // Passar o nome do jogador
            startActivity(intent);
        }
    }
}
