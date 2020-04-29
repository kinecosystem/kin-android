[kin-android](../../index.md) / [org.kin.sdk.base.tools](../index.md) / [ManagedServerSentEventStream](./index.md)

# ManagedServerSentEventStream

`class ManagedServerSentEventStream<ResponseType>`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `ManagedServerSentEventStream(requestBuilder: StreamingProtocol<ResponseType>)` |

### Properties

| Name | Summary |
|---|---|
| [listeners](listeners.md) | `val listeners: `[`CopyOnWriteArrayList`](https://docs.oracle.com/javase/6/docs/api/java/util/concurrent/CopyOnWriteArrayList.html)`<EventListener<ResponseType>>` |

### Functions

| Name | Summary |
|---|---|
| [addListener](add-listener.md) | `fun addListener(listener: EventListener<ResponseType>): `[`ManagedServerSentEventStream`](./index.md)`<ResponseType>` |
| [hasConnection](has-connection.md) | `fun hasConnection(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [removeListener](remove-listener.md) | `fun removeListener(listener: EventListener<ResponseType>): `[`ManagedServerSentEventStream`](./index.md)`<ResponseType>` |
