package jafar.stories.customview

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import jafar.stories.R

class MyCustomEmail: AppCompatEditText {

    constructor(context: Context) : super(context) { init() }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { init() }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { init() }

    private fun init() {
        addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                error = if (s.isNotEmpty() && !isValidEmail(s.toString())) context.getString(R.string.invalid_email) else null
            }
            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun isValidEmail(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = resources.getString(R.string.input_email)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }
}