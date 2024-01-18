package com.mood.ar

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.mood.ar.MediaLoder.AudioFolder
import com.mood.ar.MediaLoder.AudioItem
import com.mood.ar.MediaLoder.FileFolder
import com.mood.ar.MediaLoder.FileItem
import com.mood.ar.MediaLoder.ImageFolder
import com.mood.ar.MediaLoder.ImageItem
import com.mood.ar.MediaLoder.VideoFolder
import com.mood.ar.MediaLoder.VideoItem
import com.mood.ar.MediaLoder.printIt
import com.mood.ar.MyAPI.FileUpload
import com.mood.ar.databinding.FolderViewBinding
import com.mood.ar.databinding.ItemViewBinding
import java.io.File


open class AllFilesRecyclerAdapterX : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var list: ArrayList<*> = ArrayList<Any>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_view, parent, false)
        ) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val context = holder.itemView.context

        when (list.firstOrNull()) {
            is ImageItem -> {
                val item = list[position] as ImageItem

                ItemViewBinding.bind(holder.itemView).apply {
                    Glide.with(holder.itemView.context)
                        .load(item.uri)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .transform(CenterInside(), RoundedCorners(5))
                        .into(itemViewImg)
                }

                holder.itemView.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(item.uri, "image/*")
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //DO NOT FORGET THIS EVER
                    context.startActivity(intent)
                }
                holder.itemView.setOnLongClickListener {

                    upload(context, item.uri,"","");
                    true
                }

            }
            is AudioItem -> {
                val item = list[position] as AudioItem

                ItemViewBinding.bind(holder.itemView).apply {

                    Glide.with(holder.itemView.context)
                        .load(R.drawable.ic_audio)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .transform(CenterInside(), RoundedCorners(5))
                        .into(itemViewImg)
                }
            }
            is VideoItem -> {
                val item = list[position] as VideoItem

                ItemViewBinding.bind(holder.itemView).apply {

                    videoPlayImg.isVisible = true
                    Glide.with(holder.itemView.context)
                        .load(item.uri)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .transform(CenterInside(), RoundedCorners(5))
                        .into(itemViewImg)
                }

                holder.itemView.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(item.uri, "video/*")
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //DO NOT FORGET THIS EVER
                    context.startActivity(intent)
                }

                holder.itemView.setOnLongClickListener {
                    upload(context, item.uri,"","");
                    true
                }
            }
            is FileItem -> {
                val item = list[position] as FileItem


                ItemViewBinding.bind(holder.itemView).apply {
                    titleTxt.isVisible = true
                    titleTxt.text = item.title
                    Glide.with(holder.itemView.context)
                        .load(R.drawable.ic_files)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .transform(CenterInside(), RoundedCorners(5))
                        .into(itemViewImg)
                }
            }
        }


    }

    override fun getItemCount(): Int = list.size

    fun setData(list: ArrayList<*>) {
        this.list = list
        notifyDataSetChanged()
    }
}

fun upload(context: Context, uri : Uri, tag: String, duration: String) {

    val builder = AlertDialog.Builder(context)
    builder.setMessage("Are you sure you want to upload to the server?")
        .setCancelable(false)
        .setPositiveButton("Yes") { dialog, id ->
            context.contentResolver.query(uri, arrayOf(MediaStore.Video.Media.DURATION), null, null, null)?.use {
                val durationColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                it.moveToFirst()
                FileUpload(context).uploadFILE(uri,tag,it.getLong(durationColumn).toString())
            }

        }
        .setNegativeButton("No") { dialog, id ->
            // Dismiss the dialog
            dialog.dismiss()
        }
    val alert = builder.create()
    alert.show()
}

open class AllFolderRecyclerAdapterX : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var list: ArrayList<*> = ArrayList<Any>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.folder_view, parent, false)
        ) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


        when (list.firstOrNull()) {
            is ImageFolder -> {
                val item = list[position] as ImageFolder

                FolderViewBinding.bind(holder.itemView).apply {
                    folderTxt.text = item.folderName
                }
            }
            is AudioFolder -> {
                val item = list[position] as AudioFolder

                FolderViewBinding.bind(holder.itemView).apply {
                    folderTxt.text = item.folderName
                }
            }
            is VideoFolder -> {
                val item = list[position] as VideoFolder

                FolderViewBinding.bind(holder.itemView).apply {
                    item.folderName.printIt()
                    folderTxt.text = item.folderName
                }
            }
            is FileFolder -> {
                val item = list[position] as FileFolder

                FolderViewBinding.bind(holder.itemView).apply {
                    folderTxt.text = item.folderName
                }
            }
        }

    }

    override fun getItemCount(): Int = list.size

    fun setData(list: ArrayList<*>) {
        this.list = list
        notifyDataSetChanged()
    }


}