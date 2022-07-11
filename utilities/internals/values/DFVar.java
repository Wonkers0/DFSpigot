package me.wonk2.utilities.internals.values;

import me.wonk2.utilities.internals.enums.DFType;
import me.wonk2.utilities.internals.enums.Scope;

import java.util.HashMap;

public class DFVar {
    public static HashMap<String, DFValue> globalVars = new HashMap<>();
    /*
    Variables are mostly just DFValue instances tied to a String (their name). Instances of this class will only be used
    when formatting arguments to mimic the in-game variable data structure.
     */

    public Scope scope;
    public String name;

    public DFVar(String name, Scope scope){ // Constructor for making an instance of this class. Won't do anything by itself.
        this.name = name;
        this.scope = scope;
    }

    public static DFValue getVar(DFVar var, HashMap<String, DFValue> localStorage){
        // Use this method to get the value of a variable
        HashMap<String, DFValue> varStorage = new HashMap<>();
        if(var.scope == Scope.GLOBAL) varStorage = globalVars;
        else if (var.scope == Scope.LOCAL) varStorage = localStorage;
        else if (var.scope == Scope.SAVE) /*TODO: Grab saved variable storage from .yml file*/;

        return varStorage.containsKey(var.name) ? varStorage.get(var.name) : nullVar();
    }

    public static void setVar(DFVar var, DFValue value, HashMap<String, DFValue> localStorage){
        // Use this to assign a value to a variable.
        if(var.scope == Scope.GLOBAL) globalVars.put(var.name, value);
        else if(var.scope == Scope.LOCAL) localStorage.put(var.name, value);
        else if(var.scope == Scope.SAVE) /*TODO: Save variable to .yml file*/;
    }

    private static DFValue nullVar(){
        return new DFValue(0, null, DFType.NUM);
    }
}

