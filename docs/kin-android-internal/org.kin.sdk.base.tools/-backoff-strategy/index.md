[kin-android](../../index.md) / [org.kin.sdk.base.tools](../index.md) / [BackoffStrategy](./index.md)

# BackoffStrategy

`sealed class BackoffStrategy`

### Types

| Name | Summary |
|---|---|
| [Custom](-custom/index.md) | `class Custom : `[`BackoffStrategy`](./index.md) |
| [Exponential](-exponential/index.md) | `class Exponential : `[`BackoffStrategy`](./index.md) |
| [ExponentialIncrease](-exponential-increase/index.md) | `class ExponentialIncrease : `[`BackoffStrategy`](./index.md) |
| [Fixed](-fixed/index.md) | `class Fixed : `[`BackoffStrategy`](./index.md) |
| [Never](-never/index.md) | `class Never : `[`BackoffStrategy`](./index.md) |

### Properties

| Name | Summary |
|---|---|
| [maxAttempts](max-attempts.md) | `val maxAttempts: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

### Functions

| Name | Summary |
|---|---|
| [delayForAttempt](delay-for-attempt.md) | `fun delayForAttempt(attempt: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
| [nextDelay](next-delay.md) | `fun nextDelay(): `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
| [reset](reset.md) | `fun reset(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Companion Object Functions

| Name | Summary |
|---|---|
| [combine](combine.md) | `fun combine(vararg strategies: `[`BackoffStrategy`](./index.md)`): `[`BackoffStrategy`](./index.md) |
