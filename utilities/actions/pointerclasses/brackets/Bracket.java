package me.wonk2.utilities.actions.pointerclasses.brackets;

public class Bracket implements Cloneable{
	@Override
	public Bracket clone() {
		try {
			return (Bracket) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}
}
