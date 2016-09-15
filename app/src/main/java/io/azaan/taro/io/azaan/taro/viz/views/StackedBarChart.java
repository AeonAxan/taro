package io.azaan.taro.io.azaan.taro.viz.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.azaan.taro.R;
import io.azaan.taro.io.azaan.taro.viz.models.Slot;
import io.azaan.taro.io.azaan.taro.viz.partials.XAxis;

public class StackedBarChart extends View {
    private static final String TAG = StackedBarChart.class.getSimpleName();

    private XAxis mXAxis;

    public StackedBarChart(Context context) {
        super(context);
        init(context, null, 0);
    }

    public StackedBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public StackedBarChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    /**
     * Background color used in the view.
     * Set in XML with backgroundColor
     */
    private int mBackgroundColor;

    /**
     * Paint objects for drawing
     */
    private Paint mBackgroundPaint;

    /**
     * Takes care of initializing this class.
     */
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.StackedBarChart,
                defStyleAttr, 0
        );

        try {
            mBackgroundColor = a.getColor(R.styleable.StackedBarChart_backgroundColor, 0);
        } finally {
            a.recycle();
        }

        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setColor(mBackgroundColor);
    }

    /**
     * All dimension related calculations must be done here. This
     * function will be called whenever any size change happens.
     */
    private void calculateDimensions(int w, int h) {
        // set up axis
        mXAxis = new XAxis(super.getContext());
        mXAxis.setDimensions(w, 80);

        List<Slot> slots = new ArrayList<>();
        for (String day : Arrays.asList("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")) {
            slots.add(new Slot(day.hashCode(), day));
        }

        mXAxis.setSlots(slots);

        mXAxisBitmap = Bitmap.createBitmap(w, 80, Bitmap.Config.ARGB_8888);
    }

    private Bitmap mXAxisBitmap;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // draw background
        canvas.drawRect(0, 0, getWidth(), getHeight(), mBackgroundPaint);

        Canvas xCanvas = new Canvas(mXAxisBitmap);
        mXAxis.draw(xCanvas);

        canvas.drawBitmap(mXAxisBitmap, 0, 200, null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        boolean valid = w != 0 && h != 0;
        boolean same = oldw == w && oldh == h;

        if (valid && !same) {
            calculateDimensions(w, h);
        }
    }
}
