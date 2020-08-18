[kin-android](../../index.md) / [kin.sdk](../index.md) / [KinClient](index.md) / [&lt;init&gt;](./-init-.md)

# &lt;init&gt;

`KinClient(@NonNull context: Context, @NonNull environment: `[`Environment`](../-environment/index.md)`, appId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!)`

For more details please look at ``[`#KinClient(Context context, Environment environment, String appId, String storeKey)`](./-init-.md)

`KinClient(@NonNull context: Context, @NonNull environment: `[`Environment`](../-environment/index.md)`, @NonNull appId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, @NonNull storeKey: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`)`

Build KinClient object.

### Parameters

`context` - Context: android context

`environment` - [Environment](../-environment/index.md): the blockchain network details.

`appId` - [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html): a 4 character string which represent the application id which will be added to each transaction. **Note:** appId must contain only upper and/or lower case letters and/or digits and that the total string length is between 3 to 4. For example 1234 or 2ab3 or bcda, etc.

`storeKey` - [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html): an optional param which is the key for storing this KinClient data, different keys will store a different accounts.`KinClient(context: Context!, environment: `[`Environment`](../-environment/index.md)`!, appId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!, storeKey: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!, backupRestore: `[`BackupRestore`](../-backup-restore/index.md)`!, keyStore: `[`KeyStore`](../-key-store/index.md)`!, storage: `[`Storage`](../../org.kin.sdk.base.storage/-storage/index.md)`!, kinEnvironment: `[`KinEnvironment`](../../org.kin.sdk.base/-kin-environment/index.md)`!)`
`KinClient(context: Context!, environment: `[`Environment`](../-environment/index.md)`!, appId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!, storeKey: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!, backupRestore: `[`BackupRestore`](../-backup-restore/index.md)`!, keyStore: `[`KeyStore`](../-key-store/index.md)`!, storage: `[`Storage`](../../org.kin.sdk.base.storage/-storage/index.md)`!)`