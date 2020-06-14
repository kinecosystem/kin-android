package org.kin.sdk.demo.view.tools

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import org.kin.sdk.demo.view.custom.HeaderView
import org.kin.sdk.demo.view.custom.PlaceholderView

data class RecyclerHeaderItem(@StringRes val title: Int)

data class RecyclerPlaceholderItem(@StringRes val title: Int)

object RecyclerViewTools {
    fun header(@StringRes title: Int): RecyclerHeaderItem = RecyclerHeaderItem(title)
    fun placeholder(@StringRes title: Int): RecyclerPlaceholderItem = RecyclerPlaceholderItem(title)
}

internal class ListBackedRecyclerViewAdapter(private val builderContext: RecyclerViewBuilderContext) : RecyclerView.Adapter<ListBackedRecyclerViewAdapter.ViewHolder>() {

    private var internalRecyclerView: RecyclerView? = null
    private var currentItems: List<*>? = null

    inner class ViewHolder(val viewType: Int, itemView: View) : RecyclerView.ViewHolder(itemView)

    init {
        setHasStableIds(false)
    }

    override fun getItemViewType(position: Int): Int {
        val item = currentItems?.get(position) ?: return 0

        return when (item) {
            is RecyclerHeaderItem -> -1
            is RecyclerPlaceholderItem -> -2
            else -> {
                builderContext.getItemViewType(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = if (viewType == -1) {
            HeaderView(parent.context)
        } else if (viewType == -2) {
            PlaceholderView(parent.context)
        } else {
            builderContext.onCreateView(parent.context, viewType)
        }

        return ViewHolder(viewType, view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = currentItems?.get(position)
            ?: throw IllegalStateException("Item out of range $position")

        if (holder.viewType == -1) {
            // no need to bind a header
            val item = model as RecyclerHeaderItem

            (holder.itemView as HeaderView).title = holder.itemView.context.getString(item.title)
        } else if (holder.viewType == -2) {
            val item = model as RecyclerPlaceholderItem

            (holder.itemView as PlaceholderView).title = holder.itemView.context.getString(item.title)
        } else {
            builderContext.onBindView(
                holder.itemView.context,
                holder.viewType,
                holder.itemView,
                model
            )
        }
    }

    override fun getItemCount(): Int = currentItems?.size ?: 0

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        internalRecyclerView = recyclerView

        notifyDataSetChanged()
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)

        internalRecyclerView = null
    }

    fun updateItems(items: List<*>) {
        val currentRecyclerView = internalRecyclerView

        if (currentRecyclerView != null) {
            currentRecyclerView.post {
                if (currentItems != null) {
                    val diffResult = DiffUtil.calculateDiff(ListDiffCallback<Any>(currentItems as List<Any>, items as List<Any>))
                    currentItems = items
                    diffResult.dispatchUpdatesTo(this);
                } else {
                    currentItems = items
                    notifyDataSetChanged()
                }
            }
        } else {
            currentItems = items
        }
    }
}
data class ListDiffCallback<T>(val oldList: List<T>, val newList: List<T>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int  = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition]!! == newList[newItemPosition]

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition]!! == newList[newItemPosition]
}

class RecyclerViewBuilderContext {
    data class Layout(
        val clzz: Class<*>,
        val creator: (context: Context) -> View,
        val binder: (view: View, itemViewModel: Any) -> Unit
    )

    private val layouts = mutableListOf<Layout>()

    internal fun getItemViewType(item: Any): Int {
        val index = layouts.indexOfFirst { it.clzz.isInstance(item) }

        if (index < 0) {
            throw IllegalArgumentException("Unknown view type ${item.javaClass.name}")
        }

        return index
    }

    internal fun onCreateView(context: Context, viewType: Int): View {
        return layouts[viewType].creator(context)
    }

    internal fun onBindView(context: Context, viewType: Int, view: View, data: Any) {
        return layouts[viewType].binder(view, data)
    }

    @Suppress("UNCHECKED_CAST")
    inner class LayoutCreator<ViewType : View, ItemViewModelType> {
        lateinit var retainedCreator: ((context: Context) -> ViewType)
        lateinit var retainedBinder: ((view: ViewType, itemViewModel: ItemViewModelType) -> Unit)

        fun create(creator: (context: Context) -> ViewType) {
            retainedCreator = creator
        }

        fun bind(binder: (view: ViewType, itemViewModel: ItemViewModelType) -> Unit) {
            retainedBinder = { view, itemViewModel ->
                binder(view, itemViewModel)
            }
        }

        fun toLayout(clzz: Class<*>): Layout = Layout(clzz, { context ->
            retainedCreator(context)
        }, { view, itemViewModel ->
            retainedBinder(view as ViewType, itemViewModel as ItemViewModelType)
        })
    }

    fun addLayout(layout: Layout) {
        layouts.add(layout)
    }

    inline fun <ViewType : View, reified ItemViewModelType> layout(layouter: LayoutCreator<ViewType, ItemViewModelType>.() -> Unit) {
        val layoutCreator = LayoutCreator<ViewType, ItemViewModelType>()

        layoutCreator.layouter()

        addLayout(layoutCreator.toLayout(ItemViewModelType::class.java))
    }
}

fun RecyclerView.build(builder: RecyclerViewBuilderContext.() -> Unit) {
    val builderContext = RecyclerViewBuilderContext()

    builderContext.builder()

    adapter = ListBackedRecyclerViewAdapter(builderContext)
}

fun RecyclerView.updateItems(items: List<*>) {
    val listBackedAdapter = adapter as? ListBackedRecyclerViewAdapter
        ?: throw IllegalStateException("Recycler view not attached to a known adapter. Use RecyclerView.build to attach a layout builder")

    listBackedAdapter.updateItems(items)
}
