package org.metaborg.meta.lang.dynsem.interpreter.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;

import com.oracle.truffle.api.nodes.RootNode;

public abstract class DynSemRootNode extends RootNode {

	protected DynSemRootNode(DynSemLanguage lang) {
		super(lang);
	}

	public DynSemContext getContext() {
		return DynSemLanguage.getContext(this);
	}

}
