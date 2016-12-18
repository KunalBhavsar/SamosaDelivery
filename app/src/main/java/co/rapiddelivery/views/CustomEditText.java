package co.rapiddelivery.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import co.rapiddelivery.src.R;

/**
 * Created by Kunal on 20/07/16.
 */
public class CustomEditText extends EditText {

    private String _customFont = null;
    private int _style;

    public CustomEditText(Context context) {
        super(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomAttributes(attrs);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCustomAttributes(attrs);
    }

    /**
     * Set custom attributes, particularly we're setting our font
     *
     * @param attrs
     */
    private void setCustomAttributes(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomTextView);
        _customFont = a.getString(R.styleable.CustomTextView_font);
        if (_customFont != null) {
            if (!isInEditMode()) {
                CustomFontManager fontManager = CustomFontManager.getInstance();
                super.setTypeface(fontManager.getFont(getContext().getAssets(), _customFont), _style);
            }
        }
        a.recycle();
    }

    /**
     * If the user set a style assign it to our _style field so it can be applied to our custom font. Calling super
     * here allows you to use CustomTextView even if you're not changing the font.
     */
    public void setTypeface(Typeface tf, int style) {
        this._style = style;
        super.setTypeface(tf, style);
        return;
    }


}
