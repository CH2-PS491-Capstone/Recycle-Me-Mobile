package com.bangkit.recycleme.ui.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bangkit.recycleme.R
import com.bangkit.recycleme.ScanActivity
import com.bangkit.recycleme.SettingsActivity
import com.bangkit.recycleme.ui.welcome.AuthViewModel
import com.bangkit.recycleme.ui.welcome.MainActivity
import com.bangkit.recycleme.databinding.FragmentProfileBinding
import com.bangkit.recycleme.detail.DetailRecyclingViewModel
import com.bangkit.recycleme.models.UserModel
import com.bangkit.recycleme.di.UserPreference
import com.bangkit.recycleme.factory.ViewModelFactory
import com.bangkit.recycleme.di.dataStore
import com.bangkit.recycleme.models.Recycling
import com.bangkit.recycleme.models.RecyclingResponse
import com.bangkit.recycleme.ui.recyclingresult.RecyclingResult
import com.bangkit.recycleme.ui.recyclingresult.RecyclingResultViewModel
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {
    private val viewModel by viewModels<AuthViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    private val viewModelTotal by viewModels<ProfileViewModel> {
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

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userPreference = UserPreference.getInstance(requireContext().dataStore)

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            userPreference.getSession().collect { userModel ->
                withContext(Dispatchers.Main) {
                    displayEmail(userModel)
                }
            }
        }

        val pref = UserPreference.getInstance(requireContext().dataStore)
        val token = runBlocking { pref.getSession().first().token }

        viewModelTotal.loadTotalData(token)

        viewModelTotal.totalCoins.observe(viewLifecycleOwner) { totalCoins ->
            binding.tvCoinCount.text = totalCoins.toString()
        }

        viewModelTotal.totalRecycling.observe(viewLifecycleOwner) { totalRecycling ->
            binding.tvRecyclingCount.text = totalRecycling.toString()+"x"
        }

        viewModelTotal.error.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_SHORT).show()
        }

        binding.buttonLogout.setOnClickListener {
            viewModel.logout()
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        binding.buttonRecycling.setOnClickListener {
            val intent = Intent(requireContext(), RecyclingResult::class.java)
            startActivity(intent)
        }

        binding.buttonSetting.setOnClickListener{
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun displayEmail(userModel: UserModel) {
        val emailTextView = binding.tvProfileEmail
        emailTextView.text = userModel.email
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

