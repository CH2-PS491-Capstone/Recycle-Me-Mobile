package com.bangkit.recycleme.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bangkit.recycleme.ui.welcome.AuthViewModel
import com.bangkit.recycleme.ui.welcome.MainActivity
import com.bangkit.recycleme.databinding.FragmentProfileBinding
import com.bangkit.recycleme.models.UserModel
import com.bangkit.recycleme.di.UserPreference
import com.bangkit.recycleme.factory.ViewModelFactory
import com.bangkit.recycleme.di.dataStore
import com.bangkit.recycleme.ui.recyclingresult.RecyclingResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {
    private val viewModel by viewModels<AuthViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private lateinit var userPreference: UserPreference

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userPreference = UserPreference.getInstance(requireContext().dataStore)

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            userPreference.getSession().collect { userModel ->
                withContext(Dispatchers.Main) {
                    displayUserData(userModel)
                }
            }
        }

        binding.logoutButton.setOnClickListener {
            viewModel.logout()
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        binding.buttonRecyclingUser.setOnClickListener {
            val intent = Intent(requireContext(), RecyclingResult::class.java)
            startActivity(intent)
        }
    }

    private fun displayUserData(userModel: UserModel) {
        val emailTextView = binding.tvProfileEmail
        emailTextView.text = userModel.email
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

