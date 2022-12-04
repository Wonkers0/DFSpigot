package me.wonk2.utilities.internals;

public class ObjectArrWrapper {
	private final Object[] arr;
	public int length;
	
	public ObjectArrWrapper(Object[] arr){
		this.arr = arr;
		length = arr.length;
	}
	
	public Object get(int index){
		return index >= arr.length ? null : arr[index];
	}
	
}
