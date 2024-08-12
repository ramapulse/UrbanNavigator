package com.android.urbannavigator.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.urbannavigator.data.model.Event
import com.android.urbannavigator.data.model.Komentar
import com.android.urbannavigator.data.model.Post
import com.android.urbannavigator.data.model.Taman
import com.android.urbannavigator.data.model.Ulasan
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
    private val postRef: DatabaseReference = database.getReference("Post")
    private val ulasanRef: DatabaseReference = database.getReference("Ulasan")
    private val komentarRef: DatabaseReference = database.getReference("Komentar")


    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User> get() = _currentUser

    private val _tamanList = MutableLiveData<List<Taman>>()
    val tamanList: LiveData<List<Taman>> get() = _tamanList

    private val _communityList = MutableLiveData<List<Post>>()
    val communityList: LiveData<List<Post>> get() = _communityList

    private val _userList = MutableLiveData<List<User>>()
    val userList: LiveData<List<User>> get() = _userList
    private val _ulasanList = MutableLiveData<List<Ulasan>>()
    val ulasanList: LiveData<List<Ulasan>> get() = _ulasanList

    private val _eventList = MutableLiveData<List<Event>>()
    val eventList: LiveData<List<Event>> get() = _eventList
    private val _komentarList = MutableLiveData<List<Komentar>>()
    val komentarList: LiveData<List<Komentar>> get() = _komentarList

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _errorMsg = MutableLiveData<String>()
    val errorMsg: LiveData<String> get() = _errorMsg

    private val _errorResetApp = MutableLiveData<Boolean>()
    val errorResetApp: LiveData<Boolean> get() = _errorResetApp
    private var searchText: String = ""

    private val _filteredTamanList = MutableLiveData<List<Taman>>()
    val filteredTamanList: LiveData<List<Taman>> get() = _filteredTamanList

    fun searchPets(query: String) {
        searchText = query
        applySearchTamanFilters()
    }

    private fun applySearchTamanFilters() {
        val filteredList = _tamanList.value?.filter { taman ->
            val matchesSearch = taman.nama.contains(searchText, ignoreCase = true)
            matchesSearch }
        _filteredTamanList.value = filteredList ?: listOf()
    }

    init {
        observeCurrentUser()
        observeTamanList()
        observeEventList()
        observePostsList()
        observeUserList()
        observeUlasanList()
        observeKomentarList()
    }

    private fun observeKomentarList() {
        _loading.value = true
        komentarRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listKomentar = mutableListOf<Komentar>()
                for (komentarSnapshot in snapshot.children) {
                    val komentar = komentarSnapshot.getValue(Komentar::class.java)
                    if (komentar != null) {
                        listKomentar.add(komentar)
                    }
                }
                listKomentar.sortedBy { it.waktu }
                _loading.value = false
                _komentarList.value = listKomentar
            }

            override fun onCancelled(error: DatabaseError) {
                _errorMsg.value = error.message
                _loading.value = false
            }
        })
    }

    private fun observeUlasanList() {
        _loading.value = true
        ulasanRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listUlasan = mutableListOf<Ulasan>()
                for (ulasanSnapshot in snapshot.children) {
                    val ulasan = ulasanSnapshot.getValue(Ulasan::class.java)
                    if (ulasan != null) {
                        listUlasan.add(ulasan)
                    }
                }
                listUlasan.sortByDescending { it.waktu }
                _loading.value = false
                _ulasanList.value = listUlasan
            }

            override fun onCancelled(error: DatabaseError) {
                _errorMsg.value = error.message
                _loading.value = false
            }
        })
    }

    private fun observePostsList() {
        _loading.value = true
        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val postList = mutableListOf<Post>()
                for (postSnapshot in snapshot.children) {
                    val post = postSnapshot.getValue(Post::class.java)
                    if (post != null) {
                        postList.add(post)
                    }
                }
                postList.sortByDescending { it.waktu }
                _loading.value = false
                _communityList.value = postList
            }

            override fun onCancelled(error: DatabaseError) {
                _errorMsg.value = error.message
                _loading.value = false
            }
        })
    }

    private fun observeUserList() {
        _loading.value = true
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listUser = mutableListOf<User>()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    if (user != null) {
                        listUser.add(user)
                    }
                }
                _loading.value = false
                _userList.value = listUser
            }

            override fun onCancelled(error: DatabaseError) {
                _errorMsg.value = error.message
                _loading.value = false
            }
        })
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
                applySearchTamanFilters()
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