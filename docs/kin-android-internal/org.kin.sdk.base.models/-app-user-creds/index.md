[kin-android](../../index.md) / [org.kin.sdk.base.models](../index.md) / [AppUserCreds](./index.md)

# AppUserCreds

`data class AppUserCreds`

Used as passthrough auth params in the headers of the SubmitTransaction request

### Parameters

`appUserId` -
* app-user-id in the header of the request

`appUserPasskey` - For more information regarding these parameters and webhook integration
please consult: https://docs.kin.org/how-it-works#webhooks

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | Used as passthrough auth params in the headers of the SubmitTransaction request`AppUserCreds(appUserId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, appUserPasskey: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`)` |

### Properties

| Name | Summary |
|---|---|
| [appUserId](app-user-id.md) | <ul><li>app-user-id in the header of the request</li></ul>`val appUserId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [appUserPasskey](app-user-passkey.md) | <ul><li>app-user-passkey in the header of the request</li></ul>`val appUserPasskey: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
