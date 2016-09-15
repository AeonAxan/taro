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
 * Abstract base class for an axis.
 *
 * All axis classes must inherit from this base class
 */
public abstract class Axis {

    /**
     * Android Context
     */
    private Context mContext;


    /**
     * Is the view in debug mode.
     *
     * In debug mode debug outlines are drawn
     * as well as other debug features are enabled
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
     * Maximum separation between slots.
     * Should be set in DP
     */
    private float mMaxSeparation = 20;


    /**
     * DP's for maximum slot width
     * Should be set in DP
     */
    private float mMaxSlotWidth = 30;


    /**
     * Axis orientation
     * The orientation of this axis
     */
    private AxisOrientation mOrientation;
    public enum AxisOrientation {
        HORIZONTAL, VERTICAL
    }


    /**
     * Axis slot align.
     * Which side the slots are aligned to
     */
    private SlotAlignment mSlotAlignment = SlotAlignment.CENTER;
    public enum SlotAlignment {
        START, CENTER, END
    }


    /**
     * Calculated dimensions.
     */
    private float mSlotWidth;
    private float mSlotHeight;


    /**
     * Paint objects
     */
    private Paint mDebugPaint;


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


    public Axis(Context mContext, AxisOrientation orientation) {
        this.mContext = mContext;
        this.mOrientation = orientation;

        // convert dp values to px
        mMinSeparation = Helpers.pxFromDp(mContext, mMinSeparation);
        mMaxSeparation = Helpers.pxFromDp(mContext, mMaxSeparation);
        mMaxSlotWidth = Helpers.pxFromDp(mContext, mMaxSlotWidth);

        // set up paint objects
        mDebugPaint = new Paint();
        mDebugPaint.setColor(Color.RED);
        mDebugPaint.setStyle(Paint.Style.STROKE);
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
     * Calculates all slot positions
     * This function should be called for:
     * - Any change in dimensions
     * - Any change in dataset
     *
     * The function calculates and updates the values for all slots.
     */
    private void calculateSlotPositions() {
        float countF = (float) mSlots.size();

        boolean valid = mW > 0 && mH > 0;
        boolean hasSlots = countF > 0;
        if (!valid || !hasSlots) {
            return;
        }

        calculateSlotSize();

        float maxMajor = mOrientation == AxisOrientation.HORIZONTAL ? mW : mH;
        float majorSlotSize = mOrientation == AxisOrientation.HORIZONTAL ? mSlotWidth : mSlotHeight;

        // calculate slot separation
        float slotSeparation = (float) Math.floor((maxMajor - majorSlotSize * countF) / (countF-1));

        // if slot size calculation is implemented properly this should
        // never happen
        if (slotSeparation < mMinSeparation) {
            // slot size calculation was done wrong.
            throw new IllegalStateException(
                    "SlotSize calculation done wrong. separation < min separation" +
                    " sep=" + slotSeparation + " minSep=" + mMinSeparation
            );
        }

        // bound separation to max
        slotSeparation = Math.min(mMaxSeparation, slotSeparation);

        // all major slot size = (slot size + separation between them)
        float slotTotalWidth = majorSlotSize * countF + slotSeparation * (countF-1);


        float majorSlotStart = -1;
        switch (mSlotAlignment) {
            case START:
                majorSlotStart = 0;
                break;

            case END:
                majorSlotStart = maxMajor - slotTotalWidth;
                break;

            case CENTER:
                majorSlotStart = (maxMajor / 2f) - (slotTotalWidth / 2f);
                break;
        }

        if (majorSlotStart == -1) {
            throw new IllegalStateException("Slot alignment behaviour not defined alignment=" + mSlotAlignment);
        }

        // calculate and store slot positions
        for (int i = 0; i < mSlots.size(); i++) {
            Slot slot = mSlots.get(i);

            float slotPos = majorSlotStart + (majorSlotSize + slotSeparation) * i;
            if (mOrientation == AxisOrientation.HORIZONTAL) {
                slot._x = slotPos;
                slot._y = 0;
            } else {
                slot._x = 0;
                slot._y = slotPos;
            }
        }
    }

    /**
     * Calculates the slot size. The maximum possible size is given
     * in the major axis and is bounded by mMaxSlotWidth. In the minor axis
     * the size is the maximum possible size of the minor axis
     *
     * Must ensure that slot sizes are small enough such that minSeparation
     * can be respected.
     */
    protected void calculateSlotSize() {
        // calculate for major axis
        float majorMax = mOrientation == AxisOrientation.HORIZONTAL ? mW : mH;

        // float so all divisions are float divs
        float countF = (float) mSlots.size();

        // take the max available space (size - all min separation) and divide by number of slots
        float majorSize = (float) Math.floor((majorMax - mMinSeparation * (countF-1)) / countF);
        majorSize = Math.min(mMaxSlotWidth, majorSize);

        if (mOrientation == AxisOrientation.HORIZONTAL) {
            mSlotWidth = majorSize;
            mSlotHeight = mH;
        } else {
            mSlotWidth = mW;
            mSlotHeight = majorSize;
        }
    }


    /**
     * The child class should call this method
     * on draw.
     *
     * takes care of drawing debug data
     */
    protected void draw(Canvas canvas) {
        if (mDebug) {
            // draw slots
            for (Slot slot : mSlots) {
                canvas.drawRect(
                        slot._x, slot._y,
                        // -0.1f as if the boundary line is on the edge of the canvas
                        // it might get clipped
                        slot._x + mSlotWidth - 0.1f, slot._y + mSlotHeight - 0.1f,
                        mDebugPaint
                );
            }
        }
    }

    public List<Slot> getSlots() {
        return mSlots;
    }

    public int getW() {
        return mW;
    }

    public int getH() {
        return mH;
    }

    public float getSlotWidth() {
        return mSlotWidth;
    }

    public float getSlotHeight() {
        return mSlotHeight;
    }

    protected Context getContext() {
        return mContext;
    }

    public boolean isDebug() {
        return mDebug;
    }

    public void setDebug(boolean debug) {
        this.mDebug = debug;
    }

    public void setSlotAlignment(SlotAlignment slotAlignment) {
        this.mSlotAlignment = slotAlignment;
    }
}
