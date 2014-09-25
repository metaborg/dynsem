/**
 * 
 */
package org.metaborg.meta.interpreter.framework;

/**
 * @author vladvergu
 * 
 */
public abstract class AValue implements IMatchable {

	@SuppressWarnings("unchecked")
	@Override
	public <T> T match(Class<T> clazz) {
		if (this.getClass() == clazz) {
			return (T) this;
		} else {
			return null;
		}
	}
}
