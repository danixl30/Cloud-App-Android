package com.example.cloudapp.RecyclerView

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.cloudapp.R
import com.example.cloudapp.controller.SharedApp
import com.example.cloudapp.interfaces.ClickRecyclerListener
import java.util.*
import kotlin.collections.ArrayList

class ExplorerAdapter (var list: ArrayList<DataItems>, var clickListener:ClickRecyclerListener): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var context:Context

    inner class itemExplorerLayout(itemView: View, listener: ClickRecyclerListener): RecyclerView.ViewHolder(itemView), View.OnClickListener{
        init {
            itemView.setOnClickListener(this)
        }
        fun bind (itemData: DataItems){
            itemView.findViewById<TextView>(R.id.nameItem).text = itemData.name
            var name = itemData.name.lowercase()
            name = name.lowercase()
            println(name)
            val img = itemView.findViewById<ImageView>(R.id.imageItem)
            if (name.indexOf(".") == -1){
                img.setImageResource(R.drawable.folder)
            }else if (name.indexOf(".png") != -1 || name.indexOf(".jpg") != -1 || name.indexOf(".gift") != -1){
                img.setImageResource(R.drawable.imagefile)
            }else if (name.indexOf(".mp3") != -1 || name.indexOf(".ma4") != -1 || name.indexOf(".wav") != -1){
                img.setImageResource(R.drawable.musicicon)
            }else {
                img.setImageResource(R.drawable.singlefile)
            }
            val card = itemView.findViewById<CardView>(R.id.cardItem)
            if (itemData.selected)
                card.setCardBackgroundColor(Color.parseColor("#33A8FF"))
        }

        override fun onClick(p0: View?) {
            clickListener.onClick(p0!!, adapterPosition)
        }
    }

    inner class itemExplorerLayoutList(itemView: View, listener: ClickRecyclerListener): RecyclerView.ViewHolder(itemView), View.OnClickListener{
        init {
            itemView.setOnClickListener(this)
        }
        fun bind (itemData: DataItems){
            itemView.findViewById<TextView>(R.id.itemTextList).text = itemData.name
            var name = itemData.name.lowercase()
            name = name.lowercase()
            println(name)
            val img = itemView.findViewById<ImageView>(R.id.imageListItem)
            if (name.indexOf(".") == -1){
                img.setImageResource(R.drawable.folder)
            }else if (name.indexOf(".png") != -1 || name.indexOf(".jpg") != -1 || name.indexOf(".gift") != -1){
                img.setImageResource(R.drawable.imagefile)
            }else if (name.indexOf(".mp3") != -1 || name.indexOf(".ma4") != -1 || name.indexOf(".wav") != -1){
                img.setImageResource(R.drawable.musicicon)
            }else {
                img.setImageResource(R.drawable.singlefile)
            }
            val card = itemView.findViewById<CardView>(R.id.cardItemList)
            if (itemData.selected)
                card.setCardBackgroundColor(Color.parseColor("#33A8FF"))
        }

        override fun onClick(p0: View?) {
            clickListener.onClick(p0!!, adapterPosition)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        if (SharedApp.prefs.getTypeView() == 1){
            val v: View = LayoutInflater.from(context).inflate(R.layout.itemexplorer, parent,false)
            return itemExplorerLayout(v, clickListener)
        }else{
            val v: View = LayoutInflater.from(context).inflate(R.layout.item_explorer_list, parent,false)
            return itemExplorerLayoutList(v, clickListener)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (SharedApp.prefs.getSelectedName() == "")
            holder.itemView.animation = AnimationUtils.loadAnimation(context, R.anim.slide)
        if (SharedApp.prefs.getTypeView() == 1)
            (holder as itemExplorerLayout).bind(list[position])
        else
            (holder as itemExplorerLayoutList).bind(list[position])
    }

    override fun getItemCount(): Int {
        return  list.size
    }
}