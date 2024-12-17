package com.r_mit.taskt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExcelDataAdapter : RecyclerView.Adapter<ExcelDataAdapter.ViewHolder>() {

    private var data: List<List<Any>> = listOf()

    // Function to update the data in the adapter
    fun submitList(newData: List<List<Any>>) {
        data = newData
        notifyDataSetChanged()
    }

    // Create a new view holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_2, parent, false)
        return ViewHolder(view)
    }

    // Bind the data to the view holder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val row = data[position]
        holder.text1.text = row.joinToString(", ") // Join cell values with commas
    }

    // Return the number of items in the dataset
    override fun getItemCount(): Int = data.size

    // ViewHolder class to hold individual views for each item
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text1: TextView = view.findViewById(android.R.id.text1)
    }
}
