package jafar.stories.customview

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import jafar.stories.R

class MyCustomName: AppCompatEditText {

    constructor(context: Context) : super(context) { init() }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { init() }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { init() }

    private fun init() {}

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = resources.getString(R.string.input_name_here)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }
}