[kin-android](../index.md) / [org.kin.sdk.base.models](./index.md)

## Package org.kin.sdk.base.models

### Types

| Name | Summary |
|---|---|
| [AccountSpec](-account-spec/index.md) | A spec for how to interpret a set of accounts.`sealed class AccountSpec` |
| [AppId](-app-id/index.md) | `data class AppId` |
| [AppIdx](-app-idx/index.md) | `data class AppIdx` |
| [AppInfo](-app-info/index.md) | `data class AppInfo` |
| [AppUserCreds](-app-user-creds/index.md) | Used as passthrough auth params in the headers of the SubmitTransaction request`data class AppUserCreds` |
| [ClassicKinMemo](-classic-kin-memo/index.md) | This is used to format a text based [KinMemo](-kin-memo/index.md) with the indended format of     "appIdVersion-appId-memoSuffix" e.g. "1-aef2-someAppLandMemoInfo"`data class ClassicKinMemo` |
| [Invoice](-invoice/index.md) | Contains the information about what a given [KinPayment](-kin-payment/index.md) was for.`data class Invoice` |
| [InvoiceList](-invoice-list/index.md) | A collection of [Invoice](-invoice/index.md)s. Often submitted in the same [KinTransaction](#) together.`data class InvoiceList` |
| [Key](-key/index.md) | `sealed class Key` |
| [KinAccount](-kin-account/index.md) | `data class KinAccount` |
| [KinAmount](-kin-amount/index.md) | `data class KinAmount` |
| [KinBalance](-kin-balance/index.md) | `data class KinBalance` |
| [KinBinaryMemo](-kin-binary-memo/index.md) | A binary Kin memo format.`data class KinBinaryMemo` |
| [KinDateFormat](-kin-date-format/index.md) | `class KinDateFormat` |
| [KinMemo](-kin-memo/index.md) | `data class KinMemo` |
| [KinPayment](-kin-payment/index.md) | `data class KinPayment` |
| [KinPaymentItem](-kin-payment-item/index.md) | `data class KinPaymentItem` |
| [LineItem](-line-item/index.md) | An individual item in an [Invoice](-invoice/index.md)`data class LineItem` |
| [MemoSuffix](-memo-suffix/index.md) | `data class MemoSuffix` |
| [QuarkAmount](-quark-amount/index.md) | `data class QuarkAmount` |
| [SDKConfig](-s-d-k-config/index.md) | `object SDKConfig` |
| [SHA224Hash](-s-h-a224-hash/index.md) | The SHA-224 hash of an Invoice or InvoiceList.`data class SHA224Hash` |
| [SKU](-s-k-u/index.md) | `data class SKU` |
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
| [asKinMemo](as-kin-memo.md) | `fun `[`ClassicKinMemo`](-classic-kin-memo/index.md)`.asKinMemo(): `[`KinMemo`](-kin-memo/index.md) |
| [asKinPayments](as-kin-payments.md) | `fun `[`KinTransaction`](../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`.asKinPayments(): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinPayment`](-kin-payment/index.md)`>` |
| [asPublicKey](as-public-key.md) | `fun `[`Key`](-key/index.md)`.asPublicKey(): PublicKey` |
| [createStellarSigningAccount](create-stellar-signing-account.md) | `fun createStellarSigningAccount(privateKey: PrivateKey, sequence: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`): Account` |
| [getAgoraMemo](get-agora-memo.md) | `fun `[`KinMemo`](-kin-memo/index.md)`.getAgoraMemo(): `[`KinBinaryMemo`](-kin-binary-memo/index.md)`?` |
| [getNetwork](get-network.md) | `fun `[`NetworkEnvironment`](../org.kin.sdk.base.stellar.models/-network-environment/index.md)`.getNetwork(): Network` |
| [merge](merge.md) | `fun `[`KinAccount`](-kin-account/index.md)`.merge(newer: `[`KinAccount`](-kin-account/index.md)`): `[`KinAccount`](-kin-account/index.md) |
| [toAccount](to-account.md) | `fun `[`KinAccount`](-kin-account/index.md)`.toAccount(): Account` |
| [toKeyPair](to-key-pair.md) | `fun Id.toKeyPair(): KeyPair` |
| [toKin](to-kin.md) | `fun `[`QuarkAmount`](-quark-amount/index.md)`.toKin(): `[`KinAmount`](-kin-amount/index.md) |
| [toQuarks](to-quarks.md) | `fun `[`KinAmount`](-kin-amount/index.md)`.toQuarks(): `[`QuarkAmount`](-quark-amount/index.md) |
| [toSigningKeyPair](to-signing-key-pair.md) | `fun `[`KinAccount`](-kin-account/index.md)`.toSigningKeyPair(): KeyPair`<br>`fun PrivateKey.toSigningKeyPair(): KeyPair` |
