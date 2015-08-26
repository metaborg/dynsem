package org.metaborg.meta.interpreter.framework;

import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.source.SourceSection;

/**
 * 
 * @author vladvergu
 * 
 */
@NodeInfo(language = "Interpreter framework", description = "The abstract base node for all AST nodes")
public abstract class AbstractNode extends Node implements IMatchable,
		IConvertibleToStrategoTerm {

	public AbstractNode(SourceSection src) {
		super(src);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T match(Class<T> clazz) {
		if (this.getClass() == clazz) {
			return (T) this;
		} else {
			return null;
		}
	}

	@Override
	public String toString() {
		return NodeUtils.toString(this);
	}

}
