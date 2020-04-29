[kin-android](../../index.md) / [kin.sdk](../index.md) / [KinClient](index.md) / [minimumFeeSync](./minimum-fee-sync.md)

# minimumFeeSync

`val minimumFeeSync: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)

Get the current minimum fee that the network charges per operation.
This value is expressed in stroops.

**Note:** This method may accesses the network, and should not be called on the android main thread.

**Return**
the minimum fee.

