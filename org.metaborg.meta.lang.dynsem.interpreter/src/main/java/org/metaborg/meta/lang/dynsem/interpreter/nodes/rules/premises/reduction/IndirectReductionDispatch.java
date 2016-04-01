package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.reduction;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.ITermInstanceChecker;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.terms.BuiltinTypesGen;
import org.metaborg.meta.lang.dynsem.interpreter.terms.ITerm;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.SourceSection;

public abstract class IndirectReductionDispatch extends Node {

	protected static final int INLINE_CACHE_SIZE = 3;

	@CompilationFinal protected DynSemContext context;
	private final String arrowName;

	public IndirectReductionDispatch(String arrowname, SourceSection source) {
		super(source);
		this.arrowName = arrowname;
	}

	public String getArrowname() {
		return arrowName;
	}

	public abstract RuleResult executeDispatch(VirtualFrame frame, Object term, Object[] args);

	@Specialization(limit = "INLINE_CACHE_SIZE", guards = "check.isInstance(term)")
	protected RuleResult doDirect(VirtualFrame frame, Object term, Object[] args, //
			@Cached("lookupInstanceChecker(term)") ITermInstanceChecker check, //
			@Cached("create(lookupCallTarget(term))") DirectCallNode callnode) {
		return (RuleResult) callnode.call(frame, args);
	}

	@Specialization(contains = "doDirect")
	protected RuleResult doIndirect(VirtualFrame frame, Object term, Object[] args, //
			@Cached("create()") IndirectCallNode callnode) {
		return (RuleResult) callnode.call(frame, lookupCallTarget(term), args);
	}

	protected CallTarget lookupCallTarget(Object term) {
		ITerm con = BuiltinTypesGen.asITerm(term);
		if (context == null) {
			context = DynSemContext.LANGUAGE.getContext();
		}
		return context.getRuleRegistry().lookupRule(arrowName, con.constructor(), con.arity()).getCallTarget();
	}

	protected ITermInstanceChecker lookupInstanceChecker(Object o) {
		return BuiltinTypesGen.asITerm(o).getCheck();
	}

}
