[kin-android](../../index.md) / [kin.backupandrestore.base](../index.md) / [BaseToolbarActivity](./index.md)

# BaseToolbarActivity

`abstract class BaseToolbarActivity : AppCompatActivity, `[`KeyboardHandler`](../-keyboard-handler/index.md)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `BaseToolbarActivity()` |

### Properties

| Name | Summary |
|---|---|
| [BACKGROUND_COLOR](-b-a-c-k-g-r-o-u-n-d_-c-o-l-o-r.md) | `static val BACKGROUND_COLOR: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [EMPTY_TITLE](-e-m-p-t-y_-t-i-t-l-e.md) | `static val EMPTY_TITLE: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

### Functions

| Name | Summary |
|---|---|
| [clearSteps](clear-steps.md) | `open fun clearSteps(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [closeKeyboard](close-keyboard.md) | `open fun closeKeyboard(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [getContentLayout](get-content-layout.md) | `abstract fun getContentLayout(): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [hideNavigationIcon](hide-navigation-icon.md) | `open fun hideNavigationIcon(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onCreate](on-create.md) | `open fun onCreate(savedInstanceState: Bundle?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onSaveInstanceState](on-save-instance-state.md) | `open fun onSaveInstanceState(outState: Bundle): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [openKeyboard](open-keyboard.md) | `open fun openKeyboard(view: View!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [setNavigationClickListener](set-navigation-click-listener.md) | `open fun setNavigationClickListener(clickListener: OnClickListener!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [setNavigationIcon](set-navigation-icon.md) | `open fun setNavigationIcon(iconRes: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>`open fun setNavigationIcon(drawable: Drawable!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [setStep](set-step.md) | `open fun setStep(current: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, total: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [setToolbarColor](set-toolbar-color.md) | `open fun setToolbarColor(colorRes: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [setToolbarColorWithAnim](set-toolbar-color-with-anim.md) | `open fun setToolbarColorWithAnim(toColorRes: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, durationMilis: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [setToolbarTitle](set-toolbar-title.md) | `open fun setToolbarTitle(titleRes: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [BackupActivity](../../kin.backupandrestore.backup.view/-backup-activity/index.md) | `open class BackupActivity : `[`BaseToolbarActivity`](./index.md)`, `[`BackupView`](../../kin.backupandrestore.backup.view/-backup-view/index.md) |
| [RestoreActivity](../../kin.backupandrestore.restore.view/-restore-activity/index.md) | `open class RestoreActivity : `[`BaseToolbarActivity`](./index.md)`, `[`RestoreView`](../../kin.backupandrestore.restore.view/-restore-view/index.md) |
