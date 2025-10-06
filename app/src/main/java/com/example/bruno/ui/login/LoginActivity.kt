package com.example.bruno.ui.login
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bruno.databinding.ActivityLoginBinding
import com.example.bruno.ui.main.MainActivity
import com.example.bruno.ui.register.RegisterActivity



class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater) // conecta layout com a tela
        setContentView(binding.root)

        // chumbei as credenciais de login no proprio codigo
        val hardEmail = "teste@gmail.com"
        val hardPass = "soso"

        binding.btnLogin.setOnClickListener {// ao clicar no botão "Login"

            val email = binding.inputEmail.text.toString().trim()
            val pass  = binding.inputPassword.text.toString().trim()

            val emailOk = Patterns.EMAIL_ADDRESS.matcher(email).matches()

            if (email.isEmpty() || pass.isEmpty() || !emailOk) {

                Toast.makeText(this, "Preencha e-mail válido e senha.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
              // Se e-mail e senha forem iguais ao que esta chumbado no codigo ele  entra no app
            if (email == hardEmail && pass == hardPass) {
                //se sim abre a tela principal
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                //se nao avisa que esta errado o que foi digitado
            } else {
                Toast.makeText(this, "Credenciais inválidas.", Toast.LENGTH_SHORT).show()
            }
        }
          // se clicar em registrar abre a outra tela de cadastramento
        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))

        }
    }
}
