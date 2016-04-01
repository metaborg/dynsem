package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.Premise;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.source.SourceSection;

/**
 * 
 * A node corresponding to a (merged) DynSem rule.
 * 
 *
 * A rule has inputs of three types: (1) Read-only semantic components (ROs), (2) Pattern-bound variables (PVs), (3)
 * Read-write semantic components (RWs)
 * 
 * These inputs are passed through the arguments array in the following order <ROs,PVs,RWs>. This order coincides with
 * the binding order inside the rule.
 * 
 * @author vladvergu
 *
 */
public class DynSemRule extends Rule {

	private final String name;
	private final String constr;
	private final int arity;

	@Children protected final Premise[] premises;

	@Child protected RuleTarget target;

	public DynSemRule(String name, String constr, int arity, Premise[] premises, RuleTarget output, SourceSection source) {
		super(source);
		this.name = name;
		this.constr = constr;
		this.arity = arity;
		this.premises = premises;
		this.target = output;
	}

	@Override
	@ExplodeLoop
	public RuleResult execute(VirtualFrame frame) {
		/* evaluate the premises */
		for (int i = 0; i < premises.length; i++) {
			premises[i].execute(frame);
		}

		/* evaluate the rule target */
		return target.execute(frame);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getConstructor() {
		return constr;
	}

	@Override
	public int getArity() {
		return arity;
	}

	@Override
	public String toString() {
		return "Reduction rule: " + name + "/" + constr + "/" + arity;
	}

}
