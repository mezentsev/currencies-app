package pro.mezentsev.currencies.util

import timber.log.Timber

fun String.v() {
    Timber.v(this)
}

fun String.d() {
    Timber.d(this)
}

fun Throwable.e(text: String? = null) {
    Timber.e(this, text)
}