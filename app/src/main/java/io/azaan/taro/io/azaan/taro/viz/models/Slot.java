package io.azaan.taro.io.azaan.taro.viz.models;

/**
 * Data class used to represent a slot in an axis.
 *
 * A slot is a point in the axis where a label, major marking
 * and minor markings are set
 *
 * The specific graph and axis type decides how to display
 * this data.
 */
public class Slot {
    public int id;
    
    public String label;
    public boolean hasMinorTick;
    public boolean hasMajorTick;

    /**
     * Positioning data set by the axis. This is treated as internal
     * data and generally should not be used outside of an BaseAxis class.
     */
    public float _x;
    public float _y;
    
    public Slot(int id) {
        this(id, null, false, false);
    }
    
    public Slot(int id, String label) {
        this(id, label, false, false);
    }

    /**
     * Constructor
     * 
     * @param id unique id for every slot. ID must be unique within axis
     * @param label label to be displayed, null otherwise.
     * @param hasMinorTick does the slot have a minor tick
     * @param hasMajorTick is the slot associated with a major tick
     */
    public Slot(int id, String label, boolean hasMinorTick, boolean hasMajorTick) {
        this.id = id;
        this.label = label;
        this.hasMinorTick = hasMinorTick;
        this.hasMajorTick = hasMajorTick;
    }
}
