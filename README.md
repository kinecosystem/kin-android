# Deprecation Warning

Kins's Agora powered SDKs have been deprecated and Agora replaced with Kinetic.

## Kinetic

Kinetic is an open-source suite of tools that make it easy to build apps that integrate Solana.

It provides a consistent and clean abstraction over the Solana SDKs and enhances it with some commonly requested features like paying Solana fees on behalf of the user, tracking and timing the users transactions and sending out webhooks.

Kinetic is aimed at developers that want to build crypto-experiences for the users of their app, hiding a lot of the details about the blockchain out of sight for both the developer and the end user.

Learn more about Kinetic [here](https://developer.kin.org/docs/kinetic).

See our new suite of Kinetic SDK's [here](https://developer.kin.org/docs/developers).

# <img src="assets/kin-logo.png" height="32" alt="Kin Logo"> Kin SDK Android

[![codecov](https://codecov.io/gh/kinecosystem/kin-android/branch/master/graph/badge.svg?token=V05OQ629R5)](https://codecov.io/gh/kinecosystem/kin-android)
[![CircleCI](https://img.shields.io/circleci/build/gh/kinecosystem/kin-android/master?token=ac677bb614658377373f411ba6394e9adf112cba)](https://circleci.com/gh/kinecosystem/kin-android)
[![KDoc](https://img.shields.io/badge/Docs-KDoc-blue)](https://kinecosystem.github.io/kin-android/docs)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.kin.sdk.android/base/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.kin.sdk.android/base)

Use the Kin SDK for Android to enable the use of Kin in your app. Include only the functionality you need to provide the right experience to your users. Use just the base library to access the lightest-weight wrapper over the Kin cryptocurrency. The design library provides a set of basic UI elements to ensure the user experience offered to your users is consistent and high-quality. The spend library provides a flow for users to pay invoices with Kin. The base-compat library adds support for a pre-built wallet backup solution.

| Library&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; | Support                                                                                   | Path&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; | Description                                                                                                                                                                                                                               |
| :------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | :---------------------------------------------------------------------------------------- | :--------------------------------------------------------------------------------------------------------------------------- | :---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `base`                                                                                                                                                                                | <img src="assets/java.png" height="24">&nbsp;<img src="assets/kotlin.png" height="24">    | [`/base`](base)                                                                                                              | The foundation library used by all other libraries in the system to support basic Kin operations: <ul><li>Wallet creation and management</li><li>Send and receive Kin</li><li>Metrics interfaces</li></ul>                                |
| `base-compat`                                                                                                                                                                         | <img src="assets/android.png" height="24">&nbsp;<img src="assets/kotlin.png" height="24"> | [`/base-compat`](base-compat)                                                                                                | The [:base-compat](base-compat) library now only contains the backup & restore flow retrofitted on top of base. If you're using an old version of base-compat please consider upgrading to base. If you want to support backup & restore, you must include base **and** base-compat.                                          |
| `design`                                                                                                                                                                              | <img src="assets/android.png" height="24">&nbsp;<img src="assets/kotlin.png" height="24"> | [`/design`](design)                                                                                                          | Shared [:design](design) library components for creating consistent Kin user experiences. When creating a custom Kin experience, this library can be used to include standard UI components for displaying Kin prices, transactions, etc. |
| `spend`                                                                                                                                                                               | <img src="assets/android.png" height="24">&nbsp;<img src="assets/kotlin.png" height="24"> | [`/spend`](spend)                                                                                                            | The [:spend](spend) library provides an out of the box model UI for spending Kin within an Android application. Specificy what you're buying, your account, tap confirm. Success.                                                         |

_\*Note: [base-storage](base-storage), [base-viewmodel](base-viewmodel), & [base-viewmodel-impl](base-viewmodel-impl) are internal dependencies to other libraries that are not meant to be directly consumed at this time._

## Installation

```groovy
buildscript {
    // ...
    ext {
        versions = [:]

        versions.kin = "2.1.2"
    }
}
repositories {
    // ...
    mavenCentral()
}
dependencies {
    // ...

    implementation "org.kin.sdk.android:base:${versions.kin}"
    // Optional libraries, add as needed, see their respective READMEs for additional implementation details
    // implementation "org.kin.sdk.android:base-compat:${versions.kin}"
    // implementation "org.kin.sdk.android:design:${versions.kin}"
    // implementation "org.kin.sdk.android:spend:${versions.kin}"
}
```

## Demo App

The [demo](demo) directory includes a demo application, showcasing a Kin wallet.

## Design Showcase App

The [design-showcase](design-showcase) directory includes an application showcasing the UI widgets that are both: used in our full screen experiences, and also publicly available for all developers to use from the [design](design) library.

## Documentation

KDoc Documentation for all classes in all modules located [here](https://kinecosystem.github.io/kin-android/docs/index.html)
