[kin-android](../../index.md) / [org.kin.sdk.base.tools.sha224](../index.md) / [Digest](index.md) / [doFinal](./do-final.md)

# doFinal

`abstract fun doFinal(out: `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html)`, outOff: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)

close the digest, producing the final digest value. The doFinal
call leaves the digest reset.

### Parameters

`out` - the array the digest is to be copied into.

`outOff` - the offset into the out array the digest is to start at.