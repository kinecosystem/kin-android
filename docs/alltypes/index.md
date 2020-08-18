

### All Types

| Name | Summary |
|---|---|
|

##### [kin.sdk.exception.AccountDeletedException](../kin.sdk.exception/-account-deleted-exception/index.md)

Account was deleted using [KinClient.deleteAccount](#), and cannot be used any more.


|

##### [kin.sdk.exception.AccountNotFoundException](../kin.sdk.exception/-account-not-found-exception/index.md)

Account was not created on the blockchain


| (extensions in package org.kin.sdk.base.models)

##### [org.kin.stellarfork.responses.AccountResponse](../org.kin.sdk.base.models/org.kin.stellarfork.responses.-account-response/index.md)


|

##### [kin.sdk.AccountStatus](../kin.sdk/-account-status/index.md)


|

##### [org.kin.sdk.base.network.api.proto.AgoraKinAccountsApi](../org.kin.sdk.base.network.api.proto/-agora-kin-accounts-api/index.md)


|

##### [org.kin.sdk.base.network.api.proto.AgoraKinTransactionsApi](../org.kin.sdk.base.network.api.proto/-agora-kin-transactions-api/index.md)


|

##### [org.kin.sdk.base.stellar.models.ApiConfig](../org.kin.sdk.base.stellar.models/-api-config/index.md)


|

##### [org.kin.sdk.base.tools.BackoffStrategy](../org.kin.sdk.base.tools/-backoff-strategy/index.md)


|

##### [kin.sdk.BackupRestore](../kin.sdk/-backup-restore/index.md)


|

##### [kin.sdk.Balance](../kin.sdk/-balance/index.md)


| (extensions in package org.kin.sdk.base.models)

##### [kotlin.ByteArray](../org.kin.sdk.base.models/kotlin.-byte-array/index.md)


|

##### [org.kin.sdk.base.tools.Callback](../org.kin.sdk.base.tools/-callback/index.md)

[onCompleted](../org.kin.sdk.base.tools/-callback/on-completed.md) to be called when callback is complete with
*either* a non null value or an error but never both.


|

##### [kin.sdk.exception.CorruptedDataException](../kin.sdk.exception/-corrupted-data-exception/index.md)

Input exported account data is corrupted and cannot be imported.


|

##### [kin.sdk.exception.CreateAccountException](../kin.sdk.exception/-create-account-exception/index.md)


|

##### [kin.sdk.exception.CryptoException](../kin.sdk.exception/-crypto-exception/index.md)

Decryption/Encryption error when importing - [KinClient.importAccount](#) or
exporting [KinAccount.export](#) an account.


|

##### [org.kin.sdk.base.network.api.rest.DefaultHorizonKinAccountCreationApi](../org.kin.sdk.base.network.api.rest/-default-horizon-kin-account-creation-api/index.md)


|

##### [org.kin.sdk.base.network.api.rest.DefaultHorizonKinTransactionWhitelistingApi](../org.kin.sdk.base.network.api.rest/-default-horizon-kin-transaction-whitelisting-api/index.md)


|

##### [kin.sdk.exception.DeleteAccountException](../kin.sdk.exception/-delete-account-exception/index.md)


|

##### [org.kin.sdk.base.tools.Disposable](../org.kin.sdk.base.tools/-disposable/index.md)


|

##### [org.kin.sdk.base.tools.DisposeBag](../org.kin.sdk.base.tools/-dispose-bag/index.md)


|

##### [kin.sdk.Environment](../kin.sdk/-environment/index.md)

Provides blockchain network details


|

##### [kin.sdk.EventListener](../kin.sdk/-event-listener/index.md)


|

##### [org.kin.sdk.base.tools.ExecutorServices](../org.kin.sdk.base.tools/-executor-services/index.md)


|

##### [org.kin.sdk.base.network.api.FriendBotApi](../org.kin.sdk.base.network.api/-friend-bot-api/index.md)

This is valid for testnet only


|

##### [org.kin.sdk.base.network.api.proto.GrpcApi](../org.kin.sdk.base.network.api.proto/-grpc-api/index.md)


| (extensions in package org.kin.sdk.base.network.api.proto)

##### [org.kin.gen.transaction.v3.TransactionService.HistoryItem](../org.kin.sdk.base.network.api.proto/org.kin.gen.transaction.v3.-transaction-service.-history-item/index.md)


|

##### [org.kin.sdk.base.network.api.rest.HorizonKinApi](../org.kin.sdk.base.network.api.rest/-horizon-kin-api/index.md)


|

##### [kin.sdk.exception.IllegalAmountException](../kin.sdk.exception/-illegal-amount-exception/index.md)

amount was not legal


|

##### [kin.sdk.exception.InsufficientFeeException](../kin.sdk.exception/-insufficient-fee-exception/index.md)


|

##### [kin.sdk.exception.InsufficientKinException](../kin.sdk.exception/-insufficient-kin-exception/index.md)

Transaction failed due to insufficient kin.


|

##### [org.kin.sdk.base.models.Key](../org.kin.sdk.base.models/-key/index.md)


| (extensions in package org.kin.sdk.base.models)

##### [org.kin.stellarfork.KeyPair](../org.kin.sdk.base.models/org.kin.stellarfork.-key-pair/index.md)


|

##### [kin.sdk.KeyStore](../kin.sdk/-key-store/index.md)


|

##### [kin.sdk.KinAccount](../kin.sdk/-kin-account/index.md)

Represents an account which holds Kin.


|

##### [org.kin.sdk.base.models.KinAccount](../org.kin.sdk.base.models/-kin-account/index.md)


|

##### [org.kin.sdk.base.network.api.KinAccountApi](../org.kin.sdk.base.network.api/-kin-account-api/index.md)


|

##### [org.kin.sdk.base.KinAccountContext](../org.kin.sdk.base/-kin-account-context/index.md)


|

##### [org.kin.sdk.base.KinAccountContextBase](../org.kin.sdk.base/-kin-account-context-base/index.md)


|

##### [org.kin.sdk.base.KinAccountContextImpl](../org.kin.sdk.base/-kin-account-context-impl/index.md)

Instantiate a [KinAccountContextImpl](../org.kin.sdk.base/-kin-account-context-impl/index.md) to operate on a [KinAccount](../org.kin.sdk.base.models/-kin-account/index.md) when you have a [PrivateKey](#)
Can be used to:


|

##### [org.kin.sdk.base.KinAccountContextReadOnly](../org.kin.sdk.base/-kin-account-context-read-only/index.md)


|

##### [org.kin.sdk.base.KinAccountContextReadOnlyImpl](../org.kin.sdk.base/-kin-account-context-read-only-impl/index.md)

Instantiate a [KinAccountContextReadOnlyImpl](../org.kin.sdk.base/-kin-account-context-read-only-impl/index.md) to operate on a [KinAccount](../org.kin.sdk.base.models/-kin-account/index.md) when you only have a [PublicKey](#)
Can be used to:


|

##### [org.kin.sdk.base.network.api.KinAccountCreationApi](../org.kin.sdk.base.network.api/-kin-account-creation-api/index.md)

An API for the SDK to delegate [KinAccount](../org.kin.sdk.base.models/-kin-account/index.md) registration
with the Kin Blockchain to developers.


|

##### [org.kin.sdk.base.KinAccountReadOperations](../org.kin.sdk.base/-kin-account-read-operations/index.md)


|

##### [org.kin.sdk.base.KinAccountReadOperationsAltIdioms](../org.kin.sdk.base/-kin-account-read-operations-alt-idioms/index.md)


|

##### [org.kin.sdk.base.models.KinAmount](../org.kin.sdk.base.models/-kin-amount/index.md)


|

##### [org.kin.sdk.base.models.KinBalance](../org.kin.sdk.base.models/-kin-balance/index.md)


|

##### [kin.sdk.KinClient](../kin.sdk/-kin-client/index.md)

An account manager for a [KinAccount](../kin.sdk/-kin-account/index.md).


|

##### [org.kin.sdk.base.models.KinDateFormat](../org.kin.sdk.base.models/-kin-date-format/index.md)


|

##### [org.kin.sdk.base.KinEnvironment](../org.kin.sdk.base/-kin-environment/index.md)


|

##### [org.kin.sdk.base.tools.KinExperimental](../org.kin.sdk.base.tools/-kin-experimental/index.md)


|

##### [org.kin.sdk.base.storage.KinFileStorage](../org.kin.sdk.base.storage/-kin-file-storage/index.md)


|

##### [org.kin.sdk.base.network.api.rest.KinFriendBotApi](../org.kin.sdk.base.network.api.rest/-kin-friend-bot-api/index.md)


|

##### [org.kin.sdk.base.network.api.rest.KinJsonApi](../org.kin.sdk.base.network.api.rest/-kin-json-api/index.md)


|

##### [org.kin.sdk.base.models.KinMemo](../org.kin.sdk.base.models/-kin-memo/index.md)


|

##### [org.kin.sdk.base.stellar.models.KinOperation](../org.kin.sdk.base.stellar.models/-kin-operation/index.md)


|

##### [org.kin.sdk.base.models.KinPayment](../org.kin.sdk.base.models/-kin-payment/index.md)


|

##### [org.kin.sdk.base.models.KinPaymentItem](../org.kin.sdk.base.models/-kin-payment-item/index.md)


|

##### [org.kin.sdk.base.KinPaymentReadOperations](../org.kin.sdk.base/-kin-payment-read-operations/index.md)


|

##### [org.kin.sdk.base.KinPaymentReadOperationsAltIdioms](../org.kin.sdk.base/-kin-payment-read-operations-alt-idioms/index.md)


|

##### [org.kin.sdk.base.KinPaymentWriteOperations](../org.kin.sdk.base/-kin-payment-write-operations/index.md)


|

##### [org.kin.sdk.base.KinPaymentWriteOperationsAltIdioms](../org.kin.sdk.base/-kin-payment-write-operations-alt-idioms/index.md)


|

##### [org.kin.sdk.base.network.services.KinService](../org.kin.sdk.base.network.services/-kin-service/index.md)


|

##### [org.kin.sdk.base.network.services.KinServiceImpl](../org.kin.sdk.base.network.services/-kin-service-impl/index.md)


|

##### [org.kin.sdk.base.network.services.KinTestService](../org.kin.sdk.base.network.services/-kin-test-service/index.md)

WARNING: This *ONLY* works in test environments.


|

##### [org.kin.sdk.base.network.services.KinTestServiceImpl](../org.kin.sdk.base.network.services/-kin-test-service-impl/index.md)


|

##### [org.kin.sdk.base.stellar.models.KinTransaction](../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)


|

##### [org.kin.sdk.base.network.api.KinTransactionApi](../org.kin.sdk.base.network.api/-kin-transaction-api/index.md)


|

##### [org.kin.sdk.base.stellar.models.KinTransactions](../org.kin.sdk.base.stellar.models/-kin-transactions/index.md)


|

##### [org.kin.sdk.base.network.api.KinTransactionWhitelistingApi](../org.kin.sdk.base.network.api/-kin-transaction-whitelisting-api/index.md)


| (extensions in package org.kin.sdk.base.models)

##### [kotlin.collections.List](../org.kin.sdk.base.models/kotlin.collections.-list/index.md)


|

##### [kin.sdk.ListenerRegistration](../kin.sdk/-listener-registration/index.md)

Represents a listener to events, that can be removed using [.remove](#).


|

##### [org.kin.sdk.base.tools.ListObserver](../org.kin.sdk.base.tools/-list-observer/index.md)


|

##### [org.kin.sdk.base.tools.ListOperations](../org.kin.sdk.base.tools/-list-operations/index.md)


|

##### [org.kin.sdk.base.tools.ListSubject](../org.kin.sdk.base.tools/-list-subject/index.md)


|

##### [kin.sdk.exception.LoadAccountException](../kin.sdk.exception/-load-account-exception/index.md)


|

##### [org.kin.sdk.base.tools.ManagedServerSentEventStream](../org.kin.sdk.base.tools/-managed-server-sent-event-stream/index.md)


|

##### [org.kin.sdk.base.stellar.models.NetworkEnvironment](../org.kin.sdk.base.stellar.models/-network-environment/index.md)


|

##### [org.kin.sdk.base.tools.NetworkOperation](../org.kin.sdk.base.tools/-network-operation/index.md)


|

##### [org.kin.sdk.base.tools.NetworkOperationsHandler](../org.kin.sdk.base.tools/-network-operations-handler/index.md)


|

##### [org.kin.sdk.base.tools.NetworkOperationsHandlerException](../org.kin.sdk.base.tools/-network-operations-handler-exception/index.md)


|

##### [org.kin.sdk.base.tools.NetworkOperationsHandlerImpl](../org.kin.sdk.base.tools/-network-operations-handler-impl/index.md)


|

##### [org.kin.sdk.base.ObservationMode](../org.kin.sdk.base/-observation-mode/index.md)

Describes the mode by which updates are
presented to an [Observer](../org.kin.sdk.base.tools/-observer/index.md)


|

##### [org.kin.sdk.base.tools.Observer](../org.kin.sdk.base.tools/-observer/index.md)


|

##### [kin.sdk.exception.OperationFailedException](../kin.sdk.exception/-operation-failed-exception/index.md)


|

##### [org.kin.sdk.base.tools.Optional](../org.kin.sdk.base.tools/-optional/index.md)


|

##### [kin.sdk.PaymentInfo](../kin.sdk/-payment-info/index.md)

Represents payment issued on the blockchain.


|

##### [kin.sdk.internal.PaymentInfoImpl](../kin.sdk.internal/-payment-info-impl/index.md)


|

##### [org.kin.sdk.base.tools.Promise](../org.kin.sdk.base.tools/-promise/index.md)


|

##### [org.kin.sdk.base.tools.PromisedCallback](../org.kin.sdk.base.tools/-promised-callback/index.md)


|

##### [org.kin.sdk.base.tools.PromiseQueue](../org.kin.sdk.base.tools/-promise-queue/index.md)


|

##### [org.kin.sdk.base.models.QuarkAmount](../org.kin.sdk.base.models/-quark-amount/index.md)


|

##### [kin.utils.Request](../kin.utils/-request/index.md)

Represents method invocation, each request will run sequentially on background thread, and will notify ``[`ResultCallback`](../kin.utils/-result-callback/index.md) witch success or error on main thread.


|

##### [kin.utils.ResultCallback](../kin.utils/-result-callback/index.md)


|

##### [org.kin.sdk.base.tools.RetriesExceededException](../org.kin.sdk.base.tools/-retries-exceeded-exception/index.md)


|

##### [org.kin.sdk.base.storage.Storage](../org.kin.sdk.base.storage/-storage/index.md)


| (extensions in package org.kin.sdk.base.models)

##### [kotlin.String](../org.kin.sdk.base.models/kotlin.-string/index.md)


| (extensions in package org.kin.sdk.base.models)

##### [org.kin.stellarfork.responses.SubmitTransactionResponse](../org.kin.sdk.base.models/org.kin.stellarfork.responses.-submit-transaction-response/index.md)


|

##### [kin.sdk.Transaction](../kin.sdk/-transaction/index.md)


| (extensions in package org.kin.sdk.base.models)

##### [org.kin.stellarfork.Transaction](../org.kin.sdk.base.models/org.kin.stellarfork.-transaction/index.md)


|

##### [kin.sdk.exception.TransactionFailedException](../kin.sdk.exception/-transaction-failed-exception/index.md)

Blockchain transaction failure has happened, contains blockchain specific error details


|

##### [org.kin.sdk.base.models.TransactionHash](../org.kin.sdk.base.models/-transaction-hash/index.md)


|

##### [kin.sdk.TransactionId](../kin.sdk/-transaction-id/index.md)

Identifier of the transaction, useful for finding information about the transaction.


| (extensions in package org.kin.sdk.base.models)

##### [org.kin.stellarfork.responses.TransactionResponse](../org.kin.sdk.base.models/org.kin.stellarfork.responses.-transaction-response/index.md)


|

##### [org.kin.sdk.base.tools.ValueListener](../org.kin.sdk.base.tools/-value-listener/index.md)

May call [onNext](../org.kin.sdk.base.tools/-value-listener/on-next.md) or [onError](../org.kin.sdk.base.tools/-value-listener/on-error.md) in a sequence of value updates.
Should not emit onNext updates after an onError event.


|

##### [org.kin.sdk.base.tools.ValueSubject](../org.kin.sdk.base.tools/-value-subject/index.md)


|

##### [kin.sdk.WhitelistableTransaction](../kin.sdk/-whitelistable-transaction/index.md)

This class wraps a transaction envelope xdr in base 64(transaction payload)
and a network passphrase(the network id as string). *
Those fields are necessary for the whitelist server in order to sign this transaction to be a whitelist transaction.


