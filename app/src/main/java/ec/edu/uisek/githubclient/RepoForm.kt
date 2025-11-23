package ec.edu.uisek.githubclient

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ec.edu.uisek.githubclient.databinding.ActivityRepoFormBinding

class RepoForm : AppCompatActivity() {

    private lateinit var repoFormBinding: ActivityRepoFormBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repoFormBinding = ActivityRepoFormBinding.inflate(layoutInflater)
        setContentView(repoFormBinding.root)
        repoFormBinding.cancelButton.setOnClickListener {
            finish()
        }
        repoFormBinding.saveButton.setOnClickListener {
            createRepo()
        }
    }

    private fun validateForm(): Boolean {
        val repoName = repoFormBinding.repoNameInput.text.toString()

        if (repoName.isBlank()) {
            repoFormBinding.repoNameInput.error="El nombre del repositorio es requerido"
            return false
        }
        if (repoName.contains(" ")) {
            repoFormBinding.repoNameInput.error="El nombre del repositorio no puede contener espacios"
            return false
        }

        return true
        }

    private fun createRepo() {
        if (!validateForm()) {
            return
        }
    }
}
