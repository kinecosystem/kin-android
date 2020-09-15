[kin-android](../../index.md) / [org.kin.sdk.base](../index.md) / [KinEnvironment](./index.md)

# KinEnvironment

`sealed class KinEnvironment`

### Types

| Name | Summary |
|---|---|
| [Agora](-agora/index.md) | `class Agora : `[`KinEnvironment`](./index.md) |
| [Horizon](-horizon/index.md) | `class ~~Horizon~~ : `[`KinEnvironment`](./index.md) |

### Exceptions

| Name | Summary |
|---|---|
| [KinEnvironmentBuilderException](-kin-environment-builder-exception/index.md) | `class KinEnvironmentBuilderException : `[`IllegalStateException`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-illegal-state-exception/index.html) |

### Properties

| Name | Summary |
|---|---|
| [logger](logger.md) | `abstract val logger: `[`KinLoggerFactory`](../../org.kin.sdk.base.tools/-kin-logger-factory/index.md) |
| [networkEnvironment](network-environment.md) | `abstract val networkEnvironment: `[`NetworkEnvironment`](../../org.kin.sdk.base.stellar.models/-network-environment/index.md) |
| [service](service.md) | `abstract val service: `[`KinService`](../../org.kin.sdk.base.network.services/-kin-service/index.md) |

### Functions

| Name | Summary |
|---|---|
| [allAccountIds](all-account-ids.md) | `fun allAccountIds(): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<Id>>` |
| [importPrivateKey](import-private-key.md) | `fun importPrivateKey(privateKey: PrivateKey): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`>`<br>`fun importPrivateKey(privateKey: PrivateKey, callback: `[`Callback`](../../org.kin.sdk.base.tools/-callback/index.md)`<`[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [setEnableLogging](set-enable-logging.md) | `fun setEnableLogging(enableLogging: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
