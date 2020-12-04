package com.emrekalkan.bitcointicker.di

import com.emrekalkan.bitcointicker.data.CoinApi
import com.emrekalkan.bitcointicker.data.local.dao.CoinDao
import com.emrekalkan.bitcointicker.data.repository.CoinRepository
import com.emrekalkan.bitcointicker.data.repository.FirebaseAuthRepository
import com.emrekalkan.bitcointicker.data.repository.FirestoreRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class RepositoryModule {

    @Singleton
    @Provides
    fun provideCoinRepository(coinApi: CoinApi, coinDao: CoinDao, firebaseAuthRepository: FirebaseAuthRepository): CoinRepository {
        return CoinRepository(coinApi, coinDao, firebaseAuthRepository)
    }

    @Singleton
    @Provides
    fun provideFirebaseAuthRepository(firebaseAuth: FirebaseAuth): FirebaseAuthRepository {
        return FirebaseAuthRepository(firebaseAuth)
    }

    @Singleton
    @Provides
    fun provideFirestoreRepository(firestore: FirebaseFirestore, firebaseAuth: FirebaseAuth, coinRepository: CoinRepository): FirestoreRepository {
        return FirestoreRepository(firestore, firebaseAuth, coinRepository)
    }
}