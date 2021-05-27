# <img src="../assets/kin-logo.png" height="32" alt="Kin Logo"> Base Compat Module
[![KDoc](https://img.shields.io/badge/Docs-KDoc-blue)](https://kinecosystem.github.io/kin-android/docs)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.kin.sdk.android/base-compat/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.kin.sdk.android/base-compat)

The [:base-compat](../base-compat) module is a replacement for, and fully API compatible with [kin-sdk-android](https://github.com/kinecosystem/kin-sdk-android)

This version is based on the new [:base](../base/README.md) module under the covers, which gives higher performance and reliability that you depend on.

## Requirements
Android API 19+

## Installation
```groovy
dependencies {
    // ...
    implementation "org.kin.sdk.android:base-compat:${versions.kin}"
}
```
Alternatively...there is a shaded artifact for those that are having difficuly resolving common dependencies (e.g. grpc, guava, other google transitive deps)
```groovy
dependencies {
    // ...
    implementation "org.kin.sdk.android:base-compat-shaded:${versions.kin}"
}
```
This variant is recommended when encountering dependency collisions (e.g. duplicate classes, incompatible versions, etc).  
If you are still encountering difficulties with dependency collisions with this variant we recommend forking the library and adding additional libraries you want to shade to the list included under the shadowJar task configured in [../base/build.gradle](../base/build.gradle)

Otherwise, if you're not using [kin-sdk-android](https://github.com/kinecosystem/kin-sdk-android), please checkout [:base](../base).

## Backup & Restore
As of 1.0.0+ base-compat *only* contains the backup and restore flow, which has been retrofitted with direct support with the base module.
to backup you can call the followowing to start that ui flow:
```kotlin
 BackupAndRestoreManager(activity, 1, 2)
            .backup(kinEnvironment, KinAccount.Id(accountAddressString))
```
and to retore you can call the followowing to start that ui flow:
```kotlin
BackupAndRestoreManager(launchActivity, 1, 2).restore(kinEnvironment)
```

## Documentation
[kin-sdk-android](https://github.com/kinecosystem/kin-sdk-android) is now **Deprecated** but see the [old documentation](https://github.com/kinecosystem/kin-sdk-android/tree/master/kin-sdk) or [API reference](../docs) for more details on how to use it.
