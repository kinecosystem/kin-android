[kin-android](../../../../../index.md) / [org.kin.base.viewmodel](../../../../index.md) / [PaymentFlowViewModel](../../../index.md) / [Result](../../index.md) / [Failure](../index.md) / [Reason](./index.md)

# Reason

`enum class Reason`

### Enum Values

| Name | Summary |
|---|---|
| [CANCELLED](-c-a-n-c-e-l-l-e-d.md) | The user cancelled the flow. |
| [ALREADY_PURCHASED](-a-l-r-e-a-d-y_-p-u-r-c-h-a-s-e-d.md) | The webhoook responded saying that this invoice has already been paid for. No Payment was just made. |
| [UNKNOWN_FAILURE](-u-n-k-n-o-w-n_-f-a-i-l-u-r-e.md) | Unknown Failure |
| [UNKNOWN_INVOICE](-u-n-k-n-o-w-n_-i-n-v-o-i-c-e.md) | Cannot locate the invoice specified. |
| [UNKNOWN_PAYER_ACCOUNT](-u-n-k-n-o-w-n_-p-a-y-e-r_-a-c-c-o-u-n-t.md) | The paying KinAccount is not found in storage. |
| [INSUFFICIENT_BALANCE](-i-n-s-u-f-f-i-c-i-e-n-t_-b-a-l-a-n-c-e.md) | Not enough kin to pay for the Invoice. |
| [MISCONFIGURED_REQUEST](-m-i-s-c-o-n-f-i-g-u-r-e-d_-r-e-q-u-e-s-t.md) | Something is wrong with the request. Developer intervention is probably required. |
| [DENIED_BY_SERVICE](-d-e-n-i-e-d_-b-y_-s-e-r-v-i-c-e.md) | The webhook has denied the payment or refused to whitelist it. |
| [SDK_UPGRADE_REQUIRED](-s-d-k_-u-p-g-r-a-d-e_-r-e-q-u-i-r-e-d.md) | You require a newer SDK to communicate with the Kin Blockchain. As a developer you should direct users to upgrade your app if you get this. |
| [BAD_NETWORK](-b-a-d_-n-e-t-w-o-r-k.md) | Not able to communicate with the server due to network problems. We likely timed out and failed retrying. |

### Properties

| Name | Summary |
|---|---|
| [value](value.md) | `val value: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

### Companion Object Functions

| Name | Summary |
|---|---|
| [fromValue](from-value.md) | `fun fromValue(value: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): Reason` |
