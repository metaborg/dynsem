package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import java.util.HashSet;
import java.util.Set;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.TermVisitor;
import org.spoofax.terms.util.NotImplementedException;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public abstract class Rule extends DynSemNode {

	private final FrameDescriptor fd;

	public Rule(SourceSection sourceSection, FrameDescriptor fd) {
		super(sourceSection);
		this.fd = fd;
	}

	public abstract int getArity();

	public abstract String getConstructor();

	public abstract String getName();

	public abstract RuleResult execute(VirtualFrame frame);

	public FrameDescriptor getFrameDescriptor() {
		return fd;
	}

	public static Rule create(IStrategoTerm t) {
		assert Tools.isTermAppl(t) : "expected application term but got " + t;
		return create((IStrategoAppl) t);
	}

	public static Rule create(IStrategoAppl t) {
		if (Tools.hasConstructor(t, "Rule", 3)) {
			return ReductionRule.create(t);
		}
		throw new NotImplementedException("Unsupported rule term: " + t);
	}

	protected static FrameDescriptor createFrameDescriptor(IStrategoTerm t) {
		Set<String> vars = new HashSet<>();
		TermVisitor visitor = new TermVisitor() {

			@Override
			public void preVisit(IStrategoTerm t) {
				if (Tools.isTermAppl(t) && Tools.hasConstructor((IStrategoAppl) t, "VarRef", 1)) {
					vars.add(Tools.stringAt(t, 0).stringValue());
				}
			}
		};

		visitor.visit(t);
		FrameDescriptor fd = new FrameDescriptor();
		for (String v : vars) {
			fd.addFrameSlot(v);
		}
		return fd;
	}

}