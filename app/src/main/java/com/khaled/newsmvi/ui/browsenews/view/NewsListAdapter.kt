package com.khaled.newsmvi.ui.browsenews.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.khaled.newsmvi.R
import com.khaled.newsmvi.data.models.Article
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.item_article.view.*

class NewsListAdapter : RecyclerView.Adapter<NewsListAdapter.ViewHolder>() {
    private var articlesList = ArrayList<Article?>()

    fun insertList(articlesList: ArrayList<Article>) {
        this.articlesList.addAll(articlesList)
        this.notifyItemRangeChanged(this.itemCount, articlesList.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_article, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return articlesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article: Article? = articlesList[position]
        holder.bind(article)
    }

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        private val mImageView = mView.imageViewPicture
        private val mTextView = mView.textViewTitle
        fun bind(article: Article?) {
            mTextView.text = article?.title
            Picasso.get()
                    .load(article?.urlToImage)
                    .fit()
                    .into(mImageView, object : Callback {
                        override fun onSuccess() {
                        }

                        override fun onError(e: Exception?) {
                            mImageView.setImageResource(R.drawable.no_image)
                        }
                    })
        }
    }

}