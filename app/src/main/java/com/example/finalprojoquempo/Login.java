package com.example.finalprojoquempo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.parse.GetCallback;
import com.parse.GetDataCallback;

import com.parse.Parse;
import com.parse.ParseException;


import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class Login extends AppCompatActivity {

    private EditText etEmail;
    private EditText etSenha;
    private ImageView imageView;
    private boolean senhaVisivel = false;
    private int REQUEST_CODE;

    // Dentro do método onCreate da LoginActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Verifica se o usuário já está autenticado
        if (ParseUser.getCurrentUser() != null) {
            // Se o usuário estiver logado, redirecione para a próxima atividade
            startActivity(new Intent(this, ModoDeJogo.class));
            finish(); // Encerra a atividade de login para que o usuário não possa voltar para ela
        } else {
            // Se o usuário não estiver logado, continue com a atividade de login normalmente
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_login);

            // Restante do código de inicialização da atividade de login...
            etEmail = findViewById(R.id.etEmail);
            etSenha = findViewById(R.id.etSenha);
            imageView = findViewById(R.id.imageView);

            // Define a senha como oculta por padrão
            etSenha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

            // Adiciona um OnClickListener à ImageView
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toggle de visibilidade da senha
                    senhaVisivel = !senhaVisivel;
                    etSenha.setInputType(senhaVisivel ?
                            InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                            InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                    // Move o cursor para o final do texto
                    etSenha.setSelection(etSenha.getText().length());
                }
            });

            // Ajusta o padding para a área dos sistemas
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
    }


    public void onConfirmarButtonClick(View view) {
        Log.d("LoginActivity", "Botão de confirmação clicado");

        String email = etEmail.getText().toString();
        String senha = etSenha.getText().toString();

        Log.d("LoginActivity", "Email: " + email);
        Log.d("LoginActivity", "Senha: " + senha);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("jogadores");
        query.whereEqualTo("email", email);
        query.whereEqualTo("senha", senha);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject jogador, ParseException e) {
                if (e == null && jogador != null) {
                    String nomeJogador = jogador.getString("nome");
                    ParseFile imagem = jogador.getParseFile("imagem");
                    if (imagem != null) {
                        imagem.getDataInBackground(new GetDataCallback() {
                            public void done(byte[] data, ParseException e) {
                                if (e == null && data != null) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    File imageFile = saveImageTemporarily(bitmap);
                                    if (imageFile != null) {
                                        Intent intent = new Intent(Login.this, ModoDeJogo.class);
                                        intent.putExtra("imagePath", imageFile.getAbsolutePath());
                                        intent.putExtra("nomeJogador", nomeJogador);
                                        intent.putExtra("emailJogador", email); // Passando o email do jogador
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // Tratar erro ao salvar a imagem temporariamente
                                    }
                                } else {
                                    // Tratar erro ao recuperar os dados da imagem
                                }
                            }
                        });
                    } else {
                        // Imagem do jogador não encontrada
                        Log.e("LoginActivity", "Imagem do jogador não encontrada");
                    }
                } else {
                    // Usuário não encontrado ou erro ao consultar
                    Log.e("LoginActivity", "Falha ao consultar o usuário: " + e.getMessage());
                }
            }
        });
    }


    // Método para salvar a imagem temporariamente em um arquivo
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




