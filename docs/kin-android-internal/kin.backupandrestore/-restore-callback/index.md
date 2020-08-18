[kin-android](../../index.md) / [kin.backupandrestore](../index.md) / [RestoreCallback](./index.md)

# RestoreCallback

`interface RestoreCallback`

### Functions

| Name | Summary |
|---|---|
| [onCancel](on-cancel.md) | `abstract fun onCancel(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onFailure](on-failure.md) | `abstract fun onFailure(throwable: `[`BackupAndRestoreException`](../../kin.backupandrestore.exception/-backup-and-restore-exception/index.md)`!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onSuccess](on-success.md) | `abstract fun onSuccess(kinClient: `[`KinClient`](../../kin.sdk/-kin-client/index.md)`!, kinAccount: `[`KinAccount`](../../kin.sdk/-kin-account/index.md)`!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
