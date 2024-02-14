package com.lesleytech.notespro

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.lesleytech.notespro.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater);
        setContentView(binding.root)

        binding.loginBtn.setOnClickListener { loginUser() }

        binding.gotoSignupTxt.setOnClickListener {
            startActivity(Intent(this, CreateAccountActivity::class.java));
        }
    }

    private fun loginUser() {
        val email = binding.emailInput.text.toString();
        val password = binding.passwordInput.text.toString();

        binding.emailInput.error = null;
        binding.passwordInput.error = null;

        if (!validateData(email, password)) return;

        loginUserInFirebase(email, password);
    }

    private fun loginUserInFirebase(email: String, password: String) {
        changeInProgress(true);

        val firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                if (firebaseAuth.currentUser?.isEmailVerified == true) {
                    Utility.showToast(this, "Logged in successfully");
                    startActivity(Intent(this, MainActivity::class.java));
                    finish();
                } else {
                    Utility.showToast(this, "Please verify your email");
                    firebaseAuth.signOut()
                }
            } else {
                Utility.showToast(
                    this,
                    it.exception?.localizedMessage ?: "An error occurred",
                )
            }

            changeInProgress(false);
        }
    }

    private fun changeInProgress(inProgress: Boolean) {
        if (inProgress) {
            binding.progressBar.visibility = View.VISIBLE;
            binding.loginBtn.visibility = View.GONE;
        } else {
            binding.progressBar.visibility = View.GONE;
            binding.loginBtn.visibility = View.VISIBLE;
        }
    }

    private fun validateData(email: String, password: String): Boolean {
        if (binding.emailInput.text.isNullOrEmpty()) {
            binding.emailInput.error = "Required"
            binding.emailInput.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailInput.error = "Email is invalid";
            binding.emailInput.requestFocus();
            return false;
        }

        if (binding.passwordInput.text.isNullOrEmpty()) {
            binding.passwordInput.error = "Required"
            binding.passwordInput.requestFocus();
            return false;
        }

        if (password.length < 6) {
            binding.passwordInput.error = "Password is too short"
            binding.passwordInput.requestFocus();
            return false;
        }

        return true;
    }
}