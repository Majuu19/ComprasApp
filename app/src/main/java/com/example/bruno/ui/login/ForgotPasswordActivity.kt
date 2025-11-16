package com.example.bruno.ui.login

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.bruno.data.firebase.AuthService
import com.example.bruno.databinding.ActivityForgotPasswordBinding
import kotlinx.coroutines.launch

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private val authService = AuthService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSendEmail.setOnClickListener {
            val email = binding.inputEmail.text.toString().trim()

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Digite um e-mail válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            sendResetEmail(email)
        }
    }

    private fun sendResetEmail(email: String) {
        lifecycleScope.launch {

            val checkResult = authService.checkUserExists(email)

            if (checkResult.isSuccess) {
                if (checkResult.getOrDefault(false)) {

                    val resetResult = authService.resetPassword(email)
                    if (resetResult.isSuccess) {
                        Toast.makeText(
                            this@ForgotPasswordActivity,
                            "E-mail enviado! Verifique sua caixa de entrada.",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@ForgotPasswordActivity,
                            "Erro ao enviar e-mail. Tente novamente.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {

                    Toast.makeText(
                        this@ForgotPasswordActivity,
                        "Este e-mail não está cadastrado no sistema.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {

                Toast.makeText(
                    this@ForgotPasswordActivity,
                    "Ocorreu um erro ao verificar o e-mail. Tente novamente.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
