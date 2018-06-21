package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes.dynamicresolution;

import org.metaborg.meta.lang.dynsem.interpreter.ITermRegistry;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ALabel;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.DispatchNode;
import org.metaborg.meta.lang.dynsem.interpreter.terms.ITermInit;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

public abstract class IsWellFormedPathNode extends DynSemNode {

	@Child private DispatchNode dispatch;

	public IsWellFormedPathNode(SourceSection source) {
		super(source);
		this.dispatch = DispatchNode.create(source, "");
	}

	public abstract boolean execute(ReversedResolutionPath rrp, ALabel nextLabel);

	@Specialization
	public boolean checkWF(ReversedResolutionPath rrp, ALabel nextLabel,
			@Cached("getWellFormednessTermClass()") Class<?> wfTermClass,
			@Cached("createWellFormednessConConstructor(wfTermClass)") ITermInit wfTermInit,
			@Cached("createLabelListInit()") ITermInit listInit) {
		int pathLength = rrp != null ? rrp.size() + 1 : 1;
		ALabel[] labels = new ALabel[pathLength];
		int i = 0;
		while (rrp != null) {
			labels[i] = rrp.label();
			rrp = rrp.previous;
			i++;
		}
		labels[labels.length - 1] = nextLabel;
		Object[] args = new Object[] { wfTermInit.apply(new Object[] { listInit.apply((Object[]) labels) }) };
		return (boolean) dispatch.execute(wfTermClass, args).result;
	}

	protected Class<?> getWellFormednessTermClass() {
		CompilerAsserts.neverPartOfCompilation();
		return getContext().getTermRegistry().getConstructorClass("wf", 1);
	}

	protected ITermInit createWellFormednessConConstructor(Class<?> wfClass) {
		CompilerAsserts.neverPartOfCompilation();
		ITermRegistry registry = getContext().getTermRegistry();
		return registry.lookupClassConstructorWrapper(wfClass);
	}

	protected ITermInit createLabelListInit() {
		CompilerAsserts.neverPartOfCompilation();
		ITermRegistry registry = getContext().getTermRegistry();
		Class<?> listClass = registry.getListClass(ALabel.class);
		return registry.lookupClassConstructorWrapper(listClass);
	}

}
