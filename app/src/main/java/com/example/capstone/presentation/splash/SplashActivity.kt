package com.example.capstone.presentation.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.example.capstone.databinding.ActivitySplashBinding
import com.example.capstone.presentation.MainActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivLogo.alpha = 0f
        binding.ivLogo.scaleX = 0.7f
        binding.ivLogo.scaleY = 0.7f
        binding.tvAppName.alpha = 0f
        binding.tvTagline.alpha = 0f

        binding.ivLogo.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(800)
            .setInterpolator(DecelerateInterpolator())
            .start()

        binding.tvAppName.animate()
            .alpha(1f)
            .setDuration(800)
            .setStartDelay(200)
            .start()

        binding.tvTagline.animate()
            .alpha(1f)
            .setDuration(800)
            .setStartDelay(400)
            .start()

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 1800)
    }
}
