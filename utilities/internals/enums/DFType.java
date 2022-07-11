package me.wonk2.utilities.internals.enums;

public enum DFType {
    TXT,
    NUM,
    SND,
    LOC,
    ITEM,
    POT,
    VAR,
    ANY, // This is only used internally for parameter types.
    REPEATING // This will only be used for creating new DFValues internally, there won't be any input with this!
}
