[kin-android](../../../../index.md) / [org.kin.sdk.base.network.api](../../../index.md) / [KinTransactionApiV4](../../index.md) / [SubmitTransactionResponse](../index.md) / [Result](./index.md)

# Result

`sealed class Result`

### Types

| Name | Summary |
|---|---|
| [BadSequenceNumber](-bad-sequence-number.md) | `object BadSequenceNumber : Result` |
| [InsufficientBalance](-insufficient-balance.md) | `object InsufficientBalance : Result` |
| [InsufficientFee](-insufficient-fee.md) | `object InsufficientFee : Result` |
| [InvoiceErrors](-invoice-errors/index.md) | `data class InvoiceErrors : Result` |
| [NoAccount](-no-account.md) | `object NoAccount : Result` |
| [Ok](-ok.md) | `object Ok : Result` |
| [TransientFailure](-transient-failure/index.md) | `data class TransientFailure : Result` |
| [UndefinedError](-undefined-error/index.md) | `data class UndefinedError : Result` |
| [UpgradeRequiredError](-upgrade-required-error.md) | `object UpgradeRequiredError : Result` |
| [WebhookRejected](-webhook-rejected.md) | `object WebhookRejected : Result` |

### Properties

| Name | Summary |
|---|---|
| [value](value.md) | `val value: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
