package com.example.mywallet

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.mywallet.databinding.ItemLogExpandableBinding
import com.example.mywallet.db.AppDatabase
import com.google.android.material.dialog.MaterialAlertDialogBuilder

internal class ItemLogRVAdapter(private val dataSet: ArrayList<dataRV>, private val context: Context, private val mDB : AppDatabase) : RecyclerView.Adapter<ItemLogRVAdapter.ViewHolder>() {
    var listener : RecyclerViewClickListener? = null
    private val data = dataSet

    inner class ViewHolder(binding: ItemLogExpandableBinding) : RecyclerView.ViewHolder(binding.root) {
        val itemName : TextView
        val itemTime : TextView
        val amount : TextView
        val itemIcon : ConstraintLayout
        val item : ConstraintLayout
        val category : TextView
        val note : TextView
        val subItem : ConstraintLayout
        val deleteButton : Button
        val editButton : Button

        init {
            itemName = binding.tittle
            itemTime = binding.tittle2
            amount = binding.tittle3
            itemIcon = binding.itemIcon
            item = binding.logItemLayout
            category = binding.tittle4
            note = binding.tittle7
            subItem = binding.subItem
            deleteButton = binding.button2
            editButton = binding.button
        }
    }

    interface RecyclerViewClickListener {
        fun onClick(view: View, data: dataRV, status: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLogExpandableBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = dataSet.get(position)
        holder.itemName.text = result.getName()
        holder.itemTime.text = result.getDate() + ", " + result.getTime()
        holder.category.text = result.getCategory()
        holder.note.text = result.getNote()

        if (result.getType() == "income") {
            holder.amount.text = "+Rp " + formatter(result.getAmount().toString())
            holder.itemIcon.setBackgroundResource(R.drawable.income_color)
        } else if (result.getType() == "outcome") {
            holder.amount.text = "-Rp " + formatter(result.getAmount().toString())
            holder.itemIcon.setBackgroundResource(R.drawable.outcome_color)
        } else {
            holder.amount.text = "type has not been registered"
        }

        if (result.getIsExpanded()) {
            holder.subItem.visibility = View.VISIBLE
        } else {
            holder.subItem.visibility = View.GONE
        }

        holder.deleteButton.setOnClickListener {
            showDeleteLogDialog(it, data[position], position)
        }

        holder.item.setOnClickListener {
            data[position].setIsExpanded(!data[position].getIsExpanded())
            listener?.onClick(it, data[position], "expanded")
            notifyItemChanged(position)
        }
    }

    private fun showDeleteLogDialog(it: View, data: dataRV, position: Int) {
        MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setCancelable(false)
            .setTitle("Are you sure to delete this transaction log?")
            .setMessage("Name: " + data.getName() +
                    "\nTime : " + data.getDate() + ", " + data.getTime() +
                    "\nAmount : " + data.getAmount() +
                    "\nNote : " + if (data.getNote()=="") "-" else data.getNote()  +
                    "\n" + data.getId())
            .setPositiveButton("Delete") { dialog, which ->
                mDB.transactionDao().deleteTransaction(data.getId())
                dialog.cancel()
                dataSet.remove(data)
                notifyItemRemoved(position)
                listener?.onClick(it, data, "deleted")
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.cancel()
            }
//            .setNeutralButton("Delete") { dialog, which ->
//
//            }
            .show()
    }

    private fun formatter(amount: String): String {
        var amountEditted = ""
        val amountLength = amount.length
        var cursorPos = 0

        if (amountLength <= 3) {
            return amount
        }

        val left = amountLength % 3
        println(left)
        for (i in 0 until left) {
            amountEditted += amount[cursorPos]
            cursorPos += 1
        }

        val iterate = amountLength / 3
        for (i in 0 until iterate) {
            if (cursorPos != 0) {
                amountEditted += ","
            }
            for (i2 in cursorPos..cursorPos+2) {
                amountEditted += amount[cursorPos]
                cursorPos += 1
            }
        }

        return amountEditted
    }
}

class dataRV(id : String, name : String, date : String, time : String, type : String, amount : Int, category : String, note : String) {
    private var id : String
    private var name : String
    private var date : String
    private var time : String
    private var type : String
    private var amount : Int
    private var category: String
    private var note : String
    private var isExpand = false

    init {
        this.id = id
        this.name = name
        this.date = date
        this.time = time
        this.type = type
        this.amount = amount
        this.category = category
        this.note = note
    }

    fun getId() : String {
        return id
    }

    fun getName() : String {
        return name
    }

    fun getDate() : String {
        return date
    }

    fun getTime() : String {
        return time
    }

    fun getType() : String {
        return  type
    }

    fun getAmount() : Int {
        return amount
    }

    fun getCategory() : String {
        return category
    }

    fun getNote() : String {
        return note
    }

    fun getIsExpanded() : Boolean {
        return isExpand
    }

    fun setIsExpanded(newStat: Boolean) {
        isExpand = newStat
    }
}