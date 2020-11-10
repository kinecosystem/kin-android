package org.kin.sdk.base.tools

class Sort {
    companion object {
        @JvmStatic
        fun <T : Comparable<T>> quickSort(items: MutableList<T>, low: Int, high: Int) {
            if (low < high) {
                val partition = partition(items, low, high)
                quickSort(items, low, partition - 1)
                quickSort(items, partition + 1, high)
            }
        }

        private fun <T : Comparable<T>> partition(
            items: MutableList<T>,
            low: Int,
            high: Int
        ): Int {
            val pivot = items[high]
            var i = low - 1

            for (j in low until high) {
                if (items[j] < pivot) {
                    i++
                    items.swap(i, j)
                }
            }

            items.swap(i + 1, high)

            return i + 1
        }

        private fun <T : Comparable<T>> MutableList<T>.swap(i: Int, j: Int) {
            val temp = this[i]
            this[i] = this[j]
            this[j] = temp
        }
    }
}

fun <T : Comparable<T>> MutableList<T>.quickSort(): MutableList<T> {
    Sort.quickSort(this, 0, this.size - 1)
    return this
}
