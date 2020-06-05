# <img src="assets/kin-logo.png" height="32" alt="Kin Logo"> Kin SDK Android
[![codecov](https://codecov.io/gh/kinecosystem/kin-android/branch/master/graph/badge.svg?token=V05OQ629R5)](https://codecov.io/gh/kinecosystem/kin-android)
[![CircleCI](https://img.shields.io/circleci/build/gh/kinecosystem/kin-android/master?token=ac677bb614658377373f411ba6394e9adf112cba)](https://circleci.com/gh/kinecosystem/kin-android)
[![KDoc](https://img.shields.io/badge/Docs-KDoc-blue)](https://kinecosystem.github.io/kin-android/docs)
[![Download](https://api.bintray.com/packages/kinecosystem/kin-android/base-compat/images/download.svg) ](https://bintray.com/kinecosystem/kin-android/base-compat/_latestVersion)

Use the Kin SDK for Android to enable the use of Kin inside of your app. Include only the functionality you need to provide the right experience to your users. Use just the base library to access the lightest-weight wrapper over the Kin crytocurrency.

| Library&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; | Support                                                                                   | Path&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; | Description                                                                                                                                                                                                                                                                               |
|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:------------------------------------------------------------------------------------------|:-----------------------------------------------------------------------------------------------------------------------------|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `base`                                                                                                                                                                                | <img src="assets/java.png" height="24">&nbsp;<img src="assets/kotlin.png" height="24">    | [`/base`](base)                                                                                                              | The foundation library used by all other libraries in the system to support basic Kin operations: <ul><li>Wallet creation and management</li><li>Send and receive Kin</li></ul>                                                                                                           |
| `base-compat`                                                                                                                                                                         | <img src="assets/android.png" height="24">&nbsp;<img src="assets/kotlin.png" height="24"> | [`/base-compat`](base-compat)                                                                                                | The [:base-compat](base-compat) library implements the public surface layer to be a drop in replacement of the, now deprecated, [kin-sdk-android](https://github.com/kinecosystem/kin-sdk-android) library. Just update your version in gradle and have better performance and stability. |

## Installation

```groovy
dependencies {
    // ...
    implementation "org.kin.sdk.android:base:${versions.kin}"
    // or
    implementation "org.kin.sdk.android:base-compat:${versions.kin}"
}
repositories {
    // ...
    maven {
        url  "https://kinecosystem.bintray.com/kin-android"
    }
}
```

## Documentation
KDoc Documentation for all classes in all modules located [here](https://kinecosystem.github.io/kin-android/docs/index.html)
