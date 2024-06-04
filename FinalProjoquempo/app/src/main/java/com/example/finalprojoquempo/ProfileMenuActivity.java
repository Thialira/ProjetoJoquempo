package com.example.finalprojoquempo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileMenuActivity extends AppCompatActivity {

    private CircleImageView profileImage;
    private TextView profileName;
    private TextView profileEmail;
    private Button confirmButton;
    private ImageView editName;
    private ImageView editEmail;

    private static final int PICK_IMAGE = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private Uri imageUri;
    private String imagePath;
    private String emailJogador;

    private static final int PROFILE_UPDATE_REQUEST = 1;

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

        imagePath = getIntent().getStringExtra("imagePath");
        String nomeJogador = getIntent().getStringExtra("nomeJogador");
        emailJogador = getIntent().getStringExtra("emailJogador");

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

        profileImage.setOnClickListener(v -> openImageChooser());
        editName.setOnClickListener(v -> showEditDialog(profileName, "Editar Nome"));
        editEmail.setOnClickListener(v -> showEditDialog(profileEmail, "Editar Email"));

        confirmButton.setOnClickListener(v -> saveUserData());
    }

    private void openImageChooser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Escolher Imagem");
        builder.setItems(new CharSequence[]{"Galeria", "Câmera"}, (dialog, which) -> {
            switch (which) {
                case 0:
                    openGallery();
                    break;
                case 1:
                    dispatchTakePictureIntent();
                    break;
            }
        });
        builder.show();
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        try {
            startActivityForResult(gallery, PICK_IMAGE);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Galeria de fotos não encontrada", Toast.LENGTH_SHORT).show();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.finalprojoquempo.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        imagePath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE && data != null) {
                imageUri = data.getData();
                profileImage.setImageURI(imageUri);
                imagePath = getRealPathFromUri(imageUri);
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                // Atualiza o URI da imagem para o caminho do arquivo da imagem capturada pela câmera
                imageUri = Uri.parse(imagePath);
                profileImage.setImageURI(imageUri);
                // Salva a imagem localmente antes de enviá-la para o back4app
                saveImageLocally(imagePath);
            }
        }
    }

    private void saveImageLocally(String imagePath) {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            FileOutputStream fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getRealPathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return uri.getPath();
    }

    private void showEditDialog(TextView textView, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(textView.getText().toString());
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> textView.setText(input.getText().toString()));
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void saveUserData() {
        String updatedName = profileName.getText().toString();
        String updatedEmail = profileEmail.getText().toString();

        ParseQuery<ParseObject>query = ParseQuery.getQuery("jogadores");
        query.whereEqualTo("email", emailJogador);
        query.getFirstInBackground((jogador, e) -> {
            if (e == null && jogador != null) {
                jogador.put("nome", updatedName);
                jogador.put("email", updatedEmail);

                if (imageUri != null) {
                    // Verifica se a imagem foi salva localmente
                    if (imagePath != null && !imagePath.isEmpty()) {
                        try {
                            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                            byte[] byteArray = getBytesFromBitmap(bitmap);
                            ParseFile file = new ParseFile("profileImage.jpg", byteArray);
                            jogador.put("imagem", file);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }

                jogador.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException ex) {
                        if (ex == null) {
                            Toast.makeText(ProfileMenuActivity.this, "Dados salvos com sucesso!", Toast.LENGTH_SHORT).show();

                            // Envia os dados atualizados de volta para a tela ModoDeJogo
                            Intent intent = new Intent();
                            intent.putExtra("updatedName", updatedName);
                            intent.putExtra("updatedEmail", updatedEmail);
                            intent.putExtra("updatedImageUri", imagePath);
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            Toast.makeText(ProfileMenuActivity.this, "Erro ao salvar dados: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(ProfileMenuActivity.this, "Erro ao encontrar jogador: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProfileImage(String email) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("jogadores");
        query.whereEqualTo("email", email);
        query.getFirstInBackground((jogador, e) -> {
            if (e == null && jogador != null) {
                ParseFile imageFile = jogador.getParseFile("imagem");
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
                Toast.makeText(ProfileMenuActivity.this, "Erro ao carregar imagem do perfil: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

