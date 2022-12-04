package me.wonk2.utilities.internals;

/*
	NOTICE: Whilst there is a Pair class in the javafx library, I don't want to import a huge library
	for something that's only used in one place (that is, the LoopData class)
 */
public class Pair {
	public Object A;
	public Object B;
	
	public Pair(Object A, Object B){
		this.A = A;
		this.B = B;
	}
	
	@Override
	public int hashCode() {
		return A.hashCode() * B.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Pair)) return false;
		Pair p = (Pair) o;
		return p.A.equals(A) && p.B.equals(B);
	}
}
