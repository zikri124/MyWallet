package com.example.mywallet

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mywallet.databinding.ActivityHistoriesBinding
import com.example.mywallet.db.AppDatabase
import com.example.mywallet.db.entity.TransactionEntity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class HistoriesActivity : AppCompatActivity(), View.OnClickListener, ItemLogRVAdapter.RecyclerViewClickListener {
    private lateinit var mDB : AppDatabase
    private lateinit var uid : String
    private lateinit var binding : ActivityHistoriesBinding
    private lateinit var dataItemLogRVAdapter: ItemLogRVAdapter

    private var transactionsList = mutableListOf<TransactionEntity>()

    private lateinit var btnMenu : Button
    private lateinit var backButton: ImageButton

    private var transactionType = "all"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_histories)

        setDB()
        getLogsData("")
        setView()
    }

    private fun setView () {
        setRadioListener()

        backButton = binding.buttonBack

        backButton.setOnClickListener(this)
    }

    private fun setRadioListener() {
        var checked = binding.radioButton
        var button = binding.radioButton
        binding.typeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId) {
                R.id.radioButton -> {
                    button = binding.radioButton
                    transactionType = "all"
                }
                R.id.radioButton2 -> {
                    button = binding.radioButton2
                    transactionType = "income"
                }
                R.id.radioButton3 -> {
                    button = binding.radioButton3
                    transactionType = "outcome"
                }
            }
            setRadioColor(button, checked)
            checked = button
            setItemsData(transactionType)
        }
    }

    private fun setRadioColor(radioButton: RadioButton, radioButton2: RadioButton) {
        radioButton.background.setTint(getColor(R.color.white))
        radioButton.setTextColor(getColor(R.color.primary))

        radioButton2.background.setTint(getColor(R.color.primary))
        radioButton2.setTextColor(getColor(R.color.text_disable))
    }

    private fun setDB() {
        mDB = AppDatabase.getDatabase(applicationContext)
        uid = intent.getStringExtra("uid").toString()
    }

    private fun setRecyclerView(dataset: ArrayList<dataRV>) {
        val recyclerView : RecyclerView = binding.recyclerViewLog
        dataItemLogRVAdapter = ItemLogRVAdapter(dataset)
        recyclerView.layoutManager = LinearLayoutManager(this@HistoriesActivity)
        recyclerView.adapter = dataItemLogRVAdapter
    }

    private fun getLogsData(type : String) {
        transactionsList.clear()
        transactionsList.addAll(mDB.transactionDao().loadAllTransactions())
        setItemsData(transactionType)
    }

    private fun setItemsData(showType: String) {
        if (transactionsList.count() < 1) {
            binding.noDataImageLayout.visibility = View.VISIBLE
            binding.recyclerViewLog.visibility = View.GONE
//            contentHasLoaded = true
        } else {
            val dataset = ArrayList<dataRV>()
            for (transactionData in transactionsList!!) {
                val userid = transactionData.userId.toString()
                val type = transactionData.transactionType.toString()
                if (userid == uid && (showType == "all" || showType == type)) {
                    val id = transactionData.id
                    val name = transactionData.name.toString()
                    val date = transactionData.date.toString()
                    val time = transactionData.time.toString()
                    val amount = transactionData.amount!!.toInt()
                    val category = transactionData.categoryId.toString()
                    val note = transactionData.note.toString()
                    val data = dataRV(id, name, date, time, type, amount, category, note)
                    dataset.add(data)
                }
            }
            setRecyclerView(dataset)
            dataItemLogRVAdapter.listener = this
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            backButton.id -> {
                val intent = Intent(this@HistoriesActivity, MainActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }
        }
    }

    private fun showEditLogDialog(data: dataRV) {
        MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setCancelable(false)
            .setTitle(data.getName())
            .setMessage("Date: " + data.getDate() +
                    "\nTime : " + data.getTime() +
                    "\nAmount : " + data.getAmount() +
                    "\nNote : " + if (data.getNote()=="") "-" else data.getNote()  +
                    "\n" + data.getId())
            .setPositiveButton("Close") { dialog, which ->
                dialog.cancel()
            }
//            .setNegativeButton("Edit") { dialog, which ->
//
//            }
//            .setNeutralButton("Delete") { dialog, which ->
//
//            }
            .show()
    }

    private fun showMenu(v: View) {

    }

    override fun onClick(view: View, data: dataRV) {
        showEditLogDialog(data)
//        showMenu(view)
    }
}