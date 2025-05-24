package com.ahmed7official.securetee.vault.di

import com.ahmed7official.securetee.vault.data.crypto.SecureCryptoRepositoryImpl
import com.ahmed7official.securetee.vault.domain.crypto.CryptoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CryptoModule {
    
    @Binds
    @Singleton
    abstract fun bindCryptoRepository(
        repository: SecureCryptoRepositoryImpl
    ): CryptoRepository
} 