// ============================================
// FILE: SplashActivity.kt
// LOCATION: Ernest-Movies-app/app/src/main/java/com/ernestmovies/app/SplashActivity.kt
// ============================================
package com.ernestmovies.app

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ernestmovies.app.databinding.ActivitySplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySplashBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        animateLogo()
        startTypingAnimation()
        
        lifecycleScope.launch {
            delay(3500)
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
    
    private fun animateLogo() {
        binding.logoImageView.apply {
            alpha = 0f
            scaleX = 0.5f
            scaleY = 0.5f
            
            animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(1000)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
        }
    }
    
    private fun startTypingAnimation() {
        val fullText = "ERNEST MOVIE PALACE™"
        var currentText = ""
        
        lifecycleScope.launch {
            delay(500)
            fullText.forEach { char ->
                currentText += char
                binding.titleTextView.text = currentText
                delay(120)
            }
        }
        
        binding.loadingProgressBar.apply {
            ObjectAnimator.ofInt(this, "progress", 0, 100).apply {
                duration = 3000
                interpolator = AccelerateDecelerateInterpolator()
                start()
            }
        }
    }
}