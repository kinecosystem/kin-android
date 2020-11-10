[kin-android](../../index.md) / [org.kin.sdk.base.models.solana](../index.md) / [Transaction](./index.md)

# Transaction

`data class Transaction`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `Transaction(message: `[`Message`](../-message/index.md)`, signatures: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Signature`](../-signature/index.md)`> = emptyList())` |

### Properties

| Name | Summary |
|---|---|
| [message](message.md) | `val message: `[`Message`](../-message/index.md) |
| [numRequiredSignatures](num-required-signatures.md) | `val numRequiredSignatures: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [signatures](signatures.md) | `val signatures: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Signature`](../-signature/index.md)`>` |

### Functions

| Name | Summary |
|---|---|
| [copyAndSetRecentBlockhash](copy-and-set-recent-blockhash.md) | `fun copyAndSetRecentBlockhash(recentBlockhash: `[`Hash`](../-hash/index.md)`): `[`Transaction`](./index.md) |
| [copyAndSign](copy-and-sign.md) | `fun copyAndSign(vararg signers: PrivateKey): `[`Transaction`](./index.md) |

### Companion Object Functions

| Name | Summary |
|---|---|
| [newTransaction](new-transaction.md) | `fun newTransaction(payer: PublicKey, vararg instructions: `[`Instruction`](../-instruction/index.md)`): `[`Transaction`](./index.md) |

### Extension Properties

| Name | Summary |
|---|---|
| [fee](../../org.kin.sdk.base.stellar.models/fee.md) | `val `[`Transaction`](./index.md)`.fee: `[`QuarkAmount`](../../org.kin.sdk.base.models/-quark-amount/index.md) |
| [memo](../../org.kin.sdk.base.stellar.models/memo.md) | `val `[`Transaction`](./index.md)`.memo: `[`KinMemo`](../../org.kin.sdk.base.models/-kin-memo/index.md) |
| [paymentOperations](../../org.kin.sdk.base.stellar.models/payment-operations.md) | `val `[`Transaction`](./index.md)`.paymentOperations: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<Payment>` |
| [signingSource](../../org.kin.sdk.base.stellar.models/signing-source.md) | `val `[`Transaction`](./index.md)`.signingSource: Id` |
| [totalAmount](../../org.kin.sdk.base.stellar.models/total-amount.md) | `val `[`Transaction`](./index.md)`.totalAmount: `[`KinAmount`](../../org.kin.sdk.base.models/-kin-amount/index.md) |
| [transactionHash](../../org.kin.sdk.base.stellar.models/transaction-hash.md) | `val `[`Transaction`](./index.md)`.transactionHash: `[`TransactionHash`](../../org.kin.sdk.base.models/-transaction-hash/index.md) |

### Extension Functions

| Name | Summary |
|---|---|
| [marshal](../marshal.md) | `fun `[`Transaction`](./index.md)`.marshal(): `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html) |

### Companion Object Extension Functions

| Name | Summary |
|---|---|
| [unmarshal](../unmarshal.md) | `fun Transaction.Companion.unmarshal(bytes: `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html)`): `[`Transaction`](./index.md) |
