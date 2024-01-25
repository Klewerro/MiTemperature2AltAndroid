package com.klewerro.mitemperaturenospyware.presentation.util

import android.os.Build

fun isAndroid12OrGreater() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
