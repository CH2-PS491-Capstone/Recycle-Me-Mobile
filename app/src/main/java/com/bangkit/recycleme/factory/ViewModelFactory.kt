package com.bangkit.recycleme.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangkit.recycleme.detail.DetailRecyclingViewModel
import com.bangkit.recycleme.di.Injection
import com.bangkit.recycleme.di.UserRepository
import com.bangkit.recycleme.ui.welcome.AuthViewModel
import com.bangkit.recycleme.ui.login.LoginViewModel
import com.bangkit.recycleme.ui.profile.ProfileViewModel
import com.bangkit.recycleme.ui.recycling.RecyclingViewModel
import com.bangkit.recycleme.ui.recyclingresult.RecyclingResultViewModel
import com.bangkit.recycleme.ui.register.RegisterViewModel

class ViewModelFactory(private val repository: UserRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(repository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(repository) as T
            }
            modelClass.isAssignableFrom(RecyclingViewModel::class.java) -> {
                RecyclingViewModel(repository) as T
            }
            modelClass.isAssignableFrom(RecyclingResultViewModel::class.java) -> {
                RecyclingResultViewModel(repository) as T
            }
            modelClass.isAssignableFrom(DetailRecyclingViewModel::class.java) -> {
                DetailRecyclingViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(Injection.provideRepository(context))
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}