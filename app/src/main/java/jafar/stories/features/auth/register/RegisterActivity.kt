package jafar.stories.features.auth.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import jafar.stories.R
import jafar.stories.customview.MyCustomEmail
import jafar.stories.customview.MyCustomName
import jafar.stories.customview.MyCustomPassword
import jafar.stories.data.model.RegisterRequest
import jafar.stories.data.repository.Result
import jafar.stories.databinding.ActivityRegisterBinding
import jafar.stories.features.auth.RegisterViewModel
import jafar.stories.features.auth.login.LoginActivity
import jafar.stories.utils.ViewModelFactory
import jafar.stories.utils.hideSoftKeyboard
import jafar.stories.utils.showAlertLoading

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var name: MyCustomName
    private lateinit var email: MyCustomEmail
    private lateinit var password: MyCustomPassword
    private lateinit var loadingBar: AlertDialog
    private lateinit var register: Button
    private lateinit var viewModel: ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        name = binding.edRegisterName
        email = binding.edRegisterEmail
        password = binding.edRegisterPassword
        register = binding.regisButton
        loadingBar = showAlertLoading(this)
        loadingBar.dismiss()

        binding.loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        setupViewModel()
        verifyRegisterState()
        handleDoRegister()
        playAnimation()
    }

    private fun playAnimation() {
        val labelRegister =
            ObjectAnimator.ofFloat(binding.labelRegister, View.ALPHA, 1f).setDuration(300)
        val labelName = ObjectAnimator.ofFloat(binding.labelName, View.ALPHA, 1f).setDuration(300)
        val labelInputName =
            ObjectAnimator.ofFloat(binding.labelInputName, View.ALPHA, 1f).setDuration(300)
        val labelEmail = ObjectAnimator.ofFloat(binding.labelEmail, View.ALPHA, 1f).setDuration(300)
        val labelInputEmail =
            ObjectAnimator.ofFloat(binding.labelInputEmail, View.ALPHA, 1f).setDuration(300)
        val labelPassword =
            ObjectAnimator.ofFloat(binding.labelPassword, View.ALPHA, 1f).setDuration(300)
        val labelInputPassword =
            ObjectAnimator.ofFloat(binding.labelInputPassword, View.ALPHA, 1f).setDuration(300)
        val regisButton =
            ObjectAnimator.ofFloat(binding.regisButton, View.ALPHA, 1f).setDuration(300)
        val labelLoginButton =
            ObjectAnimator.ofFloat(binding.labelLoginButton, View.ALPHA, 1f).setDuration(300)
        val loginButton =
            ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(300)

        AnimatorSet().apply {
            playSequentially(
                labelRegister,
                labelName,
                labelInputName,
                labelEmail,
                labelInputEmail,
                labelPassword,
                labelInputPassword,
                regisButton,
                labelLoginButton,
                loginButton
            )
            startDelay = 500
            start()
        }
    }

    private fun setupViewModel() {
        val factory = ViewModelFactory.getAuthInstance(this)
        val registerViewModel: RegisterViewModel by viewModels { factory }
        viewModel = registerViewModel
    }

    private fun verifyRegisterState() {
        val registerViewModel = this.viewModel as RegisterViewModel

        registerViewModel.registerFormState.observe(this@RegisterActivity, Observer {
            val registerState = it ?: return@Observer

            register.isEnabled = registerState.isDataValid
            if (registerState.nameError != null) name.error = getString(registerState.nameError)
            if (registerState.emailError != null) email.error = getString(registerState.emailError)
            if (registerState.passwordError != null) password.error =
                getString(registerState.passwordError)
        })

        name.afterTextChanged {
            registerViewModel.registerDataChanged(
                name.text.toString(), email.text.toString(), password.text.toString()
            )
        }
        email.afterTextChanged {
            registerViewModel.registerDataChanged(
                name.text.toString(), email.text.toString(), password.text.toString()
            )
        }
        password.afterTextChanged {
            registerViewModel.registerDataChanged(
                name.text.toString(), email.text.toString(), password.text.toString()
            )
        }
    }

    private fun handleDoRegister() {
        register.setOnClickListener {
            hideSoftKeyboard(this, binding.root)
            val name = name.text.toString().trim()
            val email = email.text.toString().trim()
            val password = password.text.toString().trim()
            val request = RegisterRequest(name, email, password)

            val registerViewModel = this.viewModel as RegisterViewModel
            registerViewModel.doRegister(request).observe(this) { result ->
                if (result == null) return@observe
                when (result) {
                    is Result.Loading -> loadingBar.show()
                    is Result.Error -> {
                        if (result.error.isNotEmpty()) loadingBar.dismiss()
                        MaterialAlertDialogBuilder(this)
                            .setTitle(
                                resources.getString(
                                    R.string.title_register_failed
                                )
                            )
                            .setMessage(result.error)
                            .setPositiveButton(resources.getString(R.string.button_ok)) { dialog, _ -> dialog.dismiss() }
                            .show()
                    }
                    is Result.Success -> {
                        loadingBar.dismiss()
                        MaterialAlertDialogBuilder(this)
                            .setTitle(
                                resources.getString(
                                    R.string.title_register_success
                                )
                            )
                            .setMessage(resources.getString(R.string.body_register_success))
                            .setPositiveButton(resources.getString(R.string.button_ok)) { dialog, _ ->
                                dialog.dismiss()
                                startActivity(Intent(this, LoginActivity::class.java))
                                    .also { finishAffinity() }
                            }.show()
                    }
                }

            }
        }
    }

    private fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                afterTextChanged.invoke(editable.toString())
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

//    override fun onBackPressed() {
//        startActivity(
//            Intent(Intent.ACTION_MAIN)
//                .addCategory(Intent.CATEGORY_HOME)
//                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        )
//    }
}
