package com.rifftyo.storyappdicoding.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rifftyo.storyappdicoding.data.local.story.StoryEntity
import com.rifftyo.storyappdicoding.databinding.ItemStoryBinding
import com.rifftyo.storyappdicoding.ui.detail.DetailActivity

class StoryAdapter: PagingDataAdapter<StoryEntity, StoryAdapter.ListViewHolder>(
    DIFF_CALLBACK
) {
    class ListViewHolder(private var binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: StoryEntity) {
            binding.tvTitleItemStory.text = story.name
            binding.tvDescription.text = story.description
            Glide.with(itemView.context)
                .load(story.photoUrl)
                .into(binding.imgItemStory)
            itemView.setOnClickListener {
                val intentDetail = Intent(itemView.context, DetailActivity::class.java)
                intentDetail.putExtra(DetailActivity.EXTRA_ID, story.id)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.imgItemStory, "image"),
                        Pair(binding.tvTitleItemStory, "title"),
                        Pair(binding.tvDescription, "description")
                    )
                itemView.context.startActivity(intentDetail, optionsCompat.toBundle())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryEntity>() {
            override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}