[kin-android](../../index.md) / [kin.backupandrestore.backup.view](../index.md) / [TextWatcherAdapter](./index.md)

# TextWatcherAdapter

`open class TextWatcherAdapter : TextWatcher`

### Types

| Name | Summary |
|---|---|
| [TextChangeListener](-text-change-listener/index.md) | `interface TextChangeListener` |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `TextWatcherAdapter(textChangeListener: TextChangeListener!)` |

### Functions

| Name | Summary |
|---|---|
| [afterTextChanged](after-text-changed.md) | `open fun afterTextChanged(editable: Editable!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [beforeTextChanged](before-text-changed.md) | `open fun beforeTextChanged(s: `[`CharSequence`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char-sequence/index.html)`!, start: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, count: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, after: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onTextChanged](on-text-changed.md) | `open fun onTextChanged(s: `[`CharSequence`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char-sequence/index.html)`!, start: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, before: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, count: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [release](release.md) | `open fun release(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
