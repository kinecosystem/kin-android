# <img src="../assets/kin-logo.png" height="32" alt="Kin Logo"> Base Compat Module
[![KDoc](https://img.shields.io/badge/Docs-KDoc-blue)](https://kinecosystem.github.io/kin-android/docs)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.kin.sdk.android/base-compat/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.kin.sdk.android/base-compat)

The [:base-compat](../base-compat) module provides support for backup & restore.

This version is based on the new [:base](../base/README.md) module under the covers, which gives higher performance and reliability that you depend on.

## Requirements
Android API 19+

## Installation
```groovy
repositories {
    // ...
    mavenCentral()
}
dependencies {
    // ...
    implementation "org.kin.sdk.android:base-compat:${versions.kin}"
    implementation("org.kin.sdk.android:base:${versions.kin}") {
        exclude module: 'libsodium-jni'
    }
}
```
Both base-compat and base rely on the libsodium module, so excluding it from base prevents dependency conflicts.

### Shaded Artifact

Alternatively, there is a shaded artifact for those that are having difficuly resolving common dependencies (e.g. grpc, guava, other google transitive deps)
```groovy
dependencies {
    // ...
    // The shaded repo doesn't pulling transitive dependencies aautomatically, so add these manually
    implementation 'net.i2p.crypto:eddsa:0.3.0'
    implementation 'io.perfmark:perfmark-api:0.23.0'
    implementation "org.kin.sdk.android:base-compat-shaded:${versions.kin}"
    implementation("org.kin.sdk.android:base-shaded:${versions.kin}") {
        exclude module: 'libsodium-jni'
    }
}
```
This variant is recommended when encountering dependency collisions (e.g. duplicate classes, incompatible versions, etc).  
If you are still encountering difficulties with dependency collisions with this variant we recommend forking the library and adding additional libraries you want to shade to the list included under the shadowJar task configured in [../base/build.gradle](../base/build.gradle)

## Backup & Restore
To backup, you can call the following to start that UI flow:
```kotlin
 BackupAndRestoreManager(activity, 1, 2)
            .backup(kinEnvironment, KinAccount.Id(accountAddressString))
```
and to restore, you can call the following to start that UI flow:
```kotlin
BackupAndRestoreManager(launchActivity, 1, 2).restore(kinEnvironment)
```

## Documentation for versions < 1.0
[kin-sdk-android](https://github.com/kinecosystem/kin-sdk-android) is now **Deprecated** but see the [old documentation](https://github.com/kinecosystem/kin-sdk-android/tree/master/kin-sdk) or [API reference](../docs) for more details on how to use it.
