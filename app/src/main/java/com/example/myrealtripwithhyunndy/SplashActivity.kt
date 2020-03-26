package com.example.myrealtripwithhyunndy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashActivity : AppCompatActivity() {

    // 1.3간 스플래시 화면을 보여줌
    private val SPLASH_REMAIN_TIME: Long = 1300

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            }, SPLASH_REMAIN_TIME)
    }
}
