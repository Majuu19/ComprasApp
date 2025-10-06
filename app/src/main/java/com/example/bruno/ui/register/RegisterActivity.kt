package com.example.bruno.ui.register

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bruno.databinding.ActivityRegisterBinding


class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // liga o layout XML com o código via ViewBinding
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnCreateAccount.setOnClickListener {
            val name = binding.inputName.text.toString().trim()
            val email = binding.inputEmail.text.toString().trim()
            val pass = binding.inputPassword.text.toString().trim()
            val confirm = binding.inputConfirm.text.toString().trim()

            // validar campos
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

                else -> {
                    // passou em todas as verificações
                    Toast.makeText(
                        this,
                        "Conta criada com sucesso (simulação)",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish() // volta pra tela de login
                }
            }
        }

        // clique no texto "já tem conta? entrar"
        binding.txtBackToLogin.setOnClickListener {
            finish()
        }
    }
}
