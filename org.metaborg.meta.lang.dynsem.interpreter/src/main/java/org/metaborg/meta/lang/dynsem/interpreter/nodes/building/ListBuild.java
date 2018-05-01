package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.source.SourceSection;

public abstract class ListBuild extends TermBuild {

	public ListBuild(SourceSection source) {
		super(source);
	}

	public static TermBuild create(IStrategoAppl t, FrameDescriptor fd) {
		assert Tools.hasConstructor(t, "TypedList", 2) || Tools.hasConstructor(t, "TypedListTail", 3);

		SourceSection source = SourceUtils.dynsemSourceSectionFromATerm(t);

		final String sort = Tools.javaStringAt(t, Tools.hasConstructor(t, "TypedList", 2) ? 1 : 2);

		TermBuild tailNode = null;
		if (Tools.hasConstructor(t, "TypedListTail", 3)) {
			tailNode = TermBuild.create(Tools.applAt(t, 1), fd);
		} else {
			tailNode = NilBuildNodeGen.create(source, sort.intern());
		}

		IStrategoList elemTs = Tools.listAt(t, 0);
		for (int i = 0; i < elemTs.size(); i++) {
			tailNode = ConsBuildNodeGen.create(source, sort.intern(), TermBuild.create(Tools.applAt(elemTs, i), fd),
					tailNode);
		}

		return tailNode;
	}

}
