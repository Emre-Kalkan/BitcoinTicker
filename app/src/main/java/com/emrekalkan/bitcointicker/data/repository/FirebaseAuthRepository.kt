package com.emrekalkan.bitcointicker.data.repository

import com.emrekalkan.bitcointicker.utils.Resource
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class FirebaseAuthRepository(
    private val firebaseAuth: FirebaseAuth
) {

    fun signUp(email: String, password: String, onResult: (Resource<Task<AuthResult>>) -> Unit) {
        onResult(Resource.loading())
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onResult(Resource.success(task))
            } else {
                onResult(Resource.error("Authentication failed."))
            }
        }
    }

    fun sendEmailVerification(onResult: (Resource<Task<Void>>) -> Unit) {
        onResult(Resource.loading())
        firebaseAuth.currentUser?.sendEmailVerification()?.addOnCompleteListener {
            if (it.isSuccessful) {
                onResult(Resource.success(it))
            } else {
                onResult(Resource.error(it.exception?.localizedMessage ?: ""))
            }
        }
    }

    fun signIn(email: String, password: String, onResult: (Resource<Task<AuthResult>>) -> Unit) {
        onResult(Resource.loading())
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                onResult(Resource.success(it))
            } else {
                onResult(Resource.error(it.exception?.localizedMessage ?: "", it.exception))
            }
        }
    }

    fun getCurrentUser(): String? = firebaseAuth.currentUser?.uid
}