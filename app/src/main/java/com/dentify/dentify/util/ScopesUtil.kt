package com.dentify.dentify.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class ScopesUtil(dispatcher: CoroutineDispatcher): CoroutineScope {
    override val coroutineContext: CoroutineContext = Job()
}

