[kin-android](../../../../index.md) / [org.kin.sdk.base](../../../index.md) / [KinEnvironment](../../index.md) / [Agora](../index.md) / [Builder](index.md) / [setMinApiVersion](./set-min-api-version.md)

# setMinApiVersion

`fun setMinApiVersion(minApiVersion: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): Builder`

This option allows developers to force which api version the KinService should use.
v3 - stellar
v4 - solana
It is *not* required to set this as we default to v3 until migration day to solana.

