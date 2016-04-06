package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.Premise;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
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

	@Children protected final Premise[] premises;

	@Child protected RuleTarget target;

	public DynSemRule(String name, String constr, int arity, MatchPattern inPattern, Premise[] premises,
			RuleTarget output, SourceSection source) {
		super(source);
		this.name = name;
		this.constr = constr;
		this.arity = arity;
		this.inPattern = inPattern;
		this.premises = premises;
		this.target = output;
	}

	private final ConditionProfile condProfile = ConditionProfile.createBinaryProfile();

	@Override
	@ExplodeLoop
	public RuleResult execute(VirtualFrame frame) {
		if (condProfile.profile(inPattern.execute(frame.getArguments()[0], frame))) {

			/* evaluate the premises */
			for (int i = 0; i < premises.length; i++) {
				premises[i].execute(frame);
			}

			/* evaluate the rule target */
			return target.execute(frame);
		} else {
			CompilerAsserts.neverPartOfCompilation();
			throw new RuntimeException("Incompatible rule selection");
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
