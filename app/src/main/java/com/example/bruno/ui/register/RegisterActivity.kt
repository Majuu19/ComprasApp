package com.example.bruno.ui.register

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.bruno.data.firebase.AuthService
import com.example.bruno.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val authService = AuthService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCreateAccount.setOnClickListener {
            val name = binding.inputName.text.toString().trim()
            val email = binding.inputEmail.text.toString().trim()
            val pass = binding.inputPassword.text.toString().trim()
            val confirm = binding.inputConfirm.text.toString().trim()

            when {
                name.isEmpty() || email.isEmpty() || pass.isEmpty() || confirm.isEmpty() -> {
                    Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                }

                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    Toast.makeText(this, "E-mail inválido", Toast.LENGTH_SHORT).show()
                }

                pass != confirm -> {
                    Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show()
                }


                pass.length < 6 -> {
                    Toast.makeText(this, "A senha deve ter no mínimo 6 caracteres.", Toast.LENGTH_LONG).show()
                }

                else -> criarConta(name, email, pass)
            }
        }

        binding.txtBackToLogin.setOnClickListener { finish() }
    }

    private fun criarConta(name: String, email: String, pass: String) {
        lifecycleScope.launch {
            val result = authService.createUser(name, email, pass)

            if (result.isSuccess) {
                Toast.makeText(this@RegisterActivity, "Conta criada!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(
                    this@RegisterActivity,
                    result.exceptionOrNull()?.message ?: "Erro inesperado",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
