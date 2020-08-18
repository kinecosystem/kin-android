package org.kin.sdk.design.viewmodel.tools

typealias StateListener<StateType> = (StateType) -> Unit

interface ObservableViewModel<StateType> {
    fun addStateUpdateListener(listener: StateListener<StateType>)

    fun removeStateUpdateListener(listener: StateListener<StateType>)

    fun removeAllListeners()
}

interface ViewModel<ArgsType, StateType> : ObservableViewModel<StateType> {
    fun cleanup()
}

abstract class BaseObservableViewModel<StateType> : ObservableViewModel<StateType> {
    private var state: StateType? = null
    private val listeners = mutableListOf<StateListener<StateType>>()

    private fun getOrInitState(): StateType {
        return state ?: getDefaultState().also {
            state = it
            onStateUpdated(it)
        }
    }

    final override fun addStateUpdateListener(listener: StateListener<StateType>) {
        listeners.add(listener)
        listener(getOrInitState())
    }

    final override fun removeStateUpdateListener(listener: StateListener<StateType>) {
        listeners.remove(listener)
    }

    override fun removeAllListeners() {
        listeners.clear()
    }

    protected fun updateState(updater: (previousState: StateType) -> StateType) {
        val currentState = getOrInitState()
        val updatedState = updater(currentState)

        state = updatedState

        if (currentState != updatedState) {
            listeners.forEach { it(updatedState) }
        }

        onStateUpdated(updatedState)
    }

    protected fun <T> withState(withState: StateType.() -> T): T {
        return withState(getOrInitState())
    }

    protected abstract fun getDefaultState(): StateType

    open fun onStateUpdated(state: StateType) { }
}

abstract class BaseViewModel<ArgsType, StateType>(protected val args: ArgsType) : ViewModel<ArgsType, StateType>, BaseObservableViewModel<StateType>() {
    override fun cleanup() {
    }
}
