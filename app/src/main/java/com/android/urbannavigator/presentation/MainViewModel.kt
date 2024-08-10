package com.android.urbannavigator.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.urbannavigator.data.model.Event
import com.android.urbannavigator.data.model.Taman
import com.android.urbannavigator.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainViewModel: ViewModel() {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val userRef: DatabaseReference = database.getReference("Users")
    private val tamanRef: DatabaseReference = database.getReference("Taman")
    private val eventRef: DatabaseReference = database.getReference("Event")
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User> get() = _currentUser

    private val _tamanList = MutableLiveData<List<Taman>>()
    val tamanList: LiveData<List<Taman>> get() = _tamanList

    private val _eventList = MutableLiveData<List<Event>>()
    val eventList: LiveData<List<Event>> get() = _eventList

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _errorMsg = MutableLiveData<String>()
    val errorMsg: LiveData<String> get() = _errorMsg

    private val _errorResetApp = MutableLiveData<Boolean>()
    val errorResetApp: LiveData<Boolean> get() = _errorResetApp

    init {
        observeCurrentUser()
        observeTamanList()
        observeEventList()
    }

    private fun observeTamanList() {
        _loading.value = true
        tamanRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tamanList = mutableListOf<Taman>()
                for (tamanSnapshot in snapshot.children) {
                    val taman = tamanSnapshot.getValue(Taman::class.java)
                    if (taman != null) {
                        tamanList.add(taman)
                    }
                }
                _loading.value = false
                _tamanList.value = tamanList
            }

            override fun onCancelled(error: DatabaseError) {
                _errorMsg.value = error.message
                _loading.value = false
            }
        })
    }

    private fun observeEventList() {
        _loading.value = true
        eventRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val eventList = mutableListOf<Event>()
                for (eventSnapshot in snapshot.children) {
                    val event = eventSnapshot.getValue(Event::class.java)
                    if (event != null) {
                        eventList.add(event)
                    }
                }
                _loading.value = false
                _eventList.value = eventList
            }

            override fun onCancelled(error: DatabaseError) {
                _errorMsg.value = error.message
                _loading.value = false
            }
        })
    }

    private fun observeCurrentUser() {
        val currentUserUid = auth.currentUser?.uid
        if (currentUserUid != null) {
            _errorResetApp.value = false
            _loading.value = true
            userRef.child(currentUserUid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null) {
                        _currentUser.value = user!!
                    } else {
                        _errorResetApp.value = true
                        _errorMsg.value = "User not found"
                    }
                    _loading.value = false
                }

                override fun onCancelled(error: DatabaseError) {
                    _errorMsg.value = error.message
                    _loading.value = false
                }
            })
        }else{
            _errorResetApp.value = true
        }
    }

}