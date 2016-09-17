package io.azaan.taro.io.azaan.taro.viz.views;

import android.content.Context;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

import io.azaan.taro.io.azaan.taro.viz.AxisHelpers;
import io.azaan.taro.io.azaan.taro.viz.base.BaseChart;
import io.azaan.taro.io.azaan.taro.viz.models.Slot;
import io.azaan.taro.io.azaan.taro.viz.models.StackedBarData;
import io.azaan.taro.io.azaan.taro.viz.partials.XAxis;
import io.azaan.taro.io.azaan.taro.viz.partials.YAxis;

public class StackedBarChart extends BaseChart {
    private static final String TAG = StackedBarChart.class.getSimpleName();

    /**
     * Constructor
     * @param context android context
     */
    public StackedBarChart(Context context) {
        super(context);
        init(context, null, 0);
    }


    /**
     * Constructor
     * @param context android context
     * @param attrs attribute set
     */
    public StackedBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }


    /**
     * Constructor
     * @param context android context
     * @param attrs attribute set
     * @param defStyleAttr default style attributes
     */
    public StackedBarChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }


    /**
     * Common constructor
     */
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        XAxis xAxis = new XAxis(context);
        YAxis yAxis = new YAxis(context);

        super.configure(xAxis, yAxis);
    }

    public void setData(List<StackedBarData> data) {
        List<Slot> xSlots = AxisHelpers.makeDiscreteXSlots(data);

        List<Slot> ySlots = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            String label = AxisHelpers.humanizeValue(1432 * i);
            ySlots.add(new Slot(
                    label.hashCode(),
                    label,
                    false,
                    true
            ));
        }

        super.setXSlots(xSlots);
        super.setYSlots(ySlots);
    }
}
