package me.wonk2.utilities.values;

import me.wonk2.utilities.enums.DFType;

public class Parameter {
    public final String paramName;
    public final DFType type;
    public final Object defaultValue;
    public final boolean repeating;

    public Parameter(String paramName, DFType type, Object defaultValue, boolean repeating){
        this.type = type;
        this.defaultValue = defaultValue;
        this.repeating = repeating;
        this.paramName = paramName;
    }
}
