package com.hamurcuabi.layoutbounder.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import javax.annotation.OverridingMethodsMustInvokeSuper
import kotlin.coroutines.CoroutineContext

abstract class BasePresenter<V> : CoroutineScope {

    var view: V? = null

    private var job: Job? = null

    override val coroutineContext: CoroutineContext
        get() {
            if (job == null) {
                job = Job()
            }
            return Dispatchers.Main + job!!
        }

    @OverridingMethodsMustInvokeSuper
    open fun attach(view: V) {
        this.view = view
    }

    @OverridingMethodsMustInvokeSuper
    open fun detach() {
        view = null
        job?.cancel()
        job = null
    }
}
