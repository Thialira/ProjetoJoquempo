package com.example.finalprojoquempo;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class Jogo extends AppCompatActivity {

    private int rodadasJogadas = 0;
    private int vitoriasUsuario = 0;
    private int vitoriasApp = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jogo);

        // Recebe os dados passados do Login
        Intent intent = getIntent();
        String nomeJogador = intent.getStringExtra("nomeJogador");

        // Oculta os elementos do placar no início
        TextView textPlacar = findViewById(R.id.textPlacar);
        textPlacar.setVisibility(View.GONE);

        // Agora você pode usar nomeJogador e emailJogador conforme necessário no seu jogo

        ImageView compartilharImageView = findViewById(R.id.Compartilhar);
        compartilharImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compartilharResultado(nomeJogador); // Passar o nome do jogador ao compartilhar
            }
        });

        ImageView reiniciarImageView = findViewById(R.id.Reiniciar);
        reiniciarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reiniciarJogo(nomeJogador); // Passar o nome do jogador ao reiniciar
            }
        });

        // Adicionar a ação de fechar a atividade e ir para MainActivity
        ImageView imageSair = findViewById(R.id.imageSair);
        imageSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Jogo.this, MainActivity.class);
                startActivity(intent);
                finish(); // Fecha a atividade atual
            }
        });


    }

    private void compartilharResultado(String nomeJogador) {
        // Construir a mensagem a ser compartilhada
        String mensagem = "Placar final:\n";
        mensagem += nomeJogador + ": " + vitoriasUsuario + "\n";
        mensagem += "App: " + vitoriasApp + "\n";

        if (vitoriasUsuario > vitoriasApp) {
            mensagem += "Parabéns!!";
        } else if (vitoriasUsuario < vitoriasApp) {
            mensagem += "Não foi dessa vez, App ganhou! :(";
        } else {
            mensagem += "Empatamos!!";
        }

        // Criar um Intent para compartilhamento
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, mensagem);

        // Iniciar a atividade de compartilhamento
        startActivity(Intent.createChooser(intent, "Compartilhar via"));
    }

    private void reiniciarJogo(String nomeJogador) {
        rodadasJogadas = 0;
        vitoriasUsuario = 0;
        vitoriasApp = 0;

        TextView textResultado = findViewById(R.id.textResultado);
        TextView textVencedor = findViewById(R.id.textVencedor);
        textResultado.setText(nomeJogador + ", escolha uma opção");
        textVencedor.setVisibility(View.GONE);

        ImageView imagePedra = findViewById(R.id.imagePedra);
        ImageView imagePapel = findViewById(R.id.imagePapel);
        ImageView imageTesoura = findViewById(R.id.imageTesoura);
        imagePedra.setAlpha(1f);
        imagePapel.setAlpha(1f);
        imageTesoura.setAlpha(1f);

        ImageView imageResultado = findViewById(R.id.imageResultado);
        imageResultado.setImageResource(R.drawable.padrao);

        TextView textPlacar = findViewById(R.id.textPlacar);
        textPlacar.setVisibility(View.GONE);
    }


    public void selecionadoPedra(View view) {
        this.opcaoSelecionada("pedra");
    }

    public void selecionadoPapel(View view) {
        this.opcaoSelecionada("papel");
    }

    public void selecionadoTesoura(View view) {
        this.opcaoSelecionada("tesoura");
    }

    public void opcaoSelecionada(String opcaoSelecionada) {
        if (rodadasJogadas < 4) {
            rodadasJogadas++;

            Intent intent = getIntent();
            String nomeJogador = intent.getStringExtra("nomeJogador");

            ImageView imageResultado = findViewById(R.id.imageResultado);
            TextView textResultado = findViewById(R.id.textResultado);
            TextView textPlacar = findViewById(R.id.textPlacar);
            textPlacar.setVisibility(View.VISIBLE);

            int numero = new Random().nextInt(3);
            String[] opcoes = {"pedra", "papel", "tesoura"};
            String opcaoApp = opcoes[numero];

            switch (opcaoApp) {
                case "pedra":
                    imageResultado.setImageResource(R.drawable.pedra);
                    break;

                case "papel":
                    imageResultado.setImageResource(R.drawable.papel);
                    break;

                case "tesoura":
                    imageResultado.setImageResource(R.drawable.tesoura);
                    break;
            }

            switch (opcaoSelecionada) {
                case "pedra":
                    findViewById(R.id.imagePedra).setAlpha(0.5f);
                    break;

                case "papel":
                    findViewById(R.id.imagePapel).setAlpha(0.5f);
                    break;

                case "tesoura":
                    findViewById(R.id.imageTesoura).setAlpha(0.5f);
                    break;
            }

            if ((opcaoApp.equals("tesoura") && opcaoSelecionada.equals("papel")) ||
                    (opcaoApp.equals("papel") && opcaoSelecionada.equals("pedra")) ||
                    (opcaoApp.equals("pedra") && opcaoSelecionada.equals("tesoura"))) {
                vitoriasApp++;
                textResultado.setText(nomeJogador + ", você perdeu nesta rodada!!");
            } else if ((opcaoApp.equals("tesoura") && opcaoSelecionada.equals("pedra")) ||
                    (opcaoApp.equals("papel") && opcaoSelecionada.equals("tesoura")) ||
                    (opcaoApp.equals("pedra") && opcaoSelecionada.equals("papel"))) {
                vitoriasUsuario++;
                textResultado.setText(nomeJogador + ", você ganhou nesta rodada!!");
            } else {
                textResultado.setText(nomeJogador + ", empatamos nesta rodada!!");
            }

            textPlacar.setText(nomeJogador + ": " + vitoriasUsuario + " x " + "App: " + vitoriasApp);

            if (rodadasJogadas == 4) {
                TextView textVencedor = findViewById(R.id.textVencedor);
                if (vitoriasUsuario > vitoriasApp) {
                    textVencedor.setText(nomeJogador + ", você ganhou o jogo com " + vitoriasUsuario + " vitórias!");
                } else if (vitoriasUsuario < vitoriasApp) {
                    textVencedor.setText(nomeJogador + ", você perdeu o jogo com " + vitoriasApp + " vitórias do app!");
                } else {
                    textVencedor.setText(nomeJogador + ", o jogo empatou!");
                }
            }
        }
    }


}
