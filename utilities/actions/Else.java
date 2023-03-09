package me.wonk2.utilities.actions;

import me.wonk2.utilities.actions.pointerclasses.Conditional;

public class Else extends Conditional {
	public boolean shouldRun = false;
	
	public Else() {
		super(null, null, null, null, null, false);
	}
	
	@Override
	public boolean evaluateCondition(){
		return shouldRun;
	}
}
