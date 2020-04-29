[kin-android](../../../index.md) / [org.kin.sdk.base.tools](../../index.md) / [BackoffStrategy](../index.md) / [Exponential](./index.md)

# Exponential

`class Exponential : `[`BackoffStrategy`](../index.md)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `Exponential(initial: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)` = 1000, multiplier: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)` = 2.0, jitter: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)` = 0.5, maximumWaitTime: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)` = DEFAULT_MAX_ATTEMPT_WAIT_TIME, maxAttempts: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)` = DEFAULT_MAX_ATTEMPTS)` |

### Properties

| Name | Summary |
|---|---|
| [initial](initial.md) | `val initial: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
| [jitter](jitter.md) | `val jitter: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html) |
| [maximumWaitTime](maximum-wait-time.md) | `val maximumWaitTime: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
| [multiplier](multiplier.md) | `val multiplier: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html) |
