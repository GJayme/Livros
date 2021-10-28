package br.edu.ifsp.ads.pdm.livros.model

import android.content.ContentValues
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import br.edu.ifsp.ads.pdm.livros.R

class LivroSqlite(contexto: Context): LivroDao  {
    companion object {
        private val BD_LIVROS = "livros"
        private val TABELA_LIVRO = "livro"
        private val COLUNA_TITULO = "titulo"
        private val COLUNA_ISBN = "isbn"
        private val COLUNA_PRIMEIRO_AUTOR = "primeiro_autor"
        private val COLUNA_EDITORA = "editora"
        private val COLUNA_EDICAO = "edicao"
        private val COLUNA_PAGINAS = "paginas"

        private val CRIAR_TABELA_LIVROS_STMT = "CREATE TABLE IF NOT EXISTS ${TABELA_LIVRO} (" +
                "${COLUNA_TITULO} TEXT NOT NULL PRIMARY KEY, " +
                "${COLUNA_ISBN} TEXT NOT NULL, " +
                "${COLUNA_PRIMEIRO_AUTOR} TEXT NOT NULL, " +
                "${COLUNA_EDITORA}  TEXT NOT NULL, " +
                "${COLUNA_EDICAO} INTEGER NOT NULL, " +
                "${COLUNA_PAGINAS} INTEGER NOT NULL );"
    }

    // Referencia para o banco de dados
    private val livrosBd: SQLiteDatabase =
        contexto.openOrCreateDatabase(BD_LIVROS, MODE_PRIVATE, null)

    init {
        try {
            livrosBd.execSQL(CRIAR_TABELA_LIVROS_STMT)
        } catch (se: SQLException) {
            Log.e(contexto.getString(R.string.app_name), se.toString())
        }
    }

    override fun criarLivro(livro: Livro): Long {
        val livroCv: ContentValues = converterLivroParaContetValues(livro)

        return livrosBd.insert(TABELA_LIVRO, null, livroCv)
    }

    override fun recuperarLivro(titulo: String): Livro {
        val livroCursor = livrosBd.query(
            true, //distinct
            TABELA_LIVRO, //tabela
            null, //colunas(todas)
            "${COLUNA_TITULO} = ?", //where
            arrayOf(titulo), //valores do where
            null,
            null,
            null,
            null
        )

        return if (livroCursor.moveToFirst()) {
            with(livroCursor) {
                Livro(
                    getString(getColumnIndexOrThrow(COLUNA_TITULO)),
                    getString(getColumnIndexOrThrow(COLUNA_ISBN)),
                    getString(getColumnIndexOrThrow(COLUNA_PRIMEIRO_AUTOR)),
                    getString(getColumnIndexOrThrow(COLUNA_EDITORA)),
                    getInt(getColumnIndexOrThrow(COLUNA_EDICAO)),
                    getInt(getColumnIndexOrThrow(COLUNA_PAGINAS))
                )
            }
        } else {
            Livro()
        }
    }

    override fun recuperarLivros(): MutableList<Livro> {
        var livros: MutableList<Livro> = ArrayList()
        val livroCursor = livrosBd.rawQuery("SELECT * FROM ${TABELA_LIVRO}",null);

        if (livroCursor.moveToFirst()) {
            while (!livroCursor.isAfterLast) {
                val livro: Livro = Livro(
                    livroCursor.getString(livroCursor.getColumnIndexOrThrow(COLUNA_TITULO)),
                    livroCursor.getString(livroCursor.getColumnIndexOrThrow(COLUNA_ISBN)),
                    livroCursor.getString(livroCursor.getColumnIndexOrThrow(COLUNA_PRIMEIRO_AUTOR)),
                    livroCursor.getString(livroCursor.getColumnIndexOrThrow(COLUNA_EDITORA)),
                    livroCursor.getInt(livroCursor.getColumnIndexOrThrow(COLUNA_EDICAO)),
                    livroCursor.getInt(livroCursor.getColumnIndexOrThrow(COLUNA_PAGINAS)),
                )
                livros.add(livro)
                livroCursor.moveToNext()
            }
        }
        return livros;
    }

    override fun atualizarLivro(livro: Livro): Int {
        val livroCv: ContentValues = converterLivroParaContetValues(livro)

        return livrosBd.update(TABELA_LIVRO, livroCv, "${COLUNA_TITULO} = ?", arrayOf(livro.titulo))
    }

    override fun removerLivro(titulo: String): Int {
        return livrosBd.delete(TABELA_LIVRO, "${COLUNA_TITULO} = ?", arrayOf(titulo))
    }

    private fun converterLivroParaContetValues(livro: Livro): ContentValues {
        val livroCv: ContentValues = ContentValues()
        livroCv.put(COLUNA_TITULO, livro.titulo)
        livroCv.put(COLUNA_ISBN, livro.isbn)
        livroCv.put(COLUNA_PRIMEIRO_AUTOR, livro.primeiroAutor)
        livroCv.put(COLUNA_EDITORA, livro.editora)
        livroCv.put(COLUNA_EDICAO, livro.edicao)
        livroCv.put(COLUNA_PAGINAS, livro.paginas)
        return livroCv
    }
}