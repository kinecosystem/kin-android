[kin-android](../../index.md) / [org.kin.sdk.base.models](../index.md) / [KinAmount](./index.md)

# KinAmount

`data class KinAmount`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `KinAmount(value: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`)`<br>`KinAmount(value: `[`BigInteger`](https://docs.oracle.com/javase/6/docs/api/java/math/BigInteger.html)`)`<br>`KinAmount(value: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`)`<br>`KinAmount(value: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)`)`<br>`KinAmount(amount: `[`BigDecimal`](https://docs.oracle.com/javase/6/docs/api/java/math/BigDecimal.html)`)` |

### Properties

| Name | Summary |
|---|---|
| [value](value.md) | `val value: `[`BigDecimal`](https://docs.oracle.com/javase/6/docs/api/java/math/BigDecimal.html) |

### Functions

| Name | Summary |
|---|---|
| [equals](equals.md) | `fun equals(other: `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`?): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [hashCode](hash-code.md) | `fun hashCode(): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [minus](minus.md) | `operator fun minus(amount: `[`KinAmount`](./index.md)`): `[`KinAmount`](./index.md) |
| [plus](plus.md) | `operator fun plus(amount: `[`KinAmount`](./index.md)`): `[`KinAmount`](./index.md) |
| [toString](to-string.md) | `fun toString(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>`fun toString(precision: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Companion Object Properties

| Name | Summary |
|---|---|
| [ONE](-o-n-e.md) | `val ONE: `[`KinAmount`](./index.md) |
| [ZERO](-z-e-r-o.md) | `val ZERO: `[`KinAmount`](./index.md) |

### Companion Object Functions

| Name | Summary |
|---|---|
| [max](max.md) | `fun max(amount: `[`KinAmount`](./index.md)`, otherAmount: `[`KinAmount`](./index.md)`): `[`KinAmount`](./index.md) |

### Extension Functions

| Name | Summary |
|---|---|
| [toQuarks](../to-quarks.md) | `fun `[`KinAmount`](./index.md)`.toQuarks(): `[`QuarkAmount`](../-quark-amount/index.md) |
