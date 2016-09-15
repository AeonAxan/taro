package io.azaan.taro.io.azaan.taro.viz.partials;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import io.azaan.taro.io.azaan.taro.viz.models.Slot;

/**
 * Takes care of the XAxis, all it's calculations
 * positioning and drawing.
 *
 * The parent chart should take care of determining
 * the position as well as dimensions of the axis.
 */
public class XAxis extends Axis {

    /**
     * Paint objects
     */
    private Paint mLabelPaint;
    private Paint mLinePaint;


    /**
     * Constructor function
     * @param context Android Context
     */
    public XAxis(Context context) {
        super(context, AxisOrientation.HORIZONTAL);

        // set up paint objects
        mLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLabelPaint.setColor(Color.YELLOW);
        mLabelPaint.setTextSize(30);
        mLabelPaint.setTextAlign(Paint.Align.CENTER);

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(Color.GREEN);
    }


    /**
     * Draws the Axis on to the canvas.
     * @param canvas canvas
     */
    public void draw(Canvas canvas) {
        super.draw(canvas);

        // draw the labels
        for (Slot slot : mSlots) {
            if (slot.label == null) {
                continue;
            }

            mLabelPaint.measureText(slot.label);
            canvas.drawText(
                    slot.label,
                    slot._x + (getSlotWidth() / 2f),
                    (getSlotHeight() / 2f) - ((mLabelPaint.ascent() + mLabelPaint.descent()) / 2f),
                    mLabelPaint
            );
        }
    }
}
