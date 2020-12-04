package com.emrekalkan.bitcointicker.ui.signup

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.emrekalkan.bitcointicker.core.BaseViewModel
import com.emrekalkan.bitcointicker.data.repository.FirebaseAuthRepository
import com.emrekalkan.bitcointicker.utils.Resource
import com.emrekalkan.bitcointicker.utils.SingleLiveEvent
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignUpViewModel @ViewModelInject constructor(
    private val firebaseAuthRepository: FirebaseAuthRepository
) : BaseViewModel() {

    private val _signUpResult = MutableLiveData<Resource<Task<AuthResult>>>()
    val signUpResult: LiveData<Resource<Task<AuthResult>>> = _signUpResult

    private val _emailVerification = SingleLiveEvent<Resource<Task<Void>>>()
    val emailVerification: LiveData<Resource<Task<Void>>> = _emailVerification

    var isInitiallyAnimated: Boolean = false

    fun signUp(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseAuthRepository.signUp(email, password) { resource ->
                _signUpResult.postValue(resource)
            }
        }
    }

    fun sendEmailVerification() {
        firebaseAuthRepository.sendEmailVerification {
            _emailVerification.postValue(it)
        }
    }
}