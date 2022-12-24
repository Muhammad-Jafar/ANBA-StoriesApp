package jafar.stories.features.splashscreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import jafar.stories.R
import jafar.stories.databinding.ActivitySplashScreenBinding
import jafar.stories.features.auth.SplashScreenViewModel
import jafar.stories.features.auth.login.LoginActivity
import jafar.stories.features.main.MainActivity
import jafar.stories.utils.ViewModelFactory

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var viewModel: ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        animation()
        setupViewModel()
        splashTime()
    }

    private fun animation() {
        val appLogoAnimate = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        binding.apply {
            titleApp.animation = appLogoAnimate
            versionTitle.animation = appLogoAnimate
        }
    }

    private fun setupViewModel() {
        val factory = ViewModelFactory.getAuthInstance(this)
        val splashScreenViewModel: SplashScreenViewModel by viewModels { factory }
        viewModel = splashScreenViewModel
    }

    private fun splashTime() {
        val splashScreenViewModel = this.viewModel as SplashScreenViewModel
        splashScreenViewModel.checkLoginState.observe(this) {
            if (!it) {
                Handler(Looper.getMainLooper()).postDelayed({
                    startActivity(Intent(this, LoginActivity::class.java))
                    finishAffinity()
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }, 1000)
            } else {
                Handler(Looper.getMainLooper()).postDelayed({
                    startActivity(Intent(this, MainActivity::class.java))
                    finishAffinity()
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }, 2000)
            }
        }
    }
}

