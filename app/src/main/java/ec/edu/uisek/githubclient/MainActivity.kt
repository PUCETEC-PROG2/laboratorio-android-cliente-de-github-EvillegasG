package ec.edu.uisek.githubclient

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import ec.edu.uisek.githubclient.databinding.ActivityMainBinding
import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.models.UpdateRepoBody
import ec.edu.uisek.githubclient.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity(), ReposAdapter.RepoItemListener {
    private lateinit var binding: ActivityMainBinding

    private val reposAdapter = ReposAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.newRepoFab.setOnClickListener {
            displayNewRepoForm()
        }

        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        fetchRepositories()
    }

    private fun setupRecyclerView() {
        binding.repoRecyclerView.adapter = reposAdapter
    }

    private fun fetchRepositories() {
        val apiService = RetrofitClient.gitHubApiService
        val call = apiService.getRepos()

        call.enqueue(object : Callback<List<Repo>> {
            override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
                if (response.isSuccessful) {
                    val repos = response.body()
                    if (repos != null && repos.isNotEmpty()) {
                        reposAdapter.updateRepositories(repos)
                    } else {
                        showMessage(msg = "Usted no tiene repositorios")
                    }
                } else {
                    val errorMsg = when (response.code()) {
                        401 -> "No autorizado"
                        403 -> "Prohibido"
                        404 -> "No encontrado"
                        else -> "Error: ${response.code()}"
                    }
                    Log.e("MainActivity", "Error: $errorMsg")
                    showMessage(msg = "Error: $errorMsg")
                }
            }

            override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
                showMessage(msg = "Error: Error de Conexión")
                Log.e("MainActivity", "Error: ${t.message}")
            }
        })
    }

    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    private fun displayNewRepoForm() {
        Intent(this, RepoForm::class.java).apply {
            startActivity(this)
        }
    }


    override fun onEditClicked(repo: Repo) {

        val editText = EditText(this)
        editText.setText(repo.description)


        AlertDialog.Builder(this)
            .setTitle("Editar Repositorio")
            .setMessage("Modifica la descripción de '${repo.name}'")
            .setView(editText)
            .setPositiveButton("Guardar") { _, _ ->
                val newDescription = editText.text.toString()
                if (newDescription != repo.description) {
                    updateRepository(repo, newDescription)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }


    override fun onDeleteClicked(repo: Repo) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Eliminación")
            .setMessage("¿Estás seguro de que quieres eliminar el repositorio '${repo.name}'? Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                deleteRepository(repo)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }


    private fun updateRepository(repo: Repo, newDescription: String) {
        val owner = repo.owner.login
        val repoName = repo.name

        val updateBody = UpdateRepoBody(description = newDescription)

        val call = RetrofitClient.gitHubApiService.updateRepository(owner, repoName, updateBody)

        call.enqueue(object : Callback<Repo> {
            override fun onResponse(call: Call<Repo>, response: Response<Repo>) {
                if (response.isSuccessful) {
                    showMessage("Repositorio actualizado con éxito")
                    fetchRepositories()
                } else {
                    showMessage("Error al actualizar: ${response.code()}")
                    Log.e("MainActivity", "Error al actualizar: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Repo>, t: Throwable) {
                showMessage("Fallo de conexión al actualizar")
            }
        })
    }


    private fun deleteRepository(repo: Repo) {
        val owner = repo.owner.login
        val repoName = repo.name

        val call = RetrofitClient.gitHubApiService.deleteRepository(owner, repoName)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    showMessage("Repositorio eliminado con éxito")
                    fetchRepositories()
                } else {
                    showMessage("Error al eliminar: ${response.code()}")
                    Log.e("MainActivity", "Error al eliminar: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showMessage("Fallo de conexión al eliminar")
            }
        })
    }
}
