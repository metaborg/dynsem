package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IApplTerm;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

import mb.nabl2.terms.ITerm;

public class FallbackRule extends Rule {

	@Child private DispatchNode dispatchNode;
	private final String friendlyName;

	public FallbackRule(DynSemLanguage lang, SourceSection source, String arrowName, Class<?> fallbackOfClass) {
		super(lang, source);
		this.dispatchNode = DispatchNodeGen.create(source, arrowName);
		this.friendlyName = "Fallback rule of: " + fallbackOfClass.getName() + " -" + arrowName + "->";
		Truffle.getRuntime().createCallTarget(this);
	}

	@CompilationFinal private Class<?> nextDispatchClass;

	public RuleResult execute(VirtualFrame frame) {
		if (nextDispatchClass == null) {
			CompilerDirectives.transferToInterpreterAndInvalidate();
			nextDispatchClass = nextDispatchClass(frame);
			if (nextDispatchClass == null) {
				throw new ReductionFailure("No rules applicable for " + frame.getArguments()[0],
						InterpreterUtils.createStacktrace());
			}
		}
		RuleResult fallbackResult = dispatchNode.execute(nextDispatchClass, frame.getArguments());
		return fallbackResult;
	}

	public Class<?> nextDispatchClass(VirtualFrame frame) {
		Object inputT = frame.getArguments()[0];
		Class<?> inputClass = inputT.getClass();
		if (IApplTerm.class.isAssignableFrom(inputClass)) {
			Class<?> sortClass = IApplTerm.class.cast(inputT).getSortClass();
			if (inputClass == sortClass) {
				return ITerm.class;
			} else {
				return sortClass;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return friendlyName;
	}

}
