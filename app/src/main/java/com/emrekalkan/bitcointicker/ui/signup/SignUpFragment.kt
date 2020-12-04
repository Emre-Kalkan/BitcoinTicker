package com.emrekalkan.bitcointicker.ui.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.core.animation.doOnEnd
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.emrekalkan.bitcointicker.R
import com.emrekalkan.bitcointicker.core.BaseFragment
import com.emrekalkan.bitcointicker.databinding.FragmentSignUpBinding
import com.emrekalkan.bitcointicker.utils.Resource
import com.emrekalkan.bitcointicker.utils.ext.setError
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.atomic.AtomicBoolean

@AndroidEntryPoint
class SignUpFragment : BaseFragment<SignUpViewModel, FragmentSignUpBinding>() {
    override val viewModel: SignUpViewModel by viewModels()

    override fun getLayoutRes(): Int = R.layout.fragment_sign_up

    override fun init(savedInstanceState: Bundle?) {
        setupBindingVariables()
        observeLiveDataObjects()
        lifecycleScope.launchWhenResumed {
            initiallyAnimateViews()
        }
    }

    private fun observeLiveDataObjects() {
        viewModel.apply {
            signUpResult.observe(viewLifecycleOwner, { resource ->
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        viewModel.sendEmailVerification()
                    }
                    Resource.Status.ERROR -> {
                        Toast.makeText(requireContext(), resource.message ?: "Sign up failed!", Toast.LENGTH_SHORT).show()
                        updateLoadingDialog(false)
                    }
                    Resource.Status.LOADING -> {
                        updateLoadingDialog(true)
                    }
                }
            })

            emailVerification.observe(viewLifecycleOwner) {
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        showSnackbar("A verification mail sent to you! Please verify yourself.", Snackbar.LENGTH_LONG)
                        findNavController().popBackStack()
                    }
                    Resource.Status.ERROR -> {
                        showSnackbar("An unexpected error occurred while sending verification mail.", Snackbar.LENGTH_LONG)
                        findNavController().popBackStack()
                    }
                    Resource.Status.LOADING -> {
                    }
                }
                updateLoadingDialog(false)
            }
        }
    }

    private fun setupBindingVariables() {
        binding.apply {
            fragment = this@SignUpFragment
            viewModel = this@SignUpFragment.viewModel
        }
    }

    private fun initiallyAnimateViews() {
        if (viewModel.isInitiallyAnimated.not()) {
            AnimatorSet().apply {
                playSequentially(
                    createFadeInObjectAnimator(binding.signUpTitle1, 500),
                    createFadeInObjectAnimator(binding.signUpTitle2, 500)
                )
                start()
            }.doOnEnd {
                AnimatorSet().apply {
                    val duration: Long = 750
                    playTogether(
                        createFadeInObjectAnimator(binding.signUpEmailLayout, duration),
                        createFadeInObjectAnimator(binding.signUpPasswordLayout, duration),
                        createFadeInObjectAnimator(binding.signUpButton, duration),
                        createFadeInObjectAnimator(binding.signInText, duration)
                    )
                    start()
                }
            }
            viewModel.isInitiallyAnimated = true
        }
    }

    private fun createFadeInObjectAnimator(view: View, duration: Long): ObjectAnimator {
        return ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f).apply {
            this.duration = duration
            interpolator = DecelerateInterpolator()
        }
    }

    fun onSignInClicked() {
        findNavController().popBackStack()
    }

    fun onSignUpClicked() {
        if (checkSignUpCredentials()) {
            viewModel.signUp(
                email = binding.signUpEmail.text.toString(),
                password = binding.signUpPassword.text.toString(),
            )
        }
    }

    private fun checkSignUpCredentials(): Boolean {
        return AtomicBoolean(true).run {
            compareAndSet(true, checkEmailValidation())
            compareAndSet(true, checkPasswordValidation())
            get()
        }
    }

    private fun checkPasswordValidation(): Boolean {
        val password = binding.signUpPassword.text.toString()
        return AtomicBoolean(true).run {
            val minChar = 6
            val isValid = password.length >= minChar
            compareAndSet(true, isValid)
            binding.signUpPasswordLayout.setError(isValid, "Password should consist of at least $minChar characters")
            get()
        }
    }

    private fun checkEmailValidation(): Boolean {
        val email = binding.signUpEmail.text.toString()
        return Patterns.EMAIL_ADDRESS.matcher(email).matches().also { isValid ->
            binding.signUpEmailLayout.setError(isValid, "Email is not valid")
        }
    }
}