package br.edu.ifsp.ads.pdm.livros

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.ifsp.ads.pdm.livros.adapter.LivrosRvAdapter
import br.edu.ifsp.ads.pdm.livros.controller.LivroController
import br.edu.ifsp.ads.pdm.livros.databinding.ActivityMainBinding
import br.edu.ifsp.ads.pdm.livros.model.Livro
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), OnLivroClickListener {
    companion object Extras {
        const val EXTRA_LIVRO = "EXTRA_LIVRO"
        const val EXTRA_POSICAO = "EXTRA_POSICAO"
    }
    private val activityMainBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var livroActivityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var editarLivroActivityResultLauncher: ActivityResultLauncher<Intent>

    //Data source
    private val livrosList: MutableList<Livro> by lazy {
        livroController.buscarLivros()
    }

    // Controller
    private val livroController: LivroController by lazy {
        LivroController(this)
    }

    //Adapter
    private val livrosAdapter: LivrosRvAdapter by lazy {
        LivrosRvAdapter(this, livrosList)
    }

    // LayoutManager
    private val livrosLayoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMainBinding.root)

        //Associando Adapter e LayoutManager ao RecycleView
        activityMainBinding.LivrosRv.adapter = livrosAdapter
        activityMainBinding.LivrosRv.layoutManager = livrosLayoutManager

        livroActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
            if (resultado.resultCode == RESULT_OK) {
                resultado.data?.getParcelableExtra<Livro>(EXTRA_LIVRO)?.apply {
                    livroController.inserirLivro(this)
                    livrosList.add(this)
                    livrosAdapter.notifyDataSetChanged()
                }
            }
        }

        editarLivroActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
            if (resultado.resultCode == RESULT_OK) {
                val posicao = resultado.data?.getIntExtra(EXTRA_POSICAO, -1)
                resultado.data?.getParcelableExtra<Livro>(EXTRA_LIVRO)?.apply {
                    if (posicao != null && posicao != -1) {
                        livroController.modificarLivro(this)
                        livrosList[posicao] = this
                        livrosAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

        activityMainBinding.adicionarLivroFb.setOnClickListener {
            livroActivityResultLauncher.launch(Intent(this, LivroActivity::class.java))
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val posicao = livrosAdapter.posicao
        val livro = livrosList[posicao]

        return when(item.itemId) {
            R.id.editarLivroMi -> {
                //Editar o livro
                val editarLivroIntent = Intent(this, LivroActivity::class.java)
                editarLivroIntent.putExtra(EXTRA_LIVRO, livro)
                editarLivroIntent.putExtra(EXTRA_POSICAO, posicao)
                editarLivroActivityResultLauncher.launch(editarLivroIntent)

                true
            }
            R.id.removerLivroMi -> {
                // Remover o livro
                with(AlertDialog.Builder(this)) {
                    setMessage("Confirmar remoção?")
                    setPositiveButton("Sim") { _, _ ->
                        livroController.apagarLivro(livro.titulo)
                        livrosList.removeAt(posicao)
                        livrosAdapter.notifyDataSetChanged()
                        Snackbar.make(activityMainBinding.root, "Livro removido", Snackbar.LENGTH_SHORT).show()
                    }
                    setNegativeButton("Não") { _, _ ->
                        Snackbar.make(activityMainBinding.root, "Remoção cancelada", Snackbar.LENGTH_SHORT).show()
                    }
                    create()
                }.show()

                true
            } else -> { false }
        }
    }

    override fun onLivroClick(posicao: Int) {
        val livro = livrosList[posicao]
        val consultarLivrosIntent = Intent(this, LivroActivity::class.java)
        consultarLivrosIntent.putExtra(EXTRA_LIVRO, livro)
        startActivity(consultarLivrosIntent)
    }
}