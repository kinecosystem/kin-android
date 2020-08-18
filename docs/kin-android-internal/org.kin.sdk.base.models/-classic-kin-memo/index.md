[kin-android](../../index.md) / [org.kin.sdk.base.models](../index.md) / [ClassicKinMemo](./index.md)

# ClassicKinMemo

`data class ClassicKinMemo`

This is used to format a text based [KinMemo](../-kin-memo/index.md) with the indended format of
    "appIdVersion-appId-memoSuffix"
e.g. "1-aef2-someAppLandMemoInfo"

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | This is used to format a text based [KinMemo](../-kin-memo/index.md) with the indended format of     "appIdVersion-appId-memoSuffix" e.g. "1-aef2-someAppLandMemoInfo"`ClassicKinMemo(appIdVersion: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)` = 1, appId: `[`AppId`](../-app-id/index.md)`, memoSuffix: `[`MemoSuffix`](../-memo-suffix/index.md)`)` |

### Properties

| Name | Summary |
|---|---|
| [appId](app-id.md) | `val appId: `[`AppId`](../-app-id/index.md) |
| [appIdVersion](app-id-version.md) | `val appIdVersion: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [memoSuffix](memo-suffix.md) | `val memoSuffix: `[`MemoSuffix`](../-memo-suffix/index.md) |

### Functions

| Name | Summary |
|---|---|
| [toString](to-string.md) | `fun toString(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Extension Functions

| Name | Summary |
|---|---|
| [asKinMemo](../as-kin-memo.md) | `fun `[`ClassicKinMemo`](./index.md)`.asKinMemo(): `[`KinMemo`](../-kin-memo/index.md) |
