package com.rifftyo.storyappdicoding.ui.home

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rifftyo.storyappdicoding.data.remote.response.ListStoryItem
import com.rifftyo.storyappdicoding.databinding.ItemStoryBinding
import com.rifftyo.storyappdicoding.ui.detail.DetailActivity
import com.rifftyo.storyappdicoding.utils.StoryDiffCallback

class StoryAdapter(private var listStories: List<ListStoryItem>): RecyclerView.Adapter<StoryAdapter.ListViewHolder>() {
    class ListViewHolder(var binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val story = listStories[position]
        holder.binding.tvTitleItemStory.text = story.name
        holder.binding.tvDescription.text = story.description
        Glide.with(holder.itemView.context)
            .load(story.photoUrl)
            .into(holder.binding.imgItemStory)
        holder.itemView.setOnClickListener {
            val intentDetail = Intent(holder.itemView.context, DetailActivity::class.java)
            intentDetail.putExtra(DetailActivity.EXTRA_ID, story.id)

            val optionsCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    holder.itemView.context as Activity,
                    Pair(holder.binding.imgItemStory, "image"),
                    Pair(holder.binding.tvTitleItemStory, "title"),
                    Pair(holder.binding.tvDescription, "description")
                )
            holder.itemView.context.startActivity(intentDetail, optionsCompat.toBundle())
        }
    }

    override fun getItemCount(): Int = listStories.size

    fun updateListStory(newListStory: List<ListStoryItem>) {
        val diffCallback = StoryDiffCallback(listStories, newListStory)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.listStories = newListStory
        diffResult.dispatchUpdatesTo(this)
    }

}