package com.android.urbannavigator.presentation

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.urbannavigator.R
import com.android.urbannavigator.data.model.Event
import com.android.urbannavigator.data.model.Taman
import com.android.urbannavigator.databinding.ActivitySplashBinding
import com.android.urbannavigator.presentation.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        Handler(Looper.getMainLooper()).postDelayed({
            if(FirebaseAuth.getInstance().currentUser != null){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 2500)

//        val database = FirebaseDatabase.getInstance()
//        val tamanRef = database.getReference("Taman")
//        val newTamanRef = tamanRef.push()
//        val tamanId = newTamanRef.key ?: ""
//
//        val park = Taman(tamanId, listOf("https://firebasestorage.googleapis.com/v0/b/urbannavigator-1ee9d.appspot.com/o/taman%2FNuArt20Park%2520Tickets-2dab5a4c-426d-4ad3-8012-11e531d2962b.webp?alt=media&token=d7936ca0-126e-4132-9dec-bc6b12e8d465",
//            "https://firebasestorage.googleapis.com/v0/b/urbannavigator-1ee9d.appspot.com/o/taman%2Fdev-nu-art-sculpture-park-galeri-seni-yang-menyatu-dengan-taman.jpeg?alt=media&token=00fb5827-dd68-42da-b4a9-87c929cde748",
//            "https://firebasestorage.googleapis.com/v0/b/urbannavigator-1ee9d.appspot.com/o/taman%2Fimg-20220909-101645-80afaf80dfc3e1e885e1cfd4e33be502_600x400.jpg?alt=media&token=e0383cb5-196b-40ea-a2c9-87feea0ad6ba",
//            "https://firebasestorage.googleapis.com/v0/b/urbannavigator-1ee9d.appspot.com/o/taman%2Fnuart-sculpture-park%20(1).jpg?alt=media&token=e8f3da8a-f11f-4a6c-ac88-86732b9d4eaf",
//            "https://firebasestorage.googleapis.com/v0/b/urbannavigator-1ee9d.appspot.com/o/taman%2Fnuart-sculpture-park.jpg?alt=media&token=40f4c58f-2ecb-438e-b701-88ed4514bdb7"),
//            "NuArt Sculpture Park",
//            "Selasa",
//            "Minggu",
//            "09:00 AM",
//            "05:00 PM",
//            "NuArt Sculpture Park adalah sebuah museum galeri seni patung yang terletak di bagian Bandung Utara, Jawa Barat, Indonesia. NuArt Sculpture Park pertama kali dibuka pada tahun 2000. Tempat ini merupakan pusat seni patung karya Nyoman Nuarta. Di dalam gallery menampilkan karya–karya patung hasil Nyoman Nuarta dari awal karier hingga karya terbaru sebagai pemeran utama. Selain museum gallery, di sini juga merupakan tempat proses pembuatan patung karya Nyoman Nuarta itu sendiri.",
//            "Setra Duta Raya No.L6, Ciwaruga, Kec. Parongpong, Bandung, Jawa Barat 40151",
//            -6.877582825149174, 107.5722668366804)
//
//
//        newTamanRef.setValue(park).addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                Toast.makeText(this, "Sukses", Toast.LENGTH_SHORT).show()
//            }
//        }

//        val database = FirebaseDatabase.getInstance()
//        val tamanRef = database.getReference("Event")
//        val newTamanRef = tamanRef.push()
//        val tamanId = newTamanRef.key ?: ""
//
//        val event = Event(
//            tamanId,
//            "-O3wWCW6w35SIHHk48A0",
//            "Pop Up Market",
//            "Nikmati berbagai pilihan makanan dan minuman menarik dari tenant-tenant terbaik.\n" +
//                    "Gratis masuk area pop up market, jadi ajak keluarga dan teman-temanmu untuk merasakan keseruan ini!",
//            "https://firebasestorage.googleapis.com/v0/b/urbannavigator-1ee9d.appspot.com/o/event%2Fpop_up_nuart.jpg?alt=media&token=bd4927d7-2157-4f6b-bcbf-2340e41cdd08",
//            "9 Agustus 2024",
//            "11 Agustus 2024",
//            "11.00",
//            "22.00"
//        )
//
//
//        newTamanRef.setValue(event).addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                Toast.makeText(this, "Sukses", Toast.LENGTH_SHORT).show()
//            }
//        }
    }
}