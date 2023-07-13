package com.example.mywallet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.Toast
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mywallet.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), ItemLogRVAdapter.RecyclerViewClickListener,
    View.OnClickListener {
    private lateinit var mDB : FirebaseFirestore
    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var dataItemLogRVAdapter: ItemLogRVAdapter
    private var documents : QuerySnapshot? = null

    private lateinit var uid : String
    private lateinit var balance : String

    private lateinit var btnMenu : Button
    private lateinit var btnAllTransacction : Button
    private lateinit var btnNewInTransaction : Button

    var contentHasLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        startLoadingContent()

        setupSplashScreen(splashScreen)

        setView()
    }

    private fun setView() {
        btnMenu = mainBinding.buttonMenu
        btnAllTransacction = mainBinding.buttonAllAct
        btnNewInTransaction = mainBinding.buttonNewIn

        btnAllTransacction.setOnClickListener(this)
        btnNewInTransaction.setOnClickListener(this)
    }

    private fun setupSplashScreen(splashScreen: SplashScreen) {
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return if (contentHasLoaded) {
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        getLogsData("all")
                        true
                    } else false
                }
            }
        )
    }

    private fun startLoadingContent() {
        setDB()
        setUser()
    }

    private fun setDB() {
        mDB = Firebase.firestore
    }

    private fun setUser() {
        mDB.collection("users").document("ENBQXgcU0S4uGKRx6jwt")
            .get()
            .addOnSuccessListener { documentSnapshot ->
                uid = "ENBQXgcU0S4uGKRx6jwt"
                var name = documentSnapshot["name"].toString()
                balance = documentSnapshot["balance"].toString()
                var amount = "Rp " + formatter(balance)
                mainBinding.textViewName.text = name
                mainBinding.textViewBalance.text = amount
                contentHasLoaded = true
                Log.d("FirestoreLog", "Success")
            }
            .addOnFailureListener { exception ->
                val text = "Error when getting documents : " + exception
                Log.e("FirestoreLog", text)
                Toast.makeText(this, text,Toast.LENGTH_SHORT).show()
            }
    }

    private fun setStats(dataset: ArrayList<dataRV>) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val day = c.get(Calendar.DATE)
        val month = c.time.toString().substring(4, 7)

        val dateStr = month + " " + day + ", " + year

        var todayExpense = 0
        var monthExpense = 0
        for (dataset in dataset) {
            if (dataset.getType() == "outcome") {
                var amount = dataset.getAmount()
                if (dataset.getDate() == dateStr) {
                    todayExpense += amount
                }
                monthExpense += amount
            }
        }
        mainBinding.textViewSpent1.text = "Rp " + formatter(todayExpense.toString())
        mainBinding.textViewSpent2.text = "Rp " + formatter(monthExpense.toString())
    }

    private fun setRecyclerView(dataset: ArrayList<dataRV>) {
        val recyclerView : RecyclerView = mainBinding.recyclerViewLog
        dataItemLogRVAdapter = ItemLogRVAdapter(dataset)
        recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        recyclerView.adapter = dataItemLogRVAdapter
    }

    private fun getLogsData(category : String) {
        val c = Calendar.getInstance()
        val month = c.time.toString().substring(4, 7)
        val year = c.get(Calendar.YEAR)
        val dateStr = month + " 01, " + year

        mDB.collection("logs")
            .whereEqualTo("uid",uid)
            .whereGreaterThanOrEqualTo("date", dateStr)
            .orderBy("date", Query.Direction.DESCENDING)
            .orderBy("time", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                documents = documentSnapshot
                Log.d("FirestoreLog", "Success")
                setItemsData("all")
            }
            .addOnFailureListener { exception ->
                val text = "Error when getting documents : " + exception
                Log.e("FirestoreLog", text)
                Toast.makeText(this, text,Toast.LENGTH_SHORT).show()
            }
    }

    private fun setItemsData(type: String) {
        val dataset = ArrayList<dataRV>()
        for (document in documents!!) {
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
        setStats(dataset)
        setRecyclerView(dataset)
        dataItemLogRVAdapter.listener = this
    }

    override fun onClick(view: View, data: dataRV) {
        Log.d("Tess", data.getName())
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            btnNewInTransaction.id -> {
                val intent = Intent(this@MainActivity, NewTransactionActivity::class.java)
                intent.putExtra("uid", uid)
                intent.putExtra("balance", balance)
                startActivity(intent)
            }
            btnAllTransacction.id -> {
                val intent = Intent(this@MainActivity, HistoriesActivity::class.java)
                intent.putExtra("uid", uid)
                startActivity(intent)
            }
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
