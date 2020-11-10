[kin-android](../../../../index.md) / [org.kin.sdk.base](../../../index.md) / [KinEnvironment](../../index.md) / [Agora](../index.md) / [Builder](./index.md)

# Builder

`class Builder`

### Types

| Name | Summary |
|---|---|
| [CompletedBuilder](-completed-builder/index.md) | `inner class CompletedBuilder` |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `Builder(networkEnvironment: `[`NetworkEnvironment`](../../../../org.kin.sdk.base.stellar.models/-network-environment/index.md)`)` |

### Functions

| Name | Summary |
|---|---|
| [setAppInfoProvider](set-app-info-provider.md) | `fun setAppInfoProvider(appInfoProvider: `[`AppInfoProvider`](../../../../org.kin.sdk.base.network.services/-app-info-provider/index.md)`): Builder` |
| [setEnableLogging](set-enable-logging.md) | `fun setEnableLogging(): Builder` |
| [setKinService](set-kin-service.md) | `fun setKinService(kinService: `[`KinService`](../../../../org.kin.sdk.base.network.services/-kin-service/index.md)`): Builder` |
| [setLogger](set-logger.md) | `fun setLogger(logger: `[`KinLoggerFactory`](../../../../org.kin.sdk.base.tools/-kin-logger-factory/index.md)`): Builder` |
| [setMinApiVersion](set-min-api-version.md) | This option allows developers to force which api version the KinService should use. v3 - stellar v4 - solana It is *not* required to set this as we default to v3 until migration day to solana.`fun setMinApiVersion(minApiVersion: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): Builder` |
| [setStorage](set-storage.md) | `fun setStorage(storage: `[`Storage`](../../../../org.kin.sdk.base.storage/-storage/index.md)`): CompletedBuilder`<br>`fun setStorage(fileStorageBuilder: Builder): CompletedBuilder` |
| [testMigration](test-migration.md) | This option allows developers to force an on-demand migration from the Stellar based Kin Blockchain to Solana on TestNet only.`fun testMigration(): Builder` |
