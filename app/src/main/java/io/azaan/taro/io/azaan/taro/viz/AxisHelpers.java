package io.azaan.taro.io.azaan.taro.viz;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import io.azaan.taro.io.azaan.taro.viz.models.Slot;
import io.azaan.taro.io.azaan.taro.viz.models.XLabel;
import io.azaan.taro.io.azaan.taro.viz.models.YValue;

/**
 * Common helper methods for working with axis
 */
public class AxisHelpers {

    public enum Sort {
        ASC, DESC
    }


    /**
     * Given a list of values which has a x labels return slots
     * where all x values are labelled.
     *
     * Defaults to ascending sort
     *
     * @param values list of data values
     * @return list of slots
     */
    public static <T extends XLabel> List<Slot> makeDiscreteXSlots(List<T> values) {
        return makeDiscreteXSlots(values, Sort.ASC);
    }


    /**
     * Given a list of values which has a x labels return slots
     * where all x values are labelled.
     *
     * @param values list of data values
     * @return list of slots
     */
    public static <T extends XLabel> List<Slot> makeDiscreteXSlots(List<T> values, Sort sort) {
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        List<String> labels = uniqSortLabels(values, sort);
        List<Slot> out = new ArrayList<>(values.size());

        for (String label : labels) {
            Slot slot = new Slot(
                    label.hashCode(),
                    label,
                    false,
                    true
            );

            out.add(slot);
        }

        return out;
    }


    /**
     * Given list of values which has a label, return a list of strings
     * with all the unique values sorted
     *
     * @param values list of values
     * @param sort sort order
     * @return result labels
     */
    private static <T extends XLabel> List<String> uniqSortLabels(List<T> values, Sort sort) {
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        Set<String> set;
        if (sort == Sort.DESC)
            set = new TreeSet<>(Collections.reverseOrder(Collator.getInstance()));
        else
            set = new TreeSet<>(Collator.getInstance());

        for (XLabel value : values) {
            set.add(value.getXLabel());
        }

        return new ArrayList<>(set);
    }

    /**
     * Return a human friendly short form of a number
     *
     * @param value number
     * @return human friendly form
     */
    public static String humanizeValue(int value) {
        if (value < 1000) {
            return String.valueOf(value);

        } else {
            int thousands = value / 1000;
            int hundreds = (value / 100) % 10;

            if (hundreds == 0)
                return thousands + "k";

            return thousands + "." + hundreds + "k";
        }
    }
}
