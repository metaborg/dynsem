package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

public final class Path {
	private final PathStep[] steps;

	public Path(PathStep[] steps) {
		this.steps = steps;
	}

	public PathStep[] getSteps() {
		return steps;
	}

	@Override
	@TruffleBoundary
	public String toString() {
		StringBuilder str = new StringBuilder().append("[");
		for (int i = 0; i < steps.length; i++) {
			str.append(steps[i]);
			if (i < steps.length - 1) {
				str.append(", ");
			}
		}
		str.append("]");
		return str.toString();
	}
}
