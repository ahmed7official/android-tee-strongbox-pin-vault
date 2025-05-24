package com.ahmed7official.securetee.vault.di

import com.ahmed7official.securetee.vault.data.pin.PinRepositoryImpl
import com.ahmed7official.securetee.vault.domain.pin.PinRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPinRepository(
        repository: PinRepositoryImpl
    ): PinRepository
} 