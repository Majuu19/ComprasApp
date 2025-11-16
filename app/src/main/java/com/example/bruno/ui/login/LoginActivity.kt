package com.example.bruno.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.bruno.data.firebase.AuthService
import com.example.bruno.databinding.ActivityLoginBinding
import com.example.bruno.ui.main.MainActivity
import com.example.bruno.ui.register.RegisterActivity
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authService = AuthService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnLogin.setOnClickListener {
            val email = binding.inputEmail.text.toString().trim()
            val pass = binding.inputPassword.text.toString().trim()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Preencha e-mail e senha", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Formato de e-mail inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            fazerLogin(email, pass)
        }


        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }


        binding.btnForgotPassword.setOnClickListener {
            showForgotPasswordDialog()
        }
    }


    private fun fazerLogin(email: String, pass: String) {
        lifecycleScope.launch {
            val result = authService.login(email, pass)

            if (result.isSuccess) {
                Toast.makeText(this@LoginActivity, "Login bem-sucedido!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            } else {
                val errorMessage = getFriendlyErrorMessage(result.exceptionOrNull())
                Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun getFriendlyErrorMessage(error: Throwable?): String {
        val defaultError = "Ocorreu um erro ao fazer o login. Tente novamente."
        if (error == null) return defaultError

        return when (error) {
            is FirebaseAuthInvalidUserException -> {
                "Este usuário não existe. Por favor, cadastre-se."
            }
            is FirebaseAuthInvalidCredentialsException -> {
                "E-mail ou senha  incorretos . Tente novamente."
            }
            else -> defaultError
        }
    }


    private fun showForgotPasswordDialog() {
        val input = EditText(this).apply {
            hint = "Digite seu e-mail de cadastro"
            setPadding(60, 40, 60, 40)
        }

        AlertDialog.Builder(this)
            .setTitle("Recuperar Senha")
            .setView(input)
            .setPositiveButton("Enviar") { _, _ ->
                val email = input.text.toString().trim()
                if (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    sendPasswordResetEmail(email)
                } else {
                    Toast.makeText(this, "Por favor, insira um e-mail válido.", Toast.LENGTH_LONG).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }


    private fun sendPasswordResetEmail(email: String) {
        lifecycleScope.launch {
            val resetResult = authService.resetPassword(email)

            if (resetResult.isSuccess) {
                Toast.makeText(
                    this@LoginActivity,
                    "Um link para redefinir sua senha foi enviado para seu e-mail.",
                    Toast.LENGTH_LONG
                ).show()
            } else {

                when (resetResult.exceptionOrNull()) {
                    is FirebaseAuthInvalidUserException -> {

                        Toast.makeText(
                            this@LoginActivity,
                            "Este e-mail não está cadastrado em nosso sistema.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    else -> {

                        val error = resetResult.exceptionOrNull()
                        Toast.makeText(
                            this@LoginActivity,
                            "Erro: ${error?.localizedMessage ?: "Tente novamente"}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }
}
