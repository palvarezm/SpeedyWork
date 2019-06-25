package com.example.speedywork.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.speedywork.R
import com.example.speedywork.utils.UsersList
import com.example.speedywork.utils.validate
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    lateinit var username: String
    lateinit var password: String
    var usersList = UsersList
    val notEmpty: TextView.() -> Boolean = {text.isNotEmpty()}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setActions()
    }

    private fun setActions() {
        btLogin.setOnClickListener {
            username = tietEmail.text.toString()
            password = tietPassword.text.toString()

            if (tietEmail.validate(notEmpty, tilEmail, "Debe ingresar su correo") && tietPassword.validate(notEmpty, tilPassword, "Debe llenar su contraseña")){
                if (usersList.users.contains(username) && (usersList.passwords.contains(password))){
                    MaterialAlertDialogBuilder(this).setPositiveButton("Aceptar", null).create().apply {
                        setTitle("Exito")
                        setMessage("Datos de ingreso correctos")
                        show()
                    }
                }
                else{
                    MaterialAlertDialogBuilder(this).setPositiveButton("Aceptar", null).create().apply {
                        setTitle("Error")
                        setMessage("Datos de ingreso incorrectos")
                        show()
                    }
                }
            }

        }
    }


}
