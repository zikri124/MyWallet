package com.example.mywallet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mywallet.databinding.ActivityHistoriesBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HistoriesActivity : AppCompatActivity(), View.OnClickListener, ItemLogRVAdapter.RecyclerViewClickListener {
    private lateinit var mDB : FirebaseFirestore
    private var documents : QuerySnapshot? = null
    private lateinit var uid : String
    private lateinit var binding : ActivityHistoriesBinding
    private lateinit var dataItemLogRVAdapter: ItemLogRVAdapter

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
        radioButton.background.setTint(getColor(R.color.primary))
        radioButton.setTextColor(getColor(R.color.white))

        radioButton2.background.setTint(getColor(R.color.white))
        radioButton2.setTextColor(getColor(R.color.text_primary))
    }

    private fun setDB() {
        mDB = Firebase.firestore
        uid = intent.getStringExtra("uid").toString()
    }

    private fun setRecyclerView(dataset: ArrayList<dataRV>) {
        val recyclerView : RecyclerView = binding.recyclerViewLog
        dataItemLogRVAdapter = ItemLogRVAdapter(dataset)
        recyclerView.layoutManager = LinearLayoutManager(this@HistoriesActivity)
        recyclerView.adapter = dataItemLogRVAdapter
    }

    private fun getLogsData(category : String) {
        mDB.collection("logs")
            .whereEqualTo("uid",uid)
            .orderBy("date", Query.Direction.DESCENDING)
            .orderBy("time", Query.Direction.DESCENDING)
            .limit(200)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                documents = documentSnapshot
                Log.d("FirestoreLog", "Success")
                setItemsData("all")
            }
            .addOnFailureListener { exception ->
                val text = "Error when getting documents : " + exception
                Log.e("FirestoreLog", text)
                Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
            }
    }

    private fun setItemsData(type: String) {
        val dataset = ArrayList<dataRV>()
        for (document in documents!!) {
            if (type == "all" || document.data.getValue("type").toString() == type) {
                val id = document.id
                val name = document.data.getValue("name").toString()
                val date = document.data.getValue("date").toString()
                val time = document.data.getValue("time").toString()
                val type = document.data.getValue("type").toString()
                val amount = document.data.getValue("ammount").toString().toInt()
                val objectType = document.data.getValue("objectType").toString()
                val note = document.data.getValue("note").toString()
                val data = dataRV(id, name, date, time, type, amount, objectType, note)
                dataset.add(data)
            }
        }
        setRecyclerView(dataset)
        dataItemLogRVAdapter.listener = this
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

    override fun onClick(view: View, data: dataRV) {
        showEditLogDialog(data)
    }
}