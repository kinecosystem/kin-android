[kin-android](../index.md) / [org.kin.sdk.base.stellar.models](./index.md)

## Package org.kin.sdk.base.stellar.models

### Types

| Name | Summary |
|---|---|
| [ApiConfig](-api-config/index.md) | `sealed class ApiConfig` |
| [KinOperation](-kin-operation/index.md) | `sealed class KinOperation` |
| [KinTransaction](-kin-transaction/index.md) | `interface KinTransaction` |
| [KinTransactions](-kin-transactions/index.md) | `data class KinTransactions` |
| [NetworkEnvironment](-network-environment/index.md) | `sealed class NetworkEnvironment` |
| [SolanaKinTransaction](-solana-kin-transaction/index.md) | `data class SolanaKinTransaction : `[`KinTransaction`](-kin-transaction/index.md) |
| [StellarKinTransaction](-stellar-kin-transaction/index.md) | `data class StellarKinTransaction : `[`KinTransaction`](-kin-transaction/index.md) |

### Properties

| Name | Summary |
|---|---|
| [fee](fee.md) | `val `[`Transaction`](../org.kin.sdk.base.models.solana/-transaction/index.md)`.fee: `[`QuarkAmount`](../org.kin.sdk.base.models/-quark-amount/index.md) |
| [memo](memo.md) | `val `[`Transaction`](../org.kin.sdk.base.models.solana/-transaction/index.md)`.memo: `[`KinMemo`](../org.kin.sdk.base.models/-kin-memo/index.md) |
| [paymentOperations](payment-operations.md) | `val `[`Transaction`](../org.kin.sdk.base.models.solana/-transaction/index.md)`.paymentOperations: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<Payment>` |
| [signingSource](signing-source.md) | `val `[`Transaction`](../org.kin.sdk.base.models.solana/-transaction/index.md)`.signingSource: Id` |
| [totalAmount](total-amount.md) | `val `[`Transaction`](../org.kin.sdk.base.models.solana/-transaction/index.md)`.totalAmount: `[`KinAmount`](../org.kin.sdk.base.models/-kin-amount/index.md) |
| [transactionHash](transaction-hash.md) | `val `[`Transaction`](../org.kin.sdk.base.models.solana/-transaction/index.md)`.transactionHash: `[`TransactionHash`](../org.kin.sdk.base.models/-transaction-hash/index.md) |
