package com.example.mywallet

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mywallet.databinding.ActivityMainBinding
import com.example.mywallet.db.AppDatabase
import com.example.mywallet.db.entity.TransactionEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), ItemLogRVAdapter.RecyclerViewClickListener,
    View.OnClickListener {
    private lateinit var mDB : AppDatabase
    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var dataItemLogRVAdapter: ItemLogRVAdapter
    private lateinit var sharedpreferences: SharedPreferences

    private var transactionsList = mutableListOf<TransactionEntity>()
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

        setupSplashScreen(splashScreen)
        setDatabase()
        setUserData()
        getLogsData()
        setView()
    }

    private fun setDatabase() {
        mDB = AppDatabase.getDatabase(applicationContext)
        Log.d("db", "DB status : " + mDB.isOpen.toString())
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
                        true
                    } else false
                }
            }
        )
    }

    private fun setUserData() {
//        mDB.collection("users").document("ENBQXgcU0S4uGKRx6jwt")
//            .get()
//            .addOnSuccessListener { documentSnapshot ->
//                uid = "ENBQXgcU0S4uGKRx6jwt"
//                val name = documentSnapshot["name"].toString()
//                balance = documentSnapshot["balance"].toString()
//                val amount = "Rp " + formatter(balance)
//                mainBinding.textViewName.text = name
//                mainBinding.textViewBalance.text = amount
//                contentHasLoaded = true
//                Log.d("FirestoreLog", "Success")
//            }
//            .addOnFailureListener { exception ->
//                val text = "Error when getting documents : " + exception
//                Log.e("FirestoreLog", text)
//                Toast.makeText(this, text,Toast.LENGTH_SHORT).show()
//            }
        sharedpreferences = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
//        val userid = sharedpreferences.getString("UID", null)
//        val name = sharedpreferences.getString("NAME", null)
        val userid = "011"
        val name = "Zikri"
        uid = userid.toString()
        balance = sharedpreferences.getString("AMOUNT_STORED", "0").toString()
        if (userid == null) {
            val intent = Intent(this@MainActivity, InsertFirstDataActivity::class.java)
            startActivity(intent)
            finishAffinity()
        } else {
            val amount = "Rp " + formatter(balance)
            mainBinding.textViewName.text = name
            mainBinding.textViewBalance.text = amount
        }
    }

    private fun getLogsData() {
        val calendarInstance = Calendar.getInstance()
        var month = calendarInstance.get(Calendar.MONTH).toString()
        month = if (month.toInt() < 10) "0$month" else month
        val formattedDate = calendarInstance.get(Calendar.YEAR).toString() + "/" + month + "/" + "01"

//        mDB.collection("logs")
//            .whereEqualTo("uid",uid)
//            .whereGreaterThanOrEqualTo("date", formattedDate)
//            .orderBy("date", Query.Direction.DESCENDING)
//            .orderBy("time", Query.Direction.DESCENDING)
//            .get()
//            .addOnSuccessListener { documentSnapshot ->
//                documents = documentSnapshot
//                Log.d("FirestoreLog", "Success")
//                setItemsData()
//            }
//            .addOnFailureListener { exception ->
//                val text = "Error when getting documents : " + exception
//                Log.e("FirestoreLog", text)
//                Toast.makeText(this, text,Toast.LENGTH_SHORT).show()
//            }
        transactionsList.clear()
        transactionsList.addAll(mDB.transactionDao().loadTransactionFrom(formattedDate))
        setItemsData()
    }

    private fun setItemsData() {
        if (transactionsList.count() < 1) {
            mainBinding.noDataImageView.visibility = View.VISIBLE
            mainBinding.noDataTextView.visibility = View.VISIBLE
            mainBinding.recyclerViewLog.visibility = View.GONE
            contentHasLoaded = true
        } else {
            val dataset = ArrayList<dataRV>()
            for (transactionData in transactionsList!!) {
                val userid = transactionData.userId.toString()
                if (userid == uid) {
                    val id = transactionData.id
                    val name = transactionData.name.toString()
                    val date = transactionData.date.toString()
                    val time = transactionData.time.toString()
                    val type = transactionData.transactionType.toString()
                    val amount = transactionData.amount!!.toInt()
                    val category = transactionData.categoryId.toString()
                    val note = transactionData.note.toString()
                    val data = dataRV(id, name, date, time, type, amount, category, note)
                    dataset.add(data)
                }
            }
            setStats(dataset)
            setRecyclerView(dataset)
            dataItemLogRVAdapter.listener = this
        }
    }

    private fun setStats(datasets: ArrayList<dataRV>) {
        val formatter = SimpleDateFormat("yyyy/MM/dd")
        val formattedDate = formatter.format(Calendar.getInstance().time)

        var todayExpense = 0
        var monthExpense = 0
        for (dataset in datasets) {
            if (dataset.getType() == "outcome") {
                val amount = dataset.getAmount()
                if (dataset.getDate() == formattedDate) {
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

        contentHasLoaded = true
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
