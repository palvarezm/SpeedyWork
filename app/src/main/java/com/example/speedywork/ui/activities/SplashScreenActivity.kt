package com.example.speedywork.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.example.speedywork.R
import com.example.speedywork.utils.Animation
import kotlinx.android.synthetic.main.activity_main.*

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        showWelcomeText()
        endSplash()
    }

    private fun endSplash() {
        Handler().postDelayed({
            intent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
            startActivity(intent)
            Animation.FadeAnimation(this@SplashScreenActivity)
        },3500)
    }

    private fun showWelcomeText() {
        Handler().postDelayed({
            tvWelcomeFirst.visibility = View.VISIBLE
            tvWelcomeSecond.visibility = View.VISIBLE
        },1500)
    }
}
