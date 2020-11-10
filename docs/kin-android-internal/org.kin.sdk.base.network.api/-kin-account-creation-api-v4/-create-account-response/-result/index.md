[kin-android](../../../../index.md) / [org.kin.sdk.base.network.api](../../../index.md) / [KinAccountCreationApiV4](../../index.md) / [CreateAccountResponse](../index.md) / [Result](./index.md)

# Result

`sealed class Result`

### Types

| Name | Summary |
|---|---|
| [BadNonce](-bad-nonce.md) | `object BadNonce : Result` |
| [Exists](-exists.md) | `object Exists : Result` |
| [Ok](-ok.md) | `object Ok : Result` |
| [PayerRequired](-payer-required.md) | `object PayerRequired : Result` |
| [TransientFailure](-transient-failure/index.md) | `data class TransientFailure : Result` |
| [UndefinedError](-undefined-error/index.md) | `data class UndefinedError : Result` |
| [UpgradeRequiredError](-upgrade-required-error.md) | `object UpgradeRequiredError : Result` |

### Properties

| Name | Summary |
|---|---|
| [value](value.md) | `val value: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
