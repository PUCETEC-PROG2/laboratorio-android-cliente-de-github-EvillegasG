package ec.edu.uisek.githubclient

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ec.edu.uisek.githubclient.databinding.FragmentRepoItemBinding
import ec.edu.uisek.githubclient.models.Repo

class ReposAdapter(

    private val listener: RepoItemListener
) : RecyclerView.Adapter<ReposAdapter.RepoViewHolder>() {

    private var repositories: List<Repo> = emptyList()

    interface RepoItemListener {
        fun onEditClicked(repo: Repo)
        fun onDeleteClicked(repo: Repo)
    }

    inner class RepoViewHolder(private val binding: FragmentRepoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(repo: Repo) {
            binding.repoName.text = repo.name
            binding.repoDescription.text = repo.description ?: "Sin descripción"
            binding.repoLang.text = repo.language ?: "No especificado"

            Glide.with(binding.root.context)
                .load(repo.owner.avatarUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .circleCrop()
                .into(binding.repoOwnerImage)

            binding.editButton.setOnClickListener {
                listener.onEditClicked(repo)
            }

            binding.deleteButton.setOnClickListener {
                listener.onDeleteClicked(repo)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        val binding =
            FragmentRepoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RepoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        holder.bind(repositories[position])
    }

    override fun getItemCount(): Int = repositories.size

    fun updateRepositories(newRepoList: List<Repo>) {
        repositories = newRepoList
        notifyDataSetChanged()
    }
}
