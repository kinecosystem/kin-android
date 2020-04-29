[kin-android](../../index.md) / [kin.sdk](../index.md) / [KinAccount](index.md) / [getBalanceSync](./get-balance-sync.md)

# getBalanceSync

`@NonNull abstract fun getBalanceSync(): `[`Balance`](../-balance/index.md)

Get the current confirmed balance in kin

**Note:** This method accesses the network, and should not be called on the android main thread.

### Exceptions

`AccountNotFoundException` - if account was not created

`OperationFailedException` - any other error

**Return**
[Balance](../-balance/index.md): the balance in kin

