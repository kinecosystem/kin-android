[kin-android](../../index.md) / [org.kin.sdk.base.tools](../index.md) / [Optional](./index.md)

# Optional

`class Optional<T>`

### Properties

| Name | Summary |
|---|---|
| [isPresent](is-present.md) | `val isPresent: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |

### Functions

| Name | Summary |
|---|---|
| [equals](equals.md) | `fun equals(other: `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`?): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [get](get.md) | `fun get(): T?` |
| [hashCode](hash-code.md) | `fun hashCode(): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [map](map.md) | `fun <S> map(map: (T) -> S): `[`Optional`](./index.md)`<S>` |
| [mapNullable](map-nullable.md) | `fun <S> mapNullable(map: (T) -> S): `[`Optional`](./index.md)`<S>` |
| [orElse](or-else.md) | `fun orElse(other: T): T`<br>`fun orElse(other: () -> T): T` |
| [toString](to-string.md) | `fun toString(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Companion Object Functions

| Name | Summary |
|---|---|
| [empty](empty.md) | `fun <T> empty(): `[`Optional`](./index.md)`<T>` |
| [of](of.md) | `fun <T> of(value: T): `[`Optional`](./index.md)`<T>` |
| [ofNullable](of-nullable.md) | `fun <T> ofNullable(value: T?): `[`Optional`](./index.md)`<T>` |
