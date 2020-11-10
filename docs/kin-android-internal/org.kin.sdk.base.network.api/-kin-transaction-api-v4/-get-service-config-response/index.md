[kin-android](../../../index.md) / [org.kin.sdk.base.network.api](../../index.md) / [KinTransactionApiV4](../index.md) / [GetServiceConfigResponse](./index.md)

# GetServiceConfigResponse

`data class GetServiceConfigResponse`

### Parameters

`subsidizerAccount` - The public key of the account that the service will use to sign transactions for funding.
    If not specified, the service is *not* configured to fund transactions.

### Types

| Name | Summary |
|---|---|
| [Result](-result/index.md) | `sealed class Result` |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `GetServiceConfigResponse(result: Result, subsidizerAccount: Id?, tokenProgram: Id?, token: Id?)` |

### Properties

| Name | Summary |
|---|---|
| [result](result.md) | `val result: Result` |
| [subsidizerAccount](subsidizer-account.md) | The public key of the account that the service will use to sign transactions for funding.     If not specified, the service is *not* configured to fund transactions.`val subsidizerAccount: Id?` |
| [token](token.md) | `val token: Id?` |
| [tokenProgram](token-program.md) | TODO: remove these two after we've locked in some tokens`val tokenProgram: Id?` |
