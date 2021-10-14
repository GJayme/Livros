package br.edu.ifsp.ads.pdm.livros.controller

import br.edu.ifsp.ads.pdm.livros.MainActivity
import br.edu.ifsp.ads.pdm.livros.model.Livro
import br.edu.ifsp.ads.pdm.livros.model.LivroDao
import br.edu.ifsp.ads.pdm.livros.model.LivroSqlite

class LivroController(mainActivity: MainActivity) {
    private val livroDao: LivroDao = LivroSqlite(mainActivity)

    fun inserirLivro(livro: Livro) = livroDao.criarLivro(livro)
    fun buscarLivro(titulo: String) = livroDao.recuperarLivro(titulo)
    fun buscarLivros() = livroDao.recuperarLivros()
    fun modificarLivro(livro: Livro) = livroDao.atualizarLivro(livro)
    fun apagarLivro(titulo: String) = livroDao.removerLivro(titulo)
}