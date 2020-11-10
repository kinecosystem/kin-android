[kin-android](../../index.md) / [org.kin.sdk.base.network.services](../index.md) / [Cache](./index.md)

# Cache

`data class Cache<KEY>`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `Cache(storage: `[`MutableMap`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-map/index.html)`<KEY, `[`Pair`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)`<*, `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`>> = HashMap(), defaultTimeout: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)` = TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES), executor: `[`ExecutorService`](https://docs.oracle.com/javase/6/docs/api/java/util/concurrent/ExecutorService.html)` = Executors.newSingleThreadExecutor())` |

### Properties

| Name | Summary |
|---|---|
| [defaultTimeout](default-timeout.md) | `val defaultTimeout: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
| [executor](executor.md) | `val executor: `[`ExecutorService`](https://docs.oracle.com/javase/6/docs/api/java/util/concurrent/ExecutorService.html) |

### Functions

| Name | Summary |
|---|---|
| [invalidate](invalidate.md) | `fun invalidate(key: KEY): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [resolve](resolve.md) | `fun <VALUE> resolve(key: KEY, timeoutOverride: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)` = -1, fault: (KEY) -> `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<VALUE>): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<VALUE>` |
| [warm](warm.md) | `fun <VALUE> warm(key: KEY, fault: (KEY) -> `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<VALUE>): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<VALUE>` |
