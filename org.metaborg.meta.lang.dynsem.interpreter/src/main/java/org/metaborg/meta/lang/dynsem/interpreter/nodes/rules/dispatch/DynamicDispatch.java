package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.PremiseFailureException;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.ReductionFailure;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.ExplodeLoop.LoopExplosionKind;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.source.SourceSection;

public abstract class DynamicDispatch extends AbstractDispatch {

	public DynamicDispatch(SourceSection source, String arrowName) {
		super(source, arrowName);
	}

	@Specialization(guards = "termClass(args) == termClass", limit = "3")
	@ExplodeLoop(kind = LoopExplosionKind.FULL_EXPLODE_UNTIL_RETURN)
	public RuleResult doCaching(Object[] args, @Cached("termClass(args)") Class<?> termClass,
			@Cached("create(getSourceSection(), lookupTargets(termClass))") DirectCallChain chain) {
		return chain.execute(args);
	}

	@Specialization
	public RuleResult doLookup(Object[] args, @Cached("create()") IndirectCallNode callNode) {
		Class<?> termClass = termClass(args);
		InterpreterUtils.printlnOut("Cache miss for " + termClass.getSimpleName());
		CallTarget[] targets = lookupTargets(termClass);
		for (CallTarget target : targets) {
			try {
				return (RuleResult) callNode.call(target, args);
			} catch (PremiseFailureException pmfex) {
				;
			}
		}
		throw new ReductionFailure("Reduction failed on arrow -" + arrowName + "-> for term " + args[0],
				InterpreterUtils.createStacktrace(), this);
	}

	protected static Class<?> termClass(Object[] args) {
		return args[0].getClass();
	}

	protected CallTarget[] lookupTargets(Class<?> termClass) {
		return getContext().getRuleRegistry().lookupCallTargets(arrowName, termClass);
	}

	protected DirectCallNode[] createCallNodes(Class<?> termClass) {
		CallTarget[] targets = lookupTargets(termClass);
		DirectCallNode[] callNodes = new DirectCallNode[targets.length];
		for (int i = 0; i < targets.length; i++) {
			callNodes[i] = DirectCallNode.create(targets[i]);
		}
		return callNodes;
	}

	public static DynamicDispatch create(SourceSection source, String arrowName) {
		return DynamicDispatchNodeGen.create(source, arrowName);
	}

}
