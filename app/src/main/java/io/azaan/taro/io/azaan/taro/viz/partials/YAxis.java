package io.azaan.taro.io.azaan.taro.viz.partials;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import io.azaan.taro.io.azaan.taro.viz.base.BaseAxis;
import io.azaan.taro.io.azaan.taro.viz.models.Slot;

/**
 * Generic YAxis implementation
 */
public class YAxis extends BaseAxis {


    /**
     * Paint objects
     */
    private Paint mLabelPaint;
    private Paint mLinePaint;


    /**
     * Constructor function
     * @param context Android Context
     */
    public YAxis(Context context) {
        super(context, Orientation.VERTICAL);

        setAlignment(Alignment.START);

        // set up paint objects
        mLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLabelPaint.setColor(Color.YELLOW);
        mLabelPaint.setTextSize(30);
        mLabelPaint.setTextAlign(Paint.Align.CENTER);

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(Color.GREEN);
    }


    /**
     * Draws the BaseAxis on to the canvas.
     * @param canvas canvas
     */
    public void draw(Canvas canvas) {
        super.draw(canvas);

        // draw the line
        canvas.drawLine(getW(), 0, getW(), getH(), mLinePaint);

        // draw the labels
        for (Slot slot : getSlots()) {
            if (slot.label == null) {
                continue;
            }

            mLabelPaint.measureText(slot.label);
            canvas.drawText(
                    slot.label,
                    slot._x + (getSlotWidth() / 2f),
                    slot._y + (getSlotHeight() / 2f) - ((mLabelPaint.ascent() + mLabelPaint.descent()) / 2f),
                    mLabelPaint
            );
        }
    }
}
