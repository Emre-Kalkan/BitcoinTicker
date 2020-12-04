package com.emrekalkan.bitcointicker.ui.signin

import android.content.SharedPreferences
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.emrekalkan.bitcointicker.core.BaseViewModel
import com.emrekalkan.bitcointicker.data.repository.FirebaseAuthRepository
import com.emrekalkan.bitcointicker.utils.Resource
import com.emrekalkan.bitcointicker.utils.SharedPrefConst
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult

class SignInViewModel @ViewModelInject constructor(
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val sharedPreferences: SharedPreferences
) : BaseViewModel() {

    private val _signInResult = MutableLiveData<Resource<Task<AuthResult>>>()
    val signInResult: LiveData<Resource<Task<AuthResult>>> = _signInResult

    val email = ObservableField<String>(sharedPreferences.getString(SharedPrefConst.USER_EMAIL, ""))

    var isInitiallyAnimated: Boolean = false

    fun signIn(email: String, password: String) {
        firebaseAuthRepository.signIn(email, password) {
            _signInResult.postValue(it)

            if (it.data?.isSuccessful == true) {
                sharedPreferences.edit().putString(SharedPrefConst.USER_EMAIL, email).apply()
            }
        }
    }
}