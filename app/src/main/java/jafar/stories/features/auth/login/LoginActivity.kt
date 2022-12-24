package jafar.stories.features.auth.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import jafar.stories.R
import jafar.stories.customview.MyCustomEmail
import jafar.stories.customview.MyCustomPassword
import jafar.stories.data.model.LoginRequest
import jafar.stories.data.repository.Result
import jafar.stories.databinding.ActivityLoginBinding
import jafar.stories.features.auth.LoginViewModel
import jafar.stories.features.auth.register.RegisterActivity
import jafar.stories.features.main.MainActivity
import jafar.stories.utils.ViewModelFactory
import jafar.stories.utils.hideSoftKeyboard
import jafar.stories.utils.showAlertLoading

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var backPressedTime = 0L
    private lateinit var email: MyCustomEmail
    private lateinit var password: MyCustomPassword
    private lateinit var loadingBar: AlertDialog
    private lateinit var login: Button
    private lateinit var viewModel: ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        email = binding.edLoginEmail
        password = binding.edLoginPassword
        login = binding.loginButton
        loadingBar = showAlertLoading(this)
        loadingBar.dismiss()

        binding.regisButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        setupViewModel()
        verifyLoginState()
        doLogin()
    }

    private fun setupViewModel() {
        val factory = ViewModelFactory.getAuthInstance(this)
        val loginViewModel: LoginViewModel by viewModels { factory }
        viewModel = loginViewModel
    }

    private fun verifyLoginState() {
        val loginViewModel = this.viewModel as LoginViewModel
        loginViewModel.loginFormState.observe(this, Observer {
            val loginState = it ?: return@Observer

            login.isEnabled = loginState.isDataValid
            if (loginState.emailError != null) email.error = getString(loginState.emailError)
            if (loginState.passwordError != null) password.error =
                getString(loginState.passwordError)
        })

        email.afterTextChanged {
            loginViewModel.loginDataChanged(email.text.toString(), password.text.toString().trim())
        }
        password.afterTextChanged {
            loginViewModel.loginDataChanged(email.text.toString(), password.text.toString().trim())
        }
    }

    private fun doLogin() {
        login.setOnClickListener {
            hideSoftKeyboard(this, binding.root)
            val email = email.text.toString().trim()
            val password = password.text.toString().trim()
            val request = LoginRequest(email, password)

            val loginViewModel = this.viewModel as LoginViewModel
            loginViewModel.doLogin(request).observe(this) { result ->
                if (result == null) return@observe
                when (result) {
                    is Result.Loading -> loadingBar.show()
                    is Result.Error -> {
                        if (result.error.isNotEmpty()) loadingBar.dismiss()
                        MaterialAlertDialogBuilder(this)
                            .setTitle(
                                resources.getString(
                                    R.string.title_login_failed
                                )
                            )
                            .setMessage(result.error)
                            .setPositiveButton(resources.getString(R.string.button_ok)) { dialog, _ -> dialog.dismiss() }
                            .show()
                    }
                    is Result.Success -> {
                        loadingBar.dismiss()
                        loginViewModel.saveUserToken(result.data.token)
                        loginViewModel.saveLoginState(true)
                        startActivity(Intent(this, MainActivity::class.java))
                            .also { finishAffinity() }
                    }
                }
            }
        }
    }

    private fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                afterTextChanged.invoke(editable.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }

    override fun onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) super.onBackPressed()
        else Toast.makeText(this, getString(R.string.exit_app), Toast.LENGTH_SHORT).show()
        backPressedTime = System.currentTimeMillis()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
