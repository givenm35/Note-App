package com.lesleytech.notespro

import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.lesleytech.notespro.databinding.ActivityCreateAccountBinding

class CreateAccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateAccountBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAccountBinding.inflate(layoutInflater);
        setContentView(binding.root);

        binding.createAccountBtn.setOnClickListener { createAccount() }
        binding.gotoSignInTxt.setOnClickListener {
            onBackPressedDispatcher.onBackPressed();
        }
    }

    private fun createAccount() {
        val username = binding.usernameInput.text.trim().toString();
        val email = binding.emailInput.text.toString();
        val password = binding.passwordInput.text.toString();
        val confirmPass = binding.confirmPassInput.text.toString();

        binding.emailInput.error = null;
        binding.passwordInput.error = null;
        binding.confirmPassInput.error = null;
        binding.usernameInput.error = null;

        if (!validateData(username, email, password, confirmPass)) return;

        createAccountInFirebase(username, email, password);
    }

    private fun createAccountInFirebase(username: String, email: String, password: String) {
        changeInProgress(true);

        val firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                firebaseAuth.currentUser?.sendEmailVerification();
                firebaseAuth.currentUser?.updateProfile(userProfileChangeRequest {
                    displayName = username
                })
                    ?.addOnCompleteListener {
                        firebaseAuth.signOut();
                        onBackPressedDispatcher.onBackPressed();
                        Utility.showToast(
                            this,
                            "Account created successfully. We sent you a verification email",
                        )
                    }

            } else {
                changeInProgress(false);
                Utility.showToast(
                    this,
                    it.exception?.localizedMessage ?: "An error occurred",
                )
            }

        }
    }

    private fun changeInProgress(inProgress: Boolean) {
        if (inProgress) {
            binding.progressBar.visibility = View.VISIBLE;
            binding.createAccountBtn.visibility = View.GONE;
        } else {
            binding.progressBar.visibility = View.GONE;
            binding.createAccountBtn.visibility = View.VISIBLE;
        }
    }

    private fun validateData(
        username: String,
        email: String,
        password: String,
        confirmPass: String
    ): Boolean {
        if (binding.usernameInput.text.isNullOrEmpty()) {
            binding.passwordInput.error = "Required"
            binding.passwordInput.requestFocus();
            return false;
        }

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

        if (binding.confirmPassInput.text.isNullOrEmpty()) {
            binding.confirmPassInput.error = "Required"
            binding.confirmPassInput.requestFocus();
            return false;
        }

        if (username.length < 3) {
            binding.passwordInput.error = "Username is too short"
            binding.passwordInput.requestFocus();
            return false;
        }

        if (password.length < 6) {
            binding.passwordInput.error = "Password is too short"
            binding.passwordInput.requestFocus();
            return false;
        }

        if (confirmPass != password) {
            binding.confirmPassInput.error = "Passwords do not match";
            binding.confirmPassInput.requestFocus();
            return false;
        }

        return true;
    }
}
