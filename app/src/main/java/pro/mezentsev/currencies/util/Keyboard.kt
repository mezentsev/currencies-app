package pro.mezentsev.currencies.util

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText


fun EditText.showKeyboard() {
    post {
        requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
}