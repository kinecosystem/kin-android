[kin-android](../../index.md) / [org.kin.sdk.base.network.services](../index.md) / [MetaServiceApiImpl](./index.md)

# MetaServiceApiImpl

`class MetaServiceApiImpl : `[`MetaServiceApi`](../-meta-service-api/index.md)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `MetaServiceApiImpl(configuredMinApi: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, opHandler: `[`NetworkOperationsHandler`](../../org.kin.sdk.base.tools/-network-operations-handler/index.md)`, api: `[`KinTransactionApiV4`](../../org.kin.sdk.base.network.api/-kin-transaction-api-v4/index.md)`, storage: `[`Storage`](../../org.kin.sdk.base.storage/-storage/index.md)`)` |

### Properties

| Name | Summary |
|---|---|
| [configuredMinApi](configured-min-api.md) | `var configuredMinApi: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

### Functions

| Name | Summary |
|---|---|
| [getMinApiVersion](get-min-api-version.md) | `fun getMinApiVersion(): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`>` |
| [postInit](post-init.md) | `fun postInit(): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`>` |
