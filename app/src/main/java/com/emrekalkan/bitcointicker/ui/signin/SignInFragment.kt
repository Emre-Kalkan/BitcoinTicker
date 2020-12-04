package com.emrekalkan.bitcointicker.ui.signin

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
import com.emrekalkan.bitcointicker.databinding.FragmentSignInBinding
import com.emrekalkan.bitcointicker.utils.Resource
import com.emrekalkan.bitcointicker.utils.ext.setError
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.atomic.AtomicBoolean

@AndroidEntryPoint
class SignInFragment : BaseFragment<SignInViewModel, FragmentSignInBinding>() {
    override val viewModel: SignInViewModel by viewModels()

    override fun getLayoutRes(): Int = R.layout.fragment_sign_in

    override fun init(savedInstanceState: Bundle?) {
        setupBindingVariables()
        observeLiveDataObjects()
        lifecycleScope.launchWhenResumed {
            initiallyAnimateViews()
        }
    }

    private fun observeLiveDataObjects() {
        viewModel.apply {
            signInResult.observe(viewLifecycleOwner) {
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        if (it.data?.result?.user?.isEmailVerified == false) {
                            showSnackbar("Please verify yourself via a link that sent to your email address.", Snackbar.LENGTH_LONG)
                        } else {
                            updateLoadingDialog(false)
                            findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToCoinListFragment())
                        }
                        updateLoadingDialog(false)
                    }
                    Resource.Status.ERROR -> {
                        updateLoadingDialog(false)
                        Toast.makeText(requireContext(), it.message ?: "Sign in failed.", Toast.LENGTH_SHORT).show()
                        if (it.data?.result?.user?.isEmailVerified == false) {
                            showSnackbar("Please verify yourself via a link that sent to your email address.", Snackbar.LENGTH_LONG)
                        }
                    }
                    Resource.Status.LOADING -> {
                        updateLoadingDialog(true)
                    }
                }
            }
        }
    }

    private fun setupBindingVariables() {
        binding.apply {
            fragment = this@SignInFragment
            viewModel = this@SignInFragment.viewModel
        }
    }

    private fun initiallyAnimateViews() {
        if (viewModel.isInitiallyAnimated.not()) {
            AnimatorSet().apply {
                playSequentially(
                    createFadeInObjectAnimator(binding.welcomeText, 1000),
                    createFadeInObjectAnimator(binding.titleText, 1500)
                )
                start()
            }.doOnEnd {
                AnimatorSet().apply {
                    val duration: Long = 1500
                    playTogether(
                        createFadeInObjectAnimator(binding.emailInputLayout, duration),
                        createFadeInObjectAnimator(binding.passwordInputLayout, duration),
                        createFadeInObjectAnimator(binding.loginButton, duration),
                        createFadeInObjectAnimator(binding.signUpText, duration)
                    )
                    start()
                }
            }
            viewModel.isInitiallyAnimated = true
        } else {
            binding.welcomeText.alpha = 1f
            binding.titleText.alpha = 1f
            binding.emailInputLayout.alpha = 1f
            binding.passwordInputLayout.alpha = 1f
            binding.loginButton.alpha = 1f
            binding.signUpText.alpha = 1f
        }
    }

    private fun createFadeInObjectAnimator(view: View, duration: Long): ObjectAnimator {
        return ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f).apply {
            this.duration = duration
            interpolator = DecelerateInterpolator()
        }
    }

    private fun checkSignInCredentials(): Boolean {
        return AtomicBoolean(true).run {
            compareAndSet(true, checkEmailValidation())
            compareAndSet(true, checkPasswordValidation())
            get()
        }
    }

    private fun checkPasswordValidation(): Boolean {
        val password = binding.passwordEditText.text.toString()
        return AtomicBoolean(true).run {
            val minChar = 6
            val isValid = password.length >= minChar
            compareAndSet(true, isValid)
            binding.passwordInputLayout.setError(isValid, "Password should consist of at least $minChar characters")
            get()
        }
    }

    private fun checkEmailValidation(): Boolean {
        val email = binding.emailEditText.text.toString()
        return Patterns.EMAIL_ADDRESS.matcher(email).matches().also { isValid ->
            binding.emailInputLayout.setError(isValid, "Email is not valid")
        }
    }

    fun onSignInClicked() {
        if (checkSignInCredentials()) {
            viewModel.signIn(
                email = binding.emailEditText.text.toString(),
                password = binding.passwordEditText.text.toString()
            )
        }
    }

    fun onSignUpClicked() {
        findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToSignUpFragment())
    }
}