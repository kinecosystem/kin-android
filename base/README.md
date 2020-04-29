# <img src="../assets/kin-logo.png" height="32" alt="Kin Logo"> Base Module
[![KDoc](https://img.shields.io/badge/Docs-KDoc-blue)](https://kinecosystem.github.io/kin-android/docs)

The [:base](../base) module is the foundation upon which the rest of the sdk stands on, however can be used on it's own to headlessly access the Kin Blockchain.


<img src="../assets/kotlin.png" height="24" alt="Kin Logo"> This is a Kotlin first library, but is also fully available to Java developers.

*For Java developers that prefer more conventional Java callback/listener idioms see [Java Idioms](#java-idioms) below.*

## Installation
Add the following to your project's gradle file.
```groovy
dependencies {
    // ...
    implementation "org.kin.sdk.android:base:${versions.kin}"
}
```

## Quick Start
Everything starts with a `KinEnvironment` instance that describes which blockchain, services, and storage will be used.
```kotlin
val environment: KinEnvironment = 
    KinEnvironment.Horizon.Builder(NetworkEnvironment.KinStellarTestNet)
        .setStorage(KinFileStorage.Builder("path/to/storage/location"))
        .build()
```
For a given `KinAccount` that you want to operate on, you will need a `KinAccountContext` instance.
This will be used to both create and access all `KinAccount` and `KinPayment`s.
A `KinEnvironment`
 ```kotlin
var context: KinAccountContext =
    KinAccountContext.Builder(environment)
        .createNewAccount()
        .build()
```

### *As you may notice on the `KinAccountContext.Builder`, there are a few options on how to configure a `KinAccountContext`...*

## Creating An Account
If you want to create a new `KinAccount` use:
```kotlin
.createNewAccount()
```
## Access An Existing Account
If you want to access an existing `KinAccount` with options to send `KinPayment`s, input the `KinAccount.Id` with:
```kotlin
.useExistingAccount(KinAccount.Id("GATG_example_and_fake_key"))
```
*Note: this variant requires that the `KinEnvironment` knows about this `KinAccount`s `Key.PrivateKey` which can be imported the first time by:*
```kotlin
environment.importPrivateKey(Key.PrivateKey("SDR2_example_and_fake_key"))
    .then { success: Boolean ->
        // Completed
    }
```
## Read Only Account Data
For `KinAccount`s that you do **not** have a `Key.PrivateKey` for and only desired read-only access to the data associated with that account use:
```kotlin
.useExistingAccountReadOnly(KinAccount.Id("GATG_example_and_fake_key"))
```
## Sending Payments
Sending `KinPayment`s are easy. Just add the amount and the destination `KinAccount.Id`.

*Note: successive calls to this function before the previous is completed will be properly queued according to blockchain implementation needs.*
```kotlin
context.sendKinPayment(KinAmount(5), KinAccount.Id("GATG_example_and_fake_key"))
    .then { payment: KinPayment ->
        // Payment Completed
    }
```
Sending a batch of payments to the blockchain to be completed together, in a single transaction, is just as easy.

*Note: This operation is atomic. All payments will either succeed or fail together.*
```kotlin
context.sendKinPayments(
    listOf(
        KinPaymentItem(
            KinAmount(5),
            KinAccount.Id("GATG_example_and_fake_key")
        ), KinPaymentItem(
            KinAmount(30),
            KinAccount.Id("GBCA_example_and_fake_key")
        )
    )
).then { completedPayments: List<KinPayment> ->
    // Payments Completed
}
```

## Retrieving Account Data
The `KinAccount.Id` for a given `KinAccountContext` instance is always available
```kotlin
context.accountId
```
If you require more than just the id, the full `KinAccount` is available by querying with:
```kotlin
context.getAccount()
    .then { kinAccount: KinAccount ->
        // Do something with the account data
    }
```
Observing balance changes over time is another common account operation:

*Note: don't forget to clean up when the observer is no longer required! This can be accomplished via a `DisposeBag`, or by calling `.remove(listener)` on the `Observer`*
```kotlin
val lifecycle = DisposeBag()
context.observeBalance()
    .add { kinBalance: KinBalance ->
        // Do something on balance update
    }.disposedBy(lifecycle)
```

## Retrieving Payment Data
Weather you're looking for the full payment history, or just to be notified of new payments you can observe any changes to payments for a given account with:
```kotlin
context.observePayments()
    .add { payments: List<KinPayment> -> 
        // Will emit the full payment history by default
        // @see ObserverMode for more details
    }
    .disposedBy(lifecycle)
```
Sometimes it's useful to retrieve payments that were processed together in a single `KinTransaction`
```kotlin
context.getPaymentsForTransactionHash(TransactionHash("<txnHash>"))
    .then { payments: List<KinPayment> ->
        // Payments related to txn hash
    }
```

## Other
When done with a particular account, you can irrevokably delete the data, **including the private key**, by performing the following:
```kotlin
context.clearStorage()
    .then { success: Boolean ->
        // The data with this KinAccountContext is now gone forever
    }
```

## Java Idioms
Instead of Promise .then tail calls in Java...
```java
context.getAccount(new Callback<KinAccount>() {
        @Override
        public void onCompleted(@Nullable KinAccount value, @Nullable Throwable error) {
                
        }
    });
```
Instead of Observer .add tail calls in Java...
```java
context.observeBalance(ObservationMode.Passive.INSTANCE, new ValueListener<KinBalance>() {
        @Override
        public void onNext(KinBalance value) {
            // do something
        }
        @Override
        public void onError(@NotNull Throwable error) {
            // handle error
        }
    }).disposedBy(lifecycle);
```
Tail calls in Java with lambdas...
```java
context.getAccount()
    .then(account -> null);

context.observeBalance(ObservationMode.Passive.INSTANCE)
    .add(kinBalance -> Unit.INSTANCE)
    .disposedBy(lifecycle);
```
Tail calls in Java *NO* lambdas...
```java
context.getAccount()
    .then(new Function1<KinAccount, Unit>() {
        @Override
        public Unit invoke(KinAccount kinAccount) {
            return null;
        }
    });

context.observeBalance(ObservationMode.Passive.INSTANCE)
    .add(new Function1<KinBalance, Unit>() {
        @Override
        public Unit invoke(KinBalance kinBalance) {
            return null;
        }
    })
    .disposedBy(lifecycle);
```

