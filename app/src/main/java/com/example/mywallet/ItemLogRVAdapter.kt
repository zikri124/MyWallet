package com.example.mywallet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.mywallet.databinding.ItemLogBinding

internal class ItemLogRVAdapter(private val dataSet: ArrayList<dataRV>) : RecyclerView.Adapter<ItemLogRVAdapter.ViewHolder>() {
    var listener : RecyclerViewClickListener? = null
    private val data = dataSet

    inner class ViewHolder(binding: ItemLogBinding) : RecyclerView.ViewHolder(binding.root) {
        val itemName : TextView
        val itemTime : TextView
        val amount : TextView
        val itemIcon : ConstraintLayout
        val item : ConstraintLayout
        val objectType : TextView

        init {
            itemName = binding.tittle
            itemTime = binding.tittle2
            amount = binding.tittle3
            itemIcon = binding.itemIcon
            item = binding.logItemLayout
            objectType = binding.tittle4
        }
    }

    interface RecyclerViewClickListener {
        fun onClick(view: View, data: dataRV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = dataSet.get(position)
        holder.itemName.text = result.getName()
        holder.itemTime.text = result.getDate() + ", " + result.getTime()
        holder.objectType.text = result.getObjectType()

        if (result.getType() == "income") {
            holder.amount.text = "+Rp " + formatter(result.getAmount().toString())
            holder.itemIcon.setBackgroundResource(R.drawable.income_color)
        } else if (result.getType() == "outcome") {
            holder.amount.text = "-Rp " + formatter(result.getAmount().toString())
            holder.itemIcon.setBackgroundResource(R.drawable.outcome_color)
        } else {
            holder.amount.text = "type has not been registered"
        }

        holder.item.setOnClickListener {
            listener?.onClick(it, data[position])
        }
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

class dataRV(id : String, name : String, date : String, time : String, type : String, amount : Int, objectType : String, note : String) {
    private var id : String
    private var name : String
    private var date : String
    private var time : String
    private var type : String
    private var amount : Int
    private var objectType: String
    private var note : String

    init {
        this.id = id
        this.name = name
        this.date = date
        this.time = time
        this.type = type
        this.amount = amount
        this.objectType = objectType
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

    fun getObjectType() : String {
        return objectType
    }

    fun getNote() : String {
        return note
    }
}