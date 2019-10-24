package com.khaled.newsmvi.ui.browsenews.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.khaled.newsmvi.data.models.Article
import com.khaled.newsmvi.databinding.ItemArticleBinding

class NewsListAdapter : RecyclerView.Adapter<NewsListAdapter.ViewHolder>() {
    private var articlesList = ArrayList<Article?>()

    fun insertList(articlesList: ArrayList<Article>) {
        this.articlesList.addAll(articlesList)
        this.notifyItemRangeChanged(this.itemCount, articlesList.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemArticleBinding.inflate(inflater)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return articlesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article: Article? = articlesList[position]
        holder.bind(article)
    }

    inner class ViewHolder(private val binding: ItemArticleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(article: Article?) {
            binding.article = article
            binding.executePendingBindings()
        }
    }

}