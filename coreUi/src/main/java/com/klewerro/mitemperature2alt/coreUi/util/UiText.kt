package com.klewerro.mitemperature2alt.coreUi.util

import android.content.Context
import androidx.annotation.StringRes

sealed class UiText {
    data class DynamicString(val text: String) : UiText()
    data class StringResource(@StringRes val resId: Int, val args: List<Any> = emptyList()) :
        UiText()

    fun asString(context: Context): String = when (this) {
        is DynamicString -> text
        is StringResource -> context.getString(resId, *args.toTypedArray())
    }
}
