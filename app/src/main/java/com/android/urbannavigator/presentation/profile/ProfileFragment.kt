package com.android.urbannavigator.presentation.profile

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.android.urbannavigator.R
import com.android.urbannavigator.data.model.User
import com.android.urbannavigator.databinding.DialogChangeEmailBinding
import com.android.urbannavigator.databinding.DialogConfirmPasswordBinding
import com.android.urbannavigator.databinding.FragmentProfileBinding
import com.android.urbannavigator.presentation.LoadingDialog
import com.android.urbannavigator.presentation.MainActivity
import com.android.urbannavigator.presentation.MainViewModel
import com.android.urbannavigator.presentation.SplashActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by activityViewModels()
    private var editMode: Boolean = false
    private var localPhotoPath: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogout.setOnClickListener {
            showLogoutDialog()
        }

        mainViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            Log.d("Profile Fragment", "observer mainviewmodel")
            val it = user!!
            initChangeEmailButton(it)
            initSaveButton(it)

            Glide.with(requireContext()).load(it.profilePic).centerCrop().skipMemoryCache(true)
                .override(350,350).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).placeholder(R.drawable.img_default_profile)
                .into(binding.ivUserProfile)

            binding.etUsername.editText?.setText(it.username)
            binding.etBirthDate.editText?.setText(it.birthDate)
            binding.etLocation.editText?.setText(it.location)
            binding.etEmail.editText?.setText(it.email)

            when (it.gender) {
                "Female" -> {
                    binding.radioGenderFemale.isChecked = true
                    binding.radioGenderMale.isChecked = false
                }
                "Male" -> {
                    binding.radioGenderFemale.isChecked = false
                    binding.radioGenderMale.isChecked = true
                }
                else -> {
                    binding.radioGenderFemale.isChecked = false
                    binding.radioGenderMale.isChecked = false
                }
            }
        }

        editMode = false
        switchMode()
        binding.btnEditUsername.setOnClickListener {
            if(!editMode){
                editMode = true
                switchMode()
            }
        }
        binding.btnEditBirth.setOnClickListener {
            if(!editMode){
                editMode = true
                switchMode()
            }

        }

        binding.btnChooseDate.setOnClickListener {
            datePickerDialog(binding.etBirthDate.editText!!)
        }
        binding.btnEditLocation.setOnClickListener {
            if(!editMode){
                editMode = true
                switchMode()
            }
        }
        binding.btnEditProfile.setOnClickListener {
            if(!editMode){
                editMode = true
                switchMode()
            }
        }
        binding.btnEditEmail.setOnClickListener{
            editMode = !editMode
            switchMode()
        }

    }

    private fun initChangeEmailButton(currentUser: User) {
        val loadingDialog = LoadingDialog(requireContext())
        binding.btnEditEmail.setOnClickListener {
            val dialogView = DialogChangeEmailBinding.inflate(layoutInflater)
            val builder = AlertDialog.Builder(requireContext()).setView(dialogView.root)

            val dialog = builder.create()

            dialogView.oldEmailText.text = "Email : ${currentUser.email}"

            dialogView.buttonChange.setOnClickListener {
                val newEmail = dialogView.editTextEmail.text.toString()
                if (newEmail.isEmpty()) {
                    dialogView.editTextEmail.error = getString(R.string.error_field_kosong)
                    dialogView.editTextEmail.requestFocus()
                } else if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                    dialogView.editTextEmail.error = getString(R.string.email_harus_valid)
                    dialogView.editTextEmail.requestFocus()
                } else {
                    val alertDialogBuilder = AlertDialog.Builder(requireContext())
                    alertDialogBuilder.setMessage("Ganti Email?")

                    alertDialogBuilder.setPositiveButton("Iya") { dialogInside, _ ->
                        dialog.dismiss()
                        dialogInside.dismiss()
                        val oldDialogView = DialogConfirmPasswordBinding.inflate(layoutInflater)
                        val oldBuilder = AlertDialog.Builder(requireContext()).setView(oldDialogView.root)
                        val oldDialog = oldBuilder.create()


                        oldDialogView.buttonConfirm.setOnClickListener {
                            val oldPassword = oldDialogView.editTextPasswordOld.text.toString()
                            if (oldPassword.isEmpty()) {
                                oldDialogView.editTextPasswordOld.error = getString(R.string.error_field_kosong)
                                oldDialogView.editTextPasswordOld.requestFocus()
                            } else {
                                loadingDialog.startLoadingDialog()
                                val credential = EmailAuthProvider.getCredential(currentUser.email, oldPassword)
                                FirebaseAuth.getInstance().signInWithEmailAndPassword(currentUser.email, oldPassword)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            FirebaseAuth.getInstance().currentUser?.reauthenticate(credential)
                                                ?.addOnCompleteListener { taskCredential ->
                                                    if (taskCredential.isSuccessful) {
                                                        FirebaseAuth.getInstance().currentUser?.updateEmail(newEmail)
                                                            ?.addOnCompleteListener { taskUpdate ->
                                                                if (taskUpdate.isSuccessful) {
                                                                    val updatedValues = hashMapOf<String, Any>(
                                                                        "email" to newEmail
                                                                    )
                                                                    FirebaseDatabase.getInstance().getReference("Users")
                                                                        .child(currentUser.uid)
                                                                        .updateChildren(updatedValues)
                                                                        .addOnCompleteListener { taskDatabase ->
                                                                            if (taskDatabase.isSuccessful) {
                                                                                loadingDialog.dismissDialog()
                                                                                oldDialog.dismiss()
                                                                                makeToast("Email berhasil diganti")
                                                                            } else {
                                                                                loadingDialog.dismissDialog()
                                                                                makeToast(taskDatabase.exception?.message ?: "Error updating database")
                                                                            }
                                                                        }
                                                                } else {
                                                                    loadingDialog.dismissDialog()
                                                                    makeToast(taskUpdate.exception?.message ?: "Error updating email")
                                                                }
                                                            }
                                                    } else {
                                                        loadingDialog.dismissDialog()
                                                        makeToast(taskCredential.exception?.message ?: "Error reauthenticating")
                                                    }
                                                }
                                        } else {
                                            loadingDialog.dismissDialog()
                                            makeToast(task.exception?.message ?: "Error signing in")
                                        }
                                    }
                            }
                        }

                        oldDialog.show()
                    }

                    alertDialogBuilder.setNegativeButton("Tidak") { dialogInside, _ ->
                        dialogInside.dismiss()
                    }

                    val alertDialog = alertDialogBuilder.create()
                    alertDialog.show()
                }
            }

            dialog.show()
        }

    }

    private fun initSaveButton(currentUser: User) {
        val loadingDialog = LoadingDialog(requireContext())
        binding.btnSaveProfile.setOnClickListener {
            binding.etUsername.error = null
            when {
                binding.etUsername.editText?.text.isNullOrEmpty() -> {
                    binding.etUsername.error = getString(R.string.error_field_kosong)
                    binding.etUsername.requestFocus()
                }
                else -> {
                    loadingDialog.startLoadingDialog()
                    val username = binding.etUsername.editText?.text.toString()
                    val gender = if(binding.radioGenderFemale.isChecked) "Female" else if(binding.radioGenderMale.isChecked) "Male" else ""
                    val location = binding.etLocation.editText?.text.toString()
                    val birthdate = binding.etBirthDate.editText?.text.toString()

                    if(localPhotoPath != null){
                        val storage = FirebaseStorage.getInstance()
                        val storageRef = storage.getReference("uploadedProfilePic/${currentUser.uid}")
                        storageRef.putFile(localPhotoPath!!)
                            .addOnSuccessListener { taskSnapshot ->
                                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                                    makeToast("Foto profile berhasil diupload")
                                    val profilePhotoLink = uri.toString()
                                    val profileMap = mapOf(
                                        "profilePic" to profilePhotoLink,
                                        "username" to username,
                                        "gender" to gender,
                                        "birthDate" to birthdate,
                                        "location" to location
                                    )
                                    FirebaseDatabase.getInstance().getReference("Users")
                                        .child(currentUser.uid)
                                        .updateChildren(profileMap)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                loadingDialog.dismissDialog()
                                                makeToast("Profile berhasil diupdate")
//                                                profileView.getCurrentUser()
                                            } else {
                                                loadingDialog.dismissDialog()
                                                makeToast("Error : ${task.exception?.message.toString()}")
                                            }
                                        }
                                }.addOnFailureListener { e ->
                                    loadingDialog.dismissDialog()
                                    makeToast(e.message.toString())
                                }
                            }
                            .addOnFailureListener { e ->
                                loadingDialog.dismissDialog()
                                makeToast(e.message.toString())
                            }

                    }else{
                        val profileMap = mapOf(
                            "username" to username,
                            "gender" to gender,
                            "birthDate" to birthdate,
                            "location" to location
                        )

                        FirebaseDatabase.getInstance().getReference("Users")
                            .child(currentUser.uid)
                            .updateChildren(profileMap)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    loadingDialog.dismissDialog()
                                    makeToast("Profile berhasil diupdate")
//                                    profileView.getCurrentUser()
                                } else {
                                    loadingDialog.dismissDialog()
                                    makeToast("Error : ${task.exception?.message.toString()}")
                                }
                            }
                    }

                }
            }
        }

    }

    private fun datePickerDialog(editText: EditText) {

        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        val calendar = Calendar.getInstance()

        if (editText.text.toString().isNotEmpty()) {
            try {
                val date = dateFormat.parse(editText.text.toString())
                if (date != null) {
                    calendar.time = date
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val datePickerDialog = DatePickerDialog(
            editText.context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                editText.setText(dateFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun switchMode(){
        localPhotoPath = null
        if(editMode){
            binding.btnSaveProfile.visibility = View.VISIBLE
            binding.btnEditProfile.visibility = View.GONE
            binding.btnEditUsername.visibility = View.GONE
            binding.btnEditBirth.visibility = View.GONE
            binding.btnEditLocation.visibility = View.GONE
            binding.etUsername.editText?.inputType = InputType.TYPE_CLASS_TEXT
            binding.radioGenderFemale.isEnabled = true
            binding.radioGenderMale.isEnabled = true
            binding.btnEditProfilePicture.setOnClickListener {
                ImagePicker.with(this)
                    .compress(1024)
                    .crop(1f,1f)
                    .createIntent { intent ->
                        startForProfileImageResult.launch(intent)
                    }
            }
            binding.btnChooseDate.visibility = View.VISIBLE
            binding.btnEditProfilePicture.isEnabled = true
            binding.etLocation.editText?.inputType = InputType.TYPE_CLASS_TEXT

        }else{
            binding.btnSaveProfile.visibility = View.GONE
            binding.btnEditProfile.visibility = View.VISIBLE
            binding.btnEditUsername.visibility = View.VISIBLE
            binding.btnEditBirth.visibility = View.VISIBLE
            binding.btnEditLocation.visibility = View.VISIBLE
            binding.radioGenderFemale.isEnabled = false
            binding.radioGenderMale.isEnabled = false
            binding.btnChooseDate.visibility = View.GONE
            binding.etUsername.editText?.inputType = InputType.TYPE_NULL
            binding.etLocation.editText?.inputType = InputType.TYPE_NULL
            binding.btnEditProfilePicture.setOnClickListener {
            }
            binding.btnEditProfilePicture.isEnabled = false
        }
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            if (resultCode == Activity.RESULT_OK) {
                val fileUri = data?.data!!

                binding.ivUserProfile.setImageURI(fileUri)
                localPhotoPath = fileUri

            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                makeToast(ImagePicker.getError(data))
            } else {

            }
        }

    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("LOGOUT")
        builder.setMessage("Apakah kamu yakin akan logout?")

        builder.setPositiveButton("Iya") { dialog, which ->
            dialog.dismiss()
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(activity, SplashActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            activity?.finish()
        }

        builder.setNegativeButton(
            "Tidak"
        ) { dialog, which -> dialog.dismiss() }
        val dialog = builder.create()
        dialog.show()
    }

    private fun makeToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}