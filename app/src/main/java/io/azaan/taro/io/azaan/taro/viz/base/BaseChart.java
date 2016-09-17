package io.azaan.taro.io.azaan.taro.viz.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

import io.azaan.taro.R;
import io.azaan.taro.io.azaan.taro.viz.Helpers;
import io.azaan.taro.io.azaan.taro.viz.models.Slot;

/**
 * Abstract base class for all charts.
 *
 * A chart is the final view displayed to the user
 * which has the axis's and the graph (bar, line, etc).
 *
 * This class takes care of coordinating between these
 * views and provides common functionality needed by all charts
 */
public abstract class BaseChart extends View {
    private static final String TAG = BaseChart.class.getSimpleName();


    /**
     * Default height of the X Axis in DP
     */
    private int mXAxisHeight = 30;


    /**
     * Default width of the Y Axis in DP
     */
    private int mYAxisWidth = 30;


    /**
     * Instance of the XAxis
     */
    private BaseAxis mXAxis;


    /**
     * Instance of the Y BaseAxis
     */
    private BaseAxis mYAxis;


    /**
     * Calculated positions of the X axis top left
     */
    private float mXAxisPosX;
    private float mXAxisPosY;


    /**
     * Calculated positions of the Y axis top left
     */
    private float mYAxisPosX;
    private float mYAxisPosY;


    /**
     * Paint objects for drawing
     */
    private Paint mBackgroundPaint;


    /**
     * Canvas and the bitmap used for the x axis
     */
    private Bitmap mXAxisBitmap;
    private Canvas mXAxisCanvas;


    /**
     * Canvas and the bitmap used for the y axis
     */
    private Bitmap mYAxisBitmap;
    private Canvas mYAxisCanvas;


    /**
     * Constructor
     * @param context android context
     */
    public BaseChart(Context context) {
        super(context);
        init(context, null, 0);
    }


    /**
     * Constructor
     * @param context android context
     * @param attrs attribute set
     */
    public BaseChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }


    /**
     * Constructor
     * @param context android context
     * @param attrs attribute set
     * @param defStyleAttr default style attributes
     */
    public BaseChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }


    /**
     * Acts as a common constructor for this class.
     */
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.Chart,
                defStyleAttr, 0
        );

        int backgroundColor;
        try {
            backgroundColor = a.getColor(R.styleable.Chart_backgroundColor, 0);
        } finally {
            a.recycle();
        }

        // convert dp constants to px
        mXAxisHeight = (int) Math.floor(Helpers.pxFromDp(getContext(), mXAxisHeight));
        mYAxisWidth = (int) Math.floor(Helpers.pxFromDp(getContext(), mYAxisWidth));

        // set up paint objects
        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setColor(backgroundColor);
    }


    /**
     * Must be called to set up the chart with the axis it uses
     * and the graph view it uses
     *
     * @param xAxis x axis implementation or null if no x axis
     * @param yAxis y axis implementation or null if no y axis
     */
    public void configure(BaseAxis xAxis, BaseAxis yAxis) {
        this.mXAxis = xAxis;
        this.mYAxis = yAxis;

        // recalculate positioning and dimensions
        layout(getWidth(), getHeight());
    }

    /**
     * Takes care of determining the layout of
     * all the views
     *
     * @param w width
     * @param h height
     */
    private void layout(int w, int h) {
        boolean valid = w != 0 && h != 0;

        if (!valid) {
            return;
        }

        int availableWidth = w - getPaddingLeft() - getPaddingRight();
        int availableHeight = h - getPaddingTop() - getPaddingBottom();

        // determine dimensions
        int yWidth = mYAxis == null ? 0 : mYAxisWidth;
        int xWidth = mXAxis == null ? 0 : availableWidth - yWidth;

        int xHeight = mXAxis == null ? 0 : mXAxisHeight;
        int yHeight = mYAxis == null ? 0 : availableHeight - xHeight;

        int graphHeight = availableHeight - xHeight;
        int graphWidth = availableWidth - yWidth;

        // set up views
        setupXAxis(xWidth, xHeight);
        setupYAxis(yWidth, yHeight);

        // position the views
        int startX = getPaddingLeft();
        int startY = getPaddingTop();

        mXAxisPosX = startX + yWidth;
        mXAxisPosY = startY + graphHeight;

        mYAxisPosX = startX;
        mYAxisPosY = startY;
    }

    /**
     * Sets up the x axis if it exists
     *
     * @param w width of x axis
     * @param h height of x axis
     */
    private void setupXAxis(int w, int h) {
        if (mXAxis == null) {
            return;
        }

        if (mXAxisBitmap != null)
            mXAxisBitmap.recycle();

        mXAxis.setDimensions(w, h);
        mXAxisBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mXAxisCanvas = new Canvas(mXAxisBitmap);
    }


    /**
     * Sets up the y axis if it exists
     *
     * @param w width of y axis
     * @param h height of y axis
     */
    private void setupYAxis(int w, int h) {
        if (mYAxis == null) {
            return;
        }

        if (mYAxisBitmap != null)
            mYAxisBitmap.recycle();

        mYAxis.setDimensions(w, h);
        mYAxisBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mYAxisCanvas = new Canvas(mYAxisBitmap);
    }


    /**
     * Called to draw the view
     * @param canvas canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // draw background
        canvas.drawRect(0, 0, getWidth(), getHeight(), mBackgroundPaint);

        // draw x axis
        if (mXAxis != null) {
            mXAxis.draw(mXAxisCanvas);
            canvas.drawBitmap(mXAxisBitmap, mXAxisPosX, mXAxisPosY, null);
        }

        // draw y axis
        if (mYAxis != null) {
            mYAxis.draw(mYAxisCanvas);
            canvas.drawBitmap(mYAxisBitmap, mYAxisPosX, mYAxisPosY, null);
        }
    }


    /**
     * Called on Any Size changes
     *
     * @param w current width
     * @param h current height
     * @param oldw old width
     * @param oldh old height
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        boolean same = oldw == w && oldh == h;
        boolean valid = w != 0 && h != 0;

        if (!valid || same) {
            return;
        }

        layout(w, h);
    }

    /**
     * Sets the debug value to all the views
     *
     * @param debug
     */
    public void setDebug(boolean debug) {
        if (mXAxis != null)
            mXAxis.setDebug(debug);

        if (mYAxis != null)
            mYAxis.setDebug(debug);
    }


    public void setXSlots(List<Slot> slots) {
        if (mXAxis != null) {
            mXAxis.setSlots(slots);
        }
    }


    public void setYSlots(List<Slot> slots) {
        if (mYAxis != null) {
            mYAxis.setSlots(slots);
        }
    }
}
