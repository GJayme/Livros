package br.edu.ifsp.ads.pdm.livros.model

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class LivroFirebase: LivroDao {
    companion object {
        private val BD_LIVROS = "livros"
    }
    // Referência para o RtDb -> livros
    private val livrosRtDb = Firebase.database.getReference(BD_LIVROS)

    // Lista de livros que simula uma consulta
    private val livrosList: MutableList<Livro> = mutableListOf()
    init {
        livrosRtDb.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val novoLivro: Livro? = snapshot.value as? Livro
                novoLivro?.apply {
                    if (livrosList.find { it.titulo == this.titulo } == null) {
                        livrosList.add(this)
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val livroEditado: Livro? = snapshot.value as? Livro
                livroEditado?.apply {
                    livrosList[livrosList.indexOfFirst { it.titulo == this.titulo}] = this
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val livroRemovido: Livro? = snapshot.value as? Livro
                livroRemovido?.apply {
                    livrosList.remove(this)
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // Não se aplica
            }

            override fun onCancelled(error: DatabaseError) {
                // Não se aplica
            }
        })
        livrosRtDb.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                livrosList.clear()
                snapshot.children.forEach {
                    val livro: Livro = it.getValue<Livro>()?: Livro()
                    livrosList.add(livro)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // não se aplica
            }
        } )
    }

    override fun criarLivro(livro: Livro): Long {
        criarOuAtualizarLivro(livro)
        return 0L
    }

    override fun recuperarLivro(titulo: String): Livro = livrosList.firstOrNull { it.titulo == titulo} ?: Livro()

    override fun recuperarLivros(): MutableList<Livro> = livrosList

    override fun atualizarLivro(livro: Livro): Int {
        criarOuAtualizarLivro(livro)
        return 1
    }

    override fun removerLivro(titulo: String): Int {
        livrosRtDb.child(titulo).removeValue()
        return 1
    }

    private fun criarOuAtualizarLivro(livro: Livro) {
        livrosRtDb.child(livro.titulo).setValue(livro)
    }
}