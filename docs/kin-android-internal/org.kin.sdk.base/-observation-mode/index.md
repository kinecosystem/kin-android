[kin-android](../../index.md) / [org.kin.sdk.base](../index.md) / [ObservationMode](./index.md)

# ObservationMode

`sealed class ObservationMode`

Describes the mode by which updates are
presented to an [Observer](../../org.kin.sdk.base.tools/-observer/index.md)

### Types

| Name | Summary |
|---|---|
| [Active](-active.md) | Updates are pushed from the network. Includes all [Passive](-passive.md) updates.`object Active : `[`ObservationMode`](./index.md) |
| [ActiveNewOnly](-active-new-only.md) | Exclusively new updates from actions taken after starting to listen to this [Observer](../../org.kin.sdk.base.tools/-observer/index.md).`object ActiveNewOnly : `[`ObservationMode`](./index.md) |
| [Passive](-passive.md) | Updates are only based on local actions or via calling [Observer.requestInvalidation](../../org.kin.sdk.base.tools/-observer/request-invalidation.md)`object Passive : `[`ObservationMode`](./index.md) |

### Properties

| Name | Summary |
|---|---|
| [value](value.md) | `val value: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
