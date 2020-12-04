package com.emrekalkan.bitcointicker.ui.detail

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.emrekalkan.bitcointicker.R
import com.emrekalkan.bitcointicker.core.BaseFragment
import com.emrekalkan.bitcointicker.databinding.FragmentCoinDetailBinding
import com.emrekalkan.bitcointicker.utils.Resource
import com.emrekalkan.bitcointicker.utils.ext.fadeAnimate
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

@AndroidEntryPoint
class CoinDetailFragment : BaseFragment<CoinDetailViewModel, FragmentCoinDetailBinding>() {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var menu: Menu

    override val viewModel: CoinDetailViewModel by viewModels()

    override fun getLayoutRes(): Int = R.layout.fragment_coin_detail

    override fun init(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        binding.fragment = this
        observeLiveDataObjects()
        initBottomSheet()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_coin_detail, menu)
        this.menu = menu
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.coin_detail_favourite -> {
                viewModel.addOrRemoveAsFavouriteCoin()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPause() {
        stopPriceRefreshing()
        super.onPause()
    }

    private fun observeLiveDataObjects() {
        viewModel.apply {
            coinDetails.observe(viewLifecycleOwner) { coinDetailResource ->
                when (coinDetailResource.status) {
                    Resource.Status.SUCCESS -> {
                        binding.apply {
                            if (coinDetailResource.data != null) {
                                coinDetail = coinDetailResource.data
                                executePendingBindings()
                                coinDescription.movementMethod = LinkMovementMethod.getInstance()
                            }
                            initiallyAnimateViews()
                        }
                        relaunchPriceRefreshCoroutineIfActive()
                    }
                    Resource.Status.ERROR -> {
                        Toast.makeText(requireContext(), coinDetailResource.message, Toast.LENGTH_SHORT).show()
                        relaunchPriceRefreshCoroutineIfActive()
                    }
                    Resource.Status.LOADING -> {
                    }
                }
            }

            isFavourite.observe(viewLifecycleOwner) {
                menu.findItem(R.id.coin_detail_favourite).apply {
                    isVisible = true
                    setIcon(
                        if (it) R.drawable.ic_baseline_favorite_24
                        else R.drawable.ic_baseline_favorite_border_24
                    )
                }
            }

            favouriteOperation.observe(viewLifecycleOwner) {
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        viewModel.isFavouriteCoinInProgress = false
                        updateLoadingDialog(false)
                    }
                    Resource.Status.ERROR -> {
                        Toast.makeText(requireContext(), it.message ?: "Unexpected error occurred.", Toast.LENGTH_SHORT).show()
                        viewModel.isFavouriteCoinInProgress = false
                        updateLoadingDialog(false)
                    }
                    Resource.Status.LOADING -> updateLoadingDialog(true)
                }
            }
        }
    }

    private fun initBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetLayout)
    }

    private fun initiallyAnimateViews() {
        if (binding.coinPriceInfoCard.visibility != View.VISIBLE) {
            binding.coinPriceInfoCard.fadeAnimate(1f)
            binding.coinGeneralInfoCard.fadeAnimate(1f)
        }
    }

    private fun switchBottomSheetState() {
        bottomSheetBehavior.state = if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            BottomSheetBehavior.STATE_EXPANDED
        } else {
            BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun switchRefreshViewAttr() {
        val intervalString = viewModel.priceRefreshInterval.takeIf { it >= viewModel.minIntervalInSeconds }?.toString() ?: ""
        if (viewModel.isPriceRefreshActive) {
            binding.refreshButton.text = getString(R.string.stop_refreshing)
            binding.refreshIntervalEditText.apply {
                setText(intervalString)
                isEnabled = false
            }
        } else {
            binding.refreshButton.text = getString(R.string.start_refreshing)
            binding.refreshIntervalEditText.apply {
                setText(intervalString)
                isEnabled = true
            }
        }
    }

    private fun checkIntervalValidation(): Boolean {
        return AtomicBoolean(true).run {
            val interval = binding.refreshIntervalEditText.text?.toString()?.toIntOrNull()
            compareAndSet(true, checkMinInterval(interval))
            get()
        }
    }

    private fun checkMinInterval(interval: Int?): Boolean {
        val isValid = interval != null && interval >= viewModel.minIntervalInSeconds
        if (isValid.not()) {
            binding.refreshIntervalInputLayout.error = "Min interval is ${viewModel.minIntervalInSeconds}"
        } else {
            binding.refreshIntervalInputLayout.error = null
            viewModel.priceRefreshInterval = interval!!
        }
        return isValid
    }

    private fun stopPriceRefreshing() {
        Timber.d("Price refresh coroutine cancelled")
        viewModel.isPriceRefreshActive = false
        setRefreshImageTint()
    }

    private fun startPriceRefreshing() {
        viewModel.isPriceRefreshActive = true
        viewModel.launchPriceRefreshCoroutine()
        setRefreshImageTint()
        Timber.d("Price refresh coroutine job start called")
    }

    private fun setRefreshImageTint() {
        binding.refreshImage.imageTintList = when (viewModel.isPriceRefreshActive) {
            true -> ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.green_light))
            false -> ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.color_icon))
        }
    }

    fun onRefreshImageClicked() {
        switchRefreshViewAttr()
        switchBottomSheetState()
    }

    fun onRefreshButtonClicked() {
        if (checkIntervalValidation()) {
            if (viewModel.isPriceRefreshActive.not()) {
                startPriceRefreshing()
                switchBottomSheetState()
            } else {
                stopPriceRefreshing()
                switchBottomSheetState()
            }
        }
    }
}