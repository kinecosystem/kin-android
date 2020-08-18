[kin-android](../../index.md) / [org.kin.sdk.base.models](../index.md) / [AppInfo](./index.md)

# AppInfo

`data class AppInfo`

### Parameters

`appIndex` -
* An assigned integer for this application. Make sure you are using the correct one for your app!

`kinAccountId` -
* This [KinAccount.Id](../-kin-account/-id/index.md) is for the account you wish to collect Kin from by default.

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `AppInfo(appIndex: `[`AppIdx`](../-app-idx/index.md)`, kinAccountId: Id, appName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, appIconResourceId: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`)` |

### Properties

| Name | Summary |
|---|---|
| [appIconResourceId](app-icon-resource-id.md) | <ul><li>The ResourceId of your app's icon.</li></ul>`val appIconResourceId: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [appIndex](app-index.md) | <ul><li>An assigned integer for this application. Make sure you are using the correct one for your app!</li></ul>`val appIndex: `[`AppIdx`](../-app-idx/index.md) |
| [appName](app-name.md) | <ul><li>The name of your App that can be used to display to a user in certain contexts.</li></ul>`val appName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [kinAccountId](kin-account-id.md) | <ul><li>This [KinAccount.Id](../-kin-account/-id/index.md) is for the account you wish to collect Kin from by default.</li></ul>`val kinAccountId: Id` |
