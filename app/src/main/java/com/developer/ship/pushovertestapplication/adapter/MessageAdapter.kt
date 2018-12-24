package com.developer.ship.pushovertestapplication.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.developer.ship.pushovertestapplication.R
import com.developer.ship.pushovertestapplication.entity.PushoverMessage
import java.text.SimpleDateFormat

class MessageAdapter : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
    private var mData = listOf<PushoverMessage>()

    public fun setData(listData: List<PushoverMessage>){
        mData = listData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_rv, parent, false))
    }

    override fun getItemCount(): Int = mData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.init(mData[position])
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        private val text = view.findViewById<TextView>(R.id.tv_content)
        private val date = view.findViewById<TextView>(R.id.tv_date)

        fun init(message: PushoverMessage){
            text.text = message.message
            date.text = SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(message.date)
        }
    }
}