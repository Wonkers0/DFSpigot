package me.wonk2.utilities.values;

import me.wonk2.utilities.enums.DFType;

public class Parameter {
    public String name;
    public DFType type;
    public Object defaultValue;
    public boolean repeating;

    public Parameter(String paramName, DFType type, Object defaultValue, boolean repeating){
        this.type = type;
        this.defaultValue = defaultValue;
        this.repeating = repeating;
        this.paramName = paramName;
    }
    
    public Parameter() {} // Empty constructor needed for jackson deserialization library
}
