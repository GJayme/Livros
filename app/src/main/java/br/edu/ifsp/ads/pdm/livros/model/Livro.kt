package br.edu.ifsp.ads.pdm.livros.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Livro(
    val titulo: String = "", // chave primária
    val isbn: String = "",
    val primeiroAutor: String = "",
    val editora: String = "",
    val edicao: Int = 0,
    val paginas: Int = 0
): Parcelable

