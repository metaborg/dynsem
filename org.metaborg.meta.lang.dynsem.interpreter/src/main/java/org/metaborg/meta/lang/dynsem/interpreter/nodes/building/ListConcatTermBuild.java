package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.terms.concrete.Cons;
import org.metaborg.meta.lang.dynsem.interpreter.terms.concrete.Nil;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "left", type = TermBuild.class),
		@NodeChild(value = "right", type = TermBuild.class) })
public abstract class ListConcatTermBuild extends TermBuild {

	public ListConcatTermBuild(SourceSection source) {
		super(source);
	}

	@Specialization
	public Nil doNilNil(Nil nil1, Nil nil2) {
		return nil1;
	}

	@Specialization
	public Cons doConsNil(Cons cons, Nil nil) {
		return cons;
	}

	@Specialization
	public Cons doNilCons(Nil nil, Cons cons) {
		return cons;
	}

	@Specialization
	public Cons doConsCons(Cons cons1, Cons cons2) {
		return (Cons) cons2.prefix(cons1.subterms());
	}

	public static ListConcatTermBuild create(IStrategoAppl t, FrameDescriptor fd) {
		assert Tools.hasConstructor(t, "ListConcat", 2);

		TermBuild left = TermBuild.create(Tools.applAt(t, 0), fd);
		TermBuild right = TermBuild.create(Tools.applAt(t, 1), fd);

		return ListConcatTermBuildNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t), left, right);
	}

}
