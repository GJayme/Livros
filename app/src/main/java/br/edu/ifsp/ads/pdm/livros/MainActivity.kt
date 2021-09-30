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
import br.edu.ifsp.ads.pdm.livros.adapter.LivrosAdapter
import br.edu.ifsp.ads.pdm.livros.databinding.ActivityMainBinding
import br.edu.ifsp.ads.pdm.livros.model.Livro

class MainActivity : AppCompatActivity() {
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
    private val livrosList: MutableList<Livro> = mutableListOf()

    //Adapter genérico
//    private val livrosAdapter: ArrayAdapter<String> by lazy {
//        ArrayAdapter(this, android.R.layout.simple_list_item_1, livrosList.run {
//            val livrosStringList = mutableListOf<String>()
//            this.forEach { livro -> livrosStringList.add(livro.toString()) }
//            livrosStringList
//        })
//    }
    private val livrosAdapter: LivrosAdapter by lazy {
        LivrosAdapter(this, R.layout.layout_livro, livrosList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMainBinding.root)

        //Inicializando lista de livros
        inicializarLivrosList()

        //Associando Adapter ao ListView
        activityMainBinding.LivrosLv.adapter = livrosAdapter

        registerForContextMenu(activityMainBinding.LivrosLv)

        livroActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
            if (resultado.resultCode == RESULT_OK) {
                resultado.data?.getParcelableExtra<Livro>(EXTRA_LIVRO)?.apply {
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
                        livrosList[posicao] = this
                        livrosAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

        activityMainBinding.LivrosLv.setOnItemClickListener { _, _, posicao, _ ->
            val livro = livrosList[posicao]
            val consultarLivrosIntent = Intent(this, LivroActivity::class.java)
            consultarLivrosIntent.putExtra(EXTRA_LIVRO, livro)
            startActivity(consultarLivrosIntent)
        }

        activityMainBinding.adicionarLivroFb.setOnClickListener {
            livroActivityResultLauncher.launch(Intent(this, LivroActivity::class.java))
        }
    }

    private fun inicializarLivrosList() {
        for (indice in 1..10) {
            livrosList.add(
                Livro(
                    "Título ${indice}",
                    "Isbn ${indice}",
                    "Primeiro autor ${indice}",
                    "Editora ${indice}",
                    indice,
                    indice
                )
            )
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.context_menu_main, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {

        val posicao = (item.menuInfo as AdapterView.AdapterContextMenuInfo).position

        return when(item.itemId) {
            R.id.editarLivroMi -> {
                //Editar o livro
                val livro = livrosList[posicao]
                val editarLivroIntent = Intent(this, LivroActivity::class.java)
                editarLivroIntent.putExtra(EXTRA_LIVRO, livro)
                editarLivroIntent.putExtra(EXTRA_POSICAO, posicao)
                editarLivroActivityResultLauncher.launch(editarLivroIntent)

                true
            }
            R.id.removerLivroMi -> {
                livrosList.removeAt(posicao)
                livrosAdapter.notifyDataSetChanged()
                true
            } else -> {
                false
            }
        }
    }
}