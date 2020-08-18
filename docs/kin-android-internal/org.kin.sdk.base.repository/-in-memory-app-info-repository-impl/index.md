[kin-android](../../index.md) / [org.kin.sdk.base.repository](../index.md) / [InMemoryAppInfoRepositoryImpl](./index.md)

# InMemoryAppInfoRepositoryImpl

`class InMemoryAppInfoRepositoryImpl : `[`AppInfoRepository`](../-app-info-repository/index.md)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `InMemoryAppInfoRepositoryImpl(storage: `[`MutableMap`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-map/index.html)`<`[`AppIdx`](../../org.kin.sdk.base.models/-app-idx/index.md)`, `[`AppInfo`](../../org.kin.sdk.base.models/-app-info/index.md)`> = mutableMapOf<AppIdx, AppInfo>())` |

### Functions

| Name | Summary |
|---|---|
| [addAppInfo](add-app-info.md) | `fun addAppInfo(appInfo: `[`AppInfo`](../../org.kin.sdk.base.models/-app-info/index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [appInfoByAppIndex](app-info-by-app-index.md) | `fun appInfoByAppIndex(appIndex: `[`AppIdx`](../../org.kin.sdk.base.models/-app-idx/index.md)`): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`Optional`](../../org.kin.sdk.base.tools/-optional/index.md)`<`[`AppInfo`](../../org.kin.sdk.base.models/-app-info/index.md)`>>` |
