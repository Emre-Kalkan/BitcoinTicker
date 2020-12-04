package com.emrekalkan.bitcointicker.data.repository

import com.emrekalkan.bitcointicker.data.local.entity.FavouriteCoin
import com.emrekalkan.bitcointicker.utils.FirestoreConst
import com.emrekalkan.bitcointicker.utils.Resource
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreRepository(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val coinRepository: CoinRepository
) {
    private val favourites = "Favourites"
    private val coins = "Coins"

    private fun getCurrentUserId(): String? = firebaseAuth.currentUser?.uid

    fun addFavouriteCoin(favouriteCoin: FavouriteCoin, onResult: (Resource<Task<Void>>) -> Unit) {
        onResult(Resource.loading())
        getCurrentUserId().also { uid ->
            if (uid == null) {
                onResult(Resource.error("Unexpected error while adding to favourites."))
                return@also
            }

            firestore.collection(favourites)
                .document(uid)
                .collection(coins)
                .document(favouriteCoin.id)
                .set(
                    mapOf(
                        FirestoreConst.FavouriteCoin.id to favouriteCoin.id,
                        FirestoreConst.FavouriteCoin.name to favouriteCoin.name,
                        FirestoreConst.FavouriteCoin.symbol to favouriteCoin.symbol
                    )
                )
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onResult(Resource.success(task))
                    } else {
                        onResult(Resource.error(task.exception?.localizedMessage ?: "Unexpected error while adding to favourites.", task.exception))
                    }
                }
        }
    }

    fun removeFavouriteCoin(coinId: String, onResult: (Resource<Task<Void>>) -> Unit) {
        onResult(Resource.loading())
        getCurrentUserId().also { uid ->
            if (uid == null) {
                onResult(Resource.error("Unexpected error while adding to favourites."))
                return@also
            }

            firestore.collection(favourites)
                .document(uid)
                .collection(coins)
                .document(coinId)
                .delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onResult(Resource.success(task))
                    } else {
                        onResult(Resource.error(task.exception?.localizedMessage ?: "Unexpected error while adding to favourites.", task.exception))
                    }
                }
        }
    }

    suspend fun fetchFavouriteCoins(onResult: (Resource<List<FavouriteCoin>>) -> Unit) {
        onResult(Resource.loading(coinRepository.getFavouriteCoins()))
        getCurrentUserId().also { uid ->
            if (uid == null) {
                onResult(Resource.error("Unexpected error while adding to favourites."))
                return@also
            }

            firestore.collection(favourites)
                .document(uid)
                .collection(coins)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val favouriteCoins = ArrayList<FavouriteCoin>()
                        task.result?.documents?.forEach { documentSnapshot ->
                            favouriteCoins.add(
                                FavouriteCoin(
                                    id = documentSnapshot.getString(FirestoreConst.FavouriteCoin.id) ?: "",
                                    name = documentSnapshot.getString(FirestoreConst.FavouriteCoin.name) ?: "",
                                    symbol = documentSnapshot.getString(FirestoreConst.FavouriteCoin.symbol) ?: "",
                                    userId = uid
                                )
                            )
                        }
                        onResult(Resource.success(favouriteCoins))
                    } else {
                        onResult(Resource.error("Unexpected error while adding to favourites.", task.exception))
                    }
                }
        }
    }
}