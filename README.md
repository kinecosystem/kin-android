# <img src="assets/kin-logo.png" height="32" alt="Kin Logo"> Kin SDK Android
[![codecov](https://codecov.io/gh/kinecosystem/kin-android/branch/master/graph/badge.svg?token=V05OQ629R5)](https://codecov.io/gh/kinecosystem/kin-android)
[![CircleCI](https://img.shields.io/circleci/build/gh/kinecosystem/kin-android/master?token=ac677bb614658377373f411ba6394e9adf112cba)](https://circleci.com/gh/kinecosystem/kin-android)
[![KDoc](https://img.shields.io/badge/Docs-KDoc-blue)](https://kinecosystem.github.io/kin-android/docs)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.kin.sdk.android/base/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.kin.sdk.android/base)

Use the Kin SDK for Android to enable the use of Kin inside of your app. Include only the functionality you need to provide the right experience to your users. Include the offers library to give your users the opportunity to earn Kin in your app. Use just the base library to access the lightest-weight wrapper over the Kin crytocurrency. The design library provides a set of basic UI elements to ensure the user experience offered to your users is consistent and high-quality.

| Library&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; | Support                                                                                   | Path&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; | Description                                                                                                                                                                                                                                                                               |
|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:------------------------------------------------------------------------------------------|:-----------------------------------------------------------------------------------------------------------------------------|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `base`                                                                                                                                                                                | <img src="assets/java.png" height="24">&nbsp;<img src="assets/kotlin.png" height="24">    | [`/base`](base)                                                                                                              | The foundation library used by all other libraries in the system to support basic Kin operations: <ul><li>Wallet creation and management</li><li>Send and receive Kin</li><li>Metrics interfaces</li></ul>                                                                                |
| `base-compat`                                                                                                                                                                         | <img src="assets/android.png" height="24">&nbsp;<img src="assets/kotlin.png" height="24"> | [`/base-compat`](base-compat)                                                                                                | The [:base-compat](base-compat) library implements the public surface layer to be a drop in replacement of the, now deprecated, [kin-sdk-android](https://github.com/kinecosystem/kin-sdk-android) library. Just update your version in gradle and have better performance and stability. |
| `design`                                                                                                                                                                              | <img src="assets/android.png" height="24">&nbsp;<img src="assets/kotlin.png" height="24"> | [`/design`](design)                                                                                                          | Shared [:design](design) library components for creating consistent Kin user experiences. When creating a custom Kin experience, this library can be used to include standard UI components for displaying Kin prices, transactions, etc.                                                            |
| `spend`                                                                                                                                                                               | <img src="assets/android.png" height="24">&nbsp;<img src="assets/kotlin.png" height="24"> | [`/spend`](spend)                                                                                                            | The [:spend](spend) library provides an out of the box model UI for spending Kin within an Android application. Specificy what you're buying, your account, tap confirm. Success.|

*\*Note: [base-storage](base-storage), [base-viewmodel](base-viewmodel), & [base-viewmodel-impl](base-viewmodel-impl) are internal dependencies to other libraries that are not meant to be directly consumed at this time.*

## Installation

```groovy
buildscript {
    // ...
    ext {
        versions = [:]
        
        versions.kin = "0.4.0"
    }
}
dependencies {
    // ...
    
    // *** base-compat is for LEGACY SUPPORT ONLY ***
    // If you're a longtime Kin developer and want to use the compat 
    // interface that looks like the now deprecated SDKS
    implementation "org.kin.sdk.android:base-compat:${versions.kin}"
    
    
    // If you're a new developer or want more functionality you want a
    // mix of the libraries below:
    
    // If you just want to access the blockchain & no UI
    implementation "org.kin.sdk.android:base:${versions.kin}"
    
    // Add spend to use the modal spend flow to allow users to buy things with Kin
    implementation "org.kin.sdk.android:spend:${versions.kin}"
    
    // Add design for direct access to UI views you can use in your own app
    implementation "org.kin.sdk.android:design:${versions.kin}"
}
repositories {
    // ...
    jcenter()
    maven { url "https://jitpack.io/" } // Jitpack is used for OkSSE fork only
}
```

## Demo App

The [demo](demo) directory includes a demo application, showcasing a functional Kin wallet.

## Design Showcase App

The [design-showcase](design-showcase) directory includes an application showcasing the UI widgets that are both: used in our full screen experiences, and also publicly available for all developers to use from the [design](design) library.

## Documentation
KDoc Documentation for all classes in all modules located [here](https://kinecosystem.github.io/kin-android/docs/index.html)
