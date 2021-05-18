# <img src="../assets/kin-logo.png" height="32" alt="Kin Logo"> Spend Module
[![KDoc](https://img.shields.io/badge/Docs-KDoc-blue)](https://kinecosystem.github.io/kin-android/docs)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.kin.sdk.android/spend/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.kin.sdk.android/spend)

The spend module provides an easy to use out of the box spend flow UI that is prescribed to be used to execute spend payments for digital goods and services with Kin in your app.

## Requirements
Android API 19+

You'll need to have created a funded KinAccount and set up a valid `KinEnvironment.Agora` as documented in [base](../base).

## Installation
Add the following to your project's gradle file.
This will also transitively pull in the [base module](../base) into your project as well.
```groovy
dependencies {
    // ...
    implementation "org.kin.sdk.android:spend:${versions.kin}"
}
repositories {
    // ...
    jcenter()
}
```

##  Overview
The following video showcases how the Spend flow is used in the included [demo app](../demo) that you can try out as a user or review the code on how the experience was achieved. The actual spend flow included in this module begins with the model flow when the 'Pay Now' button is tapped and you see the screen with 'Confirm' button. It then progresses to a 'Confirming' screen, before ultimately either showing 'Confirmed' or and error state. The Confirm screen shows your AppInfo that was configured in the `AppInfoProvider` (read more about in [base](../base))and set to your KinEnvironment builder.

<img src="../assets/kin-pay-invoice-demo-app.gif" alt="kin-pay-invoice-demo-app.gif" width="300" height="auto"/>


## How to Use
To show the spend flow, first create an Invoice according to the Invoices documentation in [base](../base), and a `SpendNavigator` with the activity reference you plan on showing the spend flow from.
Then call `confirmPaymentOfInvoice` with your invoice and the valid `KinAccount.Id` corresponding to the `KinAccount` you plan on paying with. The possible failure reason's all have corresponding text that is shown the user in the event of these errors, but the reason is passed on to you as the caller to handle further if needed. e.g. redirect users to a place where they can get kin in the event they have an `INSUFFICIENT_BALANCE`, present the user with the option to go to your listing in the Google Play Store to check for updates of your app if you get `SDK_UPGRADE_REQUIRED`, etc.

*Note: This KinAccount must have been created or imported into this app so that a valid KinAccountContext can be created with it, otherwise you will end up with `UNKNOWN_PAYER_ACCOUNT`.*

```kotlin
    val kinEnvironment: KinEnvironment.Agora
    val invoice: Invoice
    val controller = SpendController(kinEnvironment, SpendNavigatorImpl(activity))

    controller.confirmPaymentOfInvoice(
        invoice,
        payerAccountId
    ) { transactionHash: TransactionHash?, failureReason: PaymentFlowViewModel.Result.Failure.Reason? ->
        if (transactionHash != null) {
            // Payment Success
        } else if (failureReason != null){
            // Payment Failure with failureReason
            when(failureReason) {
                Result.Failure.Reason.CANCELLED -> TODO()
                Result.Failure.Reason.ALREADY_PURCHASED -> TODO()
                Result.Failure.Reason.UNKNOWN_FAILURE -> TODO()
                Result.Failure.Reason.UNKNOWN_INVOICE -> TODO()
                Result.Failure.Reason.UNKNOWN_PAYER_ACCOUNT -> TODO()
                Result.Failure.Reason.INSUFFICIENT_BALANCE -> TODO()
                Result.Failure.Reason.MISCONFIGURED_REQUEST -> TODO()
                Result.Failure.Reason.DENIED_BY_SERVICE -> TODO()
                Result.Failure.Reason.SDK_UPGRADE_REQUIRED -> TODO()
                Result.Failure.Reason.BAD_NETWORK -> TODO()
            }
        }
    }
```

## Advanced Use

If you abstract navigation from your view layer, you can use the `SpendNavigator` instance from that layer and navigate using it there yourself. However, you must also make sure that the Invoice and the AppInfo are added to the appropriate repositories. This is how the [Demo App](../demo) is setup.
