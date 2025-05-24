package com.ahmed7official.securetee.vault.presentation.di

import com.ahmed7official.securetee.vault.domain.auth.BiometricAuthenticator
import com.ahmed7official.securetee.vault.presentation.auth.BiometricAuthenticatorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BiometricModule {

    @Binds
    @Singleton
    abstract fun bindBiometricAuthenticator(
        impl: BiometricAuthenticatorImpl
    ): BiometricAuthenticator
} 