[kin-android](../../index.md) / [org.kin.sdk.base.tools](../index.md) / [NetworkOperationsHandler](./index.md)

# NetworkOperationsHandler

`interface NetworkOperationsHandler`

### Functions

| Name | Summary |
|---|---|
| [queueOperation](queue-operation.md) | `abstract fun <ResponseType> queueOperation(op: `[`NetworkOperation`](../-network-operation/index.md)`<ResponseType>): `[`NetworkOperation`](../-network-operation/index.md)`<ResponseType>` |

### Extension Functions

| Name | Summary |
|---|---|
| [queueWork](../queue-work.md) | `fun <T> `[`NetworkOperationsHandler`](./index.md)`.queueWork(work: (`[`PromisedCallback`](../-promised-callback/index.md)`<T>) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Promise`](../-promise/index.md)`<T>` |

### Inheritors

| Name | Summary |
|---|---|
| [NetworkOperationsHandlerImpl](../-network-operations-handler-impl/index.md) | `class NetworkOperationsHandlerImpl : `[`NetworkOperationsHandler`](./index.md) |
