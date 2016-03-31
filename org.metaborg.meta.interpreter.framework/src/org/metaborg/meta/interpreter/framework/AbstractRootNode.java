package org.metaborg.meta.interpreter.framework;

import org.spoofax.terms.TermFactory;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.SourceSection;

/**
 * 
 * @author vladvergu
 * 
 */
@NodeInfo(language = "Interpreter framework", description = "The abstract base node for all AST nodes")
public abstract class AbstractRootNode extends RootNode implements IMatchable,
		IConvertibleToStrategoTerm {

	@TruffleBoundary
	public AbstractRootNode(SourceSection src) {
		super(null, src, FrameDescriptor.create());
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
		return toStrategoTerm(new TermFactory()).toString();
	}

}
