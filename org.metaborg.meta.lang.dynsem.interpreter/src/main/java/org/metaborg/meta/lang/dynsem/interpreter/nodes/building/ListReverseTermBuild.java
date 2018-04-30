package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.terms.concrete.Cons;
import org.metaborg.meta.lang.dynsem.interpreter.terms.concrete.ConsNilList;
import org.metaborg.meta.lang.dynsem.interpreter.terms.concrete.Nil;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "list", type = TermBuild.class) })
public abstract class ListReverseTermBuild extends TermBuild {

	public ListReverseTermBuild(SourceSection source) {
		super(source);
	}

	@Specialization
	public Nil doNil(Nil nil) {
		return nil;
	}

	public ConsNilList doCons(Cons cons) {
		final String sort = cons.sort();
		final IStrategoTerm aterm = cons.getStrategoTerm();
		Object[] elems = cons.subterms();
		ConsNilList reversed = new Nil(sort, aterm);
		for (int i = 0; i < elems.length; i++) {
			reversed = new Cons(sort, elems[i], reversed, aterm);
		}
		return reversed;
	}

	public static ListReverseTermBuild create(IStrategoAppl t, FrameDescriptor fd) {
		assert Tools.hasConstructor(t, "Reverse", 1);

		TermBuild list = TermBuild.create(Tools.applAt(t, 0), fd);

		return ListReverseTermBuildNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t), list);
	}

}
