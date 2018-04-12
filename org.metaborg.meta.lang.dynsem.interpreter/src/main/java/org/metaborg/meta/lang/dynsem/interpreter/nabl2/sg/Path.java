package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg;

public final class Path {
	private final PathStep[] steps;

	public Path(PathStep[] steps) {
		this.steps = steps;
	}

	public PathStep[] getSteps() {
		return steps;
	}

}
