package com.example.cloudapp.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.cloudapp.R
import com.example.cloudapp.interfaces.ClickRecyclerListener
import com.example.cloudapp.interfaces.SecondRecyclerListener

class PathAdapter(var list: ArrayList<DataPath>, var clickListener:SecondRecyclerListener): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class PathItemLayout(itemView: View, listener:SecondRecyclerListener):RecyclerView.ViewHolder(itemView), View.OnClickListener{
        var clickListener: SecondRecyclerListener? = null
        init {
            clickListener = listener
            //itemView.setOnClickListener(this)
        }
        fun bind(itemPath: DataPath){
            val item = itemView.findViewById<Button>(R.id.itemPathBTN)
            item.text = itemPath.name + ">"
            val button = itemView.findViewById<Button>(R.id.itemPathBTN).setOnClickListener {
                this
            }
        }
        override fun onClick(p0: View?) {
            clickListener?.onClickPath(p0!!, adapterPosition)
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.itempath, parent,false)
        return PathItemLayout(v, clickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as PathItemLayout).bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}