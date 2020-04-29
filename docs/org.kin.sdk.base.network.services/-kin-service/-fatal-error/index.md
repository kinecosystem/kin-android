[kin-android](../../../index.md) / [org.kin.sdk.base.network.services](../../index.md) / [KinService](../index.md) / [FatalError](./index.md)

# FatalError

`sealed class FatalError : `[`Exception`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-exception/index.html)

### Types

| Name | Summary |
|---|---|
| [IllegalResponse](-illegal-response.md) | `object IllegalResponse : FatalError` |
| [ItemNotFound](-item-not-found.md) | `object ItemNotFound : FatalError` |
| [PermanentlyUnavailable](-permanently-unavailable.md) | `object PermanentlyUnavailable : FatalError` |

### Exceptions

| Name | Summary |
|---|---|
| [Denied](-denied/index.md) | `open class Denied : FatalError` |
| [IllegalRequest](-illegal-request/index.md) | `open class IllegalRequest : FatalError` |
| [TransientFailure](-transient-failure/index.md) | `open class TransientFailure : FatalError` |
| [UnexpectedServiceError](-unexpected-service-error/index.md) | `open class UnexpectedServiceError : FatalError` |
