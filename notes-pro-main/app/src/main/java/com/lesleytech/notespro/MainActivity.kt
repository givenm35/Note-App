package com.lesleytech.notespro

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.lesleytech.notespro.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding;
    private val firebaseAuth = FirebaseAuth.getInstance();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.root);

        binding.helloTxt.text = "Hello ${firebaseAuth.currentUser!!.displayName}, welcome back";

        binding.logoutBtn.setOnClickListener {
            firebaseAuth.signOut();
            startActivity(Intent(this, LoginActivity::class.java));
            finish();
        }
    }
}