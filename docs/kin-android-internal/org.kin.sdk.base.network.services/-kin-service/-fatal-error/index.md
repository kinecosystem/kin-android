[kin-android](../../../index.md) / [org.kin.sdk.base.network.services](../../index.md) / [KinService](../index.md) / [FatalError](./index.md)

# FatalError

`sealed class FatalError : `[`RuntimeException`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-runtime-exception/index.html)

### Types

| Name | Summary |
|---|---|
| [BadSequenceNumberInRequest](-bad-sequence-number-in-request.md) | `object BadSequenceNumberInRequest : FatalError` |
| [IllegalResponse](-illegal-response.md) | `object IllegalResponse : FatalError` |
| [InsufficientBalanceForSourceAccountInRequest](-insufficient-balance-for-source-account-in-request.md) | `object InsufficientBalanceForSourceAccountInRequest : FatalError` |
| [InsufficientFeeInRequest](-insufficient-fee-in-request.md) | `object InsufficientFeeInRequest : FatalError` |
| [ItemNotFound](-item-not-found.md) | `object ItemNotFound : FatalError` |
| [PermanentlyUnavailable](-permanently-unavailable.md) | `object PermanentlyUnavailable : FatalError` |
| [SDKUpgradeRequired](-s-d-k-upgrade-required.md) | It is expected that this error is handled gracefully by notifying users to upgrade to a newer version of the software that should contain a more recent version of this SDK.`object SDKUpgradeRequired : FatalError` |
| [UnknownAccountInRequest](-unknown-account-in-request.md) | `object UnknownAccountInRequest : FatalError` |
| [WebhookRejectedTransaction](-webhook-rejected-transaction.md) | `object WebhookRejectedTransaction : FatalError` |

### Exceptions

| Name | Summary |
|---|---|
| [Denied](-denied/index.md) | `open class Denied : FatalError` |
| [IllegalRequest](-illegal-request/index.md) | `open class IllegalRequest : FatalError` |
| [InvoiceErrorsInRequest](-invoice-errors-in-request/index.md) | `data class InvoiceErrorsInRequest : FatalError` |
| [TransientFailure](-transient-failure/index.md) | `open class TransientFailure : FatalError` |
| [UnexpectedServiceError](-unexpected-service-error/index.md) | `open class UnexpectedServiceError : FatalError` |
