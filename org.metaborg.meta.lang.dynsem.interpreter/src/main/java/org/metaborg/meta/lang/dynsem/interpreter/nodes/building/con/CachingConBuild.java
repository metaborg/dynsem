package org.metaborg.meta.lang.dynsem.interpreter.nodes.building.con;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.terms.concrete.ApplTerm;
import org.metaborg.meta.lang.dynsem.interpreter.terms.concrete.Cons;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

@NodeChild(value = "args", type = ConArgBuild.class)
public abstract class CachingConBuild extends TermBuild {

	private final String name;
	private final String sort;
	private final IStrategoList childrenAterm;
	private final FrameDescriptor fd;

	public CachingConBuild(SourceSection source, String name, String sort, IStrategoList childrenAterm,
			FrameDescriptor fd) {
		super(source);
		this.sort = sort;
		this.name = name;
		this.childrenAterm = childrenAterm;
		this.fd = fd;
	}

	@Specialization(limit = "1", guards = { "args == args_cached" })
	public ApplTerm doSimple(Cons args, @Cached("args") Cons args_cached,
			@Cached("createAppl(args_cached)") ApplTerm result_cached) {
		return result_cached;
	}

	@Specialization
	public ApplTerm doUncached(VirtualFrame frame, Cons args) {
		CompilerDirectives.transferToInterpreterAndInvalidate();
		return replace(new ConBuild(getSourceSection(), name, sort, TermBuild.createArray(childrenAterm, fd)))
				.executeEvaluated(frame, args.subterms());
	}

	protected ApplTerm createAppl(Cons args) {
		return new ApplTerm(sort, name, args.subterms());
	}

	public static TermBuild create(IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "Con", 3);
		String constr = Tools.stringAt(t, 0).stringValue();
		String sort = Tools.applAt(t, 2).toString();
		IStrategoList childrenT = Tools.listAt(t, 1);

		TermBuild[] children = new TermBuild[childrenT.size()];
		for (int i = 0; i < children.length; i++) {
			children[i] = TermBuild.create(Tools.applAt(childrenT, i), fd);
		}
		SourceSection source = SourceUtils.dynsemSourceSectionFromATerm(t);

		if (children.length == 0) {
			return NullaryConBuildNodeGen.create(source, constr, sort);
		} else {
			return CachingConBuildNodeGen.create(source, constr, sort, childrenT, fd,
					ConArgBuild.fromTermBuilds(source, children));
		}
	}

}
