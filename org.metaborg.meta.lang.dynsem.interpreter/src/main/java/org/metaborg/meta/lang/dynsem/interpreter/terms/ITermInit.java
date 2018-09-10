package org.metaborg.meta.lang.dynsem.interpreter.terms;

public interface ITermInit {

	default public Object apply(Object... objects) {
		throw new RuntimeException("Operation is not supported");
	}

}
