[kin-android](../../index.md) / [org.kin.sdk.base.viewmodel.di](../index.md) / [SpendResolverImpl](./index.md)

# SpendResolverImpl

`class SpendResolverImpl : `[`SpendResolver`](../-spend-resolver/index.md)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `SpendResolverImpl(kinEnvironment: Agora, invoiceRepository: `[`InvoiceRepository`](../../org.kin.sdk.base.repository/-invoice-repository/index.md)` = kinEnvironment.invoiceRepository, appInfoRepository: `[`AppInfoRepository`](../../org.kin.sdk.base.repository/-app-info-repository/index.md)` = kinEnvironment.appInfoRepository, kinAccountContextRepository: `[`KinAccountContextRepository`](../../org.kin.sdk.base.repository/-kin-account-context-repository/index.md)` = InMemoryKinAccountContextRepositoryImpl(kinEnvironment))` |

### Functions

| Name | Summary |
|---|---|
| [resolve](resolve.md) | `fun resolve(navigationArgs: NavigationArgs, spendNavigator: `[`SpendNavigator`](../../org.kin.base.viewmodel.tools/-spend-navigator/index.md)`): `[`PaymentFlowViewModel`](../../org.kin.base.viewmodel/-payment-flow-view-model/index.md) |
