package com.rifftyo.storyappdicoding.ui.register

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.rifftyo.storyappdicoding.R
import com.rifftyo.storyappdicoding.data.Result
import com.rifftyo.storyappdicoding.databinding.ActivityRegisterBinding
import com.rifftyo.storyappdicoding.ui.ViewModelFactory
import com.rifftyo.storyappdicoding.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        val viewModel: RegisterViewModel by viewModels {
            factory
        }

        binding.nameEditText.addTextChangedListener { checkFieldsValidity() }
        binding.emailEditText.addTextChangedListener { checkFieldsValidity() }
        binding.passwordEditText.addTextChangedListener { checkFieldsValidity() }

        binding.btnRegisterWelcome.setOnClickListener {
            val user = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (user.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.registerUser(user, email, password).observe(this) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }

                            is Result.Success -> {
                                binding.progressBar.visibility = View.GONE
                                showDialog(
                                    "Register Success",
                                    "Your registration was successful!"
                                ) {
                                    val intentHome = Intent(
                                        this,
                                        LoginActivity::class.java
                                    ).apply {
                                        flags =
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    }
                                    startActivity(intentHome)
                                }
                            }

                            is Result.Error -> {
                                binding.progressBar.visibility = View.GONE
                                showDialog("Register Failed", result.error)
                            }
                        }
                    }
                }
            }
        }

        binding.btnRegisterWelcome.isEnabled = false

        supportActionBar?.hide()
        playAnimation()
    }

    private fun showDialog(title: String, message: String, onPositiveClick: (() -> Unit)? = null) {
        AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                onPositiveClick?.invoke()
            }
            create()
            show()
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imgRegister, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
    }

    private fun checkFieldsValidity() {
        val user = binding.nameEditText.text.toString()
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()

        val isPasswordValid = binding.passwordEditText.isPasswordValid
        val isEmailValid = binding.emailEditText.isEmailValid

        binding.btnRegisterWelcome.isEnabled =
            user.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && isPasswordValid && isEmailValid
    }
}