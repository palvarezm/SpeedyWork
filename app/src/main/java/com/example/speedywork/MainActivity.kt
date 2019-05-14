package com.example.speedywork

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        showWelcomeText()
    }

    private fun showWelcomeText() {
        Handler().postDelayed({
            tvWelcomeFirst.visibility = View.VISIBLE
            tvWelcomeSecond.visibility = View.VISIBLE
        },2000)
    }
}
