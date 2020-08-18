[kin-android](../../index.md) / [kin.backupandrestore.exception](../index.md) / [BackupAndRestoreException](./index.md)

# BackupAndRestoreException

`open class BackupAndRestoreException : `[`Exception`](https://docs.oracle.com/javase/6/docs/api/java/lang/Exception.html)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `BackupAndRestoreException(code: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, message: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!)`<br>`BackupAndRestoreException(code: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, message: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!, cause: `[`Throwable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)`!)` |

### Properties

| Name | Summary |
|---|---|
| [CODE_BACKUP_FAILED](-c-o-d-e_-b-a-c-k-u-p_-f-a-i-l-e-d.md) | `static val CODE_BACKUP_FAILED: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [CODE_RESTORE_FAILED](-c-o-d-e_-r-e-s-t-o-r-e_-f-a-i-l-e-d.md) | `static val CODE_RESTORE_FAILED: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [CODE_RESTORE_INVALID_KEYSTORE_FORMAT](-c-o-d-e_-r-e-s-t-o-r-e_-i-n-v-a-l-i-d_-k-e-y-s-t-o-r-e_-f-o-r-m-a-t.md) | `static val CODE_RESTORE_INVALID_KEYSTORE_FORMAT: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [CODE_UNEXPECTED](-c-o-d-e_-u-n-e-x-p-e-c-t-e-d.md) | `static val CODE_UNEXPECTED: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

### Functions

| Name | Summary |
|---|---|
| [getCode](get-code.md) | `open fun getCode(): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
