[kin-android](../../index.md) / [kin.sdk](../index.md) / [KinClient](index.md) / [&lt;init&gt;](./-init-.md)

# &lt;init&gt;

`KinClient(context: Context, environment: `[`Environment`](../-environment/index.md)`, appId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, storeKey: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`)`

Build KinClient object.

### Parameters

`context` - android context

`environment` - the blockchain network details.

`appId` - a 4 character string which represent the application id which will be added to each transaction.
**Note:** appId must contain only upper and/or lower case letters and/or digits and that the total string length is between 3 to 4.
For example 1234 or 2ab3 or bcda, etc.

`storeKey` - an optional param which is the key for storing this KinClient data, different keys will store a different accounts.`KinClient(context: Context, environment: `[`Environment`](../-environment/index.md)`, appId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`)`

For more details please look at [KinClient](index.md)

