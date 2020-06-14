package org.kin.sdk.demo.viewmodel.tools

typealias StateListener<StateType> = (StateType) -> Unit

interface ObservableViewModel<StateType> {
    fun addStateUpdateListener(listener: StateListener<StateType>)

    fun removeStateUpdateListener(listener: StateListener<StateType>)
}

interface ViewModel<ArgsType, StateType> : ObservableViewModel<StateType> {
    fun cleanup()
}

abstract class BaseObservableViewModel<StateType> : ObservableViewModel<StateType> {
    private var state: StateType? = null
    private val listeners = mutableListOf<StateListener<StateType>>()

    final override fun addStateUpdateListener(listener: StateListener<StateType>) {
        listeners.add(listener)

        (state ?: getDefaultState()).let { listener(it) } ?: return
    }

    final override fun removeStateUpdateListener(listener: StateListener<StateType>) {
        listeners.remove(listener)
    }

    protected fun updateState(updater: (previousState: StateType) -> StateType) {
        val currentState = state ?: getDefaultState()
        val updatedState = updater(currentState)

        state = updatedState

        if (currentState != updatedState) {
            listeners.forEach { it(updatedState) }
        }
    }

    protected abstract fun getDefaultState(): StateType
}

abstract class BaseViewModel<ArgsType, StateType>(protected val args: ArgsType) : ViewModel<ArgsType, StateType>, BaseObservableViewModel<StateType>() {
    override fun cleanup() {
    }
}
