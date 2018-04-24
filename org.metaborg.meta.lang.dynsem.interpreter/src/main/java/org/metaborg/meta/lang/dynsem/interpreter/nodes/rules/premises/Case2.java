package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.profiles.BranchProfile;
import com.oracle.truffle.api.source.SourceSection;

public abstract class Case2 extends DynSemNode {

	@Child protected MatchPattern guard;
	@Children protected Premise[] premises;
	@Child protected Case2 next;

	public Case2(SourceSection source, MatchPattern guard, Premise[] premises, Case2 next) {
		super(source);
		this.guard = guard;
		this.premises = premises;
		this.next = next;
	}

	public abstract boolean execute(VirtualFrame frame, Object t);

	@Specialization(guards = { "guard == null", "next == null" })
	@ExplodeLoop
	public boolean executeNoGuardNoNext(VirtualFrame frame, Object t) {
		evaluatePremises(frame);
		return true;
	}

	@Specialization(guards = { "guard != null", "next == null" })
	@ExplodeLoop
	public boolean executeGuardNoNext(VirtualFrame frame, Object t) {
		if (guard.executeMatch(frame, t)) {
			evaluatePremises(frame);
			return true;
		} else {
			return false;
		}
	}

	private final BranchProfile nextTaken = BranchProfile.create();

	@Specialization(guards = { "guard != null", "next != null" })
	@ExplodeLoop
	public boolean executeGuardWithNext(VirtualFrame frame, Object t) {
		if (guard.executeMatch(frame, t)) {
			evaluatePremises(frame);
			return true;
		} else {
			nextTaken.enter();
			return next.execute(frame, t);
		}
	}

	protected void evaluatePremises(VirtualFrame frame) {
		for (Premise p : premises) {
			p.execute(frame);
		}
	}

	public static Case2 create(DynSemLanguage lang, IStrategoList ts, FrameDescriptor fd) {
		if (ts.size() == 0) {
			return null;
		}

		IStrategoAppl t = Tools.applAt(ts, 0);
		if (Tools.hasConstructor(t, "CaseOtherwise", 1)) {
			IStrategoList premTs = Tools.listAt(t, 0);
			Premise[] premises = new Premise[premTs.size()];
			for (int i = 0; i < premises.length; i++) {
				premises[i] = Premise.create(lang, Tools.applAt(premTs, i), fd);
			}
			return Case2NodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t), null, premises, null);
		} else {
			assert Tools.hasConstructor(t, "CasePattern", 2);

			MatchPattern pattern = MatchPattern.create(Tools.applAt(t, 0), fd);

			IStrategoList premTs = Tools.listAt(t, 1);
			Premise[] premises = new Premise[premTs.size()];
			for (int i = 0; i < premises.length; i++) {
				premises[i] = Premise.create(lang, Tools.applAt(premTs, i), fd);
			}
			return Case2NodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t), pattern, premises,
					create(lang, ts.tail(), fd));
		}
	}

}
