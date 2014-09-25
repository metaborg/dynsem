/**
 * 
 */
package org.metaborg.meta.interpreter.framework;

/**
 * @author vladvergu
 *
 */
public interface IGenericNode extends INode {

	public INode specialize(int depth);
}
