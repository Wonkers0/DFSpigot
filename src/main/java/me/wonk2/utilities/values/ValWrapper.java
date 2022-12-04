package me.wonk2.utilities.values;

import me.wonk2.utilities.enums.DFType;

// Wrapper for DFValue class, serves to provide proper serialization using the jackson lib.
// You shouldn't really have to mess with this class tbh, it's only used internally
public class ValWrapper {
	public Object val;
	public DFType type;
	
	public ValWrapper(Object val, DFType type){
		this.val = val;
		this.type = type;
	}
	
	public ValWrapper(){} // for jackson lib
}
