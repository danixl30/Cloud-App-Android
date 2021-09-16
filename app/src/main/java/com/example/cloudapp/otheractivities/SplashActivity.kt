package com.example.cloudapp.otheractivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.example.cloudapp.MainActivity
import com.example.cloudapp.R
import com.example.cloudapp.controller.SharedApp

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()
        setContentView(R.layout.activity_splash)
        findViewById<TextView>(R.id.appName).animation = AnimationUtils.loadAnimation(this, R.anim.anim_splah_down)
        findViewById<ImageView>(R.id.appBack).animation = AnimationUtils.loadAnimation(this, R.anim.anim_splah_down)
        findViewById<TextView>(R.id.byText).animation = AnimationUtils.loadAnimation(this, R.anim.anim_splah_up)
        SharedApp.prefs.setItemClipBoard("", "", "")
        SharedApp.prefs.unSetItem()
        Handler().postDelayed(Runnable {
            val mainScreen = Intent(this, MainActivity::class.java)
            startActivity(mainScreen)
            finish()
        }, 4000)
    }
}