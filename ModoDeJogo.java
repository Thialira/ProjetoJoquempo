package com.example.finalprojoquempo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;

public class ModoDeJogo extends AppCompatActivity {

    private ImageView imageViewPerfil;
    private static final int PROFILE_UPDATE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_modo_de_jogo);
        imageViewPerfil = findViewById(R.id.imageViewPerfilModoDeJogo);
        TextView textViewNomeJogador = findViewById(R.id.textViewNomeJogador);

        imageViewPerfil.setOnClickListener(v -> openProfileMenu());

        updateProfileData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateProfileData();
    }

    private void updateProfileData() {
        Intent intent = getIntent();
        String imagePath = intent.getStringExtra("imagePath");
        String nomeJogador = intent.getStringExtra("nomeJogador");
        String emailJogador = intent.getStringExtra("emailJogador");

        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            imageViewPerfil.setImageBitmap(bitmap);
        } else {
            imageViewPerfil.setImageResource(R.drawable._58275); // Imagem padrão
        }

        if (nomeJogador != null) {
            TextView textViewNomeJogador = findViewById(R.id.textViewNomeJogador);
            textViewNomeJogador.setText(nomeJogador);
        }
    }

    private void openProfileMenu() {
        Intent intent = new Intent(this, ProfileMenuActivity.class);

        String imagePath = getIntent().getStringExtra("imagePath");
        String nomeJogador = getIntent().getStringExtra("nomeJogador");
        String emailJogador = getIntent().getStringExtra("emailJogador");

        intent.putExtra("imagePath", imagePath);
        intent.putExtra("nomeJogador", nomeJogador);
        intent.putExtra("emailJogador", emailJogador);

        startActivityForResult(intent, PROFILE_UPDATE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PROFILE_UPDATE_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                String updatedName = data.getStringExtra("updatedName");
                String updatedEmail = data.getStringExtra("updatedEmail");
                String updatedImageUri = data.getStringExtra("updatedImageUri");

                if (updatedName != null) {
                    TextView textViewNomeJogador = findViewById(R.id.textViewNomeJogador);
                    textViewNomeJogador.setText(updatedName);
                }

                if (updatedImageUri != null) {
                    Bitmap bitmap = BitmapFactory.decodeFile(updatedImageUri);
                    imageViewPerfil.setImageBitmap(bitmap);
                }

                // Update intent with new data
                Intent intent = getIntent();
                intent.putExtra("imagePath", updatedImageUri);
                intent.putExtra("nomeJogador", updatedName);
                intent.putExtra("emailJogador", updatedEmail);
            }
        }
    }

    // Dentro do método startGameActivity
    public void startGameActivity(View view) {
        Intent intent;

        if (view.getId() == R.id.tvVsIa) {
            intent = new Intent(this, Jogo.class);
            intent.putExtra("nomeJogador", getIntent().getStringExtra("nomeJogador")); // Passar o nome do jogador
            startActivity(intent);
        }
    }
}
