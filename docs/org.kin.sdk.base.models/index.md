[kin-android](../index.md) / [org.kin.sdk.base.models](./index.md)

## Package org.kin.sdk.base.models

### Types

| Name | Summary |
|---|---|
| [Key](-key/index.md) | `sealed class Key` |
| [KinAccount](-kin-account/index.md) | `data class KinAccount` |
| [KinAmount](-kin-amount/index.md) | `data class KinAmount` |
| [KinBalance](-kin-balance/index.md) | `data class KinBalance` |
| [KinDateFormat](-kin-date-format/index.md) | `class KinDateFormat` |
| [KinMemo](-kin-memo/index.md) | `data class KinMemo` |
| [KinPayment](-kin-payment/index.md) | `data class KinPayment` |
| [KinPaymentItem](-kin-payment-item/index.md) | `data class KinPaymentItem` |
| [QuarkAmount](-quark-amount/index.md) | `data class QuarkAmount` |
| [TransactionHash](-transaction-hash/index.md) | `data class TransactionHash` |

### Extensions for External Classes

| Name | Summary |
|---|---|
| [kotlin.ByteArray](kotlin.-byte-array/index.md) |  |
| [kotlin.collections.List](kotlin.collections.-list/index.md) |  |
| [kotlin.String](kotlin.-string/index.md) |  |
| [org.kin.stellarfork.KeyPair](org.kin.stellarfork.-key-pair/index.md) |  |
| [org.kin.stellarfork.responses.AccountResponse](org.kin.stellarfork.responses.-account-response/index.md) |  |
| [org.kin.stellarfork.responses.SubmitTransactionResponse](org.kin.stellarfork.responses.-submit-transaction-response/index.md) |  |
| [org.kin.stellarfork.responses.TransactionResponse](org.kin.stellarfork.responses.-transaction-response/index.md) |  |
| [org.kin.stellarfork.Transaction](org.kin.stellarfork.-transaction/index.md) |  |

### Functions

| Name | Summary |
|---|---|
| [asKinAccountId](as-kin-account-id.md) | `fun `[`Key`](-key/index.md)`.asKinAccountId(): Id` |
| [asKinPayments](as-kin-payments.md) | `fun `[`KinTransaction`](../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`.asKinPayments(): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinPayment`](-kin-payment/index.md)`>` |
| [asPublicKey](as-public-key.md) | `fun `[`Key`](-key/index.md)`.asPublicKey(): PublicKey` |
| [getNetwork](get-network.md) | `fun `[`NetworkEnvironment`](../org.kin.sdk.base.stellar.models/-network-environment/index.md)`.getNetwork(): Network` |
| [merge](merge.md) | `fun `[`KinAccount`](-kin-account/index.md)`.merge(account: `[`KinAccount`](-kin-account/index.md)`): `[`KinAccount`](-kin-account/index.md) |
| [toAccount](to-account.md) | `fun `[`KinAccount`](-kin-account/index.md)`.toAccount(): Account` |
| [toKeyPair](to-key-pair.md) | `fun Id.toKeyPair(): KeyPair` |
| [toKin](to-kin.md) | `fun `[`QuarkAmount`](-quark-amount/index.md)`.toKin(): `[`KinAmount`](-kin-amount/index.md) |
| [toQuarks](to-quarks.md) | `fun `[`KinAmount`](-kin-amount/index.md)`.toQuarks(): `[`QuarkAmount`](-quark-amount/index.md) |
| [toSigningKeyPair](to-signing-key-pair.md) | `fun `[`KinAccount`](-kin-account/index.md)`.toSigningKeyPair(): KeyPair` |
