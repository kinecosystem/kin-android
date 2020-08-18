[kin-android](../../index.md) / [kin.backupandrestore](../index.md) / [BackupAndRestoreManager](./index.md)

# BackupAndRestoreManager

`class BackupAndRestoreManager`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `BackupAndRestoreManager(activity: Activity, reqCodeBackup: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, reqCodeRestore: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`)` |

### Properties

| Name | Summary |
|---|---|
| [APP_ID_EXTRA](-a-p-p_-i-d_-e-x-t-r-a.md) | `static val APP_ID_EXTRA: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [NETWORK_PASSPHRASE_EXTRA](-n-e-t-w-o-r-k_-p-a-s-s-p-h-r-a-s-e_-e-x-t-r-a.md) | `static val NETWORK_PASSPHRASE_EXTRA: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [NETWORK_URL_EXTRA](-n-e-t-w-o-r-k_-u-r-l_-e-x-t-r-a.md) | `static val NETWORK_URL_EXTRA: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [PUBLIC_ADDRESS_EXTRA](-p-u-b-l-i-c_-a-d-d-r-e-s-s_-e-x-t-r-a.md) | `static val PUBLIC_ADDRESS_EXTRA: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [STORE_KEY_EXTRA](-s-t-o-r-e_-k-e-y_-e-x-t-r-a.md) | `static val STORE_KEY_EXTRA: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Functions

| Name | Summary |
|---|---|
| [backup](backup.md) | `fun backup(kinClient: `[`KinClient`](../../kin.sdk/-kin-client/index.md)`!, kinAccount: `[`KinAccount`](../../kin.sdk/-kin-account/index.md)`!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onActivityResult](on-activity-result.md) | `fun onActivityResult(requestCode: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, resultCode: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, data: Intent!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [registerBackupCallback](register-backup-callback.md) | `fun registerBackupCallback(backupCallback: `[`BackupCallback`](../-backup-callback/index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [registerRestoreCallback](register-restore-callback.md) | `fun registerRestoreCallback(restoreCallback: `[`RestoreCallback`](../-restore-callback/index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [release](release.md) | `fun release(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [restore](restore.md) | `fun restore(kinClient: `[`KinClient`](../../kin.sdk/-kin-client/index.md)`!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
