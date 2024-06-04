package com.example.finalprojoquempo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Cadastro extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;

    private ImageView imageViewPerfil;
    private EditText editTextNome;
    private EditText editTextEmail;
    private EditText editTextSenha;

    private TextInputLayout textInputLayoutSenha;
    private TextInputEditText etSenha;

    private Uri imageUri;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        textInputLayoutSenha = findViewById(R.id.textInputLayoutSenha);
        editTextSenha = findViewById(R.id.etSenha);
        imageViewPerfil = findViewById(R.id.imageViewPerfil);
        editTextNome = findViewById(R.id.etNome);
        editTextEmail = findViewById(R.id.etEmail);
        editTextSenha = findViewById(R.id.etSenha);

        // Configurando o clique na imagem do cadeado
        ImageView imageViewCadeado = findViewById(R.id.desbloquear);
        imageViewCadeado.setOnClickListener(v -> exibirSenha());

        // Inicializar o Parse
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("jZNeP38dSiKrOcBKxgopOpE4XqvzQLtf9S7bvx2o")
                .clientKey("s2Rpwamwz39ADAb0Mq8zJfwy73bbEUZqwXAePMMe")
                .server("https://parseapi.back4app.com/")
                .build());

        // Defina a imagem inicial aqui
        imageViewPerfil.setImageResource(R.drawable._58275);

        imageViewPerfil.setOnClickListener(v -> {
            CharSequence[] options = {"Escolher da Galeria", "Tirar Foto"};
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Cadastro.this);
            builder.setTitle("Escolha uma opção");
            builder.setItems(options, (dialog, which) -> {
                if (which == 0) {
                    escolherImagemDaGaleria();
                } else if (which == 1) {
                    capturarImagemDaCamera();
                }
            });
            builder.show();
        });
    }

    private void exibirSenha() {
        if (editTextSenha.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
            editTextSenha.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            editTextSenha.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        editTextSenha.setSelection(editTextSenha.getText().length());
    }

    private void escolherImagemDaGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void capturarImagemDaCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
        } else {
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                capturarImagemDaCamera();
            } else {
                Toast.makeText(this, "Permissão da câmera negada", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageViewPerfil.setImageURI(imageUri);
        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imageUri = salvarImagemNoCache(bitmap);
            imageViewPerfil.setImageURI(imageUri);
        }
    }

    public void onConfirmarButtonClick(View view) {
        Log.d("Cadastro", "Botão de confirmação clicado");

        String nome = editTextNome.getText().toString();
        String email = editTextEmail.getText().toString();
        String senha = editTextSenha.getText().toString();

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || imageUri == null) {
            Log.d("Cadastro", "Todos os campos são obrigatórios e uma imagem deve ser selecionada");
            return;
        }

        ParseObject jogador = new ParseObject("jogadores");
        jogador.put("nome", nome);
        jogador.put("email", email);
        jogador.put("senha", senha);

        byte[] imageData = getImageDataFromUri(imageUri);
        if (imageData != null) {
            ParseFile parseFile = new ParseFile("profileImage.jpg", imageData);
            jogador.put("imagem", parseFile);
        } else {
            Log.d("Cadastro", "Erro ao converter imagem para bytes");
            return;
        }

        jogador.saveInBackground(e -> {
            if (e == null) {
                Log.d("Cadastro", "Jogador cadastrado com sucesso!");
                Intent intent = new Intent(this, ModoDeJogo2.class);
                intent.putExtra("nomeJogador", nome);
                intent.putExtra("emailJogador", email);
                intent.putExtra("imageUri", imageUri.toString());  // Passe a URI da imagem como string

                startActivity(intent);
                finish();

            } else {
                Log.e("Cadastro", "Erro ao cadastrar jogador: " + e.getMessage());
            }
        });
    }

    private Uri salvarImagemNoCache(Bitmap bitmap) {
        try {
            File cacheDir = getCacheDir();
            File tempFile = new File(cacheDir, "temp_image.jpg");
            FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            return Uri.fromFile(tempFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] getImageDataFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
