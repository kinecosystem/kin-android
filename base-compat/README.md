# <img src="../assets/kin-logo.png" height="32" alt="Kin Logo"> Base Compat Module
[![KDoc](https://img.shields.io/badge/Docs-KDoc-blue)](https://kinecosystem.github.io/kin-android/docs)
[![Download](https://api.bintray.com/packages/kinecosystem/kin-android/base-compat/images/download.svg) ](https://bintray.com/kinecosystem/kin-android/base-compat/_latestVersion)

The [:base-compat](../base-compat) module is a replacement for, and fully API compatible with [kin-sdk-android](https://github.com/kinecosystem/kin-sdk-android)

This version is based on the new [:base](../base/README.md) module under the covers, which gives higher performance and reliability that you depend on.

## Requirements
Android API 19+

## Installation
If you're currently making use of [kin-sdk-android](https://github.com/kinecosystem/kin-sdk-android) please upgrade your gradle files accordingly:
In the old [kin-sdk-android](https://github.com/kinecosystem/kin-sdk-android) sdk you would have had this:
 ```groovy
dependencies {
    // ...
     implementation 'com.github.kinecosystem.kin-sdk-android:kin-sdk-lib:1.0.7'
}
```
Now, replace that with the following and do a clean build:
```groovy
dependencies {
    // ...
    implementation "org.kin.sdk.android:base-compat:${versions.kin}"
}
repositories {
    // ...
    jcenter()
    maven { url "https://jitpack.io/" } // Jitpack is used for OkSSE fork only
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

## Documentation
[kin-sdk-android](https://github.com/kinecosystem/kin-sdk-android) is now **Deprecated** but see the [old documentation](https://github.com/kinecosystem/kin-sdk-android/tree/master/kin-sdk) or [API reference](../docs) for more details on how to use it.

### Note on Upcoming Solana Migration
With the migration to Solana just around the corner, apps that want to continue to function during and post the move to the Solana blockchain are required to upgrade their `kin-android` sdk to 0.4.0 or higher.
*Any application that does not upgrade will start to receive a `KinService.FatalError.SDKUpgradeRequired` exception on any request made from `KinAccount`.*

#### Testing migration within your app
To enable migration of Kin3 -> Kin4 accounts on testnet, `KinClient` has a static function
`.testMigration()` that will force this sdk into a state where migration will occur on demand if
called before any KinClient instances have been created.

#### On Migration Day (Dec 8, 2020)
Apps should expect to see increased transaction times temporarily on the date of migration.
An on-demand migration will be attempted to trigger a migration, rebuild, and retry transactions that are submitted from an unmigrated account on this day and optimistically will complete successfully but are not guaranteed.
After all accounts have been migrated to Solana, transaction times should noticeably improve to around ~1s. Additional performance improvements are still possible and will roll out in future sdk releases.
