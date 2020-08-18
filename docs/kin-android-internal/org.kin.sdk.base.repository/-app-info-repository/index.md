[kin-android](../../index.md) / [org.kin.sdk.base.repository](../index.md) / [AppInfoRepository](./index.md)

# AppInfoRepository

`interface AppInfoRepository`

### Functions

| Name | Summary |
|---|---|
| [addAppInfo](add-app-info.md) | `abstract fun addAppInfo(appInfo: `[`AppInfo`](../../org.kin.sdk.base.models/-app-info/index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [appInfoByAppIndex](app-info-by-app-index.md) | `abstract fun appInfoByAppIndex(appIndex: `[`AppIdx`](../../org.kin.sdk.base.models/-app-idx/index.md)`): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`Optional`](../../org.kin.sdk.base.tools/-optional/index.md)`<`[`AppInfo`](../../org.kin.sdk.base.models/-app-info/index.md)`>>` |

### Inheritors

| Name | Summary |
|---|---|
| [InMemoryAppInfoRepositoryImpl](../-in-memory-app-info-repository-impl/index.md) | `class InMemoryAppInfoRepositoryImpl : `[`AppInfoRepository`](./index.md) |
