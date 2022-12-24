package jafar.stories.features.main.home

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import jafar.stories.data.model.ListStory
import jafar.stories.databinding.StoryItemBinding
import jafar.stories.features.main.detail.DetailStoryActivity
import jafar.stories.utils.Constanta
import jafar.stories.utils.getTimelineUpload

class StoryItemAdapter :
    PagingDataAdapter<ListStory, StoryItemAdapter.ListViewHolder>
        (DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder =
        ListViewHolder(StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    companion object {
        val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<ListStory>() {
                override fun areItemsTheSame(
                    oldItem: ListStory,
                    newItem: ListStory
                ): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(
                    oldItem: ListStory,
                    newItem: ListStory
                ): Boolean {
                    return oldItem.id == newItem.id
                }
            }
    }

    inner class ListViewHolder(private val binding: StoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ListStory) {
            with(binding) {
                Glide.with(itemView).load(item.photoUrl)
                    .transition(DrawableTransitionOptions.withCrossFade()).into(ivItemPhoto)
                tvItemName.text = item.name
                tvItemCreateAt.text = getTimelineUpload(itemView.context, item.createdAt)

                itemView.setOnClickListener {
                    Intent(itemView.context, DetailStoryActivity::class.java).also {
                        it.putExtra(Constanta.EXTRA_DATA, item)
                        val optionsCompat: ActivityOptionsCompat =
                            ActivityOptionsCompat.makeSceneTransitionAnimation(
                                itemView.context as Activity,
                                androidx.core.util.Pair(binding.ivItemPhoto, "image"),
                                androidx.core.util.Pair(binding.tvItemName, "name")
                            )
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(itemView.context, it, optionsCompat.toBundle())
                    }
                }
            }
        }
    }
}
