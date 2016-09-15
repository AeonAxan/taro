package io.azaan.taro.io.azaan.taro.viz.partials;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;

import io.azaan.taro.io.azaan.taro.viz.Helpers;
import io.azaan.taro.io.azaan.taro.viz.models.Slot;

/**
 * Takes care of the XAxis, all it's calculations
 * positioning and drawing.
 *
 * The parent chart should take care of determining
 * the position as well as dimensions of the axis.
 */
public class XAxis {

    private Context mContext;


    /**
     * Is this view in debug mode
     */
    private boolean mDebug = false;


    /**
     * width and height of the axis.
     */
    private int mW = 0;
    private int mH = 0;


    /**
     * Minimum separation between slots.
     * Should be set in DP
     */
    private float mMinSeparation = 5;


    /**
     * DP's for maximum slot width
     * Should be set in DP
     */
    private float mMaxSlotWidth = 30;


    /**
     * Calculated dimensions.
     */
    private float mSlotWidth;


    /**
     * Paint objects
     */
    private Paint mDebugPaint;
    private Paint mLabelPaint;
    private Paint mLinePaint;


    /**
     * List of all 'slots' on the x axis. A slot is a position
     * in the axis where a label or a axis tick resides. All the given
     * slots are displayed evenly separated.
     *
     * Slots should not be confused with 'points on the axis for data'.
     * Data can be either continuous or discrete and can be mapped appropriately,
     * slots are merely points for either ticks or labels on the axis. These
     * points are usually calculated by the axis data mapper and passed in.
     */
    List<Slot> mSlots = new ArrayList<>();


    /**
     * Constructor function
     * @param context Android Context
     */
    public XAxis(Context context) {
        this.mContext = context;

        // convert dp values to px
        mMinSeparation = Helpers.pxFromDp(mContext, mMinSeparation);
        mMaxSlotWidth = Helpers.pxFromDp(mContext, mMaxSlotWidth);

        // set up paint objects
        mDebugPaint = new Paint();
        mDebugPaint.setColor(Color.RED);
        mDebugPaint.setStyle(Paint.Style.STROKE);

        mLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLabelPaint.setColor(Color.YELLOW);
        mLabelPaint.setTextSize(30);
        mLabelPaint.setTextAlign(Paint.Align.CENTER);

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(Color.GREEN);
    }


    /**
     * Sets the slots for the axis
     *
     * @param slots slots
     */
    public void setSlots(List<Slot> slots) {
        mSlots = new ArrayList<>(slots);

        calculateSlotPositions();
    }

    /**
     * This function must be called for:
     * - Any change in dimensions
     * - Any change in dataset
     *
     * The function calculates and updates the values for all slots.
     */
    private void calculateSlotPositions() {
        boolean valid = mW > 0 && mH > 0;
        boolean hasSlots = mSlots.size() > 0;
        if (!valid || !hasSlots) {
            return;
        }

        float count = (float) mSlots.size();

        float slotSeparation = mMinSeparation;

        // calculate the maximum slot width possible and then bound it
        mSlotWidth = (float) Math.floor((mW - slotSeparation * (count-1)) / count);
        mSlotWidth = Math.min(mMaxSlotWidth, mSlotWidth);

        // update separation to take up all the remaining space
        slotSeparation = (float) Math.floor((mW - mSlotWidth * count) / (count-1));

        // all slot width (slot width + separation between them)
        float slotTotalWidth = mSlotWidth * mSlots.size() + slotSeparation * (mSlots.size() - 1);

        float slotStartX = (mW / 2f) - (slotTotalWidth / 2f);

        // calculate and store slot positions
        for (int i = 0; i < mSlots.size(); i++) {
            Slot slot = mSlots.get(i);

            slot._x = slotStartX;

            if (i > 0) {
                slot._x += (mSlotWidth + slotSeparation) * i;
            }
        }
    }


    /**
     * Draws the Axis on to the canvas.
     * @param canvas canvas
     */
    public void draw(Canvas canvas) {
        if (mDebug) {
            // axis outline
            canvas.drawRect(0, 0, mW, mH, mDebugPaint);

            // individual slots
            for (Slot slot : mSlots) {
                canvas.drawRect(slot._x, 0, slot._x + mSlotWidth, mH, mDebugPaint);
            }
        }

        // draw line on top of axis
        canvas.drawLine(0, 0, mW, 0, mLinePaint);

        // draw the labels
        for (Slot slot : mSlots) {
            if (slot.label == null) {
                continue;
            }

            mLabelPaint.measureText(slot.label);
            canvas.drawText(
                    slot.label,
                    slot._x + (mSlotWidth / 2f),
                    (mH / 2f) - ((mLabelPaint.ascent() + mLabelPaint.descent()) / 2f),
                    mLabelPaint
            );
        }
    }


    /**
     * Sets the width and height of the view
     *
     * @param w width
     * @param h height
     */
    public void setDimensions(int w, int h) {
        mW = w;
        mH = h;

        calculateSlotPositions();
    }


    public boolean isDebug() {
        return mDebug;
    }

    public void setDebug(boolean mDebug) {
        this.mDebug = mDebug;
    }
}
