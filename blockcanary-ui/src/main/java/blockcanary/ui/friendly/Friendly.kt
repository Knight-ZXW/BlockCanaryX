
@file:Suppress("INVISIBLE_REFERENCE",
    "INVISIBLE_MEMBER", "NOTHING_TO_INLINE")
@file:JvmName("blockcanary_Friendly")

package blockcanary.ui.friendly

import android.os.*
import blockcanary.ui.isMainThread

internal val mainHandler by lazy { Handler(Looper.getMainLooper()) }

internal fun checkMainThread() {
    check(isMainThread) {
        "Should be called from the main thread, not ${Thread.currentThread()}"
    }
}

internal fun checkNotMainThread() {
    check(!isMainThread) {
        "Should not be called from the main thread"
    }
}

/**
 * Executes the given [block] and returns elapsed time in milliseconds using [SystemClock.uptimeMillis]
 */
internal inline fun measureDurationMillis(block: () -> Unit): Long {
    val start = SystemClock.uptimeMillis()
    block()
    return SystemClock.uptimeMillis() - start
}
