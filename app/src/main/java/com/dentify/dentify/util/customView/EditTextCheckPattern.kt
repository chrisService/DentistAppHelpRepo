package com.dentify.dentify.util.customView

import android.content.Context
import android.util.AttributeSet
import android.util.Patterns
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.dentify.dentify.R

class EditTextCheckPattern: TextInputEditText {

    constructor (context: Context) : super(context) {
    }
    constructor (context : Context, attrs: AttributeSet) : super(context, attrs) {
    }
    constructor (context : Context, attrs:AttributeSet, defStyleAttr:Int) : super(context, attrs, defStyleAttr) {
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        if (!text.isNullOrEmpty()){
            if (Patterns.EMAIL_ADDRESS.matcher(text).matches()){
                this.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context,R.drawable.ic_check_icon), null)
            }else{
                this.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }
        }
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
    }
}