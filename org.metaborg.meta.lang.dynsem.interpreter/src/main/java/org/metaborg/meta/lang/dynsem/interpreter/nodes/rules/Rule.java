package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.InterpreterException;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.SourceSection;

public abstract class Rule extends RootNode {

	public Rule(SourceSection sourceSection, FrameDescriptor frameDescriptor) {
		super(DynSemLanguage.class, sourceSection, frameDescriptor);
	}

	public abstract int getArity();

	public abstract String getConstructor();

	public abstract String getName();

	protected abstract RuleResult executeSafe(VirtualFrame frame);

	public RuleResult execute(VirtualFrame frame) {
		try {
			return executeSafe(frame);
		} catch (Exception ex) {
			throw new InterpreterException("Rule failure: " + getName() + "/"
					+ getConstructor() + "/" + getArity(), ex);
		}
	}

}