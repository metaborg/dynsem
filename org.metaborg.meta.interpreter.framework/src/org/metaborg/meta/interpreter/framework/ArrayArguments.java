/**
 * 
 */
package org.metaborg.meta.interpreter.framework;

/**
 * @author vladvergu
 * 
 */
public class ArrayArguments implements IArguments {

	private final Object[] store;

	public ArrayArguments(int size) {
		store = new Object[size];
	}

	@Override
	public void setArgument(int idx, Object value) {
		store[idx] = value;

	}

	@Override
	public Object getArgument(int idx) {
		return store[idx];
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getArgument(int idx, Class<T> clazz) {
		return (T) store[idx];
	}

}
