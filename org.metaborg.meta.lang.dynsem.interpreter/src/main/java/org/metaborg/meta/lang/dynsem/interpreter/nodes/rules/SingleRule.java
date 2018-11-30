package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.ITermRegistry;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.Premise;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.PremiseFactories;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.ExplodeLoop.LoopExplosionKind;
import com.oracle.truffle.api.source.SourceSection;

public class SingleRule extends Rule {

	@Child protected RuleInputsNode inputsNode;

	@Children protected final Premise[] premises;

	@Child protected RuleTarget target;

	public SingleRule(SourceSection source, RuleInputsNode inputsNode, Premise[] premises, RuleTarget output) {
		super(source);

		this.inputsNode = inputsNode;
		this.premises = premises;
		this.target = output;
	}

	@Override
	public RuleResult evaluateRule(VirtualFrame frame) {
		/* evaluate the inputs node */
		inputsNode.execute(frame);

		/* evaluate the premises */
		evaluatePremises(frame);

		/* evaluate the rule target */
		return target.execute(frame);
	}

	@ExplodeLoop(kind = LoopExplosionKind.FULL_EXPLODE)
	private void evaluatePremises(VirtualFrame frame) {
		CompilerAsserts.compilationConstant(premises.length);
		for (int i = 0; i < premises.length; i++) {
			premises[i].execute(frame);
		}
	}

	@TruffleBoundary
	public static SingleRule createFromATerm(DynSemLanguage lang, IStrategoAppl ruleT, FrameDescriptor fd,
			ITermRegistry termReg) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(ruleT, "Rule", 5) : "Unexpected constructor " + ruleT.getConstructor();

		IStrategoList premisesTerm = Tools.listAt(ruleT, 0);
		Premise[] premises = new Premise[premisesTerm.size()];
		for (int i = 0; i < premises.length; i++) {
			premises[i] = PremiseFactories.create(lang, Tools.applAt(premisesTerm, i), fd, termReg);
		}

		IStrategoAppl relationT = Tools.applAt(ruleT, 2);
		assert Tools.hasConstructor(relationT, "Relation", 3);
		IStrategoAppl lhsSourceTerm = Tools.applAt(relationT, 0);
		IStrategoAppl lhsLeftTerm = Tools.applAt(lhsSourceTerm, 0);
		IStrategoList lhsCompsTerm = Tools.listAt(lhsSourceTerm, 1);

		RuleTarget target = RuleTarget.create(Tools.applAt(relationT, 2), fd, termReg);

		return new SingleRule(SourceUtils.dynsemSourceSectionFromATerm(ruleT),
				RuleInputsNode.create(lhsLeftTerm, lhsCompsTerm, fd, termReg), premises, target);
	}

}
