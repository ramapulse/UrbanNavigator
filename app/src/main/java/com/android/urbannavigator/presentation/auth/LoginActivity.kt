package com.android.urbannavigator.presentation.auth

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.urbannavigator.R
import com.android.urbannavigator.databinding.ActivityLoginBinding
import com.android.urbannavigator.databinding.DialogForgetPasswordBinding
import com.android.urbannavigator.presentation.LoadingDialog
import com.android.urbannavigator.presentation.MainActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadingDialog = LoadingDialog(this)
        initButton()
    }

    private fun initButton() {
        binding.btnForgetPassword.setOnClickListener {
            forgotPassword()
        }

        binding.btnToSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        binding.btnSignIn.setOnClickListener {
            binding.etEmail.error = null
            binding.etPassword.error = null
            when {
                binding.etEmail.editText?.text.isNullOrEmpty() -> {
                    binding.etEmail.error = getString(R.string.error_field_kosong)
                    binding.etEmail.requestFocus()
                }
                binding.etPassword.editText?.text.isNullOrEmpty() -> {
                    binding.etPassword.error = getString(R.string.error_field_kosong)
                    binding.etPassword.requestFocus()
                }
                !Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.editText?.text.toString()).matches() -> {
                    binding.etEmail.error = getString(R.string.email_harus_valid)
                    binding.etEmail.requestFocus()
                }
                else -> {
                    loadingDialog.startLoadingDialog()
                    val email = binding.etEmail.editText?.text.toString()
                    val password = binding.etPassword.editText?.text.toString()
                    val auth = FirebaseAuth.getInstance()

                    auth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener { authResult ->
                            makeToast(getString(R.string.login_sukses))
                            loadingDialog.dismissDialog()
                            val intent = Intent(this, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            loadingDialog.dismissDialog()
                            makeToast(e.message ?: getString(R.string.login_gagal))
                        }
                }
            }

        }
    }

    private fun forgotPassword(){
        val dialogView: DialogForgetPasswordBinding = DialogForgetPasswordBinding.inflate(layoutInflater)
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(this).setView(dialogView.getRoot())

        val dialog = builder.create()

        dialogView.buttonChange.setOnClickListener{
            dialogView.editTextEmail.error = null
            if(dialogView.editTextEmail.text.isNullOrEmpty()){
                dialogView.editTextEmail.error = getString(R.string.error_field_kosong)
                dialogView.editTextEmail.requestFocus()
            }else if(!Patterns.EMAIL_ADDRESS.matcher(dialogView.editTextEmail.text.toString()).matches()){
                dialogView.editTextEmail.error = getString(R.string.email_harus_valid)
                dialogView.editTextEmail.requestFocus()
            }else{
                loadingDialog.startLoadingDialog()
                val email = dialogView.editTextEmail.text.toString()
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        loadingDialog.dismissDialog()
                        if (task.isSuccessful) {
                            makeToast(getString(R.string.sukses_reset_password))
                            dialog.dismiss()
                        }else{
                            makeToast(getString(R.string.error_coba_lagi_nanti))
                        }
                    }
            }
        }
        dialog.show()
        dialog.setCancelable(true)
    }

    private fun makeToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}