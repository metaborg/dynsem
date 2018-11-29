package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes.dynamicresolution;

import org.metaborg.meta.lang.dynsem.interpreter.ITermRegistry;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ALabel;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.AbstractDispatch;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.DynamicDispatch;
import org.metaborg.meta.lang.dynsem.interpreter.terms.ITermInit;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

abstract class IsReversedOrder extends DynSemNode {
	protected final static String EMPTY = "";

	@Child protected AbstractDispatch dispatch;

	public IsReversedOrder(SourceSection source) {
		super(source);
		this.dispatch = DynamicDispatch.create(source, EMPTY);
	}

	public abstract boolean execute(ALabel l1, ALabel l2);

	@Specialization(guards = { "l1.equals(l1_cached)", "l2.equals(l2_cached)" }, limit = "16")
	public boolean areLabelsSwappedCached(ALabel l1, ALabel l2, @Cached("l1") ALabel l1_cached,
			@Cached("l2") ALabel l2_cached, @Cached("getOrderSwapTermClass()") Class<?> orderSwapTermClass,
			@Cached("getTermInit(orderSwapTermClass)") ITermInit orderSwapTermInit,
			@Cached("areLabelsSwapped(l1_cached, l2_cached, orderSwapTermClass, orderSwapTermInit)") boolean swapped) {
		return swapped;
	}

	@Specialization(replaces = "areLabelsSwappedCached")
	public boolean areLabelsSwapped(ALabel l1, ALabel l2,
			@Cached("getOrderSwapTermClass()") Class<?> orderSwapTermClass,
			@Cached("getTermInit(orderSwapTermClass)") ITermInit orderSwapTermInit) {
		Object[] args = new Object[] { orderSwapTermInit.apply(new Object[] { l1, l2 }) };
		return (boolean) dispatch.execute(args).result;
	}

	protected Class<?> getOrderSwapTermClass() {
		CompilerAsserts.neverPartOfCompilation();
		return getContext().getTermRegistry().getConstructorClass("order-swap", 2);
	}

	protected ITermInit getTermInit(Class<?> orderClass) {
		CompilerAsserts.neverPartOfCompilation();
		ITermRegistry registry = getContext().getTermRegistry();
		return registry.lookupClassConstructorWrapper(orderClass);
	}

}