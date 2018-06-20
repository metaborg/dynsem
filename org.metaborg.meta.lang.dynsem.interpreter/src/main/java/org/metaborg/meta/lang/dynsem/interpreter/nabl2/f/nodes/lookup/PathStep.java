package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes.lookup;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.FrameAddr;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Occurrence;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ScopeIdentifier;

import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.object.DynamicObject;

public abstract class PathStep extends Node {
	protected final ScopeIdentifier scopeIdent;

	public PathStep(ScopeIdentifier scopeIdent) {
		this.scopeIdent = scopeIdent;
	}

	public abstract Occurrence getTargetDec();

	public abstract FrameAddr executeLookup(DynamicObject frm);



}
