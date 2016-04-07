package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.Premise;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.profiles.ConditionProfile;
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

	@Child protected MatchPattern inPattern;
	@Children protected final MatchPattern[] componentPatterns;

	@Children protected final Premise[] premises;

	@Child protected RuleTarget target;

	public DynSemRule(String name, String constr, int arity, MatchPattern inPattern, MatchPattern[] componentPatterns,
			Premise[] premises, RuleTarget output, SourceSection source) {
		super(source);
		this.name = name;
		this.constr = constr;
		this.arity = arity;
		this.inPattern = inPattern;
		this.componentPatterns = componentPatterns;
		this.premises = premises;
		this.target = output;
	}

	private final ConditionProfile condProfile = ConditionProfile.createBinaryProfile();

	@Override
	public RuleResult execute(VirtualFrame frame) {
		Object[] args = frame.getArguments();
		if (condProfile.profile(inPattern.execute(args[0], frame))) {

			/* evaluate the semantic component pattern matches */
			evaluateComponentPatterns(args, frame);

			/* evaluate the premises */
			evaluatePremises(frame);

			/* evaluate the rule target */
			return target.execute(frame);
		} else {
			CompilerAsserts.neverPartOfCompilation();
			throw new RuntimeException("Incompatible rule selection");
		}
	}

	@ExplodeLoop
	private void evaluatePremises(VirtualFrame frame) {
		CompilerAsserts.compilationConstant(premises.length);
		for (int i = 0; i < premises.length; i++) {
			premises[i].execute(frame);
		}
	}

	@ExplodeLoop
	private void evaluateComponentPatterns(Object[] args, VirtualFrame frame) {
		CompilerAsserts.compilationConstant(componentPatterns.length);
		for (int i = 0; i < componentPatterns.length; i++) {
			componentPatterns[i].execute(args[i + 1], frame);
		}
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
	@TruffleBoundary
	public String toString() {
		return "Reduction rule: " + name + "/" + constr + "/" + arity;
	}

}
