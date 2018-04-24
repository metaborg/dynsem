package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IApplTerm;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class FallbackRule extends Rule {

	@Child private DispatchNode dispatchNode;
	private final Class<?> fallbackOfClass;
	private final String arrowName;

	public FallbackRule(DynSemLanguage lang, SourceSection source, String arrowName, Class<?> fallbackOfClass) {
		super(lang, source);
		this.fallbackOfClass = fallbackOfClass;
		this.dispatchNode = DispatchNodeGen.create(source, arrowName);
		this.arrowName = arrowName;
		Truffle.getRuntime().createCallTarget(this);
	}

	@CompilationFinal private Class<?> nextDispatchClass;

	@Override
	public RuleResult execute(VirtualFrame frame) {
		if (nextDispatchClass == null) {
			CompilerDirectives.transferToInterpreterAndInvalidate();
			nextDispatchClass = nextDispatchClass(frame);
			if (nextDispatchClass == null) {
				throw new ReductionFailure("No rule " + fallbackOfClass.getSimpleName() + " -" + arrowName
						+ "-> applies to " + frame.getArguments()[0], InterpreterUtils.createStacktrace(), this);
			}
		}
		RuleResult fallbackResult = dispatchNode.execute(nextDispatchClass, frame.getArguments());
		return fallbackResult;
	}

	public Class<?> nextDispatchClass(VirtualFrame frame) {
		Object input = frame.getArguments()[0];
		Class<?> classOfInput = input.getClass();
		if (IApplTerm.class.isAssignableFrom(classOfInput)) {
			if (classOfInput == fallbackOfClass) {
				return IApplTerm.class.cast(input).getSortClass();
			}
		}
		return null;
	}

	@Override
	public boolean isCloningAllowed() {
		return true;
	}

	@Override
	protected boolean isCloneUninitializedSupported() {
		return true;
	}

	@Override
	protected Rule cloneUninitialized() {
		return new FallbackRule(language(), getSourceSection(), arrowName, fallbackOfClass);
	}

	@Override
	@TruffleBoundary
	public String toString() {
		return "Fallback rule of: " + fallbackOfClass.getName() + " -" + arrowName + "->";
	}

}
