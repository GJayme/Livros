package br.edu.ifsp.ads.pdm.livros;

import static br.edu.ifsp.ads.pdm.livros.MainActivity.EXTRA_LIVRO;
import static br.edu.ifsp.ads.pdm.livros.MainActivity.EXTRA_POSICAO;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import br.edu.ifsp.ads.pdm.livros.databinding.ActivityLivroBinding;
import br.edu.ifsp.ads.pdm.livros.model.Livro;

public class LivroActivity extends AppCompatActivity {
    private ActivityLivroBinding activityLivroBinding;
    private int posicao = -1;
    private Livro livro;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLivroBinding = ActivityLivroBinding.inflate(getLayoutInflater());
        setContentView(activityLivroBinding.getRoot());

        activityLivroBinding.salvarBt.setOnClickListener(
                (View view) -> {
                    livro = new Livro(
                            activityLivroBinding.tituloEt.getText().toString(),
                            activityLivroBinding.isbnEt.getText().toString(),
                            activityLivroBinding.primeiroAutorEt.getText().toString(),
                            activityLivroBinding.editoraEt.getText().toString(),
                            Integer.parseInt(activityLivroBinding.edicaoEt.getText().toString()),
                            Integer.parseInt(activityLivroBinding.paginasEt.getText().toString())
                    );

                    Intent resultadoIntent = new Intent();
                    resultadoIntent.putExtra(EXTRA_LIVRO, livro);
                    if (posicao != -1) {
                        resultadoIntent.putExtra(EXTRA_POSICAO, posicao);
                    }
                    setResult(RESULT_OK, resultadoIntent);
                    finish();
                }
        );
        //Verificar se é uma edição e preenchendo os campos
        posicao = getIntent().getIntExtra(EXTRA_POSICAO, -1);
        Livro livro = getIntent().getParcelableExtra(EXTRA_LIVRO);
        if (livro != null) {
            activityLivroBinding.tituloEt.setEnabled(false);
            activityLivroBinding.tituloEt.setText(livro.getTitulo());
            activityLivroBinding.isbnEt.setText(livro.getIsbn());
            activityLivroBinding.primeiroAutorEt.setText(livro.getPrimeiroAutor());
            activityLivroBinding.editoraEt.setText(livro.getEditora());
            activityLivroBinding.edicaoEt.setText(String.valueOf(livro.getEdicao()));
            activityLivroBinding.paginasEt.setText(String.valueOf(livro.getPaginas()));
            if (posicao == -1) {
                for (int i = 0; i < activityLivroBinding.getRoot().getChildCount(); i++) {
                     activityLivroBinding.getRoot().getChildAt(i).setEnabled(false);
                }
                activityLivroBinding.salvarBt.setVisibility(View.GONE);
            }
        }
    }
}