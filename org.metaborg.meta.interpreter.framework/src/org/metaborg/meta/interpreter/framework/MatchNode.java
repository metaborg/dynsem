/**
 * 
 */
package org.metaborg.meta.interpreter.framework;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;

/**
 * @author vladvergu
 *
 */
public class MatchNode<T> extends Node {

	private final Class<T> matchClass;

	public MatchNode(Class<T> matchClass) {
		this.matchClass = matchClass;
	}

	@SuppressWarnings("unchecked")
	public T execute(VirtualFrame frame, Object o) {
		if (o instanceof IGenericNode) {
			o = ((IGenericNode) o).specialize();
		}
		if (o.getClass() == matchClass) {
			return (T) o;
		}
		throw BacktrackException.SINGLETON;
	}

}
