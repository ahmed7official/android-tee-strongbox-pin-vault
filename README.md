# PINVault

PINVault is a sample Android application designed to demonstrate the secure storage and management of PINs using advanced security features provided by the Android platform, such as the [Android Keystore](https://developer.android.com/training/articles/keystore) and [Trusted Execution Environment (TEE)](https://source.android.com/docs/security/features/trusty).

## Features

- **Secure PIN Storage**: Demonstrates the use of [Android Keystore](https://developer.android.com/training/articles/keystore) to securely generate and store cryptographic keys.
- **Trusted Execution Environment (TEE)**: Showcases the use of [TEE](https://source.android.com/docs/security/features/trusty) for enhanced security, ensuring that cryptographic operations are performed in a secure environment.
- **Biometric Authentication**: Includes support for biometric authentication as an additional layer of security.
- **User-Friendly Interface**: Provides a simple and intuitive interface for managing PINs.

## Technical Overview

### Android Keystore

The [Android Keystore](https://developer.android.com/training/articles/keystore) system provides a secure container to hold cryptographic keys. In this sample project, the Keystore is used to generate and store keys securely. The keys are used for encrypting and decrypting PINs, ensuring that sensitive data is protected even if the device is compromised.

### Trusted Execution Environment (TEE)

[TEE](https://source.android.com/docs/security/features/trusty) is used to perform cryptographic operations in a secure environment, isolated from the main operating system. This ensures that sensitive operations are protected from potential threats. The project checks for the availability of StrongBox, a hardware-backed Keystore, to further enhance security.

### Key Classes and Files

- **SecureCryptoRepositoryImpl.kt**: Implements the cryptographic operations using Android Keystore and TEE.
- **BiometricAuthenticator.kt**: Handles biometric authentication processes.
- **MainActivity.kt**: The main entry point of the application, setting up the user interface.

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request for any improvements or bug fixes.

## License

This project is licensed under the MIT License. 