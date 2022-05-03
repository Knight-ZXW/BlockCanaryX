package blockcanary.ui.util

import android.app.Activity
import android.content.Context
import android.os.Build.VERSION
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import blockcanary.ui.R
import blockcanary.ui.Screen

internal fun ViewGroup.inflate(layoutResId: Int) = LayoutInflater.from(context)
    .inflate(layoutResId, this, false)!!

internal val View.activity
    get() = context as Activity

@Suppress("UNCHECKED_CAST")
internal fun <T : Activity> View.activity() = context as T

internal fun Context.getColorCompat(id: Int): Int {
    return if (VERSION.SDK_INT >= 23) {
        getColor(id)
    } else {
        resources.getColor(id)
    }
}



