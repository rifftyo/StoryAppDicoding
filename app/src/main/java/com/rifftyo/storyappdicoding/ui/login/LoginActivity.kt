package com.rifftyo.storyappdicoding.ui.login

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
import com.rifftyo.storyappdicoding.databinding.ActivityLoginBinding
import com.rifftyo.storyappdicoding.ui.ViewModelFactory
import com.rifftyo.storyappdicoding.ui.home.HomeActivity


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var toastShow = false
    private val viewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.emailEditText.addTextChangedListener { checkFieldsValidity() }
        binding.passwordEditText.addTextChangedListener { checkFieldsValidity() }

        binding.btnLoginWelcome.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            binding.progressBar.visibility = View.VISIBLE

            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.loginUser(email, password)
            }
        }

        binding.btnLoginWelcome.isEnabled = false

        viewModel.loginResult.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val token = result.data.loginResult?.token
                    if (token != null) {
                        viewModel.saveUserToken(token)
                    }
                    val homeIntent = Intent(this, HomeActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    if (!toastShow) {
                        toastShow = true
                    }
                    startActivity(homeIntent)
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    if (!toastShow) {
                        showDialog("Login Failed", result.error)
                        toastShow = true
                    }
                }
            }
        }

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
        ObjectAnimator.ofFloat(binding.imgLogin, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
    }

    private fun checkFieldsValidity() {
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()

        val isPasswordValid = binding.passwordEditText.isPasswordValid
        val isEmailValid = binding.emailEditText.isEmailValid

        binding.btnLoginWelcome.isEnabled = email.isNotEmpty() && password.isNotEmpty() && isPasswordValid && isEmailValid
    }
}