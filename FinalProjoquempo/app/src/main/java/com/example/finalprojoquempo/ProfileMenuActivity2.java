package com.example.finalprojoquempo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class ProfileMenuActivity2 extends AppCompatActivity {

    private CircleImageView profileImage;
    private TextView profileName;
    private TextView profileEmail;
    private Button confirmButton;
    private ImageView editName;
    private ImageView editEmail;

    private static final int PICK_IMAGE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;

    private Uri imageUri;
    private String imagePath;
    private String emailJogador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_menu);

        profileImage = findViewById(R.id.profileImage);
        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);
        confirmButton = findViewById(R.id.confirmButton);
        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);

        // Receber dados do Intent
        imagePath = getIntent().getStringExtra("imageUri");
        String nomeJogador = getIntent().getStringExtra("nomeJogador");
        emailJogador = getIntent().getStringExtra("emailJogador");

        // Verificar permissões de armazenamento
        if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }

        // Carregar imagem do perfil do jogador do banco de dados
        loadProfileImage(emailJogador);

        if (imagePath != null) {
            profileImage.setImageURI(Uri.parse(imagePath));
        } else {
            profileImage.setImageResource(R.drawable._58275);
        }

        if (nomeJogador != null) {
            profileName.setText(nomeJogador);
        } else {
            profileName.setText("Nome não encontrado");
        }

        if (emailJogador != null) {
            profileEmail.setText(emailJogador);
        } else {
            profileEmail.setText("Email não encontrado");
        }

        // Adicionar listeners para edição
        profileImage.setOnClickListener(v -> openGallery());
        editName.setOnClickListener(v -> showEditDialog(profileName, "Editar Nome"));
        editEmail.setOnClickListener(v -> showEditDialog(profileEmail, "Editar Email"));

        // Adicionar listener de clique para o botão de confirmação
        confirmButton.setOnClickListener(v -> saveUserData());
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        try {
            startActivityForResult(gallery, PICK_IMAGE);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Galeria de fotos não encontrada", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                imageUri = data.getData();
                if (imageUri != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                        File tempFile = saveImageTemporarily(bitmap);
                        if (tempFile != null) {
                            imageUri = Uri.fromFile(tempFile);
                            profileImage.setImageURI(imageUri);
                            imagePath = imageUri.toString();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void showEditDialog(TextView textView, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(textView.getText().toString());
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> textView.setText(input.getText().toString()));
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void saveUserData() {
        String updatedName = profileName.getText().toString();
        String updatedEmail = profileEmail.getText().toString();

        // Atualizar dados no Parse
        ParseQuery<ParseObject> query = ParseQuery.getQuery("jogadores");
        query.whereEqualTo("email", emailJogador);
        query.getFirstInBackground((jogador, e) -> {
            if (e == null && jogador != null) {
                jogador.put("nome", updatedName);
                jogador.put("email", updatedEmail);

                if (imageUri != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                        byte[] data = getBytesFromBitmap(bitmap);
                        ParseFile file = new ParseFile("profileImage.jpg", data);
                        jogador.put("imagem", file);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }

                jogador.saveInBackground(ex -> {
                    if (ex == null) {
                        Toast.makeText(ProfileMenuActivity2.this, "Dados salvos com sucesso!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent();
                        intent.putExtra("updatedName", updatedName);
                        intent.putExtra("updatedEmail", updatedEmail);
                        intent.putExtra("updatedImageUri", imagePath);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        Toast.makeText(ProfileMenuActivity2.this, "Erro ao salvar dados: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(ProfileMenuActivity2.this, "Erro ao encontrar jogador: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProfileImage(String email) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("jogadores");
        query.whereEqualTo("email", email);
        query.getFirstInBackground((jogador, e) -> {
            if (e == null && jogador != null) {
                ParseFile imageFile = (ParseFile) jogador.get("imagem");
                if (imageFile != null) {
                    imageFile.getDataInBackground((data, e1) -> {
                        if (e1 == null && data != null) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                            profileImage.setImageBitmap(bitmap);
                        } else {
                            e1.printStackTrace();
                        }
                    });
                }
            } else {
                Toast.makeText(ProfileMenuActivity2.this, "Erro ao carregar imagem do perfil: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    private File saveImageTemporarily(Bitmap bitmap) {
        try {
            File cachePath = new File(getCacheDir(), "tempImage.jpg");
            cachePath.createNewFile();
            FileOutputStream stream = new FileOutputStream(cachePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.close();
            return cachePath;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
