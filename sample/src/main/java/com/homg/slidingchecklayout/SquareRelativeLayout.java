package com.homg.slidingchecklayout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by homgwu on 2018/2/2.
 */

public class SquareRelativeLayout extends RelativeLayout {
    public SquareRelativeLayout(Context context) {
        super(context);
    }

    public SquareRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpec = heightMeasureSpec;
        if (mode == MeasureSpec.EXACTLY) {
            heightSpec = widthMeasureSpec;
        }
        super.onMeasure(widthMeasureSpec, heightSpec);
    }
}
