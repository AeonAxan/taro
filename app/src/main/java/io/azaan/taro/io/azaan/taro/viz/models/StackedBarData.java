package io.azaan.taro.io.azaan.taro.viz.models;

/**
 * Data model for stacked bar charts
 */
public class StackedBarData implements XLabel, YValue {
    public final int value;
    public final String label;

    public StackedBarData(String label, int value) {
        this.value = value;
        this.label = label;
    }

    @Override
    public String getXLabel() {
        return label;
    }

    @Override
    public float getYValue() {
        return (float) value;
    }
}
