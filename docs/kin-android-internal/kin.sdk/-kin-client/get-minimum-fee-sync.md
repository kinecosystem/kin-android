[kin-android](../../index.md) / [kin.sdk](../index.md) / [KinClient](index.md) / [getMinimumFeeSync](./get-minimum-fee-sync.md)

# getMinimumFeeSync

`open fun getMinimumFeeSync(): `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)

Get the current minimum fee that the network charges per operation. This value is expressed in stroops.

**Note:** This method accesses the network, and should not be called on the android main thread.

**Return**
[Long](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html): the minimum fee.

