[kin-android](../../index.md) / [org.kin.sdk.base.repository](../index.md) / [InMemoryKinAccountContextRepositoryImpl](./index.md)

# InMemoryKinAccountContextRepositoryImpl

`class InMemoryKinAccountContextRepositoryImpl : `[`KinAccountContextRepository`](../-kin-account-context-repository/index.md)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `InMemoryKinAccountContextRepositoryImpl(kinEnvironment: `[`KinEnvironment`](../../org.kin.sdk.base/-kin-environment/index.md)`, storage: `[`MutableMap`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-map/index.html)`<Id, `[`KinAccountContext`](../../org.kin.sdk.base/-kin-account-context/index.md)`> = mutableMapOf<KinAccount.Id, KinAccountContext>())` |

### Functions

| Name | Summary |
|---|---|
| [getKinAccountContext](get-kin-account-context.md) | `fun getKinAccountContext(accountId: Id): `[`KinAccountContext`](../../org.kin.sdk.base/-kin-account-context/index.md)`?` |
