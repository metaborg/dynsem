/**
 * 
 */
package org.metaborg.meta.interpreter.framework;

/**
 * @author vladvergu
 * 
 */
public interface IArguments {

	public void setArgument(int idx, Object value);

	public Object getArgument(int idx);

	public <T> T getArgument(int idx, Class<T> clazz);
}
