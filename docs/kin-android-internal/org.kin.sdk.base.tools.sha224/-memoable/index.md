[kin-android](../../index.md) / [org.kin.sdk.base.tools.sha224](../index.md) / [Memoable](./index.md)

# Memoable

`interface Memoable`

Interface for Memoable objects. Memoable objects allow the taking of a snapshot of their internal state
via the copy() method and then reseting the object back to that state later using the reset() method.

### Functions

| Name | Summary |
|---|---|
| [copy](copy.md) | Produce a copy of this object with its configuration and in its current state.`abstract fun copy(): `[`Memoable`](./index.md) |
| [reset](reset.md) | Restore a copied object state into this object.`abstract fun reset(other: `[`Memoable`](./index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [GeneralDigest](../-general-digest/index.md) | base implementation of MD4 family style digest as outlined in "Handbook of Applied Cryptography", pages 344 - 347.`abstract class GeneralDigest : `[`ExtendedDigest`](../-extended-digest/index.md)`, `[`Memoable`](./index.md) |
