package com.mrl.pixiv.common.viewmodel

interface Reducer<S : State, A : Action> {
    /**
     * Generates a new instance of the [State] based on the [Action]
     *
     * @receiver the current [State]
     * @param action the [Action] to reduce the [State] with
     * @return the reduced [State]
     */
    fun S.reduce(action: A): S
}