package com.example.projoquempo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayOutputStream;

public class Cadastro extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageViewPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastro);
        imageViewPerfil = findViewById(R.id.imageViewPerfil);

        // Defina a imagem inicial aqui
        imageViewPerfil.setImageResource(R.drawable._58275);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void onPerfilImageClick(View view) {
        // Inicia a galeria de imagens para selecionar uma imagem
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // A imagem foi selecionada com sucesso
            Uri uri = data.getData();
            imageViewPerfil.setImageURI(uri);
        }
    }

    public void onConfirmarButtonClick(View view) {
        // Salvar as informações preenchidas pelo usuário aqui

        // Obter a URI da imagem selecionada
        Uri uriDaImagemSelecionada = obterUriDaImagemSelecionada();

        // Ir para a tela de modo de jogo e passar a URI da imagem selecionada como um extra
        Intent intent = new Intent(this, ModoDeJogo.class);
        intent.putExtra("URI_IMAGEM_PERFIL", uriDaImagemSelecionada);
        startActivity(intent);
    }


    private Uri obterUriDaImagemSelecionada() {
        // Verificar se a imageViewPerfil tem uma imagem atribuída
        Drawable drawable = imageViewPerfil.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            // Converte o bitmap em URI
            return getImageUri(getApplicationContext(), bitmap);
        } else {
            return null;
        }
    }

    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

}

