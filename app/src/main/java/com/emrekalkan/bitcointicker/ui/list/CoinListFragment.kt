package com.emrekalkan.bitcointicker.ui.list

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emrekalkan.bitcointicker.R
import com.emrekalkan.bitcointicker.core.BaseFragment
import com.emrekalkan.bitcointicker.databinding.FragmentCoinListBinding
import com.emrekalkan.bitcointicker.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CoinListFragment : BaseFragment<CoinListViewModel, FragmentCoinListBinding>() {

    private lateinit var coinListAdapter: CoinListAdapter

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override val viewModel: CoinListViewModel by viewModels()

    override fun getLayoutRes(): Int = R.layout.fragment_coin_list

    override fun init(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        initCoinListAdapter()
        observeLiveDataObjects()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.coin_list_favourites -> {
                findNavController().navigate(CoinListFragmentDirections.actionCoinListFragmentToFavouriteCoinsFragment())
                true
            }
            R.id.coin_list_log_out -> {
                viewModel.deleteSharedData()
                firebaseAuth.signOut()
                findNavController().navigate(CoinListFragmentDirections.actionCoinListFragmentToSignInFragment())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_coin_list, menu)
        val searchView = menu.findItem(R.id.coin_list_search_view).actionView as SearchView
        initSearchViewActions(searchView)
    }

    private fun initSearchViewActions(searchView: SearchView) {
        val closeButton: ImageView = searchView.findViewById(R.id.search_close_btn)
        closeButton.setColorFilter(ContextCompat.getColor(closeButton.context, R.color.design_default_color_on_primary))
        closeButton.setOnClickListener {
            searchView.onActionViewCollapsed()
        }

        searchView.findViewById<SearchView.SearchAutoComplete>(R.id.search_src_text).apply {
            setTextColor(ContextCompat.getColor(context, R.color.design_default_color_on_primary))
            setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
        }

        searchView.apply {
            queryHint = "Search a coin..."
            maxWidth = Int.MAX_VALUE

            if (viewModel.searchQuery.isNotBlank()) {
                onActionViewExpanded()
                setQuery(viewModel.searchQuery, false)
            }

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    viewModel.getCoinItemsByQuery(query)
                    searchView.clearFocus()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    // Reset filtering when user clears query
                    if (newText == "") {
                        viewModel.getCoinItemsByQuery(newText)
                    }
                    return false
                }
            })
        }
    }

    private fun initCoinListAdapter() {
        coinListAdapter = CoinListAdapter(
            onItemClick = { id ->
                CoinListFragmentDirections.actionCoinListFragmentToCoinDetailFragment(id).also { action ->
                    findNavController().navigate(action)
                }
            }
        )
        binding.coinItemRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = coinListAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))
        }
    }

    private fun observeLiveDataObjects() {
        viewModel.apply {
            coinItemList.observe(viewLifecycleOwner) { coinListResource ->
                when (coinListResource.status) {
                    Resource.Status.SUCCESS -> {
                        coinListAdapter.submitList(coinListResource.data)
                    }
                    Resource.Status.ERROR -> {
                        coinListResource.message
                    }
                    Resource.Status.LOADING -> {
                        if (coinListResource.data?.isNotEmpty() == true) {
                            coinListAdapter.submitList(coinListResource.data)
                        }
                    }
                }
            }
        }
    }
}