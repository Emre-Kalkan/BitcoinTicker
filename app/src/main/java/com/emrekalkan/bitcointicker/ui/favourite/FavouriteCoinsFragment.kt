package com.emrekalkan.bitcointicker.ui.favourite

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emrekalkan.bitcointicker.R
import com.emrekalkan.bitcointicker.core.BaseFragment
import com.emrekalkan.bitcointicker.databinding.FragmentFavouriteCoinsBinding
import com.emrekalkan.bitcointicker.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavouriteCoinsFragment : BaseFragment<FavouriteCoinsViewModel, FragmentFavouriteCoinsBinding>() {

    private lateinit var favouriteCoinsListAdapter: FavouriteCoinsListAdapter

    override val viewModel: FavouriteCoinsViewModel by viewModels()

    override fun getLayoutRes(): Int = R.layout.fragment_favourite_coins

    override fun init(savedInstanceState: Bundle?) {
        initCoinListAdapter()
        observeLiveDataObjects()
    }

    private fun initCoinListAdapter() {
        favouriteCoinsListAdapter = FavouriteCoinsListAdapter(
            onItemClick = { id ->
                findNavController().navigate(FavouriteCoinsFragmentDirections.actionFavouriteCoinsFragmentToCoinDetailFragment(id))
            }
        )
        binding.favouriteCoinsRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = favouriteCoinsListAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))
        }
    }

    private fun observeLiveDataObjects() {
        viewModel.apply {
            favouriteCoins.observe(viewLifecycleOwner) { coinListResource ->
                when (coinListResource.status) {
                    Resource.Status.SUCCESS -> {
                    }
                    Resource.Status.ERROR -> {
                        coinListResource.message
                    }
                    Resource.Status.LOADING -> {
                    }
                }
            }

            getFavouriteCoinList().observe(viewLifecycleOwner) {
                favouriteCoinsListAdapter.submitList(it)
            }
        }
    }
}