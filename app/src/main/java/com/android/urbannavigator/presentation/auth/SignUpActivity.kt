package com.android.urbannavigator.presentation.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.urbannavigator.R
import com.android.urbannavigator.data.model.User
import com.android.urbannavigator.databinding.ActivitySignUpBinding
import com.android.urbannavigator.presentation.LoadingDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        loadingDialog = LoadingDialog(this)
        initButton()
    }

    private fun initButton() {
        binding.btnToSignIn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }


        binding.btnSignUp.setOnClickListener {
            binding.etName.error = null
            binding.etEmail.error = null
            binding.etPassword.error = null
            binding.etConfirmPassword.error = null
            when {
                binding.etName.editText?.text.isNullOrEmpty() -> {
                    binding.etName.error = getString(R.string.error_field_kosong)
                    binding.etName.requestFocus()
                }
                binding.etEmail.editText?.text.isNullOrEmpty() -> {
                    binding.etEmail.error = getString(R.string.error_field_kosong)
                    binding.etEmail.requestFocus()
                }
                binding.etPassword.editText?.text.isNullOrEmpty() -> {
                    binding.etPassword.error = getString(R.string.error_field_kosong)
                    binding.etPassword.requestFocus()
                }
                binding.etConfirmPassword.editText?.text.isNullOrEmpty() -> {
                    binding.etConfirmPassword.error = getString(R.string.error_field_kosong)
                    binding.etConfirmPassword.requestFocus()
                }
                !Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.editText?.text.toString()).matches() -> {
                    binding.etEmail.error = getString(R.string.email_harus_valid)
                    binding.etEmail.requestFocus()
                }
                binding.etPassword.editText?.text.toString() != binding.etConfirmPassword.editText?.text.toString() -> {
                    binding.etConfirmPassword.error = getString(R.string.password_tidak_sama)
                    binding.etConfirmPassword.requestFocus()
                }
                else -> {
                    loadingDialog.startLoadingDialog()
                    val email = binding.etEmail.editText?.text.toString()
                    val username = binding.etName.editText?.text.toString()
                    val password = binding.etPassword.editText?.text.toString()

                    val usersRef = FirebaseDatabase.getInstance().getReference("Users")
                    val auth = FirebaseAuth.getInstance()
                    usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                loadingDialog.dismissDialog()
                                makeToast("Error : Email sudah terdaftar!")
                            } else {
                                auth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            val uid = auth.currentUser?.uid ?: ""

                                            val user = User(
                                                profilePic = "",
                                                username = username,
                                                gender = "",
                                                birthDate = "",
                                                location = "",
                                                email = email,
                                                uid = uid
                                            )

                                            FirebaseDatabase.getInstance().getReference("Users")
                                                .child(uid)
                                                .setValue(user)
                                                .addOnCompleteListener { innerTask ->
                                                    if (innerTask.isSuccessful) {
                                                        loadingDialog.dismissDialog()
                                                        makeToast(getString(R.string.register_sukses))
                                                        auth.signOut()
                                                        val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                        startActivity(intent)
                                                        finish()
                                                    } else {
                                                        loadingDialog.dismissDialog()
                                                        makeToast(getString(R.string.register_gagal))
                                                    }
                                                }
                                        } else {
                                            loadingDialog.dismissDialog()
                                            makeToast(task.exception?.toString() ?: "")
                                        }
                                    }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            loadingDialog.dismissDialog()
                            makeToast(getString(R.string.error_coba_lagi_nanti))
                        }
                    })

                }
            }
        }
    }

    private fun makeToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}