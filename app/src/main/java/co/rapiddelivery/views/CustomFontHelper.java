package co.rapiddelivery.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import co.rapiddelivery.src.R;

/**
 * Created by adityagohad on 5/4/16.
 */
public class CustomFontHelper {

    public static void setCustomFont(TextView textview, Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MDText);
        String font = a.getString(R.styleable.MDText_fontName);
        setCustomFont(textview, font, context);
        a.recycle();
    }

    public static void setCustomFont(TextView textview, String font, Context context) {
        if(font == null) {
            return;
        }
        Typeface tf = FontCache.get(font, context);
        if(tf != null) {
            textview.setTypeface(tf);
        }
    }
}
