package io.azaan.taro.io.azaan.taro.viz.partials;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.Collections;
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
    private Orientation mOrientation;

    public enum Orientation {
        HORIZONTAL, VERTICAL
    }


    /**
     * Axis slot align.
     * Which side the slots are aligned to
     */
    private Alignment mAlignment = Alignment.CENTER;

    public enum Alignment {
        START, CENTER, END
    }


    /**
     * Calculated dimensions.
     */
    private float mSlotWidth;
    private float mSlotHeight;
    private float mSlotSeparation;
    private float mSlotStartX;
    private float mSlotStartY;


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
    private List<Slot> mSlots = new ArrayList<>();


    public Axis(Context mContext, Orientation orientation) {
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

        float maxMajor = mOrientation == Orientation.HORIZONTAL ? mW : mH;
        float majorSlotSize = mOrientation == Orientation.HORIZONTAL ? mSlotWidth : mSlotHeight;

        // calculate slot separation
        float slotSeparation = (float) Math.floor((maxMajor - majorSlotSize * countF) / (countF - 1));

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
        mSlotSeparation = Math.min(mMaxSeparation, slotSeparation);

        // all major slot size = (slot size + separation between them)
        float slotTotalSize = getTotalSlotSize();

        float majorSlotStart = -1;
        switch (mAlignment) {
            case START:
                majorSlotStart = 0;
                break;

            case END:
                majorSlotStart = maxMajor - slotTotalSize;
                break;

            case CENTER:
                majorSlotStart = (maxMajor / 2f) - (slotTotalSize / 2f);
                break;
        }

        if (majorSlotStart == -1) {
            throw new IllegalStateException("Slot alignment behaviour not defined alignment=" + mAlignment);
        }

        if (mOrientation == Orientation.HORIZONTAL) {
            mSlotStartX = majorSlotStart;
            mSlotStartY = 0;
        } else {
            mSlotStartX = 0;
            mSlotStartY = majorSlotStart;
        }

        // calculate and store slot positions
        for (int i = 0; i < mSlots.size(); i++) {
            Slot slot = mSlots.get(i);

            float slotPos = majorSlotStart + (majorSlotSize + slotSeparation) * i;
            if (mOrientation == Orientation.HORIZONTAL) {
                slot._x = slotPos;
                slot._y = 0;
            } else {
                slot._x = 0;
                slot._y = slotPos;
            }
        }
    }


    /**
     * Gets the slot with the given id. null if not found
     *
     * @param slotId slot id
     * @return slot with given id
     */
    private Slot getSlotById(int slotId) {
        for (Slot slot : mSlots) {
            if (slot.id == slotId)
                return slot;
        }

        return null;
    }


    /**
     * The total size (width/height depending on the orientation)
     * of all the slots and their separation
     *
     * @return total size in pixels
     */
    private float getTotalSlotSize() {
        float countF = (float) mSlots.size();

        return mOrientation == Orientation.HORIZONTAL ?
                mSlotWidth * countF + mSlotSeparation * (countF - 1) :
                mSlotHeight * countF + mSlotSeparation * (countF - 1);
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
        float majorMax = mOrientation == Orientation.HORIZONTAL ? mW : mH;

        // float so all divisions are float divs
        float countF = (float) mSlots.size();

        // take the max available space (size - all min separation) and divide by number of slots
        float majorSize = (float) Math.floor((majorMax - mMinSeparation * (countF - 1)) / countF);
        majorSize = Math.min(mMaxSlotWidth, majorSize);

        if (mOrientation == Orientation.HORIZONTAL) {
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
    public void draw(Canvas canvas) {
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

        if (mOrientation == Orientation.VERTICAL) {
            Collections.reverse(mSlots);
        }

        calculateSlotPositions();
    }


    /**
     * Sets the orientation of the view.
     *
     * @param orientation orientation
     */
    public void setOrientation(Orientation orientation) {
        // in case of an orientation flip we should reverse the list
        // of slots

        if (orientation != mOrientation) {
            Collections.reverse(mSlots);
        }

        this.mOrientation = orientation;
    }


    /**
     * Given a slot id get the start position of the slot
     * on the major axis
     *
     * @param slotId slot id
     * @return position in pixels
     */
    public float getStartPosForSlot(int slotId) {
        Slot slot = getSlotById(slotId);
        if (slot == null) {
            throw new IllegalArgumentException("No slot with id=" + slotId);
        }

        return mOrientation == Orientation.HORIZONTAL ?
                slot._x :
                slot._y;
    }


    /**
     * Given a slot id return the center position of the slot
     * on the major axis.
     *
     * @param slotId slot id
     * @return position in pixels
     */
    public float getCenterPosForSlot(int slotId) {
        Slot slot = getSlotById(slotId);
        if (slot == null) {
            throw new IllegalArgumentException("No slot with id=" + slotId);
        }

        return mOrientation == Orientation.HORIZONTAL ?
                slot._x + mSlotWidth / 2f :
                slot._y + mSlotHeight / 2f;
    }


    /**
     * Given a percentage representing where the point is
     * on the axis (0 being starting slot and 1 being last slot)
     * get the position on the major axis
     *
     * @param perc percentage value (0-1)
     * @return x coordinate
     */
    public float getPosForPercentage(float perc) {
        if (perc < 0 || perc > 1) {
            throw new IllegalArgumentException("Percentage value must be between 0 and 1. perc=" + perc);
        }

        return mOrientation == Orientation.HORIZONTAL ?
                mSlotStartX + getTotalSlotSize() * perc :
                mSlotStartY + getTotalSlotSize() * perc;
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

    public void setAlignment(Alignment alignment) {
        this.mAlignment = alignment;
    }

}
