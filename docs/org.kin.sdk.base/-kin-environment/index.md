[kin-android](../../index.md) / [org.kin.sdk.base](../index.md) / [KinEnvironment](./index.md)

# KinEnvironment

`sealed class KinEnvironment`

### Types

| Name | Summary |
|---|---|
| [Horizon](-horizon/index.md) | `class Horizon : `[`KinEnvironment`](./index.md) |

### Properties

| Name | Summary |
|---|---|
| [logger](logger.md) | `abstract val logger: ILoggerFactory` |
| [networkEnvironment](network-environment.md) | `abstract val networkEnvironment: `[`NetworkEnvironment`](../../org.kin.sdk.base.stellar.models/-network-environment/index.md) |
| [service](service.md) | `abstract val service: `[`KinService`](../../org.kin.sdk.base.network.services/-kin-service/index.md) |

### Functions

| Name | Summary |
|---|---|
| [importPrivateKey](import-private-key.md) | `fun importPrivateKey(privateKey: PrivateKey): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`>`<br>`fun importPrivateKey(privateKey: PrivateKey, callback: `[`Callback`](../../org.kin.sdk.base.tools/-callback/index.md)`<`[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
