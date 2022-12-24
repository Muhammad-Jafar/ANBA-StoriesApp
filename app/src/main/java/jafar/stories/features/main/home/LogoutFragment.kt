package jafar.stories.features.main.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import jafar.stories.databinding.ConfirmLogoutDialogBinding
import jafar.stories.features.auth.login.LoginActivity
import jafar.stories.features.main.HomeViewModel
import jafar.stories.utils.ViewModelFactory

class LogoutFragment : BottomSheetDialogFragment() {
    private var _binding: ConfirmLogoutDialogBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ConfirmLogoutDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViewModel()
        binding.apply {
            cancelButton.setOnClickListener { this@LogoutFragment.dismiss() }
            confirmButton.setOnClickListener {
                startActivity(Intent(requireActivity(), LoginActivity::class.java)).also {
                    (viewModel as HomeViewModel).changeLoginState(false)
                    (viewModel as HomeViewModel).removeUserToken()
                }
            }
        }
    }

    private fun setupViewModel() {
        val factory = ViewModelFactory.getStoryInstance(requireContext())
        val viewModel: HomeViewModel by viewModels { factory }
        this.viewModel = viewModel
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
