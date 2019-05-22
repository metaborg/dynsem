package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Occurrence;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.TermIndex;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.Fresh;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.FreshNodeGen;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.terms.TermFactory;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "namespace", type = TermBuild.class),
		@NodeChild(value = "name", type = TermBuild.class) })
public abstract class MakeFreshOccurrence extends NativeOpBuild {

	@Child private Fresh freshGen;

	public MakeFreshOccurrence(SourceSection source) {
		super(source);
		this.freshGen = FreshNodeGen.create(source);
	}

	@Specialization
	public Occurrence execBuild(VirtualFrame frame, String namespace, String name) {
		IStrategoString resource = new TermFactory().makeString("<phantom>");
		return new Occurrence(namespace, name, new TermIndex(resource, freshGen.executeInteger(frame)));
	}

	public static MakeFreshOccurrence create(SourceSection source, TermBuild namespace, TermBuild name) {
		return ScopeNodeFactories.createMakeFreshOccurrence(source, namespace, name);
	}

}
