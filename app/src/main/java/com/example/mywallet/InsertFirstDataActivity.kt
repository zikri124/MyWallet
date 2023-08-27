package com.example.mywallet

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.databinding.DataBindingUtil
import com.example.mywallet.databinding.ActivityInsertFirstDataBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.UUID

class InsertFirstDataActivity : AppCompatActivity() {
    private lateinit var sharedpreferences: SharedPreferences
    private lateinit var binding: ActivityInsertFirstDataBinding

    private lateinit var nameInputLayout: TextInputLayout
    private lateinit var nameInputText: TextInputEditText
    private lateinit var initialBalanceInputLayout: TextInputLayout
    private lateinit var initialBalanceInputEditText: TextInputEditText
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_insert_first_data)

        sharedpreferences = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
        setView()
    }

    private fun setView() {
        nameInputLayout = binding.textInputNamelayout
        nameInputText = binding.textInputName
        initialBalanceInputLayout = binding.textInputInitialBalancelayout
        initialBalanceInputEditText = binding.textInputInitialBalance
        submitButton = binding.buttonNewUserSubmit

        submitButton.setOnClickListener {
            if (!checkIsInputError()) {
                saveUser()
            }
        }
    }

    private fun checkIsInputError(): Boolean {
        var isError = false

        if (nameInputText.text.toString() == "") {
            nameInputLayout.error = "Please fill this field"
            isError = true
        }
        if (initialBalanceInputEditText.text.toString() == "") {
            initialBalanceInputLayout.error = "Please fill this field"
            isError = true
        }

        return isError
    }

    private fun saveUser() {
        val userName = nameInputText.text.toString()
        val userBalance = initialBalanceInputEditText.text.toString()

        sharedpreferences.edit {
            putString("UID", UUID.randomUUID().toString())
            putString("USER_NAME", userName)
            putString("USER_BALANCE", userBalance)
        }

        val intent = Intent(this@InsertFirstDataActivity, MainActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }
}