[kin-android](../../../../index.md) / [org.kin.sdk.base.network.api](../../../index.md) / [KinTransactionWhitelistingApi](../../index.md) / [WhitelistTransactionResponse](../index.md) / [Result](./index.md)

# Result

`sealed class Result`

### Types

| Name | Summary |
|---|---|
| [FailedToWhitelist](-failed-to-whitelist.md) | `object FailedToWhitelist : Result` |
| [Ok](-ok.md) | `object Ok : Result` |
| [TransientFailure](-transient-failure/index.md) | `data class TransientFailure : Result` |
| [UndefinedError](-undefined-error/index.md) | `data class UndefinedError : Result` |
| [UpgradeRequiredError](-upgrade-required-error.md) | `object UpgradeRequiredError : Result` |
| [WhitelistingDisabled](-whitelisting-disabled.md) | `object WhitelistingDisabled : Result` |

### Properties

| Name | Summary |
|---|---|
| [value](value.md) | `val value: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
