package com.mood.ar.FaceModal

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.mood.ar.MainActivity
import com.mood.ar.R
import com.mood.ar.databinding.FaceRowBinding


class MediaItemAdapter(
    private val context: Context,
    private val items: ArrayList<FaceModel>
) :
    RecyclerView.Adapter<MediaItemAdapter.ViewHolder>() {
    private var onClickListener: OnClickListener? = null

    // Inflates the item views which is designed in xml layout file
    // create a new
    // ViewHolder and initializes some private fields to be used by RecyclerView.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FaceRowBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    // Binds each item in the ArrayList to a view

    // Called when RecyclerView needs
    // a new {@link ViewHolder} of the
    // given type to represent
    // an item.

    // This new ViewHolder should be constructed with
    // a new View that can represent the items
    // of the given type. You can either create a
    // new View manually or inflate it from an XML
    // layout file.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvName.text = item.name
        Glide.with(context)
            .load(item.image)
            .transition(DrawableTransitionOptions.withCrossFade())
            .transform(CenterInside(), RoundedCorners(5))
            .into(holder.image)
        // Finally add an onclickListener to the item.
        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position, item )
            }
        }

    }

    // Gets the number of items in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // A function to bind the onclickListener.
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    // onClickListener Interface
    interface OnClickListener {
        fun onClick(position: Int, model: FaceModel)
    }

    // A ViewHolder describes an item view and metadata
    // about its place within the RecyclerView.
    class ViewHolder(binding: FaceRowBinding) : RecyclerView.ViewHolder(binding.root) {
        // Holds the TextView that
        // will add each item to
        val tvName = binding.tvName
        val image = binding.image
    }

}