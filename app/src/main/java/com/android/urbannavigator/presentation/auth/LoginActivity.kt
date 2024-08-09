package com.android.urbannavigator.presentation.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.urbannavigator.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}