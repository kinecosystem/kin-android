[kin-android](../../index.md) / [kin.sdk](../index.md) / [KinAccount](index.md) / [getStatus](./get-status.md)

# getStatus

`abstract fun getStatus(): `[`Request`](../../kin.utils/-request/index.md)`<`[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`!>!`

Create ``[`Request`](../../kin.utils/-request/index.md) for getting current account status on blockchain network.

 See ``[`KinAccount#getStatusSync()`](get-status-sync.md) for possibles errors

**Return**
[Request](../../kin.utils/-request/index.md)&lt;[Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)!&gt;!: account status, either ``[`AccountStatus#NOT_CREATED`](../-account-status/-n-o-t_-c-r-e-a-t-e-d.md), or ``[` `](../-account-status/-c-r-e-a-t-e-d.md)

