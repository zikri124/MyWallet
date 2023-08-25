package com.example.mywallet

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.mywallet.databinding.ActivityNewTransactionBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class NewTransactionActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var mDB : FirebaseFirestore
    private lateinit var binding : ActivityNewTransactionBinding

    private lateinit var backButton: ImageButton
    private lateinit var submitButton : Button
    private lateinit var dateInput : TextInputEditText
    private lateinit var timeInput : TextInputEditText
    private lateinit var accountInput: AutoCompleteTextView
    private lateinit var categoryInput: AutoCompleteTextView
    private lateinit var amountInput: TextInputEditText
    private lateinit var dateInputLayout: TextInputLayout
    private lateinit var timeInputLayout: TextInputLayout
    private lateinit var accountInputLayout: TextInputLayout

    private lateinit var balance : String
    private lateinit var uid : String

    private val c = Calendar.getInstance()

    private var datePicker =
        MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
    private var timePicker =
        MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(c.get(Calendar.HOUR_OF_DAY))
            .setMinute(c.get(Calendar.MINUTE))
            .setTitleText("Select Time")
            .build()

    private var transactionType = "income"
    private var checkedAccountItem = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_transaction)

        setDB()
        setView()
        setRadioListener()
        setDropdownCategory(transactionType)
    }

    private fun setDB() {
        mDB = Firebase.firestore
        uid = intent.getStringExtra("uid").toString()
        balance = intent.getStringExtra("balance").toString()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setView() {
        backButton = binding.buttonBack3
        submitButton = binding.buttonNewActSubmit
        dateInput = binding.textInputDate
        timeInput = binding.textInputTime
        accountInput = binding.textInputAccount
        categoryInput = binding.textInputCategory
        amountInput = binding.textInputAmount
        dateInputLayout = binding.textInputDateLayout
        timeInputLayout = binding.textInputTimelayout
        accountInputLayout = binding.textInputAccountlayout

        backButton.setOnClickListener(this)
        submitButton.setOnClickListener(this)
        dateInput.setOnClickListener(this)
        timeInput.setOnClickListener(this)
        accountInputLayout.setOnClickListener(this)

        accountInput.setOnClickListener(this)

//        dateInput.setOnTouchListener { v, event ->
//            when (event?.action) {
//                MotionEvent.ACTION_DOWN -> datePicker.show(supportFragmentManager, "DATE_PICKER")
//            }
//
//            v?.onTouchEvent(event) ?: true
//        }

        dateInput.setOnClickListener {
            datePicker.show(supportFragmentManager, "DATE_PICKER")
        }

        timeInput.setOnClickListener {
            timePicker.show(supportFragmentManager, "TIME_PICKER")
        }

        datePicker.addOnPositiveButtonClickListener {
            val outputDateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }

            val temp = outputDateFormat.format(it)



            dateInput.setText(temp)
        }

        timePicker.addOnPositiveButtonClickListener {
            var timeString = ""
            val hour = timePicker.hour
            val minute = timePicker.minute

            if (hour < 10) {
                timeString += "0$hour"
            } else {
                timeString += hour
            }

            timeString += "."

            if (minute < 10) {
                timeString += "0$minute"
            } else {
                timeString += minute
            }

            timeInput.setText(timeString)
        }
    }

    private fun setRadioListener() {
        var checked = binding.radioButton
        var button = binding.radioButton
        binding.typeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId) {
                R.id.radioButton -> {
                    button = binding.radioButton
                    transactionType = "income"
                    categoryInput.text = null
                }
                R.id.radioButton2 -> {
                    button = binding.radioButton2
                    transactionType = "outcome"
                    categoryInput.text = null
                }
            }
            setRadioColor(button, checked)
            setDropdownCategory(transactionType)
            checked = button
        }
    }

    private fun setDropdownCategory(type: String) {
        var categoryArray = arrayOf("Food", "Snack", "Entertainment", "Laundry", "Grocery", "Monthly fee", "Other")

        if (type == "income") {
            categoryArray = arrayOf("Allowance", "Other")
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, categoryArray)

        categoryInput.setAdapter(adapter)
    }

    private fun setRadioColor(radioButton: RadioButton, radioButton2: RadioButton) {
        radioButton.background.setTint(getColor(R.color.white))
        radioButton.setTextColor(getColor(R.color.primary))

        radioButton2.background.setTint(getColor(R.color.primary))
        radioButton2.setTextColor(getColor(R.color.text_disable))
    }

    private fun checkBalanceIsFine() : Boolean {
        val amount : Int = binding.textInputAmount.text.toString().toInt()
        when (accountInput.text.toString()) {
            "Wallet" -> {
                if (balance.toInt() < amount) {
                    val text = "error when add log data, Not enough money in this Account"
                    Log.d("error when add log data", text)
                    Toast.makeText(this, text,Toast.LENGTH_SHORT).show()
                    return false
                }
            }
        }
        return true
    }

    private fun checkIsErrorField() : Boolean {
        var isError = false
        if (binding.textInputName.text.toString() == "") {
            isError = true
            binding.textInputNamelayout.error = "Please fill this field"
        }
        if (dateInput.text.toString() == "") {
            isError = true
            dateInputLayout.error = "Please fill this field"
        }
        if (timeInput.text.toString() == "") {
            isError = true
            timeInputLayout.error = "Please fill this field"
        }
        if (binding.textInputAmount.text.toString() == "") {
            isError = true
            binding.textInputAmountlayout.error = "Please fill this field"
        }
        if (categoryInput.text.toString() == "") {
            isError = true
            binding.textInputCategorylayout.error = "Please fill this field"
        }
        if (accountInput.text.toString() == "") {
            isError = true
            binding.textInputAccountlayout.error = "Please fill this field"
        }
        return isError
    }

    private fun submitLog() {
        val amount = binding.textInputAmount.text.toString()

        val data = hashMapOf(
            "uid" to intent.getStringExtra("uid"),
            "name" to binding.textInputName.text.toString(),
            "date" to binding.textInputDate.text.toString(),
            "time" to binding.textInputTime.text.toString(),
            "type" to transactionType,
            "amount" to binding.textInputAmount.text.toString().toInt(),
            "category" to binding.textInputCategory.text.toString(),
            "account" to binding.textInputAccount.text.toString(),
            "note" to binding.textInputNote.text.toString()
        )

        mDB.collection("logs")
            .add(data)
            .addOnSuccessListener { docRef ->
                Log.d("FirestoreLog", "Id : " + docRef.id)
                if (transactionType == "outcome") {
                    setBalance("-$amount")
                } else if (transactionType == "income") {
                    setBalance(amount)
                }
            }
            .addOnFailureListener { exception ->
                val text = "Error when adding document : " + exception
                Log.e("FirestoreLog", text)
                Toast.makeText(this, text,Toast.LENGTH_SHORT).show()
            }
    }

    private fun setBalance(amount : String) {
        when (binding.textInputAccount.text.toString()) {
            "Wallet" -> {
                mDB.collection("users").document(uid)
                    .update("balance", balance.toInt() + amount.toInt())
                    .addOnSuccessListener {
                        showResponseDialog()
                    }
                    .addOnFailureListener { exception ->
                        val text = "Error when getting documents : " + exception
                        Log.e("FirestoreLog", text)
                        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
                    }
            }
            else -> {
                showResponseDialog()
            }
        }
    }

    private fun showSelectAccountDialog() {
        val accountArray = arrayOf("Wallet", "BNI", "BRI")
        MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setTitle("Select an Account")
            .setCancelable(false)
            .setSingleChoiceItems(accountArray, checkedAccountItem, { dialogInterface, i ->
                Log.d("dialog select", i.toString())
                accountInput.setText(accountArray[i])
                checkedAccountItem = i
                dialogInterface.cancel()
            })
            .show()
    }

    private fun showResponseDialog() {
        MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setIcon(R.drawable.baseline_check_24)
            .setCancelable(false)
            .setTitle("Success add your new activity! ")
            .setMessage("You can check it in Activities logs")
            .setPositiveButton("Okay") { dialog, which ->
                val intent = Intent(this@NewTransactionActivity, MainActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }
            .show()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            backButton.id -> {
                val intent = Intent(this@NewTransactionActivity, MainActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }
            submitButton.id -> {
                if (!checkIsErrorField()) {
                    if (transactionType == "outcome") {
                        if (checkBalanceIsFine()) {
                            submitLog()
                        }
                    } else if (transactionType == "income") {
                        submitLog()
                    }
                }
            }
            accountInput.id -> {
                showSelectAccountDialog()
            }
        }
    }
}